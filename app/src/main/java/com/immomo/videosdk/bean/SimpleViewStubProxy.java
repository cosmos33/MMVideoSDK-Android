package com.immomo.videosdk.bean;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import java.util.LinkedHashSet;
import java.util.Set;

import androidx.annotation.IdRes;

/**
 * Created by chenwangwang on 2018/1/30.    <br/>
 * 简单了做了一个包装，只有inflate事件回调。 <br/><br/>
 * 使用时需要注意以下几点             <br/>
 * 1. 设置一些初始化的属性，之前是放在被代理view初始化完成之后，比如setClickListener等，               <br/>
 *      这些属性请放在View被inflate的时候，{@link #addInflateListener(OnInflateListener)}       <br/>
 * 2. 需要让View可见，并且改变其一些属性，或者调用其API时，直接使用{@link #getStubView()}获取被代理View并使用      <br/>
 * 3. 请注意，如果只是初始化一些属性，没必要直接调用{@link #getStubView()}，这样会导致被懒加载View提前被初始化，就没起到懒加载的作用了。    <br/>
 * 4. 想要知道View有没有被加载过，可以使用{@link #isInflate()}方法判断，比如需要设置View不可见时，如果根本就没有Inflate，就没必要调用{@link #getStubView()}。<br/>
 * 5. 如果需要获取其子View，可以使用{@link #getView(int)}        <br/>
 */
public class SimpleViewStubProxy<T extends View> {

    private ViewStub mViewStub;
    private T mView;
    private Set<OnInflateListener> mOnInflateListeners;
    private SparseArray<View> mViews;

    public SimpleViewStubProxy(ViewStub viewStub) {
        if (viewStub == null) {
            throw new RuntimeException("ViewStub instance can not be null");
        }
        mViewStub = viewStub;
    }

    /**
     * 获取懒加载视图节点
     */
    public T getStubView() {
        if (mViewStub != null) {
            mView = (T) mViewStub.inflate();
            mViewStub = null;
            onInflate();
        }
        return mView;
    }

    /**
     * 根据ID获取懒加载View下的某一个视图节点
     * @param viewId 视图节点ID
     * @return 视图节点对象，可能为空
     */
    public View getView(@IdRes int viewId) {
        View viewById;
        if (mViews != null) {
            viewById = mViews.get(viewId);
            if (viewById != null) {
                return viewById;
            }
        }
        viewById = getStubView().findViewById(viewId);
        if (viewById != null) {
            if (mViews == null) {
                mViews = new SparseArray<>();
            }
            mViews.put(viewId, viewById);
        }
        return viewById;
    }

    protected void onInflate() {
        if (mOnInflateListeners == null) {
            return;
        }
        for (OnInflateListener onInflateListener : mOnInflateListeners) {
            onInflateListener.onInflate(mView);
        }
    }

    /**
     * 判断懒加载试图是否已经被加载到内存
     * @return true 已经加载， false 还未加载
     */
    public boolean isInflate() {
        return mViewStub == null;
    }

    /**
     * 添加一个懒加载视图被inflate的时候监听器
     * @param onInflateListener listener
     */
    public void addInflateListener(OnInflateListener<T> onInflateListener) {
        if (onInflateListener == null) {
            return;
        }
        // invoke onInflate method if it was inflated
        if (isInflate()) {
            onInflateListener.onInflate(mView);
            return;
        }
        if (mOnInflateListeners == null) {
            mOnInflateListeners = new LinkedHashSet<>();
        }
        mOnInflateListeners.add(onInflateListener);
    }

    public ViewGroup.LayoutParams getLayoutParams() {
        if (mViewStub != null) {
            return mViewStub.getLayoutParams();
        }
        return mView.getLayoutParams();
    }

    public void setLayoutParams(ViewGroup.LayoutParams params) {
        if (mViewStub != null) {
            mViewStub.setLayoutParams(params);
        } else {
            mView.setLayoutParams(params);
        }
    }

    public void setVisibility(int visibility) {
        if (visibility == View.GONE) {
            if (isInflate()) {
                getStubView().setVisibility(View.GONE);
            }
        } else {
            getStubView().setVisibility(visibility);
        }
    }

    public int getVisibility() {
        if (isInflate()) {
            return getStubView().getVisibility();
        } else {
            return View.GONE;
        }
    }

    public interface OnInflateListener<T extends View> {
        void onInflate(T view);
    }

}
