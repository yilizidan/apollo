package org.apollo.blog.controller.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.apollo.blog.common.ApiResult;
import org.apollo.blog.common.LogAnnotation;
import org.apollo.blog.entity.User;
import org.apollo.blog.service.UserService;
import org.apollo.blog.util.Base64Utils;
import org.apollo.blog.util.CookieUtils;
import org.apollo.blog.util.MD5EncryptionUtil;
import org.apollo.blog.util.RSA.LoginRSAUtils;
import org.apollo.blog.util.StringUtil;
import org.apollo.blog.util.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api")
@Api(tags = "登录相关", description = "登录相关")
public class HomeController {

	private final String redis_pix = "apollo:login:vcode";

	private final String redis_up = "apollo:login:userpwd:pl:";

	private final String redis_upList = "apollo:login:userpwd:ul:";

	@Resource
	private RedisTemplate<String, Object> redisTemplate;
	@Resource
	private UserService userService;

	@ApiOperation(value = "登录")
	@LogAnnotation(action = "/api/login", targetType = "POST", remark = "登录")
	@PostMapping("/login")
	public ApiResult apilogin(HttpServletResponse response, HttpServletRequest request, @Param("username") String username,
							  @Param("password") String password, @Param("rememberMe") Boolean rememberMe) throws Exception {
		if (StringUtil.strIsEmpty(password)) {
			password = request.getParameter("password");
		}
		byte[] decryptByPrivateKey = LoginRSAUtils.decryptByPrivateKey(Base64Utils.decode(password.replaceAll("%2B", "+")), LoginRSAUtils.getPrivateKeyString());

		//解密后的密码
		password = new String(decryptByPrivateKey);

		Subject subject = SecurityUtils.getSubject();
		UsernamePasswordToken token = new UsernamePasswordToken(username, password, rememberMe);
		try {
			subject.login(token);
			CookieUtils.addCookie(response, "login", true);

			if (rememberMe) {
				String psd = CookieUtils.getCookie(request, "psd");
				if (StringUtil.isNotEmpty(psd)) {
					psd = psd.replaceAll("\"", "");
				}

				boolean delredis = false;

				if (!redisTemplate.hasKey(redis_up + username + ":" + psd)) {
					psd = UUID.randomUUID().toString();
					psd = psd.replaceAll("\"", "");

					delredis = true;
				}

				String uuid = request.getSession().getId();

				Set<Object> stringSet = redisTemplate.opsForSet().members(redis_upList + username);
				if (stringSet.size() > 0) {
					for (Object s : stringSet) {
						if (s != null && !StringUtil.equals(uuid, s.toString())) {
							redisTemplate.boundSetOps(redis_upList + username).remove(redis_upList + username, s);
						}
					}
				}

				if (delredis) {
					if (stringSet.size() > 0) {
						for (Object s : stringSet) {
							redisTemplate.delete(redis_up + username + ":" + s);
						}
					}
					redisTemplate.opsForValue().set(redis_up + username + ":" + psd, password);
					redisTemplate.expire(redis_up + psd, 30 * 24 * 60 * 60, TimeUnit.SECONDS);
				}

				redisTemplate.opsForSet().add(redis_upList + username, psd);

				CookieUtils.addCookie(response, "psd", psd);
				CookieUtils.addCookie(response, "ischeck", "true");
			} else {
				CookieUtils.removeCookie(request, response, "psd");
				CookieUtils.addCookie(response, "ischeck", "false");
				String psd = CookieUtils.getCookie(request, "psd");
				if (StringUtil.isNotEmpty(psd)) {
					psd = psd.replaceAll("\"", "");
					redisTemplate.delete(redis_up + username + ":" + psd);
				}
			}
			CookieUtils.addCookie(response, "urd", username);
			return ApiResult.SUCCESS;
		} catch (UnknownAccountException uae) {
			log.info("对用户[" + username + "]进行登录验证..验证未通过,未知账户");
			return ApiResult.fail("未知账户");
		} catch (IncorrectCredentialsException ice) {
			log.info("对用户[" + username + "]进行登录验证..验证未通过,错误的凭证");
			return ApiResult.fail("密码不正确");
		} catch (LockedAccountException lae) {
			log.info("对用户[" + username + "]进行登录验证..验证未通过,账户已锁定");
			return ApiResult.fail("账户已锁定");
		} catch (ExcessiveAttemptsException eae) {
			log.info("对用户[" + username + "]进行登录验证..验证未通过,错误次数大于5次,账户已锁定");
			return ApiResult.fail("用户名或密码错误次数大于5次,账户已锁定");
		} catch (DisabledAccountException sae) {
			log.info("对用户[" + username + "]进行登录验证..验证未通过,帐号已经禁止登录");
			return ApiResult.fail("帐号已经禁止登录");
		} catch (AuthenticationException e) {
			log.info("对用户[" + username + "]进行登录验证..验证未通过,堆栈轨迹如下");
			return ApiResult.fail("用户或密码错误");
		}
	}

