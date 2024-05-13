package com.li.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author malaka
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ChatWebUserApplication {

    public static void main(String[] args) {

        SpringApplication.run(ChatWebUserApplication.class, args);
    }

}
