package com.xzh.gateway.utils;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

/**
 * URI工具
 *
 * @author 向振华
 * @date 2021/01/15 15:52
 */
@Component
public class UriUtils {

    public static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    public static String global;

    @Value(value = "${open.path.global}")
    public void setGlobal(String global) {
        UriUtils.global = global;
    }

    /**
     * 是否是全局开放接口
     *
     * @param path
     * @return
     */
    public static boolean isOpenPathGlobal(String path) {
        if (StringUtils.isNotBlank(global)) {
            for (String url : global.split(";")) {
                if (ANT_PATH_MATCHER.match(url, path)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否匹配接口（配置文件接口和请求接口匹配）
     *
     * @param config
     * @param path
     * @return
     */
    public static boolean isServiceMatchPath(String config, String path) {
        if (StringUtils.isNotBlank(config)) {
            for (String url : config.split(";")) {
                if (ANT_PATH_MATCHER.match(url, path)) {
                    return true;
                }
            }
        }
        return false;
    }
}
