package com.mm.sdkdemo.recorder.helper;

import com.mm.sdkdemo.api.MoApi;
import com.mm.sdkdemo.bean.MomentFace;
import com.mm.sdkdemo.bean.MomentFaceDataProvider;

import java.util.List;
import java.util.Map;

/**
 * Created by chenwangwang on 2018/3/28   <br/>
 * 快聊面板的数据获取实现类
 */
public class CommonMomentFaceDataProvider extends MomentFaceDataProvider<Map<String, List<MomentFace>>> {

    @Override
    protected Map<String, List<MomentFace>> getFromCache() {
        return null;
    }

    @Override
    protected boolean isDataOutOfDate(Map<String, List<MomentFace>> data) {
        return true;
    }

    @Override
    protected Map<String, List<MomentFace>>  getFromServer() {
        try {
            return MoApi.getFaceData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
