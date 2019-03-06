package com.immomo.videosdk.recorder.helper;

import android.content.Context;
import android.text.TextUtils;

import com.core.glcore.util.BodyLandHelper;
import com.core.glcore.util.JsonUtil;
import com.immomo.mdlog.MDLog;
import com.immomo.mediasdk.filters.FilterChooser;
import com.immomo.videosdk.bean.MomentFace;
import com.momo.mcamera.mask.BeautyFace;
import com.momo.mcamera.mask.LookUpModel;
import com.momo.mcamera.mask.Mask;
import com.momo.mcamera.mask.MaskModel;
import com.momo.mcamera.mask.Sticker;
import com.momo.mcamera.mask.StickerAdjustFilter;
import com.momo.xeengine.XE3DEngine;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by XiongFangyu on 2017/5/10.
 */

public class VideoFaceUtils {

    public static final int INDEX_BIG_EYE = 1;
    public static final int INDEX_THIN_FACE = 2;
    public static final int INDEX_WRAP_TYPE = 0;

    public static final int INDEX_SKIN_SMOOTH = 3;
    public static final int INDEX_FACE_LIGHT = 4;

    public static final int INDEX_EXPRESSION_DETECT = 5;

    public static final int INDEX_EYECLASSIC_SWITCH = 6;

    private static float[][] thinFaceSet = new float[][]{{0.15f, 0.15f}, {0.3f, 0.3f}, {0.45f, 0.45f}, {0.6f, 0.6f}, {0.8f, 0.8f}, {0.0f, 0.0f}};

    public static MaskModel readMaskModel(Context context, File dir) {
        return readMaskModel(context, dir, true);//回退3D引擎代码
    }

