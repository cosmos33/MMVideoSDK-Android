package com.immomo.videosdk.recorder.activity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.immomo.videosdk.base.BaseActivity;
import com.immomo.videosdk.utils.ScreenUtil;

public class BaseFullScreenActivity extends BaseActivity {
    public void setFullscreen() {
        if (ScreenUtil.isSupportFullScreen()) {

            int uiFlags = 0; // hide status bar
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        //                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        //                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        |View.SYSTEM_UI_FLAG_FULLSCREEN;
            }
            if (android.os.Build.VERSION.SDK_INT >= 19) {
                uiFlags |= 0x00001000;    //SYSTEM_UI_FLAG_IMMERSIVE_STICKY: hide navigation bars - compatibility: building API level is lower thatn 19, use magic number directly for higher API target level
            } else {
                uiFlags |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
            }
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
