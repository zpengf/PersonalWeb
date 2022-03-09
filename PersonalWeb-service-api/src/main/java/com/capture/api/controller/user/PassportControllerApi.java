package com.capture.api.controller.user;

import com.capture.grace.result.GraceJSONResult;
import com.capture.pojo.bo.RegistLoginBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Api(value = "用户注册登录", tags = {"用户注册登录的controller"})
@RequestMapping("passport")
public interface PassportControllerApi {

    @ApiOperation(value = "获得短信验证码", notes = "获得短信验证码", httpMethod = "GET")
    @GetMapping("/getEmailCode")
    public GraceJSONResult getEmailCode(@RequestParam String mail, HttpServletRequest request);

    @ApiOperation(value = "一键注册登录接口", notes = "一键注册登录接口", httpMethod = "POST")
    @PostMapping("/doLogin")
    public GraceJSONResult doLogin(@RequestBody @Valid RegistLoginBO registLoginBO,
                                   BindingResult result,
                                   HttpServletRequest request,
                                   HttpServletResponse response);

//    @ApiOperation(value = "用户退出登录", notes = "用户退出登录", httpMethod = "POST")
//    @PostMapping("/logout")
//    public GraceJSONResult logout(@RequestParam String userId,
//                                   HttpServletRequest request,
//                                   HttpServletResponse response);
}
