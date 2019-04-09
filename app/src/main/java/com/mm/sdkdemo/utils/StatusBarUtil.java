package com.mm.sdkdemo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.mm.sdkdemo.R;

/**
 * 状态栏工具，参考自 https://github.com/laobie/StatusBarUtil
 * Project momodev
 * Package com.mm.framework.utils
 * Created by tangyuchun on 5/19/16.
 */
public class StatusBarUtil {

    public static final int DEFAULT_STATUS_BAR_ALPHA = 112;

    public static final int MOMO_STAUS_BAR_ALPHA = 40;

    private static int statusHeight = -404;

    /**
     * 设置状态栏颜色
     *
     * @param activity 需要设置的 activity
     * @param color    状态栏颜色值
     */
    public static void setColor(Activity activity, @ColorInt int color) {
        setColor(activity, color, DEFAULT_STATUS_BAR_ALPHA);
    }

    /**
     * 设置状态栏颜色
     *
     * @param activity       需要设置的activity
     * @param color          状态栏颜色值
     * @param statusBarAlpha 状态栏透明度
     */
    public static void setColor(Activity activity, @ColorInt int color, int statusBarAlpha) {
        if (shouldSetStatusBar()) {
            try {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                activity.getWindow().setStatusBarColor(calculateStatusColor(color, statusBarAlpha));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        //5.0 以下禁止掉此功能
        //        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        //            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //            // 生成一个状态栏大小的矩形
        //            View statusView = createStatusBarView(activity, color, statusBarAlpha);
        //            // 添加 statusView 到布局中
        //            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        //            decorView.addView(statusView);
        //            setRootView(activity);
        //        }
    }

    //    public static void setTransparent(Activity activity){
    //
    //    }
    //
    //    public static void setTranslucent(){}

    public static boolean shouldSetStatusBar() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }


    /**
     * @param window 需要设置状态栏透明的当前window
     */
    public static void setStatusBarTransparent(@NonNull Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && window != null && window.getDecorView() != null) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 为DrawerLayout 布局设置状态栏变色
     *
     * @param activity     需要设置的activity
     * @param drawerLayout DrawerLayout
     * @param color        状态栏颜色值
     */
    public static void setColorForDrawerLayout(Activity activity, DrawerLayout drawerLayout, int color) {
        setColorForDrawerLayout(activity, drawerLayout, color, DEFAULT_STATUS_BAR_ALPHA);
    }

    /**
     * 为DrawerLayout 布局设置状态栏颜色,纯色
     *
     * @param activity     需要设置的activity
     * @param drawerLayout DrawerLayout
     * @param color        状态栏颜色值
     */
    public static void setColorNoTranslucentForDrawerLayout(Activity activity, DrawerLayout drawerLayout, int color) {
        setColorForDrawerLayout(activity, drawerLayout, color, 0);
    }

    /**
     * 为DrawerLayout 布局设置状态栏变色
     *
     * @param activity       需要设置的activity
     * @param drawerLayout   DrawerLayout
     * @param color          状态栏颜色值
     * @param statusBarAlpha 状态栏透明度
     */
    public static void setColorForDrawerLayout(Activity activity, DrawerLayout drawerLayout, int color, int statusBarAlpha) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        // 生成一个状态栏大小的矩形
        View statusBarView = createStatusBarView(activity, color);
        // 添加 statusBarView 到布局中
        ViewGroup contentLayout = (ViewGroup) drawerLayout.getChildAt(0);
        contentLayout.addView(statusBarView, 0);
        // 内容布局不是 LinearLayout 时,设置padding top
        if (!(contentLayout instanceof LinearLayout) && contentLayout.getChildAt(1) != null) {
            contentLayout.getChildAt(1).setPadding(0, getStatusBarHeight(activity), 0, 0);
        }
        // 设置属性
        ViewGroup drawer = (ViewGroup) drawerLayout.getChildAt(1);
        drawerLayout.setFitsSystemWindows(false);
        contentLayout.setFitsSystemWindows(false);
        contentLayout.setClipToPadding(true);
        drawer.setFitsSystemWindows(false);

        addTranslucentView(activity, statusBarAlpha, Color.TRANSPARENT);
    }

