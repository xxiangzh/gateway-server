package com.xzh.gateway.common.mq;

import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @description RocketMQ发送者
 * @author 向振华
 * @date 2020/06/10 17:03
 */
@Component
public class RocketMQProducer {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Value("${rocketmq.producer.topic.log}")
    private String topic;

    @Value("${rocketmq.producer.tag.log}")
    private String tag;

    /**
     * 发送日志消息
     *
     * @param gatewayLog
     */
    public void send(GatewayLog gatewayLog) {
        if (gatewayLog == null){
            return;
        }
        String msg = JSONObject.toJSONString(gatewayLog);
        rocketMQTemplate.syncSend(topic + ":" + tag, msg);
    }
}