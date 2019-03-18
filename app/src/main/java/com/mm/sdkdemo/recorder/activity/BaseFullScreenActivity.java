package com.mm.sdkdemo.recorder.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.mm.sdkdemo.base.BaseActivity;
import com.mm.sdkdemo.utils.ScreenUtil;

/**
 * @author wangduanqing
 */
@SuppressLint("Registered")
public class BaseFullScreenActivity extends BaseActivity {
    public void setFullscreen() {
        if (ScreenUtil.isSupportFullScreen()) {
            int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    //                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    //                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    |View.SYSTEM_UI_FLAG_FULLSCREEN;
            uiFlags |= 0x00001000;    //SYSTEM_UI_FLAG_IMMERSIVE_STICKY: hide navigation bars - compatibility: building API level is lower thatn 19, use magic number directly for higher API target level
            getWindow().getDecorView().setSystemUiVisibility(uiFlags);

            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                      WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏s
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFullscreen();
        super.onCreate(savedInstanceState);
    }
}
