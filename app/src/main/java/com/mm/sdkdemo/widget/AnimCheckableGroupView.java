package com.mm.sdkdemo.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Xfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;

import com.mm.sdkdemo.R;
import com.mm.sdkdemo.utils.SuperOvershootInterpolator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * Created by XiongFangyu on 16/7/25.
 * 动画效果里面画笔颜色那个view
 * <p/>
 * Modified by XiongFangyu on 16/7/28
 * {@link IAnimView}增加支持{@link Drawable}
 * 可实时增加或删除子元素
 * <p/>
 * usage:
 * <p/>
 * <pre>
 *     <com.mm.momo.android.view.AnimCheckableGroupView
 *      android:layout_width="wrap_content"
 *      android:layout_height="wrap_content"
 *      app:acgv_style="@style/Anim_Checkable_Group_View_Style"/>
 *
 *      private void initView(){
 *          animCheckableView = (AnimCheckableGroupView)findViewById(R.id.anim_checkable);
 *          animCheckableView.setChildCheckListener(new AnimCheckableGroupView.ChildCheckListener() {
 *              @Override
 *              public void onChecked(IAnimView v, boolean checked) {
 *                  Log.d("animview", "onChecked: "+v.getColor());
 *              }
 *          }
 *          int[] colors = new int[]{
 *              0XFFFF0000,
 *              0XFF00FF00,
 *              0XFF0000FF,
 *              0XFFFFFF00,
 *              0XFFFF00FF,
 *              0XFF00FFFF
 *          };
 *
 *          int[] pressColors = new int[]{
 *              0X9FFF0000,
 *              0X9F00FF00,
 *              0X9F0000FF,
 *              0X9FFFFF00,
 *              0X9FFF00FF,
 *              0X9F00FFFF
 *          };
 *          animCheckableView.addColors(colors,pressColors);
 *      }
 *
 *      private void onShowButtonClick(){
 *          animCheckableView.setVisibility(View.VISIBLE);
 *          //the same as : animCheckableView.setVisibility(View.VISIBLE,true);
 *      }
 * </pre>
 */
public class AnimCheckableGroupView extends View {

    /**
     * 排列方向
     * {@link #VERTICAL_DOWN} 垂直向下排列
     * {@link #VERTICAL_UP} 垂直向上排列
     * {@link #HORIZONTAL_RIGHT} 水平向右排列
     * {@link #HORIZONTAL_LEFT} 水平向左排列
     */
    public static final int VERTICAL_DOWN = 1,
            VERTICAL_UP = 2,
            HORIZONTAL_RIGHT = 3,
            HORIZONTAL_LEFT = 4;

    /**
     * 排列方向
     */
    private int orientation = VERTICAL_DOWN;
    private int showAnimDuration;
    private int showAnimDelay;
    private int checkAnimDuration;

    private int visibility;
    /** */
    private ParamsHolder params;

    private ArrayList<AnimCheckableView> children;

    private Runnable[] showAnimRunnables;

    private AnimCheckableView checkedView;

    private AnimCheckableView pressedView;

    private Runnable hideRunnable;

    private ChildCheckListener childCheckListener;

    private boolean isMeasure = false;
    /**
     * 如果一个子view已经check了，再次点击时，不会uncheck掉
     */
    private boolean checkOnce = false;

    private boolean fixedOutsideCircle = true;

    private Xfermode mode;

    public AnimCheckableGroupView(Context context) {
        this(context, null);
    }

    public AnimCheckableGroupView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimCheckableGroupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AnimCheckableGroupView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {

        params = new ParamsHolder();
        children = new ArrayList<>(6);

        final Resources.Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(attrs,
                R.styleable.AnimCheckableGroupView, defStyleAttr, defStyleRes);
        TypedArray appearance = null;
        int ap = a.getResourceId(
                R.styleable.AnimCheckableGroupView_acgv_style, -1);
        if (ap != -1) {
            appearance = theme.obtainStyledAttributes(
                    ap, R.styleable.AnimCheckableGroupView);
        }
        initStyle(appearance);
        initStyle(a);
    }

    @SuppressWarnings("WrongConstant")
    private void initStyle(TypedArray appearance) {
        if (appearance != null) {
            final int len = appearance.getIndexCount();
            for (int i = 0; i < len; i++) {
                int attr = appearance.getIndex(i);
                switch (attr) {
                    case R.styleable.AnimCheckableGroupView_acgv_orientation:
                        setOrientation(appearance.getInt(attr, orientation));
                        break;
                    case R.styleable.AnimCheckableGroupView_acgv_show_anim_duration:
                        setShowAnimDuration(appearance.getInt(attr, showAnimDuration));
                        break;
                    case R.styleable.AnimCheckableGroupView_acgv_show_anim_delay:
                        setShowAnimDelay(appearance.getInt(attr, showAnimDelay));
                        break;
                    case R.styleable.AnimCheckableGroupView_acgv_check_anim_duration:
                        setCheckAnimDuration(appearance.getInt(attr, checkAnimDuration));
                        break;
                    case R.styleable.AnimCheckableGroupView_acgv_padding:
                        final int p = appearance.getDimensionPixelOffset(attr, 0);
                        params.padding.set(p, p, p, p);
                        break;
                    case R.styleable.AnimCheckableGroupView_acgv_padding_left:
                        params.padding.set(
                                appearance.getDimensionPixelOffset(attr, 0),
                                params.padding.top,
                                params.padding.right,
                                params.padding.bottom
                        );
                        break;
                    case R.styleable.AnimCheckableGroupView_acgv_padding_top:
                        params.padding.set(
                                params.padding.left,
                                appearance.getDimensionPixelOffset(attr, 0),
                                params.padding.right,
                                params.padding.bottom
                        );
                        break;
                    case R.styleable.AnimCheckableGroupView_acgv_padding_right:
                        params.padding.set(
                                params.padding.left,
                                params.padding.top,
                                appearance.getDimensionPixelOffset(attr, 0),
                                params.padding.bottom
                        );
                        break;
                    case R.styleable.AnimCheckableGroupView_acgv_padding_bottom:
                        params.padding.set(
                                params.padding.left,
                                params.padding.top,
                                params.padding.right,
                                appearance.getDimensionPixelOffset(attr, 0)
                        );
                        break;
                    case R.styleable.AnimCheckableGroupView_acgv_outside_width:
                        params.outsideWidth = appearance.getDimensionPixelOffset(attr, params.outsideWidth);
                        break;
                    case R.styleable.AnimCheckableGroupView_acgv_radius:
                        params.normalRadius = appearance.getDimensionPixelOffset(attr, params.normalRadius);
                        break;
                    case R.styleable.AnimCheckableGroupView_acgv_checked_radius:
                        params.checkedRadius = appearance.getDimensionPixelOffset(attr, params.checkedRadius);
                        break;
                    case R.styleable.AnimCheckableGroupView_acgv_color:
                        params.color = appearance.getColor(attr, params.color);
                        break;
                    case R.styleable.AnimCheckableGroupView_acgv_pressed_color:
                        params.pressedColor = appearance.getColor(attr, params.pressedColor);
                        break;
                    case R.styleable.AnimCheckableGroupView_acgv_outside_color:
                        params.outsideColor = appearance.getColor(attr, params.outsideColor);
                        break;
                    case R.styleable.AnimCheckableGroupView_acgv_draw_outside:
                        params.drawOutside = appearance.getBoolean(attr, params.drawOutside);
                        break;
                    case R.styleable.AnimCheckableGroupView_acgv_fixed_outside:
                        fixedOutsideCircle = appearance.getBoolean(attr, fixedOutsideCircle);
                        break;
                    case R.styleable.AnimCheckableGroupView_acgv_colors:
                        final int colorsRes = appearance.getResourceId(attr, -1);
                        if (colorsRes > 0) {
                            int[] colors = getResources().getIntArray(colorsRes);
                            addColors(colors);
                        }
                        break;
                    case R.styleable.AnimCheckableGroupView_acgv_drawables:
                        final int drawableRes = appearance.getResourceId(attr, -1);
                        if (drawableRes > 0) {
                            int[] res = getDrawableIds(drawableRes);
                            if (res != null) {
                                final int l = res.length;
                                Drawable[] drawables = new Drawable[l];
                                for (int j = 0; j < l; j++) {
                                    int dres = res[j];
                                    if (dres > 0) {
                                        drawables[j] = getResources().getDrawable(dres);
                                    }
                                }
                                addDrawables(drawables);
                            }
                        }
                        break;
                }
            }
            appearance.recycle();
        }
    }

    private int[] getDrawableIds(int arrayRes) {
        TypedArray ar = getResources().obtainTypedArray(arrayRes);
        final int len = ar.length();
        int[] resIds = null;
        if (len > 0) {
            resIds = new int[len];
            for (int i = 0; i < len; i++) {
                resIds[i] = ar.getResourceId(i, 0);
            }
        }
        ar.recycle();
        return resIds;
    }

    @Override
    protected void onMeasure(int w, int h) {
        final int childCount = children.size();
        if (childCount <= 0) {
            isMeasure = true;
            super.onMeasure(w, h);
            return;
        }

        int childMaxWidth = 0, childMaxHeight = 0;
        int childCheckedWidth = 0, childCheckedHeight = 0;

        for (int i = 0; i < childCount; i++) {
            final AnimCheckableView c = children.get(i);
            final int maxRaidus = (int) (Math.max(c.normalRadius, c.checkedRadius) * 2);
            final int cw = maxRaidus + params.padding.left + params.padding.right;
            final int ch = maxRaidus + params.padding.top + params.padding.bottom;
            if (i == 0) {
                childCheckedWidth = (int) (c.checkedRadius * 2 + params.padding.left + params.padding.right);
                childCheckedHeight = (int) (c.checkedRadius * 2 + params.padding.top + params.padding.bottom);
            }
            childMaxWidth += cw;
            childMaxHeight += ch;
        }
        final AnimCheckableView child = children.get(0);
        final float radiusOffset = Math.abs(child.checkedRadius - child.normalRadius) * 2;
        childMaxWidth -= radiusOffset * (childCount - 1);

        int finalWidth = childCheckedWidth + getPaddingLeft() + getPaddingRight();
        int finalHeight = childCheckedHeight + getPaddingTop() + getPaddingBottom();

        if (orientation == HORIZONTAL_LEFT || orientation == HORIZONTAL_RIGHT) {
            finalWidth = childMaxWidth + getPaddingLeft() + getPaddingRight();
        } else if (orientation == VERTICAL_DOWN || orientation == VERTICAL_UP) {
            finalHeight = childMaxHeight + getPaddingTop() + getPaddingBottom();
        }

        setMeasuredDimension(finalWidth, finalHeight);
        layoutChildren();
        isMeasure = true;
    }

    /**
     * 布局子view {@link #children}
     * 在{@link #onMeasure(int, int)}中调用
     * 当子view请求刷新并需要修改布局时调用 {@link RequestInvalidateListener#needInvalidate(boolean)}
     */
    private void layoutChildren() {
        final int childCount = children.size();
        if (childCount <= 0) {
            return;
        }

        final int pl = getPaddingLeft();
        final int pt = getPaddingTop();
        final int pr = getPaddingRight();
        final int pb = getPaddingBottom();

        final int layoutWidth = getMeasuredWidth();
        final int layoutHeight = getMeasuredHeight();

        int allChildrenWidth = 0, allChildrenHeight = 0;

        switch (orientation) {
            case HORIZONTAL_LEFT:
                allChildrenWidth = layoutWidth - pr;
                break;
            case HORIZONTAL_RIGHT:
                allChildrenWidth = pl;
                break;
            case VERTICAL_DOWN:
                allChildrenHeight = pt;
                break;
            case VERTICAL_UP:
                allChildrenHeight = layoutHeight - pb;
                break;
        }

        for (int i = 0; i < childCount; i++) {
            final AnimCheckableView c = children.get(i);
            final int radius = getRadiusWhenLayout(c);
            final int cw = radius * 2 + params.padding.left + params.padding.right;
            final int ch = radius * 2 + params.padding.top + params.padding.bottom;

            int cl = 0, ct = 0;
            switch (orientation) {
                case HORIZONTAL_LEFT:
                    cl = allChildrenWidth - cw;
                    ct = pt;
                    allChildrenWidth = cl;
                    break;
                case HORIZONTAL_RIGHT:
                    cl = allChildrenWidth;
                    ct = pt;
                    allChildrenWidth = cw + cl;
                    break;
                case VERTICAL_DOWN:
                    ct = allChildrenHeight;
                    cl = pl;
                    allChildrenHeight = ch + ct;
                    break;
                case VERTICAL_UP:
                    ct = allChildrenHeight - ch;
                    cl = pl;
                    allChildrenHeight = ct;
                    break;
            }
            if (c.isChecked() && !isMeasure) {
                final int ncw = (int) (c.normalRadius * 2 + params.padding.left + params.padding.right);
                final int nch = (int) (c.normalRadius * 2 + params.padding.top + params.padding.bottom);
                c.layout(cl, ct, cl + ncw, ct + nch);
            }
            c.layout(cl, ct, cl + cw, ct + ch);
        }
    }

    public void setCheckOnce(boolean pCheckOnce) {
        checkOnce = pCheckOnce;
    }

    /**
     * layout时获取{@link AnimCheckableView}的半径
     * 当{@link AnimCheckableView#inCheckAnim}为{true}:返回{@link AnimCheckableView#drawRadius}
     * 否则:返回{@link AnimCheckableView#normalRadius}
     *
     * @param c 正在layout的view
     * @return 半径
     */
    private int getRadiusWhenLayout(AnimCheckableView c) {
        if (c.inCheckAnim)
            return (int) c.drawRadius;
        else if (c.isChecked())
            return (int) c.checkedRadius;
        else
            return (int) c.normalRadius;
    }

    /**
     * 获取最后一个view后面多出来的空像素
     * @return
     */
    public int getEndOffsetPx() {
        if (children != null && !children.isEmpty()) {
            final AnimCheckableView child = children.get(0);
            return (int) (Math.abs(child.checkedRadius - child.normalRadius) * 2);
        }
        return -1;
    }

    public void layoutAndInvalidate() {
        layoutChildren();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int childCount = children.size();

        if (childCount <= 0)
            return;

        for (int i = 0; i < childCount; i++) {
            children.get(i).draw(canvas);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeRunnables();
    }

    private void removeRunnables() {
        if (showAnimRunnables != null && showAnimRunnables.length > 0) {
            for (int i = 0; i < showAnimRunnables.length; i++) {
                final Runnable r = showAnimRunnables[i];
                if (r != null) {
                    removeCallbacks(r);
                }
            }
        }
        if (hideRunnable != null)
            removeCallbacks(hideRunnable);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        final int childCount = children.size();
        if (childCount <= 0)
            return super.onTouchEvent(e);

        final int action = MotionEventCompat.getActionMasked(e);
        final int x = (int) e.getX();
        final int y = (int) e.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                checkPressed(x, y, childCount);
                break;
            case MotionEvent.ACTION_UP:
                if (pressedView != null) {
                    pressedView.pressed = false;
                    if (pressedView.rect.contains(x, y)) {
                        if (checkOnce) {
                            if (!pressedView.isChecked()) {
                                pressedView.toggle(true);
                            } else {
                                pressedView.setChecked(true, true);
                            }
                        } else {
                            pressedView.toggle(true);
                        }
                    } else {
                        invalidate();
                    }
                }
                pressedView = null;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                if (pressedView != null)
                    pressedView.pressed = false;
                invalidate();
                break;
        }
        return true;
    }

    /**
     * 检查触摸点是否在子{@link AnimCheckableView}内
     * 若在其中一个子view中,则赋值{@link #pressedView}并将{@link AnimCheckableView#pressed}置为true
     * 然后刷新
     *
     * @param x          x坐标
     * @param y          y坐标
     * @param childCount {@link #children}.size
     */
    private void checkPressed(int x, int y, int childCount) {
        if (pressedView != null)
            pressedView.pressed = false;
        for (int i = 0; i < childCount; i++) {
            AnimCheckableView v = children.get(i);
            if (v.rect.contains(x, y)) {
                v.pressed = true;
                pressedView = v;
                invalidate();
                break;
            }
        }
    }

    private void initRunnables(int i) {
        if (children.size() <= 0)
            return;
        if (showAnimRunnables == null || showAnimRunnables.length != children.size()) {
            showAnimRunnables = new Runnable[children.size()];
        }
        if (showAnimRunnables[i] == null) {
            showAnimRunnables[i] = new ShowAnimRunnable(i);
        }
    }

    public void show(boolean anim) {
        final int childCount = children.size();
        if (childCount <= 0)
            return;
        if (!anim) {
            for (int i = 0; i < childCount; i++) {
                AnimCheckableView c = children.get(i);
                c.show(false);
            }
        } else {
            for (int i = 0; i < childCount; i++) {
                initRunnables(i);
                ((ShowAnimRunnable) showAnimRunnables[i]).setShow(true);
                postDelayed(showAnimRunnables[i], showAnimDelay * i);
            }
        }
    }

    public void hide(boolean anim) {
        final int childCount = children.size();
        if (childCount <= 0)
            return;
        if (anim) {
            for (int i = 0; i < childCount; i++) {
                initRunnables(i);
                ((ShowAnimRunnable) showAnimRunnables[i]).setShow(false);
                postDelayed(showAnimRunnables[i], showAnimDelay * i);
            }
            if (hideRunnable == null) {
                hideRunnable = new HideRunnable();
            }
            postDelayed(hideRunnable, showAnimDelay * (childCount - 1) + showAnimDuration);
        }
    }

    public void setVisibility(int visibility, boolean anim) {
        if (!anim) {
            super.setVisibility(visibility);
        } else {
            setVisibility(visibility);
        }
    }

    @Override
    public void setVisibility(int visibility) {
        if (children.size() <= 0) {
            super.setVisibility(visibility);
            return;
        }
        this.visibility = visibility;
        if (visibility == VISIBLE) {
            super.setVisibility(visibility);
            for (AnimCheckableView v : children) {
                v.realTimeDrawOutside = false;
                v.drawInSide = false;
            }
            show(true);
        } else {
            hide(true);
        }
    }

    public void setChildCheckListener(ChildCheckListener childCheckListener) {
        this.childCheckListener = childCheckListener;
    }

    /**
     * 设置方向
     *
     * @param orientation {@link #VERTICAL_DOWN} {@link #VERTICAL_UP} {@link #HORIZONTAL_RIGHT} {@link #HORIZONTAL_LEFT}其中之一
     */
    public void setOrientation(@OrientationInt int orientation) {
        this.orientation = orientation;
        if (children.size() > 0 && isMeasure) {
            requestLayout();
        }
    }

    public void setShowAnimDuration(int showAnimDuration) {
        this.showAnimDuration = showAnimDuration;
        final int cc = children.size();
        if (cc > 0) {
            for (int i = 0; i < cc; i++) {
                AnimCheckableView v = children.get(i);
                v.showAnimDuration = showAnimDuration;
            }
        }
    }

    public void setCheckAnimDuration(int checkAnimDuration) {
        this.checkAnimDuration = checkAnimDuration;
        final int cc = children.size();
        if (cc > 0) {
            for (int i = 0; i < cc; i++) {
                AnimCheckableView v = children.get(i);
                v.checkAnimDuration = checkAnimDuration;
            }
        }
    }

    public void setShowAnimDelay(int showAnimDelay) {
        this.showAnimDelay = showAnimDelay;
    }

    public void addColors(int[] colors, int[] pressedColors) {
        final int len = colors.length;
        for (int i = 0; i < len; i++) {
            final int color = colors[i];
            final int pressed = pressedColors[i];
            AnimCheckableView view = generateAnimView();
            view.setColor(color);
            view.pressedColor = pressed;
            children.add(view);
        }
        if (isMeasure) {
            requestLayout();
        } else
            invalidate();
    }

    public void addColors(int[] colors) {
        addColors(colors, colors);
    }

    public void addColor(int color, int pressedColor) {
        AnimCheckableView view = generateAnimView();
        view.setColor(color);
        view.pressedColor = pressedColor;
        children.add(view);
        if (isMeasure)
            requestLayout();
        else
            invalidate();
    }

    public void addColor(int color) {
        addColor(color, color);
    }

    public void addDrawables(Drawable... drawables) {
        if (drawables == null || drawables.length <= 0)
            return;
        for (int i = 0; i < drawables.length; i++) {
            final Drawable d = drawables[i];
            if (d != null) {
                AnimCheckableView view = generateAnimView();
                view.setDrawable(d);
                children.add(view);
            }
        }
        if (isMeasure)
            requestLayout();
        else
            invalidate();
    }

    public void removeViewByDrawable(Drawable drawable) {
        if (children == null || children.size() <= 0)
            return;
        for (AnimCheckableView v : children) {
            if (v.drawable == drawable) {
                children.remove(v);
                if (isMeasure)
                    requestLayout();
                else
                    invalidate();
                return;
            }
        }
    }

    public void removeViewByIndex(int index) {
        if (children == null || children.size() <= index)
            return;
        for (int i = 0, l = children.size(); i < l; i++) {
            if (i > index) {
                final AnimCheckableView v = children.get(i);
                v.index--;
            }
        }
        children.remove(index);
        if (isMeasure)
            requestLayout();
        else
            invalidate();
    }

    @Deprecated
    public void removeViewByColor(int color) {
        if (children == null || children.size() <= 0)
            return;
        for (AnimCheckableView v : children) {
            if (v.color == color) {
                children.remove(v);
                if (isMeasure)
                    requestLayout();
                else
                    invalidate();
                return;
            }
        }
    }

    public void setCheck(int index, boolean checked, boolean anim) {
        if (children == null || children.size() <= index)
            return;
        final AnimCheckableView view = children.get(index);
        if (view.checked == checked)
            return;
        view.setChecked(checked, anim);
    }

    /**
     * 使用{@link #params}新建一个{@link AnimCheckableView}并设置各项参数
     * {@link AnimCheckableView#index} 实际上是此view在{@link #children}中的index
     *
     * @return 新的AnimCheckableView
     */
    private AnimCheckableView generateAnimView() {
        final AnimCheckableView view = new AnimCheckableView(params);
        view.showAnimDuration = showAnimDuration;
        view.checkAnimDuration = checkAnimDuration;
        view.index = children.size();
        view.listener = requestInvalidateListener;
        view.onCheckedListener = new OnCheckedListener() {
            @Override
            public void onChecked(AnimCheckableView v, boolean checked) {
                if (!checked) {
                    if (checkedView == v)
                        checkedView = null;
                    return;
                }
                if (checkedView != null && checkedView != view) {
                    checkedView.toggle(true);
                }
                if (checked)
                    checkedView = view;
                if (childCheckListener != null)
                    childCheckListener.onChecked(v, checked, v.index);
            }
        };
        return view;
    }

    private final RequestInvalidateListener requestInvalidateListener =
            new RequestInvalidateListener() {
                @Override
                public void needInvalidate(boolean needLayout) {
                    if (needLayout)
                        layoutChildren();
                    invalidate();
                }
            };

    /**
     * 每个view画一个圆圈,并提供显示,隐藏,check的动画
     * {@link #children}中包含多个{@link AnimCheckableView}
     * 通过{@link ParamsHolder}提供参数
     */
    private class AnimCheckableView implements IAnimView {

        int index;
        Rect rect;

        int color;
        int pressedColor = 0xCFFF0000;
        int outSideColor = Color.WHITE;
        int outSideWidth;
        Drawable drawable;

        float normalRadius;
        float checkedRadius;
        float drawRadius;
        boolean checked = false;
        boolean pressed = false;
        boolean realTimeDrawOutside = true;
        boolean drawOutSide = true;
        boolean drawInSide = true;
        boolean inCheckAnim = false;

        Interpolator interpolator = new SuperOvershootInterpolator(1, 0.5);
        ValueAnimator showAnim;
        ValueAnimator checkAnim;
        int showAnimDuration;
        int checkAnimDuration;

        RequestInvalidateListener listener;
        OnCheckedListener onCheckedListener;

        private Paint mPaint;

        private PointF srcCenter;

        private Path clipPath;

        private int oldOrientation;

        AnimCheckableView(ParamsHolder params) {
            if (params != null) {
                color = params.color;
                if (pressedColor == 0)
                    pressedColor = color;
                else
                    pressedColor = params.pressedColor;
                drawable = params.drawable;
                outSideColor = params.outsideColor;
                outSideWidth = params.outsideWidth;
                normalRadius = params.normalRadius;
                checkedRadius = params.checkedRadius;
                drawOutSide = params.drawOutside;
            }
            init();
        }

        private void init() {
            rect = new Rect();
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            srcCenter = new PointF();
            clipPath = new Path();
            drawRadius = normalRadius;
            realTimeDrawOutside = drawOutSide;
            initShowAnim();
            initCheckAnim();
        }

        void layout(int l, int t, int r, int b) {
            rect.set(l, t, r, b);
            if (oldOrientation == orientation) {
                switch (orientation) {
                    case VERTICAL_DOWN:
                    case VERTICAL_UP:
                        srcCenter.set(srcCenter.x, (b + t) / 2);
                        break;
                    case HORIZONTAL_RIGHT:
                    case HORIZONTAL_LEFT:
                        srcCenter.set((r + l) / 2, srcCenter.y);
                        break;
                }
            } else {
                srcCenter.set((r + l) / 2, (b + t) / 2);
            }
            oldOrientation = orientation;
        }

        void draw(Canvas canvas) {
            if (drawInSide) {
                mPaint.setXfermode(null);
                if (drawable != null) {
                    canvas.save();
                    clipPath.reset();
                    clipPath.addCircle(srcCenter.x, srcCenter.y, drawRadius, Path.Direction.CCW);
                    canvas.clipPath(clipPath, Region.Op.REPLACE);
                    drawable.setBounds(
                            (int) (srcCenter.x - drawRadius),
                            (int) (srcCenter.y - drawRadius),
                            (int) (srcCenter.x + drawRadius),
                            (int) (srcCenter.y + drawRadius)
                    );
                    drawable.draw(canvas);
                    canvas.restore();
                } else {
                    if (pressed) {
                        mPaint.setColor(pressedColor);
                    } else {
                        mPaint.setColor(color);
                    }
                    mPaint.setStyle(Paint.Style.FILL);
                    canvas.drawCircle(srcCenter.x, srcCenter.y, drawRadius, mPaint);
                }
            }

            if (realTimeDrawOutside) {
                mPaint.setColor(outSideColor);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeWidth(outSideWidth);
                float r = fixedOutsideCircle ? normalRadius : drawRadius;
                r += outSideWidth>>1;
                if (outSideColor == Color.TRANSPARENT) {
                    if (mode == null) {
                        mode = new PorterDuffXfermode(PorterDuff.Mode.SRC);
                        setLayerType(LAYER_TYPE_SOFTWARE, null);
                    }
                    mPaint.setXfermode(mode);
                }
                canvas.drawCircle(srcCenter.x, srcCenter.y, r, mPaint);
            }
        }

        private void initShowAnim() {
            if (showAnim == null) {
                showAnim = new ValueAnimator();
                showAnim.addUpdateListener(showAnimUpdateListener);
            }
            if (interpolator != null)
                showAnim.setInterpolator(interpolator);
            showAnim.setDuration(showAnimDuration);
        }

        private final ValueAnimator.AnimatorUpdateListener showAnimUpdateListener =
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        final float value = (float) animation.getAnimatedValue();
                        drawRadius = value;
                        noticeListener(false);
                    }
                };

        void show(boolean anim) {
            drawInSide = true;
            realTimeDrawOutside = drawOutSide;
            if (!anim) {
                drawRadius = normalRadius;
                noticeListener(false);
            } else {
                initShowAnim();
                if (!checked)
                    showAnim.setFloatValues(0, normalRadius);
                else {
                    showAnim.setFloatValues(0, checkedRadius);
                }
                showAnim.start();
            }
        }

        void hide(boolean anim) {
            if (!anim) {
                realTimeDrawOutside = false;
                drawInSide = false;
                noticeListener(false);
            } else {
                initShowAnim();
                if (!checked)
                    showAnim.setFloatValues(normalRadius, 0);
                else
                    showAnim.setFloatValues(checkedRadius, 0);
                showAnim.start();
            }
        }

        void setChecked(boolean checked, boolean anim) {
            if (checkAnim != null && checkAnim.isRunning()) {
                checkAnim.end();
            }
            this.checked = checked;
            noticeChecked();
            if (anim) {
                initCheckAnim();
                checkAnim.start();
            } else {
                drawRadius = checked ? checkedRadius : normalRadius;
                noticeListener(true);
            }
        }

        void toggle(boolean anim) {
            setChecked(!checked, anim);
        }

        private void initCheckAnim() {
            if (checkAnim == null) {
                checkAnim = new ValueAnimator();
                checkAnim.addUpdateListener(checkAnimUpdateListener);
                checkAnim.addListener(checkListener);
            }
            if (interpolator != null)
                checkAnim.setInterpolator(interpolator);
            checkAnim.setDuration(checkAnimDuration);
            if (checked)
                checkAnim.setFloatValues(normalRadius, checkedRadius);
            else
                checkAnim.setFloatValues(checkedRadius, normalRadius);
        }

        private ValueAnimator.AnimatorUpdateListener checkAnimUpdateListener =
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        final float value = (float) animation.getAnimatedValue();
                        drawRadius = value;
                        noticeListener(true);
                    }
                };

        private AnimatorListenerAdapter checkListener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                inCheckAnim = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                inCheckAnim = false;
            }
        };

        private void noticeChecked() {
            if (onCheckedListener != null)
                onCheckedListener.onChecked(this, checked);
        }

        private void noticeListener(boolean needLayout) {
            if (listener != null)
                listener.needInvalidate(needLayout);
        }

        @Override
        public int getColor() {
            if (drawable != null && drawable instanceof ColorDrawable) {
                return ((ColorDrawable) drawable).getColor();
            }
            return color;
        }

        @Override
        public void setColor(int color) {
            if (drawable != null && drawable instanceof ColorDrawable) {
                ((ColorDrawable) drawable).setColor(color);
            } else
                this.color = color;
        }

        @Override
        public Drawable getDrawable() {
            return drawable;
        }

        @Override
        public void setDrawable(Drawable drawable) {
            this.drawable = drawable;
        }

        @Override
        public boolean isChecked() {
            return checked;
        }

        @Override
        public int getIndex() {
            return index;
        }
    }

    /**
     * 存放{@link AnimCheckableView}的一些参数
     */
    private static class ParamsHolder {
        Rect padding = new Rect();
        int outsideWidth;
        int normalRadius;
        int checkedRadius;
        int color;
        int pressedColor = 0;
        int outsideColor;
        boolean drawOutside = true;
        Drawable drawable;
    }

    /**
     * {@link AnimCheckableView}内部请求刷新
     */
    private static interface RequestInvalidateListener {
        /**
         * 请求刷新
         *
         * @param needLayout 是否需要重新计算位置
         */
        void needInvalidate(boolean needLayout);
    }

    /**
     * {@link AnimCheckableView#onCheckedListener}
     * 给这个view提供回调
     */
    private static interface OnCheckedListener {
        void onChecked(AnimCheckableView v, boolean checked);
    }

    private class ShowAnimRunnable implements Runnable {

        private int index;

        private boolean show = true;

        ShowAnimRunnable(int i) {
            index = i;
        }

        public void setShow(boolean show) {
            this.show = show;
        }

        @Override
        public void run() {
            AnimCheckableView v = children.get(index);
            if (v != null) {
                if (show)
                    v.show(true);
                else
                    v.hide(true);
            }
        }
    }

    private class HideRunnable implements Runnable {

        @Override
        public void run() {
            if (visibility == GONE || visibility == INVISIBLE) {
                AnimCheckableGroupView.super.setVisibility(visibility);
            }
        }
    }

    @IntDef({VERTICAL_DOWN, VERTICAL_UP, HORIZONTAL_LEFT, HORIZONTAL_RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface OrientationInt {
    }

    /**
     * {@link #childCheckListener}
     * 给外部提供回调
     */
    public interface ChildCheckListener {
        void onChecked(IAnimView v, boolean checked, int pos);
    }
}
