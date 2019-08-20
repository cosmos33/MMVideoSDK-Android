package com.mm.recorduisdk.widget;


import android.graphics.drawable.Drawable;

/**
 * Created by XiongFangyu on 16/7/25.
 *
 * Modified by XiongFangyu on 16/7/28
 *
 * 增加支持{@link Drawable}
 *
 * {@link AnimCheckableGroupView}中的子view
 *
 */
public interface IAnimView {

    int getIndex();

    int getColor();

    void setColor(int color);

    Drawable getDrawable();

    void setDrawable(Drawable drawable);

    boolean isChecked();
}
