package com.xzh.gateway.utils;

import com.xzh.gateway.entity.Constant;
import com.xzh.gateway.error.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * 响应工具
 *
 * @author 向振华
 * @date 2021/02/03 17:04
 */
@Slf4j
public class ResponseUtils {

    /**
     * 返回response
     *
     * @param exchange
     * @param message
     * @return
     */
    public static Mono<Void> fail(ServerWebExchange exchange, String message) {
        Map<String, Object> resultMap = new HashMap<>(4);
        resultMap.put("code", Constant.ERROR_CODE);
        resultMap.put("message", StringUtils.isBlank(message) ? Constant.ERROR_MESSAGE : message);
        resultMap.put("data", null);
        return Mono.defer(() -> {
            byte[] bytes;
            try {
                bytes = new ObjectMapper().writeValueAsBytes(resultMap);
            } catch (Exception e) {
                log.error("网关响应异常：", e);
                throw new BusinessException(Constant.ERROR_MESSAGE);
            }
            ServerHttpResponse response = exchange.getResponse();
            response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_UTF8.toString());
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Flux.just(buffer));
        });
    }
}
