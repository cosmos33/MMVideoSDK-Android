package com.mm.recorduisdk.recorder.sticker;

import android.graphics.PointF;

import com.momo.mcamera.mask.MaskModel;

/**
 * Created by zhu.tao on 2017/6/23.
 */

public class StickerPostionInfo {

    public MaskModel model;
    public int stickerId;
    public PointF pointF;
    public float angle;
    public float scale;

    public StickerPostionInfo(MaskModel model, int stickerId, PointF pointF, float angle, float scale) {
        this.model = model;
        this.stickerId = stickerId;
        this.pointF = pointF;
        this.angle = angle;
        this.scale = scale;
    }
}
