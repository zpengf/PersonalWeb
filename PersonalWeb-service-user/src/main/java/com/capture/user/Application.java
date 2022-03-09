package com.capture.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
@EnableSwagger2
@MapperScan(basePackages = "com.capture.user.mapper")
@ComponentScan(basePackages = {"com.capture","org.n3r.idworker"})
@SpringBootApplication
public class Application {


    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
