package org.apollo.blog.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apollo.blog.entity.User;
import org.apollo.blog.shiro.cache.RedisCacheManager;
import org.apollo.blog.shiro.session.RedisSessionDAO;
import org.crazycake.shiro.RedisManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.HashSet;
import java.util.concurrent.ConcurrentMap;

/**
 * 节点控制器
 */
@Slf4j
@Controller
@Api(tags = "节点控制器", description = "获取界面")
public class NodePageController {

	/********************** 登录、注册、退出等相关接口 **********************/

	/**
	 * 失效退出
	 */
	@ApiOperation(value = "失效退出")
	@GetMapping("/kickout")
	public String kickout() throws Exception {
		return "login";
	}

	/**
	 * 注册
	 */
	@ApiOperation(value = "注册")
	@GetMapping("/registered")
	public String registered() {
		return "registered";
	}

	/**
	 * 忘记密码
	 */
	@ApiOperation(value = "忘记密码")
	@GetMapping("/forgetpassword")
	public String forgetpassword() {
		return "forgetpassword";
	}

	/**
	 * 登录
	 */
	@ApiOperation(value = "登录")
	@GetMapping({"/login"})
	public String login() throws Exception {
		return "login";
	}

	/********************** 异常相关接口 **********************/

	@ApiOperation(value = "403")
	@GetMapping("/403")
	public String unauthorizedRole() {
		log.error("------没有权限-------");
		return "403";
	}

	@ApiOperation(value = "404")
	@GetMapping("/404")
	public String load404() {
		return "404";
	}

	@ApiOperation(value = "500")
	@GetMapping("/500")
	public String load500() {
		return "500";
	}

	/********************** 节点相关接口 **********************/

	/**
	 * 主页
	 */
	@ApiOperation(value = "主页")
	@GetMapping({"/index"})
	public String index() {
		return "index";
	}

	/**
	 * 个人资料
	 */
	@ApiOperation(value = "个人资料")
	@GetMapping({"/profile"})
	public String profile() {
		return "profile";
	}

	/**
	 * 首页
	 */
	@ApiOperation(value = "首页")
	@GetMapping({"/portal"})
	public String portal() {
		return "portal";
	}

	/**
	 * simple
	 */
	@ApiOperation(value = "simple")
	@GetMapping({"/simple"})
	public String simple() {
		return "simple";
	}

	/**
	 * 用户节点管理
	 */
	@ApiOperation(value = "用户节点管理")
	@GetMapping({"/nodeManage"})
	public String nodeManage() {
		return "nodeManage";
	}

	/**
	 * 用户角色节点管理
	 */
	@ApiOperation(value = "用户角色节点管理")
	@GetMapping({"/roleManage"})
	public String roleManage() {
		return "roleManage";
	}

	/**
	 * 修改头像
	 */
	@ApiOperation(value = "修改头像")
	@GetMapping({"/photoClip"})
	public String photoClip() {
		return "photoClip";
	}


	/**
	 * 节点设置
	 */
	@ApiOperation(value = "节点设置")
	@GetMapping({"/nodeLinkManage"})
	public String nodeLinkManage() {
		return "nodeLinkManage";
	}

	/**
	 * 服务配置
	 */
	@ApiOperation(value = "服务配置")
	@GetMapping({"/schedulejob"})
	public String scheduleJob() {
		return "schedulejob";
	}

	/**
	 * bilibili
	 */
	@ApiOperation(value = "bilibili")
	@GetMapping({"/bilibili"})
	public String bilibili() {
		return "bilibili";
	}

	/**
	 * bilibili完结动漫详情
	 */
	@ApiOperation(value = "bilibili完结动漫详情")
	@GetMapping({"/bilibiliBeOverDetails"})
	public String bilibiliBeOverDetails() {
		return "bilibilidetails";
	}

	@ApiOperation(value = "国漫番剧")
	@GetMapping({"/bilibiliByChina"})
	public String bilibiliByChina() {
		return "ChinaAnime";
	}

	@ApiOperation(value = "日漫番剧")
	@GetMapping({"/bilibiliByJapan"})
	public String bilibiliByJapan() {
		return "JapanAnime";
	}

	@Resource
	private RedisTemplate<String, Object> redisTemplate;
	@Resource
	private RedisManager redisManager;
	@Resource
	private RedisSessionDAO redisSessionDAO;

	@ApiOperation(value = "退出")
	@GetMapping("/logout")
	public String logout(HttpServletRequest request) throws Exception {

		String sessionId = request.getSession().getId();
		log.info("sessionId=" + sessionId);

		Subject subject = SecurityUtils.getSubject();
		User user = (User) subject.getPrincipal();
		//无登陆信息,直接跳转到登陆界面
		if (user == null) {
			return "login";
		}
		String username = user.getUsername();

		RedisCacheManager redisCacheManager = new RedisCacheManager();
		redisCacheManager.setRedisManager(redisManager);
		redisCacheManager.delCache(username);

		subject.logout();

		redisTemplate.delete(redisSessionDAO.getRedisStringKey(username));

		ConcurrentMap<String, HashSet<Serializable>> concurrentMap = redisSessionDAO.getUserMap();
		concurrentMap.remove(username);

		return "login";
	}

}
