package com.li.chat.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author malaka
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ChatAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatAdminApplication.class, args);
    }

}