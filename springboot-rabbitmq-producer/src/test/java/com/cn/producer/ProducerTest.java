package com.cn.producer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProducerTest {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 过期队列消息
     * 投递到该队列的消息如果没有消费都将在6秒之后被删除
     */
    @Test
    public void ttlQueueTest() {
        //路由键与队列同名，所以这里只用两个参数
        rabbitTemplate.convertAndSend("my_ttl_queue", "发送到过期队列my_ttl_queue，6秒内不消费就不能再消费");
    }

    /**
     * 测试发送6秒后就过期的消息
     */
    @Test
    public void ttlMessageTest() {
        MessageProperties messageProperties = new MessageProperties();
        //设置过期时间为10秒
        messageProperties.setExpiration("10000");
        Message message = new Message("测试过期消息，10秒后过期".getBytes(), messageProperties);
        //路由键与队列同名，所以这里只用两个参数
        rabbitTemplate.convertAndSend("my_ttl_queue", message);
    }

    @Test
    public void dlxTTLMessageTest() {
        rabbitTemplate.convertAndSend("my_normal_exchange", "my_ttl_dlx",
                "测试过期消息：10秒后过期就会被投递到死信交换机中");
    }

    @Test
    public void dlxMaxMessageTest() {
        rabbitTemplate.convertAndSend("my_normal_exchange", "my_max_dlx",
                "测试消息3：投递到该队列的消息最多2个消息，如果超过则最早的消息被删除投递到死信交换机");
    }

    @Test
    public void queueTest() {
        //路由键与队列同名，所以这里只用两个参数
        rabbitTemplate.convertAndSend("spring-queue", "只发队列spring-queue的消息");
    }

    @Test
    public void testFailQueueTest() {
        /**
         * 如果exchange不存在即错误，confirm被回调，ack=false，return不会被回调
         * 如果exchange正确，queue错误，confirm被回调，ack=true，return被回调
         * 如果exchange正确，queue正确，消息发送成功，ack=true，return不会被回调，只有消息成功发送到交换机，且没有成功发送到队列才调用return回调
         */
//        rabbitTemplate.convertAndSend("test_fail_exchange","test_fail", "测试消息发送失败进行确认应答");
        //这种使用默认的交换机即default-exchange
//        rabbitTemplate.convertAndSend("test_fail", "测试消息发送失败进行确认应答");
        rabbitTemplate.convertAndSend("spring-queue", "发送成功消息");
    }

    @Test
    @Transactional
    //由于springboot的test类中，如果带有事务注解，那么最后会默认回滚，所以这里把默认回滚改成不回滚。
    //如果测试报错，需要回滚，就不能改默认值了
//    @Rollback(value = false)
    public void queueTest2() {
        rabbitTemplate.convertAndSend("spring-queue", "只发队列spring-queue的消息--05");
        System.out.println("-----doSomething;可以是数据库操作，也可以是其他业务操作-------");
        System.out.println(1/0);
        rabbitTemplate.convertAndSend("spring-queue", "只发队列spring-queue的消息--06");
    }
}
