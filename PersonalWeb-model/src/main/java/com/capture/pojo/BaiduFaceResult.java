package com.capture.pojo;

import java.util.ArrayList;

/**
 * @ClassName: BaiduFaceResult
 * @Author: pengfeizhang
 * @Description: 百度云人脸对比返回来的数据
 * @Date: 2022/3/20 下午7:01
 * @Version: 1.0
 */
public class BaiduFaceResult {

    private Integer score;

    private ArrayList<Object> face_list;

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public ArrayList<Object> getFace_list() {
        return face_list;
    }

    public void setFace_list(ArrayList<Object> face_list) {
        this.face_list = face_list;
    }
}
