package com.capture.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @ClassName: Application
 * @Author: pengfeizhang
 * @Description: 启动类
 * @Date: 2022/3/7 下午7:48
 * @Version: 1.0
 */
@MapperScan(basePackages = "com.capture.admin.mapper")
@ComponentScan(basePackages = {"com.capture","org.n3r.idworker"})
@SpringBootApplication
//开启注册中心客户端
@EnableEurekaClient

//自定义负载均衡 调用的是service-user微服务 现已改成在配置文件配置 见application.yml
//@RibbonClient(name = "service-user",configuration = MyRule.class)

//开启Feign 这样可以直接调用其他服务 不需要使用restTemplate调接口
//@EnableFeignClients({"com.capture"})

//开启Hystrix熔断机制
@EnableHystrix
public class Application {


    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
