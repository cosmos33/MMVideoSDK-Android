package com.mm.recorduisdk.recorder.helper;

import com.mm.recorduisdk.IRecordResourceConfig;
import com.mm.recorduisdk.RecordUISDK;
import com.mm.recorduisdk.widget.BeautyAdapterData;
import com.mm.recorduisdk.widget.MomentFilterPanelLayout;
import com.momo.mcamera.mask.facewarp.FaceBeautyID;
import com.momo.xeengine.lightningrender.ILightningRender;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jiabin on 2017/7/13.
 */

public class VideoPanelFaceAndSkinManager {
    public static final String TYPE_MICRO = "type_micro";
    public static final String TYPE_MAKEUP = "type_makeup";
    private static volatile VideoPanelFaceAndSkinManager instance;

    private VideoPanelFaceAndSkinManager() {
    }

    public static VideoPanelFaceAndSkinManager getInstance() {
        if (instance == null) {
            synchronized (VideoPanelFaceAndSkinManager.class) {
                if (instance == null) {
                    instance = new VideoPanelFaceAndSkinManager();
                }
            }
        }
        return instance;
    }

    //1档  20%  2档 40%  3档 55%  4档75%  5档100% [0] smooth [1]light
    private float[][] beautyFace = new float[][]{{0.0f, 0.1f}, {0.15f, 0.1f}, {0.25f, 0.15f}, {0.35f, 0.25f}, {0.450f, 0.35f}, {0.85f, 0.8f}};
    private float[][] thinFace = new float[][]{{0.0f, 0.0f}, {0.15f, 0.15f}, {0.3f, 0.3f}, {0.45f, 0.45f}, {0.6f, 0.6f}, {0.8f, 0.8f}};

    private float[] slimmingFace = new float[]{0.0f, 0.2f, 0.4f, 0.6f, 0.8f, 1.0f};
    private float[] longLegsFace = new float[]{0.0f, 0.3f, 0.50f, 0.6f, 0.8f, 1.0f};

    private class MakeupMap {
        String dir;
        String type;

        public MakeupMap(String dir, String type) {
            this.dir = dir;
            this.type = type;
        }
    }


    private class BeautyType {
        String type;
        String content;

        public BeautyType(String type, String content) {
            this.type = type;
            this.content = content;
        }
    }

    private BeautyType[] beautyTypes = new BeautyType[]{
            new BeautyType(FaceBeautyID.JAW_SHAPE, "削脸"),
            new BeautyType(FaceBeautyID.FACE_WIDTH, "脸宽"),
            new BeautyType(FaceBeautyID.CHIN_LENGTH, "下巴"),
            new BeautyType(FaceBeautyID.FOREHEAD, "额头"),
            new BeautyType(FaceBeautyID.SHORTEN_FACE, "短脸"),
            new BeautyType(FaceBeautyID.EYE_TILT, "眼睛角度"),
            new BeautyType(FaceBeautyID.EYE_DISTANCE, "眼距"),
            new BeautyType(FaceBeautyID.NOSE_LIFT, "鼻高"),
            new BeautyType(FaceBeautyID.NOSE_SIZE, "鼻子大小"),
            new BeautyType(FaceBeautyID.NOSE_WIDTH, "鼻子宽度"),
            new BeautyType(FaceBeautyID.NOSE_RIDGE_WIDTH, "鼻梁"),
            new BeautyType(FaceBeautyID.NOSE_TIP_SIZE, "鼻尖"),
            new BeautyType(FaceBeautyID.LIP_THICKNESS, "嘴唇厚度"),
            new BeautyType(FaceBeautyID.MOUTH_SIZE, "嘴唇大小"),
            new BeautyType(FaceBeautyID.EYE_BRIGHTEN, "亮眼"),
            new BeautyType(FaceBeautyID.TEETH_WHITEN, "白牙"),
            new BeautyType(FaceBeautyID.SHARP_LIGHTNING, "锐化"),
            new BeautyType(FaceBeautyID.REMOVE_NASOLABIAL_FOLDS, "祛法令纹"),
            new BeautyType(FaceBeautyID.REMOVE_POUCH, "祛眼袋"),
            new BeautyType(FaceBeautyID.EYE_HEIGHT, "眼高"),
            new BeautyType(FaceBeautyID.CHEEKBONE_WIDTH, "颧骨"),
            new BeautyType(FaceBeautyID.JAW_WIDTH, "下颌骨"),
            new BeautyType(FaceBeautyID.SKIN_RUDDY, "红润"),
    };

