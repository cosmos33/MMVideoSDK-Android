package com.mm.recorduisdk.bean.element;

import android.content.Context;
import android.util.ArrayMap;

import java.util.List;

/**
 * Created by chenwangwang on 2018/3/8.
 */
public class ElementManager {
    private Context mContext;
    private List<Element> mElements;
    private ArrayMap<Class<? extends Element>, Element> mClassElementArrayMap = new ArrayMap<>();

    /**
     * 为Element提供的上下文环境，提供公共的接口
     */
    private IElementContext mElementContext = new IElementContext() {
        @Override
        public Context getContext() {
            return mContext;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T extends Element> T getElement(Class<T> eleClass) {
            return (T) mClassElementArrayMap.get(eleClass);
        }
    };

    public ElementManager(Context context, List<Element> elements) {
        if (elements == null || elements.isEmpty()) {
            throw new RuntimeException("elements can not be null");
        }
        this.mContext = context;
        mElements = elements;
        for (Element element : elements) {
            mClassElementArrayMap.put(element.getClass(), element);
            element.attach(mElementContext);
        }
    }

    public List<Element> getElements() {
        return mElements;
    }

    public void onCreate() {
        for (Element element : mElements) {
            element.onCreate();
        }
    }

    public void onDestroy() {
        for (Element element : mElements) {
            element.onDestroy();
        }
    }

    public void onPause() {
        for (Element element : mElements) {
            element.onPause();
        }
    }

    public void onResume() {
        for (Element element : mElements) {
            element.onResume();
        }
    }
}
