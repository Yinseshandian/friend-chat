package com.li.chat.filter;

import com.li.chat.common.utils.RequestContext;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author malaka
 */

@Component
public class CommonFilter implements  GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 从我们的nginx 重写请求获取的参数
        String sourceIp = exchange.getRequest().getHeaders().getFirst("X-Real-IP");
        ServerHttpRequest mutableReq = exchange.getRequest()
                .mutate()
                .header("X-Real-IP", sourceIp)
                .header("X-Forwarded-For", sourceIp)
                .header(RequestContext.USER_ID_KEY, "10000")
                .build();


        ServerWebExchange mutableExchange = exchange.mutate().request(mutableReq).build();
        return chain.filter(mutableExchange);

    }

}