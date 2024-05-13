package com.li.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author malaka
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ChatBaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatBaseApplication.class, args);
    }

}
