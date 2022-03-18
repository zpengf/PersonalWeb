package com.capture.article.controller;

import com.capture.api.config.RabbitMQConfig;
import com.capture.api.config.RabbitMQDelayConfig;
import com.capture.grace.result.GraceJSONResult;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("producer")
public class HelloController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/hello")
    public Object hello() {

        /**
         * RabbitMQ 的路由规则 routing key
         *  display.*.* -> * 代表一个占位符
         *      例：
         *          display.do.download  匹配
         *          display.do.upload.done   不匹配
         *
         * display.# -> # 代表任意多个占位符
         *      例:
         *          display.do.download  匹配
         *          display.do.upload.done.over   匹配
         *
         *
         */

//        rabbitTemplate.convertAndSend(
//                RabbitMQConfig.EXCHANGE_ARTICLE,
//                "article.hello",
//                "这是从生产者发送的消息~");

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_ARTICLE,
                "article.publish.download.do",
                "1001");



        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_ARTICLE,
                "article.success.do",
                "1002");

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_ARTICLE,
                "article.play",
                "1003");

        return GraceJSONResult.ok();
    }

    @GetMapping("/delay")
    public Object delay() {

        MessagePostProcessor messagePostProcessor = new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                // 设置消息的持久
                message.getMessageProperties()
                        .setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                // 设置消息延迟的时间，单位ms毫秒
                message.getMessageProperties()
                        .setDelay(5000);
                return message;
            }
        };

        rabbitTemplate.convertAndSend(
                RabbitMQDelayConfig.EXCHANGE_DELAY,
                "delay.demo",
                "这是一条延迟消息~~",
                messagePostProcessor);

        System.out.println("生产者发送的延迟消息：" + new Date());

        return "OK";
    }

}
