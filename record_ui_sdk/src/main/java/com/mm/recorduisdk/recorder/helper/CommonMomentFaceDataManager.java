package com.mm.recorduisdk.recorder.helper;

import com.mm.recorduisdk.bean.CommonMomentFaceBean;
import com.mm.recorduisdk.bean.MomentFaceDataProvider;
import com.mm.recorduisdk.moment.MomentFaceDataManager;

/**
 * Created by chenwangwang on 2018/3/26.
 */
public class CommonMomentFaceDataManager extends MomentFaceDataManager<CommonMomentFaceBean> {

    private static class Holder {
        static final CommonMomentFaceDataProvider sInstance = new CommonMomentFaceDataProvider();
    }

    @Override
    protected MomentFaceDataProvider<CommonMomentFaceBean> getDataProvider() {
        return Holder.sInstance;
    }
}
