package com.mm.sdkdemo.utils;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.mm.mmutil.app.AppContext;

/**
 * 适配挖孔屏幕，全面屏工具类
 */
public class ScreenUtil {

    private final static Object obj = new Object();

    private static int mIsSupportFullScreen = -1;

    /**
     * @return 如果是挖孔屏幕，不支持全屏return false
     */
    public static boolean isSupportFullScreen() {
        if (mIsSupportFullScreen < 0) {
            synchronized (obj) {
                mIsSupportFullScreen = NotchScreenUtil.hasNotchInScreen() ? 0 : 1;
            }
        }
        return mIsSupportFullScreen == 1;
    }

    /**
     * 设置全屏
     * 如果未异形屏，可能不允许全屏.
     *
     * @param context
     * @return 如果支持全屏，返回true，否则false
     */
    public static boolean setFullscreenIfNeed(Activity context) {
        if (ScreenUtil.isSupportFullScreen()) {
            setFullscreen(context);
            return true;
        } else {
            cleanFullscreen(context);
            return false;
        }
    }

    /**
     * 默认的全屏是指隐藏状态栏，但是导航栏依然显示的全屏样式。如果再某些播放页面需要
     *
     * @param context
     */
    public static void setFullscreen(Activity context) {
        int uiFlags =
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE //if without contentView, will occur crash. 'ActionBarContainer.setAlpha(float)' on a null object reference
                        //                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        //                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        |View.SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar
        if (android.os.Build.VERSION.SDK_INT >= 19) {
            uiFlags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY; // hide navigation bars - compatibility: building API level is lower thatn 19, use magic number directly for higher API target level
        } else {
            uiFlags |= View.SYSTEM_UI_FLAG_LOW_PROFILE; //hide notification content ,bg always show.
        }
        context.getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        context.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//backup
    }

    private static void cleanFullscreen(Activity context) {
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            int visibility = context.getWindow().getDecorView().getSystemUiVisibility();
            visibility = visibility&(~View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            context.getWindow().getDecorView().setSystemUiVisibility(visibility);
        }
    }

    /**
     * 是否是全面屏
     *
     * @return
     */
    public static boolean isHighAspectRatio() {
        return getAspectRatio() > 2.0;
    }

    public static float getAspectRatio() {
        DisplayMetrics dm = AppContext.getContext().getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        if (screenHeight <= 0) {
            return 1;
        }
        return screenHeight / (float) screenWidth;
    }

}