	@ApiOperation(value = "记住密码返回密码")
	@PostMapping("/rememberMe")
	public ApiResult rememberMe(HttpServletResponse response, HttpServletRequest request) {
		String urd = CookieUtils.getCookie(request, "urd");
		String psd = CookieUtils.getCookie(request, "psd");
		if (StringUtil.isNotEmpty(psd)) {
			psd = psd.replaceAll("\"", "");
		}

		String password = "";
		if (redisTemplate.hasKey(redis_up + urd + ":" + psd)) {
			password = (String) redisTemplate.opsForValue().get(redis_up + urd + ":" + psd);
		}
		Map<String, Object> map = new HashMap<>();
		try {
			map.put("psd", password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ApiResult.sucess(map);
	}

	@ApiOperation(value = "获取公钥")
	@PostMapping("/getPublicKey")
	public ApiResult getPublicKey() {
		return ApiResult.sucess(LoginRSAUtils.getPublicKeyString());
	}

	@ApiOperation(value = "注册")
	@PostMapping("/registered")
	@ExceptionHandler
	public ApiResult apiregistered(@Param("username") String username, @Param("password") String password,
	                               @Param("vcode") String vcode) throws Exception {
		String key = redis_pix + ":" + username;
		if (redisTemplate.hasKey(key)) {
			String code = (String) redisTemplate.opsForValue().get(key);
			if (!code.equals(vcode)) {
				return ApiResult.fail("验证码不正确,请重新确认邮件。");
			}
		} else {
			return ApiResult.fail("请获取验证码再输入。");
		}
		try {
			User user = userService.findByUsername(username);
			if (user == null) {
				user = User.builder()
						.username(username)
						.salt(MD5EncryptionUtil.getSalt(username))
						.password(MD5EncryptionUtil.encryptionPwd(password, user.getSalt(), username))
						.disabled(false)
						.build();
				userService.insert(user);
				return ApiResult.SUCCESS;
			} else {
				return ApiResult.fail("已存在该账号，请重新注册账号。");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return ApiResult.fail("系统异常，注册账号失败！");
		}
	}

	@ApiOperation(value = "忘记密码-修改密码")
	@PostMapping("/forgotpassword")
	@ExceptionHandler
	public ApiResult apiForgotPassWord(@Param("username") String username, @Param("password") String password,
	                                   @Param("vcode") String vcode) throws Exception {
		String key = redis_pix + ":" + username;
		if (redisTemplate.hasKey(key)) {
			String code = (String) redisTemplate.opsForValue().get(key);
			if (!code.equals(vcode)) {
				return ApiResult.fail("验证码不正确,请重新确认邮件。");
			}
		} else {
			return ApiResult.fail("请获取验证码再输入。");
		}
		try {
			User user = userService.findByUsername(username);
			if (user == null) {
				return ApiResult.fail("不存在该账号，请核对账号。");
			} else {
				user.setPassword(MD5EncryptionUtil.encryptionPwd(password, user.getSalt(), username));
				user.setDisabled(false);
				userService.updateByid(user);
				return ApiResult.SUCCESS;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return ApiResult.fail("系统异常，修改密码失败！");
		}
	}
}
