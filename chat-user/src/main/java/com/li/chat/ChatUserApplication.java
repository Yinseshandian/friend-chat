package com.li.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.li.chat.repository")
@EnableDiscoveryClient
public class ChatUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatUserApplication.class, args);
    }

}
