package com.capture.user.controller;

//import com.capture.api.BaseController;
import com.capture.api.BaseController;
import com.capture.api.controller.user.PassportControllerApi;
import com.capture.enums.UserStatus;
import com.capture.grace.result.GraceJSONResult;
import com.capture.grace.result.ResponseStatusEnum;
import com.capture.pojo.AppUser;
import com.capture.pojo.bo.RegistLoginBO;
//import com.capture.user.service.UserService;
import com.capture.user.service.UserService;
import com.capture.utils.IPUtil;
import com.capture.utils.JsonUtils;
import com.capture.utils.MailUtil;
import com.capture.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class  PassportController extends BaseController implements PassportControllerApi {

    final static Logger logger = LoggerFactory.getLogger(PassportController.class);

    @Autowired
    private MailUtil mailUtil;

    @Autowired
    private UserService userService;

    @Override
    public GraceJSONResult  getEmailCode(@RequestParam String mail, HttpServletRequest request) {

        // 获得用户ip
        String userIp = IPUtil.getRequestIp(request);

        // 根据用户的ip进行限制，限制用户在60秒内只能获得一次验证码
        redis.setnx60s(EMAIL_CODE  + ":" + userIp, userIp);

        String code =  mailUtil.generateRandomCode(6);

        try {
            mailUtil.sendMail(mail,code);
        } catch (Exception e) {
             return GraceJSONResult.errorMsg(e.getMessage());
        }

        // 把验证码存入redis，用于后续进行验证
        redis.set(EMAIL_CODE + ":" + mail, code, 30 * 60);

        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult doLogin(@Valid RegistLoginBO registLoginBO,
                                   BindingResult result,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {

        // 0.判断BindingResult中是否保存了错误的验证信息，如果有，则需要返回
        if (result.hasErrors()) {
            Map<String, String> map = getErrors(result);
            return GraceJSONResult.errorMap(map);
        }

        String email = registLoginBO.getEmail();
        String emailCode = registLoginBO.getEmailCode();

        // 1. 校验验证码是否匹配
        String redisEmailCode = redis.get(EMAIL_CODE + ":" + email);
        if (StringUtils.isBlank(redisEmailCode) || !redisEmailCode.equalsIgnoreCase(emailCode)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.EMAIL_CODE_ERROR);
        }

        // 2. 查询数据库，判断该用户注册
        AppUser user = userService.queryEmailIsExist(email);
        if (user != null && user.getActiveStatus() == UserStatus.FROZEN.type) {
            // 如果用户不为空，并且状态为冻结，则直接抛出异常，禁止登录
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_FROZEN);
        } else if (user == null) {
            // 如果用户没有注册过，则为null，需要注册信息入库
            user = userService.createUser(email);
        }

        // 3. 保存用户分布式会话的相关操作  存redis
        int userActiveStatus = user.getActiveStatus();
        if (userActiveStatus != UserStatus.FROZEN.type) {
            // 保存token到redis
            String uToken = UUID.randomUUID().toString();
            redis.set(REDIS_USER_TOKEN + ":" + user.getId(), uToken);
            redis.set(REDIS_USER_INFO + ":" + user.getId(), JsonUtils.objectToJson(user));

            // 保存用户id和token到cookie中
            setCookie(request, response, "utoken", uToken, COOKIE_MONTH);
            setCookie(request, response, "uid", user.getId(), COOKIE_MONTH);
        }

        // 4. 用户登录或注册成功以后，需要删除redis中的短信验证码，验证码只能使用一次，用过后则作废
        redis.del(EMAIL_CODE + ":" + email);

        // 5. 返回用户状态
        return GraceJSONResult.ok(userActiveStatus);

    }

    @Override
    public GraceJSONResult logout(String userId,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {

        redis.del(REDIS_USER_TOKEN + ":" + userId);

        setCookie(request, response, "utoken", "", COOKIE_DELETE);
        setCookie(request, response, "uid", "", COOKIE_DELETE);

        return GraceJSONResult.ok();
    }
}
