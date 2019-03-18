package com.mm.sdkdemo.bean.element;

/**
 * Created by chenwangwang on 2018/3/8.
 */

import android.content.Context;

/**
 * Created by chenwangwang on 2018/3/5.
 * {@link Element}的上下文
 */
interface IElementContext {

    /**
     * 获取上下文
     */
    Context getContext();

    /**
     * 获取当前ElementManager里注册的其他element对象
     * @param eleClass 其他element的class对象
     * @param <T> Element类型
     * @return element对象
     */
    <T extends Element> T getElement(Class<T> eleClass);

}
