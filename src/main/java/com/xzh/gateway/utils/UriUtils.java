package com.xzh.gateway.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

/**
 * URI工具
 *
 * @author 向振华
 * @date 2020/09/08 14:54
 */
@Component
public class UriUtils {

    public static final String BRANCH = ";";

    public static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    public static String openPaths;

    @Value(value = "${global.open.paths}")
    public void setOpenPaths(String openPaths) {
        UriUtils.openPaths = openPaths;
    }

    /**
     * 是否是全局开放接口
     *
     * @param path
     * @return
     */
    public static boolean isGlobalOpenPath(String path) {
        if (StringUtils.isNotBlank(openPaths)) {
            for (String openPath : openPaths.split(BRANCH)) {
                if (ANT_PATH_MATCHER.match(openPath, path)) {
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
            for (String url : config.split(BRANCH)) {
                if (ANT_PATH_MATCHER.match(url, path)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取请求路径
     *
     * @param path
     * @return
     */
    public static String getUrl(String path) {
        return split(path, 1);
    }

    /**
     * 对请求url切分
     *
     * @param path
     * @param index
     * @return
     */
    public static String split(String path, int index) {
        String[] split = StringUtils.split(path, "/", 2);
        return "/" + split[index];
    }
}
