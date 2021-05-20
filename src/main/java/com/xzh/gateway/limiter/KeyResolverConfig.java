package com.xzh.gateway.limiter;

import com.alibaba.fastjson.JSONObject;
import com.xzh.gateway.entity.Constant;
import com.xzh.gateway.utils.IpUtils;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 限流器规则
 *
 * @author 向振华
 * @date 2021/02/05 11:38
 */
@Configuration
public class KeyResolverConfig {

    @Primary
    @Bean
    public KeyResolver apiKeyResolver() {
        return exchange -> Mono.just(getKey(exchange));
    }

    private String getKey(ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        JSONObject userTokenCache = exchange.getAttribute(Constant.USER_TOKEN_CACHE);
        if (userTokenCache != null && !userTokenCache.isEmpty()) {
            return path + userTokenCache.toString();
        } else {
            return path + IpUtils.getIp(exchange);
        }
    }
}
