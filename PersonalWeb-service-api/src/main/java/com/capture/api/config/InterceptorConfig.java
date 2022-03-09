package com.capture.api.config;

import com.capture.api.interceptors.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Bean
    public PassportInterceptor passportInterceptor() {
        return new PassportInterceptor();
    }

    @Bean
    public UserTokenInterceptor userTokenInterceptor() {
        return new UserTokenInterceptor();
    }

    @Bean
    public ArticleReadInterceptor articleReadInterceptor() {
        return new ArticleReadInterceptor();
    }

    @Bean
    public UserActiveInterceptor userActiveInterceptor() {
        return new UserActiveInterceptor();
    }

    @Bean
    public AdminTokenInterceptor adminTokenInterceptor() {
        return new AdminTokenInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(passportInterceptor())
                .addPathPatterns("/passport/getEmailCode");

        registry.addInterceptor(userTokenInterceptor())
                .addPathPatterns("/user/getAccountInfo")
                .addPathPatterns("/user/updateUserInfo")
                .addPathPatterns("/fs/uploadFace")
                .addPathPatterns("/fs/uploadSomeFiles")
                .addPathPatterns("/fans/follow")
                .addPathPatterns("/fans/unfollow");

        registry.addInterceptor(userActiveInterceptor())
                .addPathPatterns("/fs/uploadSomeFiles")
                .addPathPatterns("/fans/follow")
                .addPathPatterns("/fans/unfollow");

        registry.addInterceptor(adminTokenInterceptor())
                .addPathPatterns("/adminMng/adminIsExist")
                .addPathPatterns("/adminMng/addNewAdmin")
                .addPathPatterns("/adminMng/getAdminList")
                .addPathPatterns("/fs/uploadToGridFS")
                .addPathPatterns("/fs/readInGridFS")
                .addPathPatterns("/friendLinkMng/saveOrUpdateFriendLink")
                .addPathPatterns("/friendLinkMng/getFriendLinkList")
                .addPathPatterns("/friendLinkMng/delete")
                .addPathPatterns("/categoryMng/saveOrUpdateCategory")
                .addPathPatterns("/categoryMng/getCatList");

        registry.addInterceptor(articleReadInterceptor())
                .addPathPatterns("/portal/article/readArticle");

    }
}
