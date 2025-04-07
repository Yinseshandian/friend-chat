package com.li.chat.config;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.List;

/**
 * @author malaka
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Autowired
    TypeResolver typeResolver;
    @Bean
    public Docket userApi(){

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.li.chat.controller"))
                .paths(PathSelectors.any())
                .build()
                .groupName("UserGroup");
    }

    private List<ResponseMessage> getResponseMessages() {
        List<ResponseMessage> collect = Arrays.asList((new ResponseMessageBuilder()).code(200).message("成功").build(), (new ResponseMessageBuilder()).code(500).message("系统繁忙").build(), (new ResponseMessageBuilder()).code(501).message("服务超时").build(), (new ResponseMessageBuilder()).code(403).message("会话超时，请重新登录").build(), (new ResponseMessageBuilder()).code(401).message("缺少token参数").build());
        return collect;
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("用户接口")
                .description("用户端接口，群组、好友、用户信息。\n接口访问需要请求头携带 Authorization 鉴权")
                .version("1.0")
                .build();
    }
}