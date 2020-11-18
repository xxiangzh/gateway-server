package com.xzh.gateway.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xzh.gateway.common.BusinessException;
import com.xzh.gateway.common.Constant;
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
 * @date 2020/09/08 09:31
 */
@Slf4j
public class ResponseUtils {

    /**
     * 返回response
     *
     * @param exchange
     * @param message  异常信息
     * @param status   data中的status
     * @return
     */
    public static Mono<Void> errorInfo(ServerWebExchange exchange, String message, Integer status) {
        Map<String, Object> resultMap = new HashMap<>(8);
        resultMap.put("status", status == null ? Constant.CODE_GATEWAY : status);
        resultMap.put("message", StringUtils.isBlank(message) ? Constant.ERROR_MESSAGE_EXCEPTION : message);
        resultMap.put("info", null);
        resultMap.put("data", null);
        return Mono.defer(() -> {
            byte[] bytes;
            try {
                bytes = new ObjectMapper().writeValueAsBytes(resultMap);
            } catch (JsonProcessingException e) {
                log.error("网关响应异常：", e);
                throw new BusinessException("信息序列化异常");
            } catch (Exception e) {
                log.error("网关响应异常：", e);
                throw new BusinessException("写入响应异常");
            }
            ServerHttpResponse response = exchange.getResponse();
            response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_UTF8.toString());
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Flux.just(buffer));
        });
    }
}
