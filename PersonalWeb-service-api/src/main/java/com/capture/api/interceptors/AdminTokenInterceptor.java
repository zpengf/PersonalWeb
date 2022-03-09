package com.capture.api.interceptors;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AdminTokenInterceptor extends BaseInterceptor implements HandlerInterceptor {

    /**
     * 拦截请求，在访问controller调用之前
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String adminUserId = request.getHeader("adminUserId");
        String adminUserToken = request.getHeader("adminUserToken");

        System.out.println("=====================================================================");
        System.out.println("AdminTokenInterceptor - adminUserId = " + adminUserId);
        System.out.println("AdminTokenInterceptor - adminUserToken = " + adminUserToken);
        System.out.println("=====================================================================");

        boolean run = verifyUserIdToken(adminUserId, adminUserToken, REDIS_ADMIN_TOKEN);
        return run;
    }

    /**
     * 请求访问controller之后，渲染视图之前
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 请求访问controller之后，渲染视图之后
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
