// package com.li.chat.config;
//
// import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
// import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
//
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
//
// /**
//  * @author malaka
//  */
//
// @Slf4j
// @Configuration
// public class LocalDateTimeSerializerConfig {
//
//     private String pattern= "yyyy-MM-dd HH:mm:ss";
//
//     // localDateTime 反序列化器
//     @Bean
//     public LocalDateTimeDeserializer localDateTimeDeserializer() {
//         return new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(pattern));
//     }
//
//     @Bean
//     public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
//         return builder -> {
//             builder.deserializerByType(LocalDateTime.class, localDateTimeDeserializer());
//             builder.simpleDateFormat(pattern);
//         };
//     }
// }