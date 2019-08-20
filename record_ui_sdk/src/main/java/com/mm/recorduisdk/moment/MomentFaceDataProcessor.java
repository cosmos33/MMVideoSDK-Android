package com.mm.recorduisdk.moment;

import com.mm.recorduisdk.bean.CommonMomentFaceBean;
import com.mm.recorduisdk.bean.MomentFace;

/**
 * Created by chenwangwang on 2018/3/28.
 * 对变脸数据进行加工的模板类
 */
public abstract class MomentFaceDataProcessor {

    /**
     * 数据拉取成功之后，会调用该方法，可以对数据进行加工，比如增加分类，过滤变脸数据等
     * @param data 数据
     * @param <T> {@link CommonMomentFaceBean}或者其子类
     */
    public abstract <T extends CommonMomentFaceBean> void process(T data);


    /**
     * 当变脸资源下载成功时，需要处理的逻辑
     * @param face 变脸资源信息
     * @param modelsManager models管理器
     */
    public void onMomentFaceDownloadSuccess(MomentFace face, MomentFaceModelsManager modelsManager) {

    }

}
