package com.mm.sdkdemo.bean;

/**
 * Created by chenwangwang on 2018/4/2   <br/>
 * 用来存放希望默认显示的变脸信息
 */
public class TolerantMoment {

    // 分类ID
    private String classId;

    // 变脸ID
    private String faceId;

    public TolerantMoment(String classId, String faceId) {
        this.classId = classId;
        this.faceId = faceId;
    }

    public String getClassId() {
        return classId;
    }

    public String getFaceId() {
        return faceId;
    }
}
