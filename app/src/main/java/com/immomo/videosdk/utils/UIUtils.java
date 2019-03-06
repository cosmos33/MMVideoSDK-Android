package com.immomo.videosdk.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.immomo.mmutil.app.AppContext;

import java.lang.reflect.Method;

public class UIUtils {

    /**
     * 获取屏幕分辨率宽度 *
     */
    public static int getScreenWidth() {
        return getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕分辨率高度 *
     */
    public static int getScreenHeight() {
        return getDisplayMetrics().heightPixels;
    }


    /**
     * 获取真正屏幕高度（若有虚拟按键，会加上虚拟按键）
     *
     * @return
     */
    public static int getRealScreenHeight() {
        int h = 0;
        WindowManager windowManager = (WindowManager) AppContext.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            Class c = Class.forName("android.view.Display");
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            h = dm.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (h <= 0)
            h = getScreenHeight();
        return h;
    }

    /**
     * 获取虚拟按键高度
     *
     * @return
     */
    public static int getVirtualBarHeight() {
        return getRealScreenHeight() - getScreenHeight();
    }

    private static DisplayMetrics getDisplayMetrics() {
        return AppContext.getContext().getResources().getDisplayMetrics();
    }

    public static void hideInputMethod(Activity activity) {
        InputMethodManager im = ((InputMethodManager) AppContext.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE));
        View curFocusView = activity.getCurrentFocus();
        if (curFocusView != null) {
            im.hideSoftInputFromWindow(curFocusView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static int getColor(int resource) {
        return AppContext.getContext().getResources().getColor(resource);
    }

    public static int sp2pix(float sp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getDisplayMetrics()));
    }


    public static int getPixels(float dip) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getDisplayMetrics()));
    }

    public static int getDimensionPixelSize(int id) {
        return AppContext.getContext().getResources().getDimensionPixelSize(id);
    }

    public static Drawable getDrawable(int res) {
        return AppContext.getContext().getResources().getDrawable(res);
    }

    public static void setTopDrawable(TextView textView, int res) {
        Drawable drawable = AppContext.getContext().getResources().getDrawable(res);
        /// 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        textView.setCompoundDrawables(null, drawable, null, null);
    }

    public static String formatTime(int ms) {
        StringBuilder sb = new StringBuilder();
        int second = ms / 1000;
        int minute = second / 60;
        second = second % 60;
        if (minute < 10) {
            sb.append("0");
        }
        sb.append(minute);
        sb.append(":");
        if (second < 10) {
            sb.append("0");
        }
        sb.append(second);
        return sb.toString();
    }
}
