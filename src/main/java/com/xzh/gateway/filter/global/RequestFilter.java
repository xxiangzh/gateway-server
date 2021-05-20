package com.xzh.gateway.filter.global;


import com.xzh.gateway.entity.Constant;
import com.xzh.gateway.utils.TokenUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 请求过滤器
 *
 * @author 向振华
 * @date 2021/01/30 10:01
 */
@Component
public class RequestFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return DataBufferUtils.join(exchange.getRequest().getBody()).map(dataBuffer -> {
            byte[] bytes = new byte[dataBuffer.readableByteCount()];
            dataBuffer.read(bytes);
            DataBufferUtils.release(dataBuffer);
            return bytes;
        }).defaultIfEmpty(new byte[0])
                .doOnNext(bytes -> {
                    // 将请求体存入Attributes
                    exchange.getAttributes().put(Constant.REQUEST_BODY_CACHE, bytes);
                    // 请求时间
                    exchange.getAttributes().put(Constant.REQUEST_TIME_CACHE, System.currentTimeMillis());
                    // 登录用户信息
                    exchange.getAttributes().put(Constant.USER_TOKEN_CACHE, TokenUtils.get(exchange));
                })
                .then(chain.filter(exchange));
    }

    @Override
    public int getOrder() {
        return -99;
    }
}