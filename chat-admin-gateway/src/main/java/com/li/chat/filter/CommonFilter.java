package com.li.chat.filter;

import com.li.chat.common.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author malaka
 */

@Component
@Order(1)
public class CommonFilter implements  GlobalFilter {

    @Autowired
    private RedisCache redisCache;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        String sourceIp = request.getHeaders().getFirst("X-Real-IP");
        ServerHttpRequest mutableReq = request
                .mutate()
                .header("X-Real-IP", sourceIp)
                .header("X-Forwarded-For", sourceIp)
                .build();


        ServerWebExchange mutableExchange = exchange.mutate().request(mutableReq).build();
        return chain.filter(mutableExchange);

    }

}