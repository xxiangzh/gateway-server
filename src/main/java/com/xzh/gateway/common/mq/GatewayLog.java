package com.xzh.gateway.common.mq;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 网关日志
 *
 * @author 向振华
 * @date 2020/07/28 17:19
 */
@Data
public class GatewayLog {

    /**
     * 日志类型
     */
    private Integer logType;

    /**
     * 日志
     */
    private List<Log> logList;

    public GatewayLog() {
        this.logType = 4;
        this.logList = Lists.newArrayList(new Log());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Log {

        /**
         * 用户ID
         */
        private Long userId;

        /**
         * IP
         */
        private String ip;

        /**
         * uri
         */
        private String uri;

        /**
         * 请求报文
         */
        private String requestMessage;

        /**
         * 响应报文
         */
        private String responseMessage;

        /**
         * 返回状态
         */
        private Integer status;

        /**
         * 请求时间时间戳
         */
        private Long requestTime;

        /**
         * 响应时间时间戳
         */
        private Long responseTime;

        /**
         * 处理时间
         */
        private Long handleTime;
    }

    public void setUserId(Long userId) {
        this.logList.get(0).setUserId(userId);
    }

    public void setIp(String ip) {
        this.logList.get(0).setIp(ip);
    }

    public void setUri(String uri) {
        this.logList.get(0).setUri(uri);
    }

    public void setRequestMessage(String requestMessage) {
        this.logList.get(0).setRequestMessage(requestMessage);
    }

    public void setResponseMessage(String responseMessage) {
        this.logList.get(0).setResponseMessage(responseMessage);
    }

    public void setStatus(Integer status) {
        this.logList.get(0).setStatus(status);
    }

    public void setRequestTime(Long requestTime) {
        this.logList.get(0).setRequestTime(requestTime);
    }

    public void setResponseTimeAndHandleTime(Long responseTime) {
        this.logList.get(0).setResponseTime(responseTime);
        this.logList.get(0).setHandleTime(responseTime - this.logList.get(0).getRequestTime());
    }
}