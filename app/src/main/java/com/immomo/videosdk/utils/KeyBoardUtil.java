package com.immomo.videosdk.utils;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by zhoukai on 13-12-23.
 */
public class KeyBoardUtil {

    /**
     * 隐藏软键盘，类型InputMethodManager.HIDE_NOT_ALWAYS
     *
     * @param activity
     */
    public static void hideSoftKeyboardNotAlways(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            IBinder windowToken = view.getWindowToken();
            hideSoftKeyboardNotAlways(activity, windowToken);
        }
    }

    /**
     * 隐藏软键盘，类型InputMethodManager.HIDE_NOT_ALWAYS
     *
     * @param activity
     */
    public static void hideSoftKeyboardNotAlways(Activity activity, IBinder windowToken) {
        hideSoftKeyboardNotAlways(activity, windowToken, null);
    }

    /**
     * 隐藏软键盘，类型InputMethodManager.HIDE_NOT_ALWAYS
     *
     * @param activity
     * @param resultReceiver 非常有用的参数，键盘隐藏完成回调，可知道隐藏完成时机。
     */
    public static void hideSoftKeyboardNotAlways(Activity activity, IBinder windowToken, ResultReceiver resultReceiver) {
//        ((InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
//                windowToken, InputMethodManager.HIDE_NOT_ALWAYS, resultReceiver);
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(windowToken, 0); //强制隐藏键盘
    }

    /**
     * 弹出软键盘，类型InputMethodManager.SHOW_FORCE
     * @param activity
     */
    public static void showSoftKeyboardForce(Activity activity, View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(view, InputMethodManager.SHOW_FORCED, null);
    }
}
