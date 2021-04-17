package com.cn.consumer.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MyListener {
    @RabbitListener(queues = "item.queue")
    public void msg(String msg) {
        System.out.println("消费者下消费消息：" + msg);
    }
}
