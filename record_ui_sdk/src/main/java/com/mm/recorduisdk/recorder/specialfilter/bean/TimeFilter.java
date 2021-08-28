package com.mm.recorduisdk.recorder.specialfilter.bean;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class TimeFilter {

    //清空
    public static final int TYPE_NO = 1;
    public static final int TYPE_FAST = 2;
    public static final int TYPE_SLOW = 3;
    // 倒放
    public static final int TYPE__BACK = 4;
    // 反复
    public static final int TYPE_REPEAT = 5;


    @IntDef({TYPE_NO, TYPE_FAST, TYPE_SLOW, TYPE__BACK,TYPE_REPEAT})
    @Retention(RetentionPolicy.SOURCE)
    @interface TimeType{

    }

    @DrawableRes
    private int normalRes;

    private String name;

    @TimeType
    private int type;

    private long start;
    private long end;

    private boolean isSelect;

    private String tag;

    public TimeFilter(int normalRes, String name,@TimeType int type,String tag) {
        this.normalRes = normalRes;
        this.name = name;
        this.type = type;
        this.tag = tag;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public int getNormalRes() {
        return normalRes;
    }

    public void setNormalRes(int normalRes) {
        this.normalRes = normalRes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    @TimeType
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isBackType(){
        return type == TYPE__BACK;
    }

    public boolean isFastType(){
        return type == TYPE_FAST;
    }


    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "TimeFilter{" +
                "normalRes=" + normalRes +
                ", name='" + name + '\'' +
                '}';
    }
}
