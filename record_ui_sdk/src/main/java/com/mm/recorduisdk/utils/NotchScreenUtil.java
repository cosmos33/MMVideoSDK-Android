package com.mm.recorduisdk.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.mm.base_business.utils.DeviceUtils;
import com.mm.mmutil.app.AppContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 挖孔屏幕适配.
 */
public class NotchScreenUtil {

    public static final int FLAG_NOTCH_SUPPORT_FOR_HUAWEI = 0x00010000;

    /**
     * 获取
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        return getStatusBarHeightForHuawei(context.getApplicationContext());
    }

    private static int getStatusBarHeightForHuawei(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 设置应用窗口在华为挖孔屏手机使用挖孔区
     *
     * @param window 应用页面 window 对象
     */
    public static void setFullScreenWindowLayout(Window window) {
        String manufacturer = Build.MANUFACTURER;
        if (manufacturer.equalsIgnoreCase("huawei")) {
            setFullScreenWindowLayoutInDisplayCutout(window);
        }
    }

    @TargetApi(19)
    private static void setFullScreenWindowLayoutInDisplayCutout(Window window) {
        if (window == null) {
            return;
        }
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        try {
            Class layoutParamsExCls = Class.forName("com.huawei.android.view.LayoutParamsEx");
            Constructor con = layoutParamsExCls.getConstructor(WindowManager.LayoutParams.class);
            Object layoutParamsExObj = con.newInstance(layoutParams);
            Method method = layoutParamsExCls.getMethod("addHwFlags", int.class);
            method.invoke(layoutParamsExObj, FLAG_NOTCH_SUPPORT_FOR_HUAWEI);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            Log.e("test", "hw notch screen flag api error");
        } catch (Exception e) {
            Log.e("test", "other Exception");
        }
    }

    /**
     * 时候存在挖孔屏幕
     *
     * @return
     */
    public static boolean hasNotchInScreen() {
        if (DeviceUtils.isVivo()) {
            return hasNotchInScreenForVivo();
        } else if (DeviceUtils.isHuaWei()) {
            return hasNotchInScreenForHuawei();
        } else if (DeviceUtils.isOppo()) {
            return hasNotchInScreenForOppo();
        } else if (DeviceUtils.isMIUI()) {
            return DeviceUtils.isNotchScreenForMiUi();
        } else {
            return false;
        }
    }

    private static boolean hasNotchInScreenForHuawei() {
        boolean ret = false;
        try {
            Class HwNotchSizeUtil = Class.forName("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (boolean) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            Log.e("screen", "hasNotchInScreenForHuawei ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e("screen", "hasNotchInScreenForHuawei NoSuchMethodException");
        } catch (Exception e) {
            Log.e("screen", "hasNotchInScreenForHuawei Exception");
        } finally {
            return ret;
        }
    }

    private static boolean hasNotchInScreenForOppo() {
        return AppContext.getContext().getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }

    private static int[] getNotchSizeForHuawei(@NonNull Context context) {
        int[] ret = new int[]{0, 0};
        try {
            ClassLoader cl = context.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("getNotchSize");
            ret = (int[]) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            Log.e("screen", "getNotchSizeForHuawei ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e("screen", "getNotchSizeForHuawei NoSuchMethodException");
        } catch (Exception e) {
            Log.e("screen", "getNotchSizeForHuawei Exception");
        } finally {
            return ret;
        }
    }

    private static boolean hasNotchInScreenForVivo() {
//        android.util.FtFeature
//        接口：public static boolean isFeatureSupport (int mask);
//        参数说明:
//        0x00000020表示是否有凹槽;
//        0x00000008表示是否有圆角
//        返回值:
//        ture表示具备此特征;
//        false表示没有此特性；
        try {
            Class ftFeature = Class.forName("android.util.FtFeature");
            if (ftFeature == null) {
                return false;
            }
            Method method = ftFeature.getMethod("isFeatureSupport", int.class);
            if (method == null) {
                return false;
            }
            Boolean result = (Boolean) method.invoke(ftFeature, 0x00000020);
            if (result == null) {
                return false;
            }
            return result;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取齐刘海的尺寸
     *
     * @param context
     * @return
     */
    public static int[] getNotchSize(@NonNull Context context) {
        return getNotchSizeForHuawei(context.getApplicationContext());
    }

}
