package org.apollo.blog.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apollo.blog.common.ApiResult;
import org.apollo.blog.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
@Api(tags = "测试类", description = "测试类")
public class TestController {

    @Autowired
    private TestService testService;

    @GetMapping("test") //接口地址配置  RequestMapping PostMapping  GetMapping
    @ApiOperation("测试")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "名称", required = true),
            @ApiImplicitParam(name = "integer", value = "integer", required = true),
    })
    public ApiResult<?> addTest(String name, Integer integer) {
        return ApiResult.sucess(testService.getData(name, integer));
    }
}
