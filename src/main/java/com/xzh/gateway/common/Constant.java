package com.xzh.gateway.common;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @Description 常量类
 * @Author chenpiqian
 * @Date: 2019-09-25
 */
public class Constant {
    /**
     * 错误码
     */
    public static final String STATUS = "status";
    public static final Integer CODE_GATEWAY = 2;
    public static final Integer CODE_GATEWAY_NO_STATUS = -1;
    public static final Integer CODE_GATEWAY_NO_RES = -2;
    public static final Integer CODE_TOKEN_INVALID = 512345;
    public static final Integer CODE_RESOURCE_INVALID = 612345;

    /**
     * 错误消息
     */
    public static final String SERVICE = "服务";
    public static final String ERROR_MESSAGE_EXCEPTION = "服务异常！";
    public static final String ERROR_MESSAGE_UNAVAILABLE = "服务暂不可用！";
    public static final String ERROR_JSON_FORMAT_UNAVAILABLE = "{\"data\":null,\"message\":\"%s暂不可用\",\"status\":2,\"info\":null}";

    /**
     * header字段
     */
    public static final String AUTHORIZATION = "Authorization";
    public static final String HEADER_SA2_USER_ID = "sa2UserId";
    public static final String HEADER_COMPANY_MAIN_DATA_ID = "companyMainDataId";
    public static final String HEADER_ORG_ID = "orgId";

    /**
     * access_token前缀
     */
    public static final String REDID_TOKEN_PREFIX = "security-auth2:token:access:";

    /**
     * 用户信息缓存
     */
    public static final String COMMON_PARAM_PREFIX = "security-auth2:common-param:";

    /**
     * 用户权限key
     */
    public static final String USER_RESOURCES = "security-auth2:user-resources:";

    /**
     * 通用权限key
     */
    public static final String COMMON_RESOURCES = "security-auth2:common-resources";

    /**
     * 请求体缓存
     */
    public static final String REQUEST_BODY_CACHE = "requestBodyCache";

    /**
     * 请求时间记录缓存
     */
    public static final String REQUEST_TIME_CACHE = "requestTimeCache";

    /**
     * 可解析的content-type
     */
    public static final List<String> CONTENT_TYPES = Lists.newArrayList("application/json;charset=UTF-8", "text/plain;charset=UTF-8");
}
