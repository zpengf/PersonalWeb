package com.capture.article.stream;


import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.stereotype.Component;

/**
 * 声明构建通道channel
 *
 * 做个demo测试 底层用的是rabbitmq 项目实际没有用到stream
 *
 */
@Component
public interface MyStreamChannel {

    String OUTPUT = "myOutput";
    String INPUT = "myInput";

    @Output(MyStreamChannel.OUTPUT)
    MessageChannel output();

    @Input(MyStreamChannel.INPUT)
    SubscribableChannel input();
}
