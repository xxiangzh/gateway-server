package com.xzh.gateway.filter.gateway;

import com.xzh.gateway.common.Constant;
import com.xzh.gateway.utils.ResponseUtils;
import com.xzh.gateway.utils.UriUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractNameValueGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 文件大小过滤器
 *
 * @author 向振华
 * @date 2020/09/07 15:20
 */
@Component
public class FileSizeGatewayFilterFactory extends AbstractNameValueGatewayFilterFactory {

    @Override
    public GatewayFilter apply(NameValueConfig config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            if (!UriUtils.isServiceMatchPath(config.getValue(), path)) {
                return chain.filter(exchange);
            }
            ServerHttpRequest request = exchange.getRequest();
            List<String> list = request.getHeaders().get("Content-Length");
            if (!CollectionUtils.isEmpty(list) && Long.parseLong(list.get(0)) > Long.parseLong(config.getName())) {
                return ResponseUtils.errorInfo(exchange, "文件大小超限制", Constant.CODE_GATEWAY);
            }
            return chain.filter(exchange);
        };
    }
}