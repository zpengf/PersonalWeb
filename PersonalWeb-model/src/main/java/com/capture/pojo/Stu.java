package com.capture.pojo;

import java.util.Date;
import java.util.List;
import java.util.Map;
/**
 * 演示 freemarker 用
 */
public class Stu {

    private String uid;
    private String username;
    private Integer age;
    private Date birthday;
    private Float amount;
    private boolean haveChild;

    private Spouse spouse;    // 配偶

    private List<Article> articleList;
    private Map<String, String> parents;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Spouse getSpouse() {
        return spouse;
    }

    public void setSpouse(Spouse spouse) {
        this.spouse = spouse;
    }

    public boolean isHaveChild() {
        return haveChild;
    }

    public void setHaveChild(boolean haveChild) {
        this.haveChild = haveChild;
    }

    public List<Article> getArticleList() {
        return articleList;
    }

    public void setArticleList(List<Article> articleList) {
        this.articleList = articleList;
    }

    public Map<String, String> getParents() {
        return parents;
    }

    public void setParents(Map<String, String> parents) {
        this.parents = parents;
    }
}
