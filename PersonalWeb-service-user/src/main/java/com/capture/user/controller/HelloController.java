package com.capture.user.controller;

import com.capture.api.controller.user.HelloControllerApi;
import com.capture.grace.result.GraceJSONResult;
import com.capture.grace.result.IMOOCJSONResult;
import com.capture.utils.RedisOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: HelloController
 * @Author: pengfeizhang
 * @Description: 测试
 * @Date: 2022/3/7 下午7:41
 * @Version: 1.0
 */
@RestController //这里需要注明 因为需要被扫描到
public class HelloController implements HelloControllerApi {

    final static Logger logger = LoggerFactory.getLogger(HelloController.class);

    @Autowired
    private RedisOperator redisOperator;

    public Object hello(){
        logger.debug("debug");
        logger.info("info");
        logger.warn("warn");
        logger.error("error");
        return IMOOCJSONResult.ok();
    }

    @GetMapping("/redis")
    public Object getRedis(){
        redisOperator.set("age","18");
        return GraceJSONResult.ok(redisOperator.get("age"));
    }

}
