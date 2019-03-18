package com.mm.sdkdemo.utils;

import android.os.Handler;

import java.lang.ref.WeakReference;

/**
 * 使用弱引用方式的Handler，在界面中使用Handler时，务必继承此类，防止内存泄漏
 * 举例如下
 * <p/>
 * private static class MyHandler extends UIHandler<MainChatRoomTabFragment>{
 * public MyHandler(MainChatRoomTabFragment cls){
 * super(cls);
 * }
 * <p/>
 * ...
 * }
 * 确保MyHandler是static类，不直接调用MainChatRoomTabFragment 里面的方法,如果需要使用Fragment的方法，通过getRef()来获得，并且要做非空判断；
 * <p/>
 * 在Fragment或者Activity中使用Handler时,使用如下方式，传入Fragment的引用即可
 * private MyHandler handler=new MyHandler(MainChatRoomTabFragment.this);
 * <p/>
 * <p/>
 * Project momodev
 * Package com.mm.momo
 * Created by tangyuchun on 4/9/15.
 */
public class UIHandler<T> extends Handler {
    private WeakReference<T> ref;

    public UIHandler(T cls) {
        ref = new WeakReference<T>(cls);
    }

    /**
     * 获得实例的引用，有可能为空，务必做非空判断
     *
     * @return
     */
    public T getRef() {
        return ref != null ? ref.get() : null;
    }
}
