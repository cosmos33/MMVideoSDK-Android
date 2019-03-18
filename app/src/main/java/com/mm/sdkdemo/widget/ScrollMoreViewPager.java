package com.mm.sdkdemo.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.mm.sdkdemo.R;

/**
 * Created by XiongFangyu on 2017/6/7.
 *
 * 包含更多监听的Viewpager
 * 监听见 {@link OnScrollMoreListener}
 *
 * 可通过xml : smvp_move_slop 设置滑动多少距离才会触发
 * 可通过xml : smvp_scroll_more_listener(类名全称，可为空，可为错误类名)
 *  设置监听{@link #setOnScrollMoreListener(OnScrollMoreListener)}
 */
public class ScrollMoreViewPager extends ViewPager {

    private int currentPage;
    private float offset;
    private float downX;
    protected int mTouchSlop;
    private boolean doingMore = false;
    private OnScrollMoreListener onScrollMoreListener;
    private BeforeCheckEnableTouchListener beforeCheckEnableTouchListener;

    public ScrollMoreViewPager(Context context) {
        this(context, null);
    }

    public ScrollMoreViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        if (context == null || attrs == null)
            return;

        final Resources.Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(attrs,
                                                    R.styleable.ScrollMoreViewPager, 0, 0);
        TypedArray appearance = null;
        int ap = a.getResourceId(
                R.styleable.ScrollMoreViewPager_smvp_style, -1);
        if (ap != -1) {
            appearance = theme.obtainStyledAttributes(
                    ap, R.styleable.ScrollMoreViewPager);
        }
        initStyle(appearance);
        initStyle(a);
    }

    private void initStyle(TypedArray a) {
        if (a != null) {
            mTouchSlop = a.getDimensionPixelOffset(R.styleable.ScrollMoreViewPager_smvp_move_slop, mTouchSlop);
            setListenerClass(a.getString(R.styleable.ScrollMoreViewPager_smvp_scroll_more_listener));
            a.recycle();
        }
    }

    private void setListenerClass(String clz) {
        if (TextUtils.isEmpty(clz))
            return;
        try {
            Class<? extends OnScrollMoreListener> c = (Class<? extends OnScrollMoreListener>) Class.forName(clz);
            OnScrollMoreListener l = c.newInstance();
            setOnScrollMoreListener(l);
        } catch (Exception ignore) {

        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isEnabled())
            return false;
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean result = false;
        if (beforeCheckEnableTouchListener != null)
            result = beforeCheckEnableTouchListener.onTouch(ev);
        if (!isEnabled())
            return result;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                doingMore = false;
                downX = ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                PagerAdapter adapter = getAdapter();
                if (adapter.getCount() - 1 == currentPage) {
                    final float x = ev.getX();
                    if (downX - x > mTouchSlop) {
                        onScrollMore(true);
                    }
                } else if (0 == currentPage && offset == 0) {
                    final float x = ev.getX();
                    if (x - downX > mTouchSlop) {
                        onScrollMore(false);
                    }
                }
                break;
        }
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPageScrolled(int position, float offset, int offsetPixels) {
        super.onPageScrolled(position, offset, offsetPixels);
        currentPage = position;
        this.offset = offset;
    }

    public void setTouchSlop(int touchSlop) {
        this.mTouchSlop = touchSlop;
    }

    public void setOnScrollMoreListener(OnScrollMoreListener onScrollMoreListener) {
        this.onScrollMoreListener = onScrollMoreListener;
    }

    private void onScrollMore(boolean left) {
        if (doingMore)
            return;
        doingMore = true;
        if (onScrollMoreListener != null) {
            onScrollMoreListener.onScrollMore(getContext(), left);
        }
    }

    public void setBeforeCheckEnableTouchListener(BeforeCheckEnableTouchListener beforeCheckEnableTouchListener) {
        this.beforeCheckEnableTouchListener = beforeCheckEnableTouchListener;
    }

    public interface BeforeCheckEnableTouchListener {
        boolean onTouch(MotionEvent e);
    }

    public interface OnScrollMoreListener {
        /**
         * 当滑动到最右边，且手指继续往左滑动时，触发
         * 或滑动到最左边，且手指继续往右滑动时，触发
         * @param context
         * @param toLeft 手指滑动方向
         */
        void onScrollMore(Context context, boolean toLeft);
    }
}
