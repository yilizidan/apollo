package org.apollo.blog.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apollo.blog.common.ApiResult;
import org.apollo.blog.processor.MailProcessor;
import org.apollo.blog.util.VCodeUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/mail")
@Api(tags = "邮件模块",description = "推送邮件")
public class MailController {

	private final String redis_pix = "apollo:login:vcode";

	@Resource
	private MailProcessor mailProcessor;
	@Resource
	private RedisTemplate<String, Object> redisTemplate;

	@ApiOperation(value = "发送模板邮件")
	@PostMapping({"/TemplateMail"})
	public ApiResult sendTemplateMail() {
		try {
			mailProcessor.sendTemplateMail("green025275100101@outlook.com", "主题：模板邮件");
			return ApiResult.sucess("模板邮件已经发送");
		} catch (Exception e) {
			log.error("发送模板邮件时发生异常！", e.getMessage());
		}
		return ApiResult.fail("发送模板邮件时发生异常");
	}

	@ApiOperation(value = "发送邮件")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "username", value = "用户账号"),
	})
	@PostMapping({"/sendvcode"})
	public ApiResult sendvcode(@Param("username") String username) {
		username = username.toLowerCase();
		try {
			String vcode = VCodeUtil.getRandomCode(8);
			mailProcessor.sendTemplateMail(username, vcode, "注册验证码");
			String key = redis_pix + ":" + username;
			redisTemplate.opsForValue().set(key, vcode);
			redisTemplate.expire(key, 1200L, TimeUnit.SECONDS);
			return ApiResult.sucess("邮件已经发送");
		} catch (Exception e) {
			log.error("发送邮件时发生异常！", e.getMessage());
		}
		return ApiResult.fail("发送邮件时发生异常");
	}
}
