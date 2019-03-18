package com.mm.sdkdemo.utils.toolbar;

import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.MenuRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mm.sdkdemo.R;
import com.mm.sdkdemo.base.BaseActivity;
import com.mm.sdkdemo.base.BaseFragment;

/**
 * Toolbar工具类，方便在Fragment或者BaseToolbarActivity中使用
 * Project momodev
 * Package com.mm.momo.view.toolbar
 * Created by tangyuchun on 4/5/16.
 * <p/>
 * <p/>
 * <p/>
 * Toolbar有很多种设置参数：
 * 1.设置左边空白距离  app:contentInsetStart="xdp" 就可以自己设置空白区域，默认是 16dp
 * 2.关闭/开启 返回按钮 {@link #enableNavigationButton(boolean, int)} 或者 {@link #enableNavigationButton(boolean, int)} }
 * 3.是否显示Toolbar底部的分割线 {@link #showShadow(boolean)}
 */
public class ToolbarHelper {
    private View appBarLayout;
    private Toolbar mToolbar;

    private ToolbarHelper() {
    }

    public static ToolbarHelper buildFromActivity(BaseActivity activity, View.OnClickListener backButtonListener) {
        ToolbarHelper toolbarHelper = new ToolbarHelper();

        View appBar = activity.findViewById(R.id.appbar_id);
        //Activity中的Toolbar的ID一律是 activity_toolbar_id
        View view = activity.findViewById(R.id.toolbar_id);
        if (view != null) {
            Toolbar toolbar = (Toolbar) view;
            toolbar.setOnMenuItemClickListener(activity);

            //设置返回按钮点击事件
            if (backButtonListener != null) {
                toolbar.setNavigationOnClickListener(backButtonListener);
            }
            toolbarHelper.mToolbar = toolbar;

            toolbarHelper.init();
        }

        if (appBar != null) {
            toolbarHelper.appBarLayout = appBar;
        }
        return toolbarHelper;
    }

    /**
     * 从一个BaseFragment中初始化ToolbarHelper
     *
     * @param fragment
     * @return
     */
    public static ToolbarHelper buildFromFragment(BaseFragment fragment) {
        ToolbarHelper toolbarHelper = new ToolbarHelper();
        View appBar = fragment.findViewById(R.id.appbar_id);
        View viewToolbar = fragment.findViewById(R.id.toolbar_id);

        if (appBar != null && appBar instanceof CompatAppbarLayout) {
            toolbarHelper.appBarLayout = appBar;
        }
        if (viewToolbar != null && viewToolbar instanceof Toolbar) {
            toolbarHelper.mToolbar = (Toolbar) viewToolbar;
        }
        return toolbarHelper;
    }

    private void init() {
        showShadow(true);
    }

    /**
     * 是否支持返回按钮
     *
     * @param flag      true 可以点击触发返回 false 不显示返回按钮
     * @param iconResId 设置返回按钮的icon
     */
    public void enableNavigationButton(boolean flag, @DrawableRes int iconResId) {
        if (!flag || iconResId <= 0) {
            mToolbar.setNavigationIcon(null);
        } else {
            mToolbar.setNavigationIcon(iconResId);
        }
    }

    /**
     * 设置返回按钮的图标，如果想要禁掉返回按钮，直接 传入0 即可
     *
     * @param iconResId ic_toolbar_back_gray_24dp 或者 ic_toolbar_back_white_24dp
     */
    public void setNavigationIcon(@DrawableRes int iconResId) {
        if (iconResId <= 0) {
            mToolbar.setNavigationIcon(null);
        } else {
            mToolbar.setNavigationIcon(iconResId);
        }
    }

    public void setNavigationIcon(Drawable icon) {
        if (mToolbar != null) {
            mToolbar.setNavigationIcon(icon);
        }
    }

