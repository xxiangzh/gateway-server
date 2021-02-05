package com.xzh.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.xzh.gateway.entity.Constant;
import com.xzh.gateway.utils.PermissionUtils;
import com.xzh.gateway.utils.ResponseUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 权限过滤器
 *
 * @author 向振华
 * @date 2021/01/15 15:52
 */
@Component
public class PermissionFilter implements GlobalFilter, Ordered {

    @Autowired
    private PermissionUtils permissionUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst(Constant.USER_TOKEN);
        if (StringUtils.isBlank(token)) {
            return chain.filter(exchange);
        }
        String permissionKey = JSONObject.parseObject(token).getString("permissionKey");
        String path = exchange.getRequest().getURI().getPath();
        Mono<Boolean> allowed = permissionUtils.isAllowed(permissionKey, path);
        return allowed.flatMap(a -> {
            if (a) {
                return chain.filter(exchange);
            } else {
                return ResponseUtils.fail(exchange, "权限不足");
            }
        });
    }

    @Override
    public int getOrder() {
        return 2;
    }
}