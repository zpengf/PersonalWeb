package com.capture.api.controller.user;

import com.capture.api.controller.user.fallbacks.UserControllerFactoryFallback;
import com.capture.grace.result.GraceJSONResult;
import com.capture.pojo.bo.RegistLoginBO;
import com.capture.pojo.bo.UpdateUserInfoBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Api(value = "用户信息相关Controller", tags = {"用户信息相关Controller"})
@RequestMapping("user")

//开启FeignClient是为了直接调用其他服务的接口 详见queryByIds实现方法
//加fallbackFactory如果客户端降级 就是如果访问的服务出了问题 这里的问题是所有service-user服务都死掉
//就会走UserControllerFactoryFallback相应的方法 避免出现雪崩
//@FeignClient(value = "service-user",fallbackFactory = UserControllerFactoryFallback.class)
public interface UserControllerApi {

    @ApiOperation(value = "获得用户基本信息", notes = "获得用户基本信息", httpMethod = "POST")
    @PostMapping("/getUserInfo")
    public GraceJSONResult getUserInfo(@RequestParam String userId);

    @ApiOperation(value = "获得用户账户信息", notes = "获得用户账户信息", httpMethod = "POST")
    @PostMapping("/getAccountInfo")
    public GraceJSONResult getAccountInfo(@RequestParam String userId);

    @ApiOperation(value = "修改/完善用户信息", notes = "修改/完善用户信息", httpMethod = "POST")
    @PostMapping("/updateUserInfo")
    public GraceJSONResult updateUserInfo(
            @RequestBody @Valid UpdateUserInfoBO updateUserInfoBO);
    //因为使用feign导致BindingResult result 有问题这里取消 写一个全局统一的异常处理 在GraceExceptionHandler
    //    public GraceJSONResult updateUserInfo(
    //                    @RequestBody @Valid UpdateUserInfoBO updateUserInfoBO,
    //                    BindingResult result);

    @ApiOperation(value = "根据用户的ids查询用户列表", notes = "根据用户的ids查询用户列表", httpMethod = "GET")
    @GetMapping("/queryByIds")
    public GraceJSONResult queryByIds(@RequestParam String userIds);

}
