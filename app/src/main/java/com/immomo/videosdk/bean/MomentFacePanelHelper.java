package com.immomo.videosdk.bean;

import com.momo.mcamera.mask.MaskModel;

/**
 * Created by chenwangwang on 2018/3/29   <br/>
 * 配置变脸面板的帮助类
 */
public abstract class MomentFacePanelHelper implements MaskLoadCallback {

    /**
     * 选中某一个之后变脸资源后，是否需要进行下载
     * @return true 需要下载，false 不需要
     */
    protected boolean loadMaskWhenClick(MomentFace face) {
        // 默认不自动下载
        return false;
    }

    @Override
    public void onMaskLoadSuccess(MaskModel maskModel, MomentFace face) {

    }

    @Override
    public void onMaskLoadFailed(MomentFace face) {

    }

    /**
     * 选中某一个资源的时候回调，该回调发生于{@link #loadMaskWhenClick(MomentFace)}之前
     * @param face 素材信息
     *
     */
    protected abstract void onItemSelected(MomentFace face);

    /**
     * 清空变脸时回调
     */
    protected abstract void onClear();
}
