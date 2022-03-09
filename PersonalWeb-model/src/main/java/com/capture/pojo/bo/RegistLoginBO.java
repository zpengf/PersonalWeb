package com.capture.pojo.bo;

import javax.validation.constraints.NotBlank;

public class RegistLoginBO {

    @NotBlank(message = "邮箱不能为空!")
    private String email;
    @NotBlank(message = "验证码不能为空!")
    private String emailCode;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailCode() {
        return emailCode;
    }

    public void setEmailCode(String emailCode) {
        this.emailCode = emailCode;
    }

    @Override
    public String toString() {
        return "RegistLoginBO{" +
                "email='" + email + '\'' +
                ", emailCode='" + emailCode + '\'' +
                '}';
    }
}
