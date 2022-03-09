package com.capture.api.interceptors;

import com.capture.exception.GraceException;
import com.capture.grace.result.ResponseStatusEnum;
import com.capture.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class BaseInterceptor {

    @Autowired
    public RedisOperator redis;

    public static final String REDIS_USER_TOKEN = "redis_user_token";
    public static final String REDIS_USER_INFO = "redis_user_info";
    public static final String REDIS_ADMIN_TOKEN = "redis_admin_token";

    public boolean verifyUserIdToken(String id,
                                     String token,
                                     String redisKeyPrefix) {

        if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(token)) {
            String redisToken = redis.get(redisKeyPrefix + ":" + id);
            if (StringUtils.isBlank(id)) {
                GraceException.display(ResponseStatusEnum.UN_LOGIN);
                return false;
            } else {
                if (!redisToken.equalsIgnoreCase(token)) {
                    GraceException.display(ResponseStatusEnum.TICKET_INVALID);
                    return false;
                }
            }
        } else {
            GraceException.display(ResponseStatusEnum.UN_LOGIN);
            return false;
        }

        return true;
    }

    // 从cookie中取值
    public String getCookie(HttpServletRequest request, String key) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for(Cookie cookie : cookies){
            if(cookie.getName().equals(key)){
                String value = cookie.getValue();
                return value;
            }
        }
        return null;
    }

}
