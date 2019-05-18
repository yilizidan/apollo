package org.apollo.blog.controller.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apollo.blog.common.ApiResult;
import org.apollo.blog.entity.User;
import org.apollo.blog.models.personal.PersonalInfoVO;
import org.apollo.blog.service.UserService;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 个人编辑操作类
 */
@Slf4j
@RestController
@RequestMapping("/api")
@Api(tags = "个人编辑操作类", description = "个人编辑")
public class PersonalController {

    @Resource
    private UserService userService;

    @ApiOperation(value = "获取个人信息")
    @PostMapping({"/init/personalinfo"})
    @RequiresAuthentication
    public ApiResult<PersonalInfoVO> personalinfo() {
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        return ApiResult.sucess(userService.getUserInfo(user.getId()));
    }

    @ApiOperation(value = "获取头像")
    @PostMapping({"/init/personalPic"})
    public ApiResult personalPic() {
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        long userid = user.getId();
        long l2 = System.currentTimeMillis();
        String propic = userService.getUserImage(userid);
        if (propic == null) {
            return ApiResult.fail("获取头像失败!");
        } else {
            System.out.println("获取默认头像 响应时间:" + (System.currentTimeMillis() - l2) + "ms");
            return ApiResult.sucess(propic);
        }
    }

    /**
     * 切换身份，登录后，动态更改subject的用户属性
     */
    public void setUser(User user) {
        Subject subject = SecurityUtils.getSubject();
        PrincipalCollection principalCollection = subject.getPrincipals();
        String realmName = principalCollection.getRealmNames().iterator().next();
        PrincipalCollection newPrincipalCollection = new SimplePrincipalCollection(user, realmName);
        subject.runAs(newPrincipalCollection);
    }

    /**
     * 修改头像
     */
    @ApiOperation(value = "修改头像")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "picsrc", value = "图片base64地址"),
    })
    @PostMapping("/edit/headPortrait")
    public ApiResult headPortrait(@Param("picsrc") String picsrc) throws Exception {
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        long userid = user.getId();
        userService.updateUserImage(userid, picsrc.replaceAll("%2B", "+"));
        return ApiResult.sucess("头像设置成功！");
    }

}
