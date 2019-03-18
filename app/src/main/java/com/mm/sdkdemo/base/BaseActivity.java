package com.mm.sdkdemo.base;

import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mm.mediasdk.utils.UIUtils;
import com.mm.sdkdemo.R;
import com.mm.sdkdemo.utils.DeviceUtils;
import com.mm.sdkdemo.utils.StatusBarUtil;
import com.mm.sdkdemo.utils.toolbar.ToolbarHelper;

/**
 * Project momodev
 * <p>
 * Package com.mm.momo.android.activity
 * Created by tangyuchun on 3/31/16.
 */
public class BaseActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {
    protected ToolbarHelper toolbarHelper;
    private Toolbar mToolbar;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        beforeSetContentView();
        super.setContentView(layoutResID);
        init();
    }

    @Override
    public void setContentView(View view) {
        beforeSetContentView();
        super.setContentView(view);
        init();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        beforeSetContentView();
        super.setContentView(view, params);
        init();
    }

    protected void beforeSetContentView() {
        initStatusBar();
    }

    protected void init() {
        initToolbar();
    }

    protected void initToolbar() {
        toolbarHelper = ToolbarHelper.buildFromActivity(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackButtonClicked();
            }
        });
        mToolbar = toolbarHelper.getToolbar();
        if (!isShowBack() && mToolbar != null) {
            toolbarHelper.setNavigationIcon(0);
        }
    }

    /**
     * 是否需要展示后退键:重写此方法
     *
     * @return 是否展示
     */
    protected boolean isShowBack() {
        return true;
    }

    /**
     * 使用的是否是白色主题,如果使用黑色主题，应该返回false
     *
     * @return
     */
    protected boolean isLightTheme() {
        return true;
    }

    /**
     * 是否设置状态栏
     *
     * @return
     */
    protected boolean enableStatusBarColor() {
        return true;
    }

    /**
     * 自己指定状态栏颜色，需要配合 {@link #isLightTheme()} 来使用
     *
     * @return
     */
    protected int getCustomStatusBarColor() {
        if (isLightTheme()) {
            return getResources().getColor(R.color.status_bar_color_light);
        } else {
            return getResources().getColor(R.color.status_bar_color_dark);
        }
    }

    /**
     * 设置状态栏的颜色，由于陌陌的状态栏要求是白色，会导致很多手机状态栏颜色看不到，特此使用以下方案
     * A.小米手机 5.0～6.0 仍旧使用白色，因为小米可以单独设置状态栏文字颜色；
     * B.其他 5.0～6.0的手机，使用白色半透明；
     * C.6.0以上手机，直接使用 白色，并且主题设置 windowLightStatusBar 为true或者false即可
     */
    protected void initStatusBar() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        if (!enableStatusBarColor()) {
            return;
        }
        setStatusBarColor(getCustomStatusBarColor(), isLightTheme());
    }

    /**
     * 设置activity状态栏颜色
     *
     * @param statusColor  指定的颜色
     * @param isLightTheme 是否是light模式 light模式下，状态栏文字和标题都是黑色的，反之为白色
     */
    protected void setStatusBarColor(int statusColor, boolean isLightTheme) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        //需要配合 @style/Theme.App 和 @style/Theme.App.Dark 来使用
        if (isLightTheme) {
            //如果是白色状态栏 5.0～6.0 之间，非小米手机，就需要遮盖一层，避免状态栏看不到
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.M && !DeviceUtils.isMIUI()) {
                StatusBarUtil.setColor(this, statusColor, StatusBarUtil.MOMO_STAUS_BAR_ALPHA);
            } else {
                StatusBarUtil.setColor(this, statusColor, 0);
                //如果改成白色的状态栏，需要手动设置 windowLightStatusBar 为true,不需要在主题中设置
                //将状态栏icon设置为黑色
                setStatusBarTheme(false);
            }
        } else {
            // windowLightStatusBar 默认为false，即状态栏默认是白色的图标
            StatusBarUtil.setColor(this, statusColor, 0);
            //将状态栏icon设置为白色
            setStatusBarTheme(true);
        }
    }

    /**
     * 设置状态栏的图标文字颜色
     * 通过 6.0中提供的 {@link View#SYSTEM_UI_FLAG_LIGHT_STATUS_BAR} 来设置，
     * 只有在使用了FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS 并且没有使用 FLAG_TRANSLUCENT_STATUS的时候才有
     *
     * @param isWhiteText 是否需要将icon颜色设为白色
     */
    public void setStatusBarTheme(boolean isWhiteText) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = getWindow().getDecorView();
            if (decorView != null) {
                int vis = decorView.getSystemUiVisibility();
                if (isWhiteText) {
                    vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                } else {
                    vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                decorView.setSystemUiVisibility(vis);
            }
        }
        setupStatusBarForOtherSystem(isWhiteText);
    }

    /**
     * 为其他系统设置状态栏
     */
    protected void setupStatusBarForOtherSystem(boolean isWhiteText) {
        if (isWhiteText) {
            DeviceUtils.setMiuiStatusBarDarkMode(this, false);
        } else {
            // 如果是MIUI，则状态栏设置为light模式
            DeviceUtils.setMiuiStatusBarDarkMode(this, true);
        }
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    protected void onBackButtonClicked() {
        UIUtils.hideInputMethod(this);
        onBackPressed();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    @Override
    public void
    setTitle(CharSequence title) {
        if (toolbarHelper != null) {
            toolbarHelper.setTitle(title);
        }
        super.setTitle(title);
    }

    @Override
    public void setTitle(int titleId) {
        if (toolbarHelper != null) {
            toolbarHelper.setTitle(titleId);
        }
        super.setTitle(titleId);
    }
}
