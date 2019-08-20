package com.mm.recorduisdk.moment;

import android.support.annotation.WorkerThread;

import com.mm.recorduisdk.bean.CommonMomentFaceBean;

import java.io.File;


/**
 * Created by chenwangwang on 2018/3/26.
 * 定义了时刻变脸的数据获取接口的类
 * @param <T> 这个是你的数据类型，每个变脸模块的数据bean可能都不一样。
 */
public abstract class MomentFaceDataProvider<T extends CommonMomentFaceBean> {

    /**
     * 从缓存中读取
     */
    protected abstract T getFromCache();

    /**
     * 数据是否过期
     */
    protected abstract boolean isDataOutOfDate(T data);

    /**
     * 从服务器拉取数据
     */
    protected abstract T getFromServer();

    /**
     * 保存数据到缓存
     */
    protected abstract void saveToCache(T data);

    /**
     * 拉取数据，请在工作线程调用
     */
    @WorkerThread
    public T fetchData() {
        // 这里锁的是当前对象，所以需要把当前对象做成单利。
        synchronized (getClass()) {
            // 1. 从缓存获取
            T data = null;

            try {
                data = getFromCache();
            } catch (Exception e) {
            }

            // 2. 如果缓存失效，从服务器获取
            if (isDataOutOfDate(data)) {
                data = getFromServer();
                // 3. 如果服务器数据可用，存储到缓存
                if (data != null) {
                    try {
                        saveToCache(data);
                    } catch (Exception e) {
                    }
                    data.setFromServer(true);
                }
            }

            return data;
        }
    }

    /**
     * 复写该方法来设置缓存的文件
     */
    protected File getCacheFile() {
        return null;
    }
}
