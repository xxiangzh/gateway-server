package com.xzh.gateway.filter.gateway;

import com.xzh.gateway.common.Constant;
import com.xzh.gateway.common.redis.RedisService;
import com.xzh.gateway.utils.ResponseUtils;
import com.xzh.gateway.utils.TokenUtils;
import com.xzh.gateway.utils.UriUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * 权限过滤器（接口访问权限校验）
 *
 * @author: 向振华
 * @date: 2020/09/07 13:46
 */
@Component
public class ResourceGatewayFilterFactory extends AbstractGatewayFilterFactory<AbstractGatewayFilterFactory.NameConfig> {

    public final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Resource
    public RedisService redisService;

    public ResourceGatewayFilterFactory() {
        super(NameConfig.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("name");
    }

    @Override
    public GatewayFilter apply(NameConfig config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            // 开放接口
            if (UriUtils.isGlobalOpenPath(path) || UriUtils.isServiceMatchPath(config.getName(), path)) {
                return chain.filter(exchange);
            }
            String url = UriUtils.getUrl(path);
            // 通用url
            String commonKey = Constant.COMMON_RESOURCES;
            Set<Object> commonResources = redisService.sGet(commonKey);
            for (Object resource : commonResources) {
                if (antPathMatcher.match(resource.toString(), url)) {
                    return chain.filter(exchange);
                }
            }
            String userId = TokenUtils.getUserId(exchange.getRequest());
            // 权限
            String key = Constant.USER_RESOURCES + userId;
            Set<Object> resources = redisService.sGet(key);
            if (CollectionUtils.isEmpty(resources)) {
                return ResponseUtils.errorInfo(exchange, "权限不足。", Constant.CODE_RESOURCE_INVALID);
            }
            for (Object resource : resources) {
                if (antPathMatcher.match(resource.toString(), url)) {
                    return chain.filter(exchange);
                }
            }
            return ResponseUtils.errorInfo(exchange, "权限不足.", Constant.CODE_RESOURCE_INVALID);
        };
    }
}