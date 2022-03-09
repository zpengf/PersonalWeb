package com.capture.api.interceptors;

import com.capture.utils.IPUtil;
import com.capture.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ArticleReadInterceptor extends BaseInterceptor implements HandlerInterceptor {

    @Autowired
    public RedisOperator redis;
    public static final String REDIS_ALREADY_READ = "redis_already_read";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String articleId = request.getParameter("articleId");

        String userIp = IPUtil.getRequestIp(request);
        boolean isExist = redis.keyIsExist(REDIS_ALREADY_READ + ":" +  articleId + ":" + userIp);

        if (isExist) {
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
