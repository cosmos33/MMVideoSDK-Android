package com.mm.recorduisdk.recorder.editor;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by XiongFangyu on 2017/8/9.
 */

public class EditRecorder {
    private static final String KEY_CHANGE_VIDEO = "KEY_CHANGE_VIDEO";
    /**
     * ---------------
     * *            贴纸（文字贴纸）
     *   *          配乐
     *     *        涂鸦
     *       *      变速
     * ---------------
     */
    private int changedStates = 0x0000;

    public void setChangeSticker(boolean has) {
        if (has) {
            changedStates |= 0x1000;
        } else {
            changedStates &= 0x0111;
        }
    }

    public boolean isChangeFilter() {
        return isChangeSticker() || isChangePaint();
    }

    public boolean isChangeSticker() {
        return (changedStates & 0x1000) == 0x1000;
    }

    public void setChangeMusic(boolean has) {
        if (has) {
            changedStates |= 0x0100;
        } else {
            changedStates &= 0x1011;
        }
    }

    public void setChangePaint(boolean has) {
        if (has) {
            changedStates |= 0x0010;
        } else {
            changedStates &= 0x1101;
        }
    }

    public boolean isChangePaint() {
        return (changedStates & 0x0010) == 0x0010;
    }

    public void setChangeSpeed(boolean has) {
        if (has) {
            changedStates |= 0x0001;
        } else {
            changedStates &= 0x1110;
        }
    }

    public boolean isChangeVideo() {
        return changedStates != 0;
    }

    //仅视频发生改变
    public boolean isOnlyChangeVideo() {
        int videoChangeStates = changedStates & 0x1011;
        return videoChangeStates != 0;
    }

    public void onSaveInstanceState(Bundle outState) {
        if (outState != null)
            outState.putInt(KEY_CHANGE_VIDEO, changedStates);
    }

    public void onRestoredState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null)
            changedStates = savedInstanceState.getInt(KEY_CHANGE_VIDEO, 0);
    }
}
