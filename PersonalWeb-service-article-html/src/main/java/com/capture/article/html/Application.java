package com.capture.article.html;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@MapperScan(basePackages = "com.capture.article.mapper")
@ComponentScan(basePackages = {"com.capture", "org.n3r.idworker"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
