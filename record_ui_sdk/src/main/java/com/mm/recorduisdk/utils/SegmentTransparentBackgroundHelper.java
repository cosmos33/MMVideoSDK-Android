package com.mm.recorduisdk.utils;

import android.graphics.Bitmap;

import com.core.glcore.cv.MMFrameInfo;
import com.core.glcore.cv.MMParamsInfo;
import com.core.glcore.util.SegmentHelper;
import com.immomo.resdownloader.manager.DynamicResourceConstants;
import com.immomo.resdownloader.manager.ModelResourceManager;
import com.mm.mediasdk.utils.ImageUtil;
import com.mm.mmutil.FileUtil;
import com.momocv.MMFrame;

import java.io.File;

/**
 * Created on 2019/8/9.
 *
 * @author jianxi[mabeijianxi@gmail.com]
 */
public class SegmentTransparentBackgroundHelper {


    public static Bitmap segmentProcess(Bitmap sourceBitmap) {


        File fp = ModelResourceManager.getInstance().getResource(DynamicResourceConstants.ITEM_NAME_MMCV_SG_MODEL);
        if (fp != null && FileUtil.isValidFile(fp)) {
            SegmentHelper.setModelPath(fp.getAbsolutePath());
        } else {
            return sourceBitmap;
        }

        byte[] sourceByteColors = ImageUtil.bitmap2RGB(sourceBitmap);

        SegmentHelper.setWidth(sourceBitmap.getWidth());
        SegmentHelper.setHeight(sourceBitmap.getHeight());
        SegmentHelper.setRotateDegree(0);
        SegmentHelper.setRestoreDegree(0);
        SegmentHelper.setIsFrontCamera(false);

        MMFrameInfo mmcvFrame = new MMFrameInfo();
        MMParamsInfo params = new MMParamsInfo(MMParamsInfo.SEGMENT_PARAMS_TYPE);
        mmcvFrame.setFormat(MMFrame.MMFormat.FMT_RGBA);
        params.setSegmentParamsType(false);
        mmcvFrame.setDataPtr(sourceByteColors);
        mmcvFrame.setDataLen(sourceByteColors.length);
        mmcvFrame.setWidth(sourceBitmap.getWidth());
        mmcvFrame.setHeight(sourceBitmap.getHeight());
        mmcvFrame.setStep_(sourceBitmap.getWidth());

        params.setFlipedShow(SegmentHelper.isFrontCamera());
        params.setRotateDegree(SegmentHelper.getRotateDegree());
        params.setRestoreDegree(SegmentHelper.getRestoreDegree());

        byte[] result = SegmentHelper.process(mmcvFrame, params, true);


        if (result != null) {

            int totalPixelCount = sourceBitmap.getWidth() * sourceBitmap.getHeight();

            if (totalPixelCount == result.length * 3 || totalPixelCount == result.length * 4) {
                int[] targetIntColors = new int[result.length];

                sourceBitmap.getPixels(targetIntColors, 0, sourceBitmap.getWidth(), 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight());

                for (int i = 0; i < result.length; i++) {
                    targetIntColors[i] = ((0x000000ff & result[i]) << 24) | (targetIntColors[i] & 0x00ffffff);
                }
                Bitmap resultBitmap = Bitmap.createBitmap(targetIntColors, sourceBitmap.getWidth(), sourceBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                return resultBitmap;
            }
        }
        return sourceBitmap;
    }


    public static void release() {
        SegmentHelper.release();
    }
}
