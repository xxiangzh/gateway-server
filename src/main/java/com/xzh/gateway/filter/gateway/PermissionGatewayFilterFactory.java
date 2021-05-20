package com.xzh.gateway.filter.gateway;

import com.alibaba.fastjson.JSONObject;
import com.xzh.gateway.entity.Constant;
import com.xzh.gateway.utils.PermissionUtils;
import com.xzh.gateway.utils.ResponseUtils;
import com.xzh.gateway.utils.UriUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 权限过滤器
 *
 * @author 向振华
 * @date 2021/01/15 15:52
 */
@Component
public class PermissionGatewayFilterFactory extends AbstractGatewayFilterFactory<PermissionGatewayFilterFactory.Config> {

    @Autowired
    private PermissionUtils permissionUtils;

    public PermissionGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            JSONObject userTokenCache = exchange.getAttribute(Constant.USER_TOKEN_CACHE);
            // 未登录
            if (userTokenCache == null || userTokenCache.isEmpty()) {
                return chain.filter(exchange);
            }
            String path = exchange.getRequest().getURI().getPath();
            // 开放接口
            if (UriUtils.isOpenPathGlobal(path) || UriUtils.isServiceMatchPath(config.getNotNeedPermission(), path)) {
                return chain.filter(exchange);
            }
            String permissionKey = userTokenCache.getString("permissionKey");
            Mono<Boolean> allowed = permissionUtils.isAllowed(permissionKey, path);
            return allowed.flatMap(a -> a ? chain.filter(exchange) : ResponseUtils.fail(exchange, null, "权限不足"));
        };
    }

    @Data
    public static class Config {
        private String notNeedPermission;
    }
}