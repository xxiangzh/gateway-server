package com.xzh.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.xzh.gateway.entity.Constant;
import com.xzh.gateway.utils.ResponseUtils;
import com.xzh.gateway.utils.UriUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
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
        // 是否登录
        JSONObject userTokenCache = exchange.getAttribute(Constant.USER_TOKEN_CACHE);
        if (userTokenCache == null || userTokenCache.isEmpty()) {
            return ResponseUtils.fail(exchange, Constant.NOT_LOGIN, "未登录");
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 1;
    }
}