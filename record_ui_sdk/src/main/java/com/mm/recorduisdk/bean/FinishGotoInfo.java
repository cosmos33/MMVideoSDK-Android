package com.mm.recorduisdk.bean;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created on 2019/7/17.
 *
 * @author jianxi[mabeijianxi@gmail.com]
 */
public class FinishGotoInfo implements Parcelable {


    /**
     * 关闭当前Activity
     */
    private boolean finishCurrentActivity = true;
    /**
     * 需要跳转的Activity类名
     */
    private String gotoActivityName;

    /**
     * 可作为返回参数或跳转编辑图片参数
     * 不做修改，原样传入或返回
     * 若设置了gotoActivityName，则参数原样设置进入Intent中
     */
    private Bundle extraBundle;

    /**
     * 是 FinishResult模式 还是直接跳转其他界面的模式
     */
    private boolean isNeedFinishResultMode = true;

    public FinishGotoInfo(String gotoActivityName, Bundle extraBundle) {
        this(true, gotoActivityName, extraBundle);
    }

    private FinishGotoInfo(boolean finishCurrentActivity, String gotoActivityName, Bundle extraBundle) {
        this.finishCurrentActivity = finishCurrentActivity;
        this.gotoActivityName = gotoActivityName;
        this.extraBundle = extraBundle;
        this.isNeedFinishResultMode = false;

    }


    public FinishGotoInfo() {
        this.isNeedFinishResultMode = true;
    }

    public FinishGotoInfo(Bundle extraBundle) {
        this.extraBundle = extraBundle;
        this.isNeedFinishResultMode = true;
    }

    protected FinishGotoInfo(Parcel in) {
        finishCurrentActivity = in.readByte() != 0;
        gotoActivityName = in.readString();
        extraBundle = in.readBundle();
        isNeedFinishResultMode = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (finishCurrentActivity ? 1 : 0));
        dest.writeString(gotoActivityName);
        dest.writeBundle(extraBundle);
        dest.writeByte((byte) (isNeedFinishResultMode ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FinishGotoInfo> CREATOR = new Creator<FinishGotoInfo>() {
        @Override
        public FinishGotoInfo createFromParcel(Parcel in) {
            return new FinishGotoInfo(in);
        }

        @Override
        public FinishGotoInfo[] newArray(int size) {
            return new FinishGotoInfo[size];
        }
    };

    public boolean isFinishCurrentActivity() {
        return finishCurrentActivity;
    }

    public String getGotoActivityName() {
        return gotoActivityName;
    }


    public Bundle getExtraBundle() {
        return extraBundle;
    }

    public boolean isNeedFinishResultMode() {
        return isNeedFinishResultMode;
    }

}
