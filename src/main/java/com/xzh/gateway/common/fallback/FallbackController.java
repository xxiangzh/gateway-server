package com.xzh.gateway.common.fallback;

import com.xzh.gateway.common.Constant;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

/**
 * 服务降级
 *
 * @author 向振华
 * @date 2020/09/14 17:04
 */
@RestController
@RequestMapping
public class FallbackController {

    @RequestMapping("/incaseoffailureusethis")
    public Object fallback(ServerWebExchange exchange) {
        Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
        return String.format(Constant.ERROR_JSON_FORMAT_UNAVAILABLE, route != null ? route.getId() : Constant.SERVICE);
    }
}