package com.li.chat.config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author malaka
 */
@Configuration
public class MinioConfiguration {

    @Bean
    public MinioClient minioClient(MinioConfig minioConfig) {
        return MinioClient.builder()
                .endpoint(minioConfig.getEndpoint())
                .credentials(minioConfig.getAccessKey(), minioConfig.getSecretKey())
                .build();
    }

}
