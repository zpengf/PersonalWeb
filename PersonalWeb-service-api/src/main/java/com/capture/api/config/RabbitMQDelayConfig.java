package com.capture.api.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 的配置类
 */
@Configuration
public class RabbitMQDelayConfig {

    // 定义交换机的名字
    public static final String EXCHANGE_DELAY = "exchange_delay";

    // 定义队列的名字
    public static final String QUEUE_DELAY = "queue_delay";

    // 创建交换机
    @Bean(EXCHANGE_DELAY)
    public Exchange exchange(){
        return ExchangeBuilder
                .topicExchange(EXCHANGE_DELAY)
                .delayed()          // 开启支持延迟消息
                .durable(true)
                .build();
    }

    // 创建队列
    @Bean(QUEUE_DELAY)
    public Queue queue(){
        return new Queue(QUEUE_DELAY);
    }

    // 队列绑定交换机
    @Bean
    public Binding delayBinding(
            @Qualifier(QUEUE_DELAY) Queue queue,
            @Qualifier(EXCHANGE_DELAY) Exchange exchange){
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("publish.delay.#")
                .noargs();      // 执行绑定
    }

}
