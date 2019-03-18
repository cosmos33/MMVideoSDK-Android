package com.mm.sdkdemo.bean;

import com.momo.mcamera.mask.MaskModel;

/**
 * Created by chenwangwang on 2018/3/29   <br/>
 * 变脸模型信息加载到内存回调
 */
public interface MaskLoadCallback {

    /**
     * 加载成功
     * @param maskModel 加载到内存的模型信息
     * @param face 变脸信息
     */
    void onMaskLoadSuccess(MaskModel maskModel, MomentFace face);

    /**
     * 加载失败
     * @param face 变脸信息
     */
    void onMaskLoadFailed(MomentFace face);

}
