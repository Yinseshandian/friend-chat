package com.li.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author malaka
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ChatAdminGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatAdminGatewayApplication.class, args);
    }

}
