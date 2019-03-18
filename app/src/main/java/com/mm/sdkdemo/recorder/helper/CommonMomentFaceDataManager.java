package com.mm.sdkdemo.recorder.helper;

import com.mm.sdkdemo.bean.MomentFace;
import com.mm.sdkdemo.bean.MomentFaceDataProvider;
import com.mm.sdkdemo.widget.MomentFaceDataManager;

import java.util.List;

/**
 * Created by chenwangwang on 2018/3/26.
 */
public class CommonMomentFaceDataManager extends MomentFaceDataManager<List<MomentFace>> {

    private static class Holder {
        static final CommonMomentFaceDataProvider sInstance = new CommonMomentFaceDataProvider();
    }

    @Override
    protected MomentFaceDataProvider<List<MomentFace>> getDataProvider() {
        return Holder.sInstance;
    }
}
