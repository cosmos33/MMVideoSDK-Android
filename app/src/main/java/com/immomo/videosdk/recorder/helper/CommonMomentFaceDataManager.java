package com.immomo.videosdk.recorder.helper;

import com.immomo.videosdk.bean.MomentFace;
import com.immomo.videosdk.bean.MomentFaceDataProvider;
import com.immomo.videosdk.widget.MomentFaceDataManager;

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
