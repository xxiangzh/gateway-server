package com.xzh.gateway.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xzh.gateway.entity.Constant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

/**
 * Token工具
 *
 * @author 向振华
 * @date 2021/01/15 15:52
 */
@Slf4j
@Component
public class TokenUtils {

    public static String jwtSigningKey;

    @Value("${jwt.signing.key}")
    public void setJwtSigning(String jwtSigningKey) {
        TokenUtils.jwtSigningKey = jwtSigningKey;
    }

    /**
     * 获取token实例
     *
     * @param exchange
     * @return
     */
    public static JSONObject get(ServerWebExchange exchange) {
        JSONObject jsonObject = new JSONObject();
        String authorization = exchange.getRequest().getHeaders().getFirst(Constant.AUTHORIZATION);
        if (authorization != null && StringUtils.startsWithIgnoreCase(authorization, Constant.BEARER)) {
            String token = authorization.substring(Constant.BEARER.length());
            try {
                Claims claims = Jwts.parser()
                        .setSigningKey(jwtSigningKey)
                        .parseClaimsJws(token)
                        .getBody();
                JSONObject parseObject = JSON.parseObject(claims.getSubject());
                if (parseObject != null) {
                    jsonObject = parseObject;
                }
            } catch (Exception e) {
                log.error("解析异常：", e);
            }
        }
        return jsonObject;

    }
}