    //    /**
    //     * 为DrawerLayout 布局设置状态栏变色(5.0以下无半透明效果,不建议使用)
    //     *
    //     * @param activity     需要设置的activity
    //     * @param drawerLayout DrawerLayout
    //     * @param color        状态栏颜色值
    //     */
    //    public static void setColorForDrawerLayoutDiff(Activity activity, DrawerLayout drawerLayout, int color) {
    //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
    //            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    //            // 生成一个状态栏大小的矩形
    //            View statusBarView = createStatusBarView(activity, color);
    //            // 添加 statusBarView 到布局中
    //            ViewGroup contentLayout = (ViewGroup) drawerLayout.getChildAt(0);
    //            contentLayout.addView(statusBarView, 0);
    //            // 内容布局不是 LinearLayout 时,设置padding top
    //            if (!(contentLayout instanceof LinearLayout) && contentLayout.getChildAt(1) != null) {
    //                contentLayout.getChildAt(1).setPadding(0, getStatusBarHeight(activity), 0, 0);
    //            }
    //            // 设置属性
    //            ViewGroup drawer = (ViewGroup) drawerLayout.getChildAt(1);
    //            drawerLayout.setFitsSystemWindows(false);
    //            contentLayout.setFitsSystemWindows(false);
    //            contentLayout.setClipToPadding(true);
    //            drawer.setFitsSystemWindows(false);
    //        }
    //    }

    /**
     * 为 DrawerLayout 布局设置状态栏透明
     *
     * @param activity     需要设置的activity
     * @param drawerLayout DrawerLayout
     */
    public static void setTranslucentForDrawerLayout(Activity activity, DrawerLayout drawerLayout) {
        setTranslucentForDrawerLayout(activity, drawerLayout, DEFAULT_STATUS_BAR_ALPHA);
    }

