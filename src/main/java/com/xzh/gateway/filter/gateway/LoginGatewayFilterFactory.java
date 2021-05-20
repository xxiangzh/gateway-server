package com.xzh.gateway.filter.gateway;

import com.alibaba.fastjson.JSONObject;
import com.xzh.gateway.entity.Constant;
import com.xzh.gateway.utils.ResponseUtils;
import com.xzh.gateway.utils.UriUtils;
import lombok.Data;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

/**
 * 登陆过滤器
 *
 * @author 向振华
 * @date 2021/01/15 15:52
 */
@Component
public class LoginGatewayFilterFactory extends AbstractGatewayFilterFactory<LoginGatewayFilterFactory.Config> {

    public LoginGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            // 开放接口
            if (UriUtils.isOpenPathGlobal(path) || UriUtils.isServiceMatchPath(config.getNotNeedLogin(), path)) {
                return chain.filter(exchange);
            }
            // 是否登录
            JSONObject userTokenCache = exchange.getAttribute(Constant.USER_TOKEN_CACHE);
            if (userTokenCache == null || userTokenCache.isEmpty()) {
                return ResponseUtils.fail(exchange, Constant.NOT_LOGIN, "未登录");
            }
            return chain.filter(exchange);
        };
    }

    @Data
    public static class Config {
        private String notNeedLogin;
    }
}