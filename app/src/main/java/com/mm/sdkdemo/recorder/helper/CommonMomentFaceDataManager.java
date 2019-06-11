package com.mm.sdkdemo.recorder.helper;

import com.mm.sdkdemo.bean.MomentFace;
import com.mm.sdkdemo.bean.MomentFaceDataProvider;
import com.mm.sdkdemo.widget.MomentFaceDataManager;

import java.util.List;
import java.util.Map;

/**
 * Created by chenwangwang on 2018/3/26.
 */
public class CommonMomentFaceDataManager extends MomentFaceDataManager<Map<String,List<MomentFace>>> {

    private static class Holder {
        static final CommonMomentFaceDataProvider sInstance = new CommonMomentFaceDataProvider();
    }

    @Override
    protected MomentFaceDataProvider<Map<String,List<MomentFace>>> getDataProvider() {
        return Holder.sInstance;
    }
}
