package com.capture.api.controller.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: HelloController
 * @Author: pengfeizhang
 * @Description: 测试
 * @Date: 2022/3/7 下午7:41
 * @Version: 1.0
 */

@Api(value = "HelloControllerApi标题",tags = {"hello功能的controller"})
public interface HelloControllerApi {


    @ApiOperation(value = "hello方法的接口",notes = "hello方法的接口",httpMethod = "GET")
    @GetMapping("/hello")
    public Object hello();

}
