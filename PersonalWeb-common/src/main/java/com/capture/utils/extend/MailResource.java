package com.capture.utils.extend;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @ClassName: MailResource
 * @Author: pengfeizhang
 * @Description: 邮箱相关配置
 * @Date: 2022/3/9 上午10:32
 * @Version: 1.0
 */

@Component
@PropertySource("classpath:mail.properties")
@ConfigurationProperties(prefix = "mail")
public class MailResource {

    private String myMail;
    private String authCode;

    public String getMyMail() {
        return myMail;
    }

    public void setMyMail(String myMail) {
        this.myMail = myMail;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }
}