    public void setNavigationOnClickListener(View.OnClickListener backClickListener) {
        //设置返回按钮点击事件
        if (backClickListener != null) {
            mToolbar.setNavigationOnClickListener(backClickListener);
        }
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    public View getAppBarLayout() {
        return appBarLayout;
    }

    /**
     * 自行指定Toolbar，同时应该指定外层的 CompatAppbarLayout
     *
     * @param pAppBarLayout
     * @param pToolbar
     */
    public static ToolbarHelper buildCustom(View pAppBarLayout, Toolbar pToolbar) {
        ToolbarHelper toolbarHelper = new ToolbarHelper();
        toolbarHelper.appBarLayout = pAppBarLayout;
        toolbarHelper.mToolbar = pToolbar;
        return toolbarHelper;
    }

    /**
     * 设置标题文本
     *
     * @param title
     */
    public void setTitle(CharSequence title) {
        if (mToolbar != null) {
            mToolbar.setTitle(title);
        }
    }

    /**
     * 设置副标题
     *
     * @param subTitle
     */
    public void setSubTitle(CharSequence subTitle) {
        if (mToolbar != null) {
            mToolbar.setSubtitle(subTitle);
        }
    }

    /**
     * 清空菜单
     */
    public void clearMenus() {
        if (mToolbar != null) {
            mToolbar.getMenu().clear();
        }
    }

    /**
     * 加载一个菜单xml文件，加载时会清空已有的菜单
     *
     * @param menuResId
     * @param pMenuItemClickListener
     */
    public void inflateMenu(@MenuRes int menuResId, @Nullable Toolbar.OnMenuItemClickListener pMenuItemClickListener) {
        if (mToolbar != null) {
            mToolbar.getMenu().clear();
            mToolbar.inflateMenu(menuResId);
            mToolbar.setOnMenuItemClickListener(pMenuItemClickListener);
        }
    }

    /**
     * 设置标题文本
     *
     * @param titleResId
     */
    public void setTitle(@StringRes int titleResId) {
        if (mToolbar != null) {
            mToolbar.setTitle(titleResId);
        }
    }

    /**
     * 设置标题文字颜色
     *
     * @param color
     */
    public void setTitleColor(@ColorInt int color) {
        if (mToolbar != null) {
            mToolbar.setTitleTextColor(color);
        }
    }

    /**
     * 设置副标题的颜色
     *
     * @param color
     */
    public void setSubTitleColor(@ColorInt int color) {
        if (mToolbar != null) {
            mToolbar.setSubtitleTextColor(color);
        }
    }

    /**
     * 在Toolbar右边增加菜单按钮,按钮的点击事件在 {@link Toolbar.OnMenuItemClickListener} 中处理
     *
     * @param itemId
     * @param itemText
     * @param iconResId 按钮图标
     * @param listener  按钮单击事件，如果是在 {@link BaseActivity} 中，则不需要指定
     */
    public MenuItem addRightMenu(int itemId, CharSequence itemText, @DrawableRes int iconResId, MenuItem.OnMenuItemClickListener listener) {
        if (mToolbar != null) {
            Menu menu = mToolbar.getMenu();
            itemId = itemId == 0 ? Menu.NONE : itemId;
            MenuItem item = menu.add(Menu.NONE, itemId, Menu.NONE, itemText);
            if (listener != null) {
                item.setOnMenuItemClickListener(listener);
            }
            if (iconResId > 0) {
                item.setIcon(iconResId);
            }
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            return item;
        }
        return null;
    }

    /**
     * 获得菜单对应的View，类型是 {@link ActionMenuItemView} 继承于TexView
     *
     * @param menuId
     * @return
     */
    public ActionMenuItemView getMenuView(int menuId) {
        if (mToolbar != null) {
            return (ActionMenuItemView) mToolbar.findViewById(menuId);
        }
        return null;
    }

    public void setMenuTextColor(int menuId, @ColorInt int color) {
        if (mToolbar != null && mToolbar.getMenu() != null) {
            Menu menu = mToolbar.getMenu();
            setMenuTextColor(menu.findItem(menuId), color);
        }
    }

    /**
     * 设置菜单文字颜色
     *
     * @param item  哪个item
     * @param color 要设置的颜色
     */
    public void setMenuTextColor(MenuItem item, @ColorInt int color) {
        if (item != null && mToolbar != null) {
            View view = mToolbar.findViewById(item.getItemId());
            if (view instanceof ActionMenuItemView) {
                ((ActionMenuItemView) view).setTextColor(color);
            }
        }
    }

    /**
     * 根据ID找到对应的menu
     *
     * @param menuId
     * @return
     */
    public MenuItem findMenuItemById(@IdRes int menuId) {
        if (mToolbar != null) {
            return mToolbar.getMenu().findItem(menuId);
        }
        return null;
    }

    /**
     * 设置是否显示某一个菜单
     *
     * @param menuId
     * @param isVisible
     */
    public void setMenuVisibility(@IdRes int menuId, boolean isVisible) {
        if (mToolbar != null) {
            MenuItem item = mToolbar.getMenu().findItem(menuId);
            if (item != null) {
                item.setVisible(isVisible);
            }
        }
    }

    public Menu getMenu() {
        if (mToolbar != null) {
            return mToolbar.getMenu();
        }
        return null;
    }

    /**
     * 设置菜单颜色:如果已经修改过颜色，则需要修改ActionView的文字
     *
     * @param itemId
     * @param text
     */
    public void setMenuText(int itemId, String text) {
        if (mToolbar == null || mToolbar.getMenu() == null) {
            return;
        }
        Menu menu = mToolbar.getMenu();
        MenuItem item = menu.findItem(itemId);
        if (item == null) {
            return;
        }
        View actionView = item.getActionView();
        if (actionView != null && actionView instanceof TextView) {
            ((TextView) actionView).setText(text);
            return;
        }
        item.setTitle(text);
    }

    public void show() {
        if (appBarLayout != null && appBarLayout.getVisibility() != View.VISIBLE) {
            appBarLayout.setVisibility(View.VISIBLE);
        }

        if (mToolbar != null && mToolbar.getVisibility() != View.VISIBLE) {
            mToolbar.setVisibility(View.VISIBLE);
        }
    }

    public void hide() {
        if (appBarLayout != null && appBarLayout.getVisibility() != View.GONE) {
            appBarLayout.setVisibility(View.GONE);
        }
        if (mToolbar != null && mToolbar.getVisibility() != View.GONE) {
            mToolbar.setVisibility(View.GONE);
        }
    }

    public void setBackgroundColor(int color) {
        if (appBarLayout != null) {
            appBarLayout.setBackgroundColor(color);
        } else {
            if (mToolbar != null) {
                mToolbar.setBackgroundColor(color);
            }
        }
    }

    public void setBackgroundRes(int resid) {
        if (appBarLayout != null) {
            appBarLayout.setBackgroundResource(resid);
        } else {
            if (mToolbar != null) {
                mToolbar.setBackgroundResource(resid);
            }
        }
    }

    /**
     * 控制是否显示阴影
     *
     * @param flag
     */
    public void showShadow(boolean flag) {
        if (appBarLayout != null && appBarLayout instanceof CompatAppbarLayout) {
            ((CompatAppbarLayout) appBarLayout).showShadow(flag);
        }
    }

    /**
     * 更多弹出菜单是否正在显示
     *
     * @return
     */
    public boolean isMoreMenuShowing() {
        if (mToolbar != null) {
            return mToolbar.isOverflowMenuShowPending();
        }
        return false;
    }

    /**
     * 设置Toolbar的背景色，如果Toolbar包裹在 appbar中，则会改变appbar的背景，否则，会直接改变Toolbar的背景
     *
     * @param color
     */
    public void setToolbarBackgroundColor(@ColorInt int color) {
        if (appBarLayout != null) {
            appBarLayout.setBackgroundColor(color);
        } else {
            if (mToolbar != null) {
                mToolbar.setBackgroundColor(color);
            }
        }
    }

    /**
     * 设置Toolbar 背景透明度，如果木有包裹 {@link CompatAppbarLayout}，会直接设置在Toolbar上
     *
     * @param alpha
     */
    public void setToolbarBackgroundAlpha(int alpha) {
        View targetView = appBarLayout;
        if (targetView == null) {
            targetView = mToolbar;
        }
        if (null != targetView) {
            Drawable drawable = targetView.getBackground();
            if (null != drawable) {
                if (alpha >= 255) {
                    alpha = 255;
                }
                if (alpha <= 0) {
                    alpha = 0;
                }
                drawable.mutate().setAlpha(alpha);
            }
        }
    }

    /**
     * 设置Toolbar左侧的空白区域 默认是 16dp
     *
     * @param insetStartInPixels 单位必须是像素
     */
    public void setContentInsetStart(int insetStartInPixels) {
        if (mToolbar != null) {
            mToolbar.setContentInsetsRelative(insetStartInPixels, mToolbar.getContentInsetEnd());
        }
    }

    /**
     * 设置Toolbar右侧的空白区域 默认是 16dp
     *
     * @param insetEndInPixels 单位必须是像素
     */
    public void setContentInsetEnd(int insetEndInPixels) {
        if (mToolbar != null) {
            mToolbar.setContentInsetsRelative(mToolbar.getContentInsetEnd(), insetEndInPixels);
        }
    }

}
