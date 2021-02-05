package com.xzh.gateway.utils;

import com.xzh.gateway.entity.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 权限工具
 *
 * @author 向振华
 * @date 2021/01/19 09:30
 */
@Component
public class PermissionUtils {

    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    public static AntPathMatcher antPathMatcher = new AntPathMatcher();

    public Mono<Boolean> isAllowed(String key, String path) {
        Flux<String> allPermissions = reactiveRedisTemplate.opsForSet().members(key.substring(0, 15) + Constant.ALL);
        Flux<String> userPermissions = reactiveRedisTemplate.opsForSet().members(key);
        Mono<Boolean> needVerify = allPermissions
                .filter(p -> antPathMatcher.match(p.replace("\"", ""), path))
                .count().map((count) -> count == 0);
        Mono<Boolean> isAllowed = userPermissions
                .filter(p -> antPathMatcher.match(p.replace("\"", ""), path))
                .count().map((count) -> count > 0);
        return Flux.merge(needVerify, isAllowed).reduce((x, y) -> x || y);
    }
}