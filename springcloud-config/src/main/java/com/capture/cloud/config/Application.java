package com.capture.cloud.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,
                                    MongoAutoConfiguration.class})
@ComponentScan(basePackages = {"com.capture", "org.n3r.idworker"})
@EnableEurekaClient
@EnableConfigServer //感觉有点鸡肋 每次刷新其他配置需要调端口刷新
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
