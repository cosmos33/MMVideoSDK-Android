package com.mm.sdkdemo.recorder.helper;

import com.mm.sdkdemo.api.MoApi;
import com.mm.sdkdemo.bean.MomentFace;
import com.mm.sdkdemo.bean.MomentFaceDataProvider;

import java.util.List;

/**
 * Created by chenwangwang on 2018/3/28   <br/>
 * 快聊面板的数据获取实现类
 */
public class CommonMomentFaceDataProvider extends MomentFaceDataProvider<List<MomentFace>> {

    @Override
    protected List<MomentFace> getFromCache() {
        return null;
    }

    @Override
    protected boolean isDataOutOfDate(List<MomentFace> data) {
        return true;
    }

    @Override
    protected List<MomentFace> getFromServer() {
        try {
            return MoApi.getFaceData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
