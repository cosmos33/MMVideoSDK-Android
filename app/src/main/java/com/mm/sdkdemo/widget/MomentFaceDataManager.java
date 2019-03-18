package com.mm.sdkdemo.widget;

import com.immomo.mmutil.task.MomoMainThreadExecutor;
import com.immomo.mmutil.task.ThreadUtils;
import com.mm.sdkdemo.bean.MomentFaceDataProcessor;
import com.mm.sdkdemo.bean.MomentFaceDataProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenwangwang on 2018/3/26.
 * 管理里的是变脸面板的数据的增删改查，其他逻辑（UI等），不要放这里。
 * @param <T> 这是当前变脸面板管理的数据bean类型
 */
public abstract class MomentFaceDataManager<T> {

    // 数据提供者接口
    private volatile MomentFaceDataProvider<T> mDataProvider;

    // 变脸数据
    private T mData;

    // 对数据进行加工的对象集合
    private List<MomentFaceDataProcessor> mDataProcessors;

    /**
     * 拉取（本地/服务器）数据
     * @param callback 数据拉取结果
     */
    public void loadData(final FaceDataLoadCallback<T> callback) {
        if (callback == null) {
            return;
        }
        if (mData != null) {
            invokeFaceDataLoadCallback(true, callback);
            return;
        }
        // 异步拉取
        ThreadUtils.execute(ThreadUtils.TYPE_INNER, new Runnable() {
            @Override
            public void run() {
                MomentFaceDataProvider<T> dataProvider = getDataProviderInternal();

                // 拉取数据
                T data = dataProvider.fetchData();

                mData = data;

                // 回调请求结果
                invokeFaceDataLoadCallback(mData != null, callback);
            }
        });
    }

    /**
     * 回调变脸数据拉取结果，回调方法在主线程调用。
     * @param success true拉取成功，false失败
     * @param callback 回调接口对象
     */
    private void invokeFaceDataLoadCallback(final boolean success, final FaceDataLoadCallback<T> callback) {
        MomoMainThreadExecutor.post(new Runnable() {
            @Override
            public void run() {
                if (success) {
                    if (mData != null) {
                        callback.onFaceDataLoadSuccess(mData);
                    }
                } else {
                    callback.onFaceDataLoadFailed();
                }
            }
        });
    }

    /**
     * 添加数据加工器
     * @param dataProcessor 数据加工器
     */
    public void addDataProcessor(MomentFaceDataProcessor dataProcessor) {
        if (mDataProcessors == null) {
            mDataProcessors = new ArrayList<>();
        }
        mDataProcessors.add(dataProcessor);
    }

    public List<MomentFaceDataProcessor> getDataProcessors() {
        return mDataProcessors;
    }

    /**
     * 获取数据，可能为空，需要确保已经拉取完数据
     * @return 可能为null
     */
    public T getData() {
        return mData;
    }

    private MomentFaceDataProvider<T> getDataProviderInternal() {
        if (mDataProvider == null) {
            mDataProvider = getDataProvider();
        }
        return mDataProvider;
    }

    /**
     * 创建数据获取获取对象
     */
    protected abstract MomentFaceDataProvider<T> getDataProvider();

    /**
     * 变脸资源列表数据加载结果回调接口
     */
    public interface FaceDataLoadCallback<N> {
        /**
         * 成功
         */
        void onFaceDataLoadSuccess(N data);

        /**
         * 失败
         */
        void onFaceDataLoadFailed();
    }

}
