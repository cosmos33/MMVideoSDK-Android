package com.mm.sdkdemo.widget;

import com.mm.sdkdemo.bean.IMomentFacePresenter;
import com.mm.sdkdemo.bean.MomentFace;

import java.util.List;
import java.util.Map;

/**
 * Created by chenwangwang on 2018/3/29   <br/>
 * 定义时刻变脸的UI接口
 */
public interface IMomentFaceView extends MomentFaceDataManager.FaceDataLoadCallback<Map<String, List<MomentFace>>> {

    void attachPresenter(IMomentFacePresenter presenter);

    void showLoadingView();

    /**
     * 资源下载失败之后，请求UI弹出提示语
     */
    void showDownLoadFailedTip();

    /**
     * 通知UI刷新对应的变脸项
     */
    void notifyItemChanged(MomentFace face);

    /**
     * 是否选中的某一项
     * @return true 选中了，false 没有选中
     */
    boolean hasSelectItem();
}
