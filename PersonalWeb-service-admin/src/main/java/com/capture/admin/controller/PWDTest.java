package com.capture.admin.controller;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class PWDTest {

    public static void main(String[] args) {
        String pwd = BCrypt.hashpw("admin", BCrypt.gensalt());
        System.out.println(pwd);
    }

}
