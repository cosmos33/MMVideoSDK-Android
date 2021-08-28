package com.mm.recorduisdk.recorder.editor.image_composition_video.bean;

import androidx.annotation.DrawableRes;


public class LiveAnimate {

    private String name;

    @DrawableRes
    private int img;

    private int animateType;

    private boolean isSelect = false;

    private String path;


    public LiveAnimate(String name, @DrawableRes int img, int animateType) {
        this.name = name;
        this.img = img;
        this.animateType = animateType;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public int getAnimateType() {
        return animateType;
    }

    public void setAnimateType(int animateType) {
        this.animateType = animateType;
    }


    @Override
    public String toString() {
        return "LiveAnimate{" +
                "name='" + name + '\'' +
                ", img=" + img +
                ", animateType=" + animateType +
                ", path=" + path +
                ", isSelect=" + isSelect +
                '}';
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public interface AnimateType {

        int ANIMATE_HORIZONTAL = 1;
        // soft
        int ANIMATE_SOFT = 2;
        // quit
        int ANIMATE_QUIC = 3;
        int ANIMATE_SHOW = 4;

    }
}