    /**
     * 美颜，瘦脸，瘦身，长腿
     *
     * @return
     */
    public List getFaceEditType() {
        List<BeautyAdapterData> data = new ArrayList<>(6);
        data.add(new BeautyAdapterData("0"));
        data.add(new BeautyAdapterData("1"));
        data.add(new BeautyAdapterData("2"));
        data.add(new BeautyAdapterData("3"));
        data.add(new BeautyAdapterData("4"));
        data.add(new BeautyAdapterData("5"));
        return data;
    }

    public List<BeautyAdapterData> getMicroBeautyData() {
        List<BeautyAdapterData> data = new ArrayList<>(beautyTypes.length);
        for (BeautyType beautyTYpe : beautyTypes) {
            data.add(new BeautyAdapterData(TYPE_MICRO, beautyTYpe.type, beautyTYpe.content));
        }
        return data;
    }

    public List<BeautyAdapterData> getMakeupData() {
        IRecordResourceConfig<File> makeUpHomeDirConfig = RecordUISDK.getResourceGetter().getMakeUpHomeDirConfig();
        File makeupDir = makeUpHomeDirConfig.getResource();
        File[] files = makeupDir.listFiles();
        List<BeautyAdapterData> data = new ArrayList<>(files.length);
        Map<String, BeautyType> dirMap = initMakeupMap();
        for (File file : files) {
            if (file.getName().startsWith(".") && file.isFile()) {
                continue;
            }
            BeautyType beautyType = dirMap.get(file.getName());
            File[] makeupTypes = file.listFiles();
            if (makeupTypes == null) {
                continue;
            }
            for (File subFiles : makeupTypes) {
                if (subFiles.getName().startsWith(".")) {
                    continue;
                }
                data.add(new BeautyAdapterData(TYPE_MAKEUP, beautyType.type, String.format("%s_%s", beautyType.content, subFiles.getName()), null, subFiles.getPath()));
            }
        }
        return data;
    }

    private Map<String, BeautyType> initMakeupMap() {
        Map<String, BeautyType> map = new HashMap<>(7);
        map.put("makeup_cheek", new BeautyType(ILightningRender.IMakeupLevel.MAKEUP_BLUSH, "腮红"));
        map.put("makeup_eyebrow", new BeautyType(ILightningRender.IMakeupLevel.MAKEUP_EYEBROW, "眉毛"));
        map.put("makeup_eyeshadow", new BeautyType(ILightningRender.IMakeupLevel.MAKEUP_EYES, "眼影"));
        map.put("makeup_lips", new BeautyType(ILightningRender.IMakeupLevel.MAKEUP_LIPS, "口红"));
        map.put("makeup_style", new BeautyType(ILightningRender.IMakeupLevel.MAKEUP_ALL, "整妆"));
        map.put("makeup_pupil", new BeautyType(ILightningRender.IMakeupLevel.MAKEUP_PUPIL, "美瞳"));
        map.put("makeup_facial", new BeautyType(ILightningRender.IMakeupLevel.MAKEUP_FACIAL, "修容"));
        return map;
    }


    /**
     * 返回值有可能未空
     *
     * @param level
     * @param type
     * @return
     */
    public float[] getFaceSkinLevel(int level, int type) {
        float[][] levelData;
        switch (type) {
            case MomentFilterPanelLayout.TYPE_BEAUTY:
                levelData = beautyFace;
                break;
            case MomentFilterPanelLayout.TYPE_EYE_AND_THIN:
                levelData = thinFace;
                break;
            default:
                levelData = null;
                break;
        }
        assert levelData != null;
        return levelData[level];
    }


    public float getSlimmingAndLongLegsLevel(int level, int type) {
        float[] levelData;
        switch (type) {
            case MomentFilterPanelLayout.TYPE_SLIMMING:
                levelData = slimmingFace;
                break;
            case MomentFilterPanelLayout.TYPE_LONG_LEGS:
                levelData = longLegsFace;
                break;
            default:
                levelData = null;
                break;
        }
        assert levelData != null;
        return levelData[level];
    }
}