package com.li.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author malaka
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@ConfigurationPropertiesScan
public class NettyLoginApplication {

    public static void main(String[] args) {
        SpringApplication.run(NettyLoginApplication.class, args);
    }

}