    /**
     * 为 DrawerLayout 布局设置状态栏透明
     *
     * @param activity     需要设置的activity
     * @param drawerLayout DrawerLayout
     */
    public static void setTranslucentForDrawerLayout(Activity activity, DrawerLayout drawerLayout, int statusBarAlpha) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        setTransparentForDrawerLayout(activity, drawerLayout);
        addTranslucentView(activity, statusBarAlpha, Color.TRANSPARENT);
    }

    /**
     * 为 DrawerLayout 布局设置状态栏透明
     *
     * @param activity     需要设置的activity
     * @param drawerLayout DrawerLayout
     */
    public static void setTransparentForDrawerLayout(Activity activity, DrawerLayout drawerLayout) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        ViewGroup contentLayout = (ViewGroup) drawerLayout.getChildAt(0);
        // 内容布局不是 LinearLayout 时,设置padding top
        if (!(contentLayout instanceof LinearLayout) && contentLayout.getChildAt(1) != null) {
            contentLayout.getChildAt(1).setPadding(0, getStatusBarHeight(activity), 0, 0);
        }

        // 设置属性
        ViewGroup drawer = (ViewGroup) drawerLayout.getChildAt(1);
        drawerLayout.setFitsSystemWindows(false);
        contentLayout.setFitsSystemWindows(false);
        contentLayout.setClipToPadding(true);
        drawer.setFitsSystemWindows(false);
    }
    //
    //    /**
    //     * 为 DrawerLayout 布局设置状态栏透明(5.0以上半透明效果,不建议使用)
    //     *
    //     * @param activity     需要设置的activity
    //     * @param drawerLayout DrawerLayout
    //     */
    //    public static void setTranslucentForDrawerLayoutDiff(Activity activity, DrawerLayout drawerLayout) {
    //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
    //            // 设置状态栏透明
    //            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    //            // 设置内容布局属性
    //            ViewGroup contentLayout = (ViewGroup) drawerLayout.getChildAt(0);
    //            contentLayout.setFitsSystemWindows(true);
    //            contentLayout.setClipToPadding(true);
    //            // 设置抽屉布局属性
    //            ViewGroup vg = (ViewGroup) drawerLayout.getChildAt(1);
    //            vg.setFitsSystemWindows(false);
    //            // 设置 DrawerLayout 属性
    //            drawerLayout.setFitsSystemWindows(false);
    //        }
    //    }

    /**
     * 添加半透明矩形条
     *
     * @param activity       需要设置的 activity
     * @param statusBarAlpha 透明值
     */
    private static void addTranslucentView(Activity activity, int statusBarAlpha, int color) {
        ViewGroup contentView = activity.findViewById(android.R.id.content);
        // 移除半透明矩形,以免叠加
        if (contentView.getChildCount() > 1) {
            contentView.removeViewAt(1);
        }
        contentView.addView(createTranslucentStatusBarView(activity, statusBarAlpha, color));
    }

    /**
     * 生成一个和状态栏大小相同的彩色矩形条
     *
     * @param activity 需要设置的 activity
     * @param color    状态栏颜色值
     * @return 状态栏矩形条
     */
    private static View createStatusBarView(Activity activity, int color) {
        // 绘制一个和状态栏一样高的矩形
        View statusBarView = new View(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getStatusBarHeight(activity));
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(color);
        statusBarView.setId(R.id.status_bar_view_id);
        return statusBarView;
    }

    /**
     * 生成一个和状态栏大小相同的半透明矩形条
     *
     * @param activity 需要设置的activity
     * @param color    状态栏颜色值
     * @param alpha    透明值
     * @return 状态栏矩形条
     */
    private static View createStatusBarView(Activity activity, int color, int alpha) {
        // 绘制一个和状态栏一样高的矩形
        View statusBarView = new View(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getStatusBarHeight(activity));
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(calculateStatusColor(color, alpha));
        return statusBarView;
    }

    /**
     * 设置根布局参数
     */
    private static void setRootView(Activity activity) {
        ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        rootView.setFitsSystemWindows(true);
        rootView.setClipToPadding(true);
    }


    /**
     * 创建半透明矩形 View
     *
     * @param alpha 透明值
     * @return 半透明 View
     */
    private static View createTranslucentStatusBarView(Activity activity, int alpha, int color) {
        // 绘制一个和状态栏一样高的矩形
        View statusBarView = new View(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getStatusBarHeight(activity));
        statusBarView.setLayoutParams(params);

        if (color == Color.TRANSPARENT) {
            statusBarView.setBackgroundColor(Color.argb(alpha, 0, 0, 0));
        } else {
            int newColor = Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
            statusBarView.setBackgroundColor(newColor);
        }
        return statusBarView;
    }

    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        if (statusHeight != -404) {
            return statusHeight;
        }
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int height = context.getResources().getDimensionPixelSize(resourceId);
        if (height == 0) {
            height = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25f, context.getResources().getDisplayMetrics()));
        }
        statusHeight = height;
        return statusHeight;
    }

    public static int getNavigationheight(Context context) {
        if (hasNavigationBar(context)) {
            Resources resources = context.getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                return resources.getDimensionPixelSize(resourceId);
            }
        }
        return 0;
    }

    public static boolean hasNavigationBar(Context context) {
        //FIX https://fabric.io/momo6/android/apps/com.mm.momo/issues/5c1137f4f8b88c29638e931d
        if (context == null) {
            return false;
        }
        Resources resources = context.getResources();
        int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        return id > 0 && resources.getBoolean(id);
    }


    /**
     * 计算状态栏颜色
     *
     * @param color color值
     * @param alpha alpha值
     * @return 最终的状态栏颜色
     */
    public static int calculateStatusColor(int color, int alpha) {
        float a = 1 - alpha / 255f;
        int red = color >> 16 & 0xff;
        int green = color >> 8 & 0xff;
        int blue = color & 0xff;
        red = (int) (red * a + 0.5);
        green = (int) (green * a + 0.5);
        blue = (int) (blue * a + 0.5);
        return 0xff << 24 | red << 16 | green << 8 | blue;
    }


    /**
     * /////////////////////////////////////////////////////////////////////////
     * // Make StatusBar TRANSPARENT. Believe it or not, it truly works well! //
     * /////////////////////////////////////////////////////////////////////////
     */
    public static void setStatusBarTransparent(@NonNull Activity activity, @IdRes int contentViewId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
                setContentViewPadding(activity, contentViewId);
                return;
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            setContentViewPadding(activity, contentViewId);
        }
    }

    private static void setContentViewPadding(Activity activity, int contentViewId) {
        View contentView = activity.findViewById(contentViewId);
        if (contentView != null) {
            contentView.setPadding(0, getStatusBarHeight(activity), 0, 0);
        }
    }

    private static void setWindowFlag(Activity activity, final int bits, boolean enabled) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        if (enabled) {
            params.flags |= bits;
        } else {
            params.flags &= ~bits;
        }
        window.setAttributes(params);
    }

}
