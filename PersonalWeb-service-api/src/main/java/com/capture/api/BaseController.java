package com.capture.api;

import com.capture.grace.result.GraceJSONResult;
import com.capture.pojo.vo.AppUserVO;
import com.capture.utils.JsonUtils;
import com.capture.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;


public class BaseController {

    @Autowired
    public RedisOperator redis;

    @Autowired
    public RestTemplate restTemplate;

    // 注入服务发现，可以获得已经注册的服务相关信息
    @Autowired
    private DiscoveryClient discoveryClient;

    public static final String EMAIL_CODE = "emailCode";
    public static final String REDIS_USER_TOKEN = "redis_user_token";
    public static final String REDIS_USER_INFO = "redis_user_info";
    public static final String REDIS_ADMIN_TOKEN = "redis_admin_token";

    public static final String REDIS_ALL_CATEGORY = "redis_all_category";

    public static final String REDIS_WRITER_FANS_COUNTS = "redis_writer_fans_counts";
    public static final String REDIS_MY_FOLLOW_COUNTS = "redis_my_follow_counts";

    public static final String REDIS_ARTICLE_READ_COUNTS = "redis_article_read_counts";
    public static final String REDIS_ALREADY_READ = "redis_already_read";

    public static final String REDIS_ARTICLE_COMMENT_COUNTS = "redis_article_comment_counts";

    @Value("${website.domain-name}")
    public String DOMAIN_NAME;

    @Value("${transferInterface.article}")
    public String articleServiceInterface;

    @Value("${transferInterface.user}")
    public String userServiceInterface;

    @Value("${transferInterface.article_html}")
    public String articleHtmlInterface;

    @Value("${transferInterface.files}")
    public String filesServiceInterface;

    @Value("${transferInterface.admin}")
    public String adminServiceInterface;



    public static final Integer COOKIE_MONTH = 30 * 24 * 60 * 60;
    public static final Integer COOKIE_DELETE = 0;

    public static final Integer COMMON_START_PAGE = 1;
    public static final Integer COMMON_PAGE_SIZE = 10;

    /**
     *
     * @param request
     * @param response
     * @param cookieName
     * @param cookieValue
     * @param maxAge cookie 存活时间
     */
    public void setCookie(HttpServletRequest request,
                          HttpServletResponse response,
                          String cookieName,
                          String cookieValue,
                          Integer maxAge) {
        try {
            cookieValue = URLEncoder.encode(cookieValue, "utf-8");
            setCookieValue(request, response, cookieName, cookieValue, maxAge);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void setCookieValue(HttpServletRequest request,
                          HttpServletResponse response,
                          String cookieName,
                          String cookieValue,
                          Integer maxAge) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setMaxAge(maxAge);
//        cookie.setDomain("imoocnews.com");
        cookie.setDomain(DOMAIN_NAME);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public void deleteCookie(HttpServletRequest request,
                             HttpServletResponse response,
                             String cookieName) {
        try {
            String deleteValue = URLEncoder.encode("", "utf-8");
            setCookieValue(request, response, cookieName, deleteValue, COOKIE_DELETE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public Integer getCountsFromRedis(String key) {
        String countsStr = redis.get(key);
        if (StringUtils.isBlank(countsStr)) {
            countsStr = "0";
        }
        return Integer.valueOf(countsStr);
    }

    public List<AppUserVO> getBasicUserList(Set idSet) {

        /**
         * 不使用springCloud微服务
         */
//        String userServerUrlExecute
//                = userServiceInterface + "user/queryByIds?userIds=" + JsonUtils.objectToJson(idSet);

        /**
         * 使用微服务（1）
         */
//        ServiceInstance userServiceAddress =
//                discoveryClient.getInstances("SERVICE-USER").get(0);
//        String userServerUrlExecute
//                = "http://" + userServiceAddress.getHost() + ":" + userServiceAddress.getPort() +
//                "/user/queryByIds?userIds=" + JsonUtils.objectToJson(idSet);

        /**
         * 使用微服务（2）
         * 直接用SERVICE-USER 这么直接拼框架不能识别 需要给restTemplate做个负载均衡处理
         * 去restTemplate配置文件查看配置 配置文件在api模块 config包 CloudConfig文件
         */
        String serviceId = "SERVICE-USER";
        String userServerUrlExecute
                = "http://" + serviceId + "/user/queryByIds?userIds=" + JsonUtils.objectToJson(idSet);


        ResponseEntity<GraceJSONResult> responseEntity
                = restTemplate.getForEntity(userServerUrlExecute, GraceJSONResult.class);
        GraceJSONResult bodyResult = responseEntity.getBody();

        /**
         * 使用feign组件 直接通过接口调用 不太好用 改动很多
         */
//        GraceJSONResult bodyResult = userControllerApi.queryByIds(JsonUtils.objectToJson(idSet));


        List<AppUserVO> appUserVOList = null;
        if (bodyResult.getStatus() == 200) {
            String userJson = JsonUtils.objectToJson(bodyResult.getData());
            appUserVOList = JsonUtils.jsonToList(userJson, AppUserVO.class);
        } else {
            appUserVOList = new ArrayList<>();
        }
        return appUserVOList;

    }

}
