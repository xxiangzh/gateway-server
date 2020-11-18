package com.xzh.gateway.filter.global;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import com.xzh.gateway.common.Constant;
import com.xzh.gateway.common.mq.GatewayLog;
import com.xzh.gateway.common.mq.RocketMQProducer;
import com.xzh.gateway.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR;

/**
 * 响应过滤器
 *
 * @author 向振华
 * @date 2020/07/31 11:13
 */
@Slf4j
@Component
public class ResponseFilter implements GlobalFilter, Ordered {

    @Resource
    public RocketMQProducer rocketMQProducer;

    @Override
    public int getOrder() {
        return -2;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                @Override
                public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                    Object contentType = exchange.getAttribute(ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR);
                    if (HttpStatus.OK.equals(getStatusCode()) && body instanceof Flux
                            && Constant.CONTENT_TYPES.contains(contentType)) {
                        Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                        return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                            DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                            DataBuffer join = dataBufferFactory.join(dataBuffers);
                            byte[] content = new byte[join.readableByteCount()];
                            join.read(content);
                            DataBufferUtils.release(join);
                            String responseData = new String(content, Charsets.UTF_8);
                            CompletableFuture.runAsync(() -> sendLog(exchange, responseData));
                            byte[] uppedContent = responseData.getBytes(Charsets.UTF_8);
                            return bufferFactory.wrap(uppedContent);
                        }));
                    } else {
                        CompletableFuture.runAsync(() -> sendLog(exchange, null));
                        return chain.filter(exchange);
                    }
                }
            };
            return chain.filter(exchange.mutate().response(decoratedResponse).build());
        } catch (Exception e) {
            log.error("网关响应解析异常：", e);
            return chain.filter(exchange);
        }
    }

    /**
     * 发送日志
     *
     * @param exchange
     * @param responseData
     */
    private void sendLog(ServerWebExchange exchange, String responseData) {
        ServerHttpRequest request = exchange.getRequest();
        GatewayLog gatewayLog = new GatewayLog();
        gatewayLog.setUserId(getUserId(request));
        gatewayLog.setIp(getIp(request));
        gatewayLog.setUri(request.getURI().getPath());
        gatewayLog.setRequestMessage(getRequestBody(exchange));
        gatewayLog.setRequestTime(getRequestTime(exchange));
        gatewayLog.setResponseTimeAndHandleTime(System.currentTimeMillis());
        gatewayLog.setResponseMessage(responseData);
        gatewayLog.setStatus(getStatus(responseData));
        rocketMQProducer.send(gatewayLog);
    }

    /**
     * 获取状态
     *
     * @param responseData
     * @return
     */
    private Integer getStatus(String responseData) {
        if (StringUtils.isBlank(responseData)) {
            return Constant.CODE_GATEWAY_NO_RES;
        }
        try {
            JSONObject data = JSONObject.parseObject(responseData);
            if (data != null && data.containsKey(Constant.STATUS)) {
                return (Integer) data.get(Constant.STATUS);
            }
        } catch (Exception ignored) {
        }
        return Constant.CODE_GATEWAY_NO_STATUS;
    }

    /**
     * 获取用户ID
     *
     * @param request
     * @return
     */
    private long getUserId(ServerHttpRequest request) {
        String userId = TokenUtils.getUserId(request);
        if (StringUtils.isBlank(userId) || !StringUtils.isNumeric(userId)) {
            return 0L;
        }
        return Long.parseLong(userId);
    }

    /**
     * 获取请求体
     *
     * @param exchange
     * @return
     */
    private String getRequestBody(ServerWebExchange exchange) {
        Object requestBodyCache = exchange.getAttribute(Constant.REQUEST_BODY_CACHE);
        if (requestBodyCache == null) {
            return null;
        }
        byte[] body = (byte[]) requestBodyCache;
        String requestBody = new String(body, StandardCharsets.UTF_8);
        if (StringUtils.isBlank(requestBody)) {
            return null;
        }
        return requestBody;
    }

    /**
     * 获取请求时间
     *
     * @param exchange
     * @return
     */
    private Long getRequestTime(ServerWebExchange exchange) {
        Object requestTimeCache = exchange.getAttribute(Constant.REQUEST_TIME_CACHE);
        if (requestTimeCache == null) {
            return System.currentTimeMillis();
        }
        return (Long) requestTimeCache;
    }

    /**
     * 获取请求IP
     *
     * @param request
     * @return
     */
    private static String getIp(ServerHttpRequest request) {
        if (request == null) {
            return null;
        }
        String unknown = "unknown";
        String branch = ",";
        HttpHeaders headers = request.getHeaders();
        String ip = headers.getFirst("x-forwarded-for");
        if (ip != null && ip.length() != 0 && !unknown.equalsIgnoreCase(ip)) {
            if (ip.contains(branch)) {
                ip = ip.split(branch)[0];
            }
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = headers.getFirst("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = headers.getFirst("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = headers.getFirst("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = headers.getFirst("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = headers.getFirst("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            if (request.getRemoteAddress() != null) {
                ip = request.getRemoteAddress().getAddress().getHostAddress();
            }
        }
        return ip;
    }
}