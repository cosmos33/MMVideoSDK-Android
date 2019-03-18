package com.mm.sdkdemo.base.cement;

import android.support.annotation.CallSuper;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author xudong
 * @since 2017/2/9
 */
public abstract class CementLoadMoreModel<VH extends CementViewHolder> extends
        CementModel<VH> {
    public static final int START = 0;
    public static final int COMPLETE = 1;
    public static final int FAILED = 2;

    @IntDef({START, COMPLETE, FAILED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LoadMoreState {
    }

    @LoadMoreState
    private int state = COMPLETE;

    public final void setState(@LoadMoreState int state) {
        this.state = state;
    }

    @CallSuper
    @Override
    public void bindData(@NonNull VH holder) {
        switch (state) {
            case START: {
                onLoadMoreStart(holder);
                break;
            }
            case COMPLETE: {
                onLoadMoreComplete(holder);
                break;
            }
            case FAILED: {
                onLoadMoreFailed(holder);
                break;
            }
        }
    }

    /**
     * before loading, show "loading..."
     */
    public abstract void onLoadMoreStart(@NonNull VH holder);

    /**
     * after loading and showing data, show "click to load" for next loading
     */
    public abstract void onLoadMoreComplete(@NonNull VH holder);

    /**
     * after loading failed, show "click to retry"
     */
    public abstract void onLoadMoreFailed(@NonNull VH holder);
}
