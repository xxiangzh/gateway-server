package com.xzh.gateway.utils;

import com.xzh.gateway.common.Constant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * Token工具
 *
 * @author 向振华
 * @date 2020/09/08 09:48
 */
@Component
public class TokenUtils {

    public static String jwtSigning;

    @Value("${custom.sso.jwtSigningKey}")
    public void setJwtSigning(String jwtSigning) {
        TokenUtils.jwtSigning = jwtSigning;
    }

    /**
     * 获取token
     *
     * @param serverHttpRequest
     * @return
     */
    public static String getToken(ServerHttpRequest serverHttpRequest) {
        List<String> authorizations = serverHttpRequest.getHeaders().get(Constant.AUTHORIZATION);
        if (CollectionUtils.isEmpty(authorizations) || StringUtils.isBlank(authorizations.get(0))) {
            return null;
        }
        return StringUtils.substring(authorizations.get(0), 6).trim();
    }

    /**
     * 获取claims
     *
     * @param token
     * @return
     */
    public static Claims getClaims(String token) {
        Claims claims;
        try {
            //解析claims
            claims = Jwts.parser()
                    .setSigningKey(jwtSigning.getBytes())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
        Date expiration = claims.getExpiration();
        if (expiration == null || expiration.before(new Date())) {
            return null;
        }
        return claims;
    }

    /**
     * 校验token
     *
     * @param token
     * @return
     */
    public static boolean verifyToken(String token) {
        return getClaims(token) != null;
    }

    /**
     * 获取用户ID
     *
     * @param request
     * @return
     */
    public static String getUserId(ServerHttpRequest request) {
        String token = getToken(request);
        if (StringUtils.isBlank(token)) {
            return null;
        }
        Claims claims = getClaims(token);
        if (claims == null) {
            return null;
        }
        Object userId = claims.get(Constant.HEADER_SA2_USER_ID);
        if (userId == null) {
            return null;
        }
        return userId.toString();
    }
}