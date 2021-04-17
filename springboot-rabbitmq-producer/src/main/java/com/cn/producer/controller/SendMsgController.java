package com.cn.producer.controller;

import com.cn.producer.config.RabbitmqConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SendMsgController {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping(value = "/sendMsg")
    public String sendMsg(@RequestParam String msg, @RequestParam String key) {
        /**
         * 参数1：交换机名称
         * 参数2：路由key
         * 参数3：发送的消息
         */
        rabbitTemplate.convertAndSend(RabbitmqConfig.ITEM_TOPIC_EXCHANGE, key, msg);
        return "消息发送成功";
    }
}
