package com.xzh.gateway.filter.global;

import com.xzh.gateway.common.Constant;
import com.xzh.gateway.common.redis.RedisService;
import com.xzh.gateway.utils.TokenUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * 请求头过滤器（设置请求头，并删除Authorization）
 *
 * @author 向振华
 * @date 2020/07/30 15:23
 */
@Component
public class HeaderFilter implements GlobalFilter, Ordered {

    @Resource
    public RedisService redisService;

    @Override
    public int getOrder() {
        return 100;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取用户ID
        String sa2UserId = TokenUtils.getUserId(exchange.getRequest());
        ServerHttpRequest request = exchange.getRequest().mutate().headers(httpHeaders -> {
            httpHeaders.remove(Constant.AUTHORIZATION);
            if (StringUtils.isNotBlank(sa2UserId)) {
                httpHeaders.remove(Constant.HEADER_SA2_USER_ID);
                httpHeaders.add(Constant.HEADER_SA2_USER_ID, sa2UserId);
                httpHeaders.remove(Constant.HEADER_COMPANY_MAIN_DATA_ID);
                httpHeaders.add(Constant.HEADER_COMPANY_MAIN_DATA_ID, getCompanyMainDataId(sa2UserId));
                httpHeaders.remove(Constant.HEADER_ORG_ID);
                httpHeaders.add(Constant.HEADER_ORG_ID, getOrgId(sa2UserId));
            }
        }).build();
        return chain.filter(exchange.mutate().request(request).build());
    }

    /**
     * 主数据公司id
     *
     * @param userId
     * @return
     */
    private String getCompanyMainDataId(String userId) {
        Object companyMainDataId = redisService.hget(Constant.COMMON_PARAM_PREFIX + userId, Constant.HEADER_COMPANY_MAIN_DATA_ID);
        return companyMainDataId == null ? "" : String.valueOf(companyMainDataId);
    }

    /**
     * 统一门户公司id
     *
     * @param userId
     * @return
     */
    private String getOrgId(String userId) {
        Object orgId = redisService.hget(Constant.COMMON_PARAM_PREFIX + userId, Constant.HEADER_ORG_ID);
        return orgId == null ? "" : String.valueOf(orgId);
    }
}
