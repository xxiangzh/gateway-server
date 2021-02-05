package com.xzh.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.xzh.gateway.entity.Constant;
import com.xzh.gateway.utils.TokenUtils;
import com.xzh.gateway.utils.UriUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 登陆过滤器
 *
 * @author 向振华
 * @date 2021/01/15 15:52
 */
@Component
public class LoginFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        // 开放接口
        if (UriUtils.isOpenPathGlobal(path)) {
            return chain.filter(exchange);
        }
        JSONObject token = TokenUtils.verifyGet(exchange);
        ServerHttpRequest request = exchange.getRequest().mutate().headers(httpHeaders -> {
            httpHeaders.remove(Constant.AUTHORIZATION);
            httpHeaders.remove(Constant.USER_TOKEN);
            httpHeaders.add(Constant.USER_TOKEN, token.toJSONString());
        }).build();
        return chain.filter(exchange.mutate().request(request).build());
    }

    @Override
    public int getOrder() {
        return 1;
    }
}