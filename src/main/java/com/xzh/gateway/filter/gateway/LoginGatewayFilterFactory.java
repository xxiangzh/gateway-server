package com.xzh.gateway.filter.gateway;

import com.xzh.gateway.common.Constant;
import com.xzh.gateway.utils.ResponseUtils;
import com.xzh.gateway.utils.TokenUtils;
import com.xzh.gateway.utils.UriUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 登录过滤器（用户登录、客户端登录等token校验）
 *
 * @author 向振华
 * @date 2020/09/07 15:20
 */
@Component
public class LoginGatewayFilterFactory extends AbstractGatewayFilterFactory<AbstractGatewayFilterFactory.NameConfig> {

    public LoginGatewayFilterFactory() {
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
            String token = TokenUtils.getToken(exchange.getRequest());
            if (StringUtils.isBlank(token)) {
                return ResponseUtils.errorInfo(exchange, "未登录！", Constant.CODE_TOKEN_INVALID);
            }
            if (!TokenUtils.verifyToken(token)) {
                return ResponseUtils.errorInfo(exchange, "token无效！", Constant.CODE_TOKEN_INVALID);
            }
            return chain.filter(exchange);
        };
    }
}