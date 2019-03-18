package com.mm.sdkdemo.bean.element;

import android.content.Context;
import android.view.View;

/**
 * Created by chenwangwang on 2018/3/7.     <br/>
 *                                          <br/>
 * 比fragment还小的单元，管理一个视图结构，相对较轻。<br/>
 *                                          <br/>
 * 《《你给我一个View，我给你一个牛逼的界面》》     <br/>
 */
public class Element<T extends View> {

    private T mView;
    private IElementContext mElementContext;
    private boolean isDestroy;

    public Element(T view) {
        if (view == null) {
            throw new RuntimeException("view can not be null");
        }
        mView = view;
    }

    void attach(IElementContext elementContext) {
        this.mElementContext = elementContext;
    }

    /**
     * 获取当前ElementManager里注册的其他element对象
     * @param eleClass 其他element的class对象
     * @param <N> Element类型
     * @return element对象
     */
    public <N extends Element> N getElement(Class<N> eleClass) {
        return mElementContext.getElement(eleClass);
    }

    /**
     * 根据ID查找View
     * @param id id
     * @return view对象
     */
    public View findViewById(int id) {
        return mView.findViewById(id);
    }

    protected Context getContext() {
        return mElementContext.getContext();
    }

    public T getView() {
        return mView;
    }

    protected void onCreate() {

    }

    protected void onDestroy() {
        this.isDestroy = true;
    }

    public boolean isDestroy() {
        return isDestroy;
    }

    protected void onPause() {

    }

    protected void onResume() {

    }
}
