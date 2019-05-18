package org.apollo.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * http://127.0.0.1:8456/laplace/swagger-ui.html#/访问swagger2
 *
 * @author Order
 * @date 2018/8/21 16:06:21
 */
@Configuration
@EnableSwagger2
@Profile({"local", "test"})
public class Swagger2Config {

    /**
     * 指定扫描的包
     */
    @Bean
    public Docket createRestApi() {
        List<ResponseMessage> responseMessageList = new ArrayList<>();
        responseMessageList.add(new ResponseMessageBuilder().code(200).message("成功").build());
        responseMessageList.add(new ResponseMessageBuilder().code(400).message("业务逻辑异常").build());
        responseMessageList.add(new ResponseMessageBuilder().code(401).message("未授权操作").build());
        responseMessageList.add(new ResponseMessageBuilder().code(402).message("登录超时").build());
        responseMessageList.add(new ResponseMessageBuilder().code(404).message("找不到资源").build());
        responseMessageList.add(new ResponseMessageBuilder().code(406).message("参数校验异常").build());
        responseMessageList.add(new ResponseMessageBuilder().code(500).message("服务器内部错误").build());

        return new Docket(DocumentationType.SWAGGER_2)
                .globalResponseMessage(RequestMethod.GET, responseMessageList)
                .globalResponseMessage(RequestMethod.POST, responseMessageList)
                .globalResponseMessage(RequestMethod.PUT, responseMessageList)
                .globalResponseMessage(RequestMethod.DELETE, responseMessageList)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.apollo.blog.controller"))
                .paths(PathSelectors.any())
                .build();

    }

    /**
     * swagger2构建文档的基本信息配置
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                // 页面标题
                .title("通用科技接口API")
                // 描述
                //.description("简单优雅的restfun风格，http://blog.csdn.net/saytime")
                // 版本号
                .version("1.0")
                .build();
    }

}
