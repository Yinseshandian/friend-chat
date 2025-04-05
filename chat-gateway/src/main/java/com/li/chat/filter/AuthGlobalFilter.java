package com.li.chat.filter;

import com.li.chat.common.enums.RedisCachePrefixEnum;
import com.li.chat.common.utils.RedisCache;
import com.li.chat.common.utils.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author malaka
 */
@Component
@Order(0)
public class AuthGlobalFilter implements GlobalFilter {


    @Autowired
    private RedisCache redisCache;

    private List<String> excludePaths = CollectionUtils.arrayToList(new String[] {"/user/auth/imgcaptcha","/user/auth/register","/user/auth/login"});

    public void setExcludePaths(List<String> excludePaths) {
        this.excludePaths = excludePaths;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        // 1. 检查是否在排除路径中
        if (isExcluded(path)) {
            return chain.filter(exchange);
        }

        // 2. 提取Token
        String token = extractToken(exchange.getRequest());

        // 3. 验证Token有效性
        String key = RedisCachePrefixEnum.USER_AUTH_LOGIN_TOKEN_TO_ID + token;

        Object obj = redisCache.getCacheObject(key);

        Long uid;
        if (obj instanceof Integer){
            uid = ((Integer)obj).longValue();
        }else {
            uid = (Long) obj;
        }

        if (uid != null) {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpRequest mutableReq = request
                    .mutate()
                    .header(RequestContext.USER_ID_KEY, uid + "")
                    .build();

            ServerWebExchange mutableExchange = exchange.mutate().request(mutableReq).build();
            return chain.filter(mutableExchange);
        }else {
            return unauthorizedResponse(exchange);
        }
    }

    private boolean isExcluded(String path) {
        PathPatternParser parser = new PathPatternParser();
        return excludePaths.stream()
                .anyMatch(pattern -> parser.parse(pattern).matches(PathContainer.parsePath(path)));
    }

    private String extractToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader == null) {
            return null;
        }
        if (authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return authHeader;
    }

    // 返回401未授权响应
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().add("Content-Type", "application/json");
        String body = "{\"code\": 401, \"message\": \"登录过期，请登录\"}";
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}