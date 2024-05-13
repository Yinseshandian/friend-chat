package com.li.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author malaka
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ChatGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatGatewayApplication.class, args);
    }

}
