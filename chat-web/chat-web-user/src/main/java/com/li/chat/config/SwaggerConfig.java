package com.li.chat.config;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Predicates;
import com.li.chat.common.utils.ResultData;
import com.li.chat.vo.TestResult;
import com.li.chat.vo.UserInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
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
                .directModelSubstitute(TestResult.class, TestResult.class)
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