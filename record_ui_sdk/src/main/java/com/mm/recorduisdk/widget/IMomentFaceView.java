package com.mm.recorduisdk.widget;

import com.mm.recorduisdk.bean.CommonMomentFaceBean;
import com.mm.recorduisdk.bean.IMomentFacePresenter;
import com.mm.recorduisdk.bean.MomentFace;
import com.mm.recorduisdk.moment.MomentFaceDataManager;

/**
 * Created by chenwangwang on 2018/3/29   <br/>
 * 定义时刻变脸的UI接口
 */
public interface IMomentFaceView extends MomentFaceDataManager.FaceDataLoadCallback<CommonMomentFaceBean> {

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
