package com.mm.recorduisdk.recorder.helper;

import com.mm.recorduisdk.widget.MomentFilterPanelLayout;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jiabin on 2017/7/13.
 */

public class VideoPanelFaceAndSkinManager {

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

    /**
     * 美颜，瘦脸，瘦身，长腿type
     *
     * @return
     */
    public List getFaceEditType() {
        Integer[] type = new Integer[]{0, 1, 2, 3, 4, 5};
        return Arrays.asList(type);
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