    public static MaskModel readMaskModel(Context context, File dir, boolean newEngine) {
        if (!dir.exists()) {
            return null;
        }
        String jsonPath = dir.getPath() + "/" + "params.txt";
        String jsonStr = JsonUtil.getInstance().jsonStringFromFile(context, jsonPath);
        if (!TextUtils.isEmpty(jsonStr)) {
            MaskModel maskModel = null;
            try {
                maskModel = JsonUtil.getInstance().fromJson(jsonStr, MaskModel.class);
            } catch (Throwable e) {
                MDLog.printErrStackTrace("VideoFaceUtils", e);
                return null;
            }
            if (maskModel.getStickers() == null) {
                maskModel.setStickers(new ArrayList<Sticker>());
            }
            if (null != maskModel && !TextUtils.isEmpty(maskModel.getSound())) {
                maskModel.setSoundPath(dir.getPath() + "/" + maskModel.getSound());
            }
            if (null != maskModel.getLookUpFilters()) {
                /**
                 * Mask 转换成Sticker
                 */
                for (LookUpModel lookupFilter : maskModel.getLookUpFilters()) {
                    lookupFilter.setLookupPath(dir.getPath() + "/" + lookupFilter.getFolder() + "/" + "lookup.png");
                    Sticker sticker = new Sticker();
                    sticker.setStickerType(Sticker.FACE_LOOK_UP_TYPE);
                    sticker.setTriggerType(lookupFilter.getTriggerType());
                    sticker.setLookUpModel(lookupFilter);
                    sticker.setHiddenTriggerType(lookupFilter.getHiddenTriggerType());
                    maskModel.getStickers().add(0, sticker);
                }
            }
            if (null != maskModel.getMasks()) {
                /**
                 * Mask 转换成Sticker
                 */
                for (Mask mask : maskModel.getMasks()) {
                    String pointsPath = dir.getPath() + "/" + mask.getFolder() + "/" + "metadata.json";
                    String jsonPointsStr = JsonUtil.getInstance().jsonStringFromFile(context, pointsPath);
                    Mask mask1 = JsonUtil.getInstance().fromJson(jsonPointsStr, Mask.class);
                    if (mask1 == null || mask1.landmarks == null) continue;
                    mask.landmarks = mask1.landmarks;
                    mask.setTexturePath(dir.getPath() + "/" + mask.getFolder() + "/" + "texture.png");
                    Sticker sticker = new Sticker();
                    sticker.setStickerType(Sticker.FACE_MASK_TYPE);
                    sticker.setTriggerType(mask.getTriggerType());
                    sticker.setHiddenTriggerType(mask.getHiddenTriggerType());
                    sticker.setMask(mask);
                    maskModel.getStickers().add(0, sticker);
                }
            }

            if (null != maskModel.getDistortionList()) {
                for (Mask mask : maskModel.getDistortionList()) {
                    String pointsPath = dir.getPath() + "/" + mask.getFolder() + "/" + "metadata.json";
                    //                    String jsonPointsStr = JsonUtil.getInstance().jsonStringFromFile(context, pointsPath);
                    //                    Mask mask1 = JsonUtil.getInstance().fromJson(jsonPointsStr, Mask.class);
                    //                    if (mask1 == null || mask1.landmarks == null) continue;
                    //                    mask.landmarks = mask1.landmarks;
                    //                    mask.setTexturePath(dir.uri2Path() + "/" + mask.getFolder() + "/" + "texture.png");
                    Sticker sticker = new Sticker();
                    //                    sticker.setStickerType(Sticker.FACE_MASK_TYPE);
                    sticker.setTriggerType(mask.getTriggerType());
                    sticker.setHiddenTriggerType(mask.getHiddenTriggerType());
                    sticker.setMask(mask);
                    maskModel.getStickers().add(0, sticker);
                    maskModel.setFaceScale(mask.getStrength());
                    maskModel.setFaceFacialFeatureScale(mask.getStrengthB());
                    maskModel.setWrapType(mask.getType());
                }

            }

            if (maskModel.getAdditionalInfo() != null) {
                BodyLandHelper.setUseBodyLand(maskModel.getAdditionalInfo().isBodyDetectEnable());
            }
            boolean has3dEngineType = false;
            for (Sticker sticker : maskModel.getStickers()) {
                sticker.setFrameRate(maskModel.getFrameRate());
                sticker.setImageFolderPath(dir.getPath());

                if (!TextUtils.isEmpty(sticker.getLayerType()) && sticker.getLayerType().equals(Sticker.FACE_3D_MASK_TYPE)) {
                    has3dEngineType = true;
                    maskModel.setXengineEsPath(dir.getPath().substring(0, dir.getPath().lastIndexOf('/')));
                }
                if (sticker.getAdditionalInfo() != null) {
                    int mode = sticker.getAdditionalInfo().getSoundPitchShift();
                    if (mode != 0) {
                        maskModel.setSoundPitchMode(mode);
                    }
                }
            }
            if (has3dEngineType) {
                XE3DEngine.getInstance().configlibraryPath(MomentFaceFileUtil.getMomentFaceHomeDir().getPath());
            }
            return maskModel;
        }
        return null;
    }

    public static MaskModel readMaskModel(Context context, MomentFace face) {
        if (face == null) {
            return null;
        }
        //        if (face.isFaceRig()) {
        //            return readMaskModel(context, new File(MomentFaceFileUtil.getMomentFaceHomeDir(), "girl"));
        //        }
        return readMaskModel(context, MomentFaceFileUtil.getFaceResourceDir(face));
    }

    public static float[] addMaskModel(MaskModel maskModel, StickerAdjustFilter mStickerAdjustFilter,
                                       FilterChooser mFilterChooser, float skinLevel, int modelType, boolean openGestureDetector) {
        int wrapType = maskModel.getWrapType();
        float bigEye;
        float thinFace;
        float skinSmooth = -1;
        float skinWhiten = -1;
        float expressionDetect = -1;
        float eyeClassicSwitch = -1;
        if (wrapType == 1) {
            BeautyFace beautyFace = maskModel.getBeautyFace();
            if (beautyFace != null) {

                //                float bigEyeDefault = thinFaceSet[1][0];
                //                float thinFaceDefault = thinFaceSet[1][1];
                bigEye = beautyFace.getBigEyeValue();
                thinFace = beautyFace.getThinFaceValue();
                //                bigEye = bigEye <= 1 ? bigEye < 0 ? bigEyeDefault : bigEye : 1;
                //                thinFace = thinFace <= 1 ? thinFace < 0 ? thinFaceDefault : thinFace : 1;
                wrapType = 9;

                skinSmooth = beautyFace.getSkinSmoothingValue();
                skinWhiten = beautyFace.getSkinWhitenValue();
            } else {
                wrapType = 9;
                bigEye = -1;
                thinFace = -1;
            }
        } else {
            bigEye = maskModel.getFaceFacialFeatureScale();
            thinFace = maskModel.getFaceScale();
        }

        if (mStickerAdjustFilter != null) {

            mStickerAdjustFilter.stopGestureDetect();
            if (openGestureDetector) {
                for (Sticker sticker : maskModel.getStickers()) {
                    if (sticker.getObjectTriggerType() != null) {
                        //只有objectTriggerType不为null才开启gestureDetect。
                        mStickerAdjustFilter.startGestureDetect(sticker.isUseHandGestureDetectNewVersion(), sticker.getHandGestureType());
                        break;
                    }
                }
            }

            for (Sticker sticker : maskModel.getStickers()) {
                if (sticker.getTriggerType() == 1024 || sticker.getTriggerType() == 512 || (!TextUtils.isEmpty(sticker.getLayerType()) && sticker.getLayerType().equals(Sticker.FACE_3D_MASK_TYPE))) {
                    expressionDetect = 1;
                    break;
                } else if (sticker.getTriggerType() == 8) {
                    //眨眼开关
                    eyeClassicSwitch = 1;
                    break;
                }
            }

            mStickerAdjustFilter.setBigEye(bigEye);
            mStickerAdjustFilter.setThinFace(thinFace);
            //开启播放声音
            mStickerAdjustFilter.setEnableSound(true);
            maskModel.setModelType(modelType);
            mStickerAdjustFilter.addMaskModel(maskModel);
        }
        //如果没有配置美颜参数，则需要恢复默认的参数值得
        if (mFilterChooser != null)
            mFilterChooser.setSkinLevel(skinLevel);
        return new float[]{wrapType, bigEye, thinFace, skinSmooth, skinWhiten, expressionDetect, eyeClassicSwitch};
    }

    /**
     * 添加手势model
     *
     * @param mStickerAdjustFilter filter
     * @param maskModel            maskModel
     * @param gestureTriggerType   手势类型 如 {@link Sticker#GESTURE_TYPE_HEART}
     */
    public static void addGestureModel(StickerAdjustFilter mStickerAdjustFilter, MaskModel maskModel, String gestureTriggerType) {
        if (mStickerAdjustFilter != null) {
            mStickerAdjustFilter.startGestureDetect();
            mStickerAdjustFilter.setEnableSound(true);
            mStickerAdjustFilter.addGestureModel(gestureTriggerType, maskModel);
        }
    }

    /**
     * 添加AR礼物Model
     *
     * @param mStickerAdjustFilter filter
     * @param maskModel            maskModel
     * @param duration             礼物时长
     */
    public static void addGiftModel(StickerAdjustFilter mStickerAdjustFilter, MaskModel maskModel, long duration) {
        if (mStickerAdjustFilter != null) {
            maskModel.setDuration(duration);
            maskModel.setModelType(MaskModel.TYPE_VIDEO_GIFT);
            if (maskModel.getStickers() != null && maskModel.getStickers().size() > 0) {
                for (Sticker sticker : maskModel.getStickers()) {
                    sticker.setStickerType("");
                }
            }
            mStickerAdjustFilter.setEnableSound(true);
            mStickerAdjustFilter.addMaskModel(maskModel);
        }
    }

    public static void clearFace(StickerAdjustFilter mStickerAdjustFilter, FilterChooser mFilterChooser, float skinLevel) {
        mStickerAdjustFilter.clearMaskFilters();

        mStickerAdjustFilter.setBigEye(0f);
        mStickerAdjustFilter.setThinFace(0f);

        mStickerAdjustFilter.stopGestureDetect();
        //如果没有配置美颜参数，则需要恢复默认的参数值得
        if (mFilterChooser != null)
            mFilterChooser.setSkinLevel(skinLevel);
    }
}
