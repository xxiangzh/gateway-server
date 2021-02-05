package com.xzh.gateway.entity;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * 常量类
 *
 * @author 向振华
 * @date 2021/01/15 15:52
 */
public class Constant {

    public static final String CODE = "code";
    public static final int CODE_GATEWAY_NO_CODE = -1;
    public static final String ERROR_MESSAGE = "网络繁忙，请稍后重试";
    public static final int ERROR_CODE = 2;
    public static final int NOT_FOUND = 404;
    public static final int NOT_LOGIN = 935;
    public static final String USER_TOKEN = "user-token";
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String ALL = "all";
    public static final String REQUEST_BODY_CACHE = "requestBodyCache";
    public static final String REQUEST_TIME_CACHE = "requestTimeCache";
    public static final List<String> CONTENT_TYPES = Lists.newArrayList("application/json;charset=UTF-8", "text/plain;charset=UTF-8");
    public static final String DATE_TIME = "yyyy-MM-dd HH:mm:ss";

}
