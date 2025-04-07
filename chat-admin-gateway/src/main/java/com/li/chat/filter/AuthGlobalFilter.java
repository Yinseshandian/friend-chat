package com.li.chat.filter;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.li.chat.common.enums.RedisCachePrefixEnum;
import com.li.chat.common.utils.RedisCache;
import com.li.chat.common.utils.RequestContext;
import com.li.chat.common.utils.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author malaka
 */
@Component
@Order(0)
public class AuthGlobalFilter implements  GlobalFilter{


    @Autowired
    private RedisCache redisCache;

    private static final List<String> SKIP_AUTH_PATHS = Arrays.asList(
            "/admin/login",
            "/admin/captcha",
            "/admin/logout"
    );

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public AuthGlobalFilter(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 获取请求路径
        String path = request.getPath().value();

        // 跳过认证的路径
        if (isSkipAuth(path)) {
            return chain.filter(exchange);
        }

        // 获取token
        String token = getToken(request);
        if (StringUtils.isEmpty(token)) {
            return unauthorized(response, "登录已过期，请重新登录");
        }

        // 校验token
        String adminIdStr = redisTemplate.opsForValue().get(RedisCachePrefixEnum.ADMIN_AUTH_LOGIN_TOKEN_TO_ID + token);
        if (StringUtils.isEmpty(adminIdStr)) {
            return unauthorized(response, "登录已过期，请重新登录");
        }

        // 刷新token过期时间 30 分钟
        redisTemplate.expire(RedisCachePrefixEnum.ADMIN_AUTH_LOGIN_TOKEN_TO_ID + token, 30, TimeUnit.MINUTES);

        ServerHttpRequest mutatedRequest = request.mutate()
                .header(RequestContext.USER_ID_KEY, adminIdStr + "")
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    private boolean isSkipAuth(String path) {
        return SKIP_AUTH_PATHS.stream().anyMatch(path::endsWith);
    }

    private String getToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader == null) {
            return null;
        }
        if (authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return authHeader;
    }

    private Mono<Void> unauthorized(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ResultData resultData = ResultData.error(401, message);
        String body;
        try {
            body = objectMapper.writeValueAsString(resultData);
        } catch (JsonProcessingException e) {
            body = "{\"code\":401,\"message\":\"未授权\"}";
        }

        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

}