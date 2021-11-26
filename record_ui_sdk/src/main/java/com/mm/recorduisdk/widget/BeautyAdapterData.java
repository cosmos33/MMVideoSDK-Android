package com.mm.recorduisdk.widget;

public class BeautyAdapterData {
    public String beautyType;
    public String beautyInnerType;
    public String content;
    public String picUrl;
    public String path;
    public float intensity;

    public BeautyAdapterData(String beautyType, String beautyInnerType, String content, String picUrl, String path) {
        this.beautyType = beautyType;
        this.beautyInnerType = beautyInnerType;
        this.content = content;
        this.picUrl = picUrl;
        this.path = path;
    }

    public BeautyAdapterData(String beautyType, String beautyInnerType, String content) {
        this.beautyType = beautyType;
        this.beautyInnerType = beautyInnerType;
        this.content = content;
    }

    public BeautyAdapterData(String content) {
        this.content = content;
    }
}
