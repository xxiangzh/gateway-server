package com.xzh.gateway.filter.global;

import com.alibaba.fastjson.JSONObject;
import com.xzh.gateway.entity.Constant;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 请求头过滤器
 *
 * @author 向振华
 * @date 2021/03/11 15:52
 */
@Component
public class HeaderFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        JSONObject userTokenCache = exchange.getAttribute(Constant.USER_TOKEN_CACHE);
        if (userTokenCache == null || userTokenCache.isEmpty()) {
            return chain.filter(exchange);
        }
        ServerHttpRequest request = exchange.getRequest().mutate().headers(httpHeaders -> {
            httpHeaders.remove(Constant.AUTHORIZATION);
            httpHeaders.remove(Constant.USER_TOKEN);
            httpHeaders.add(Constant.USER_TOKEN, userTokenCache.toJSONString());
        }).build();
        return chain.filter(exchange.mutate().request(request).build());
    }

    @Override
    public int getOrder() {
        return 99;
    }
}