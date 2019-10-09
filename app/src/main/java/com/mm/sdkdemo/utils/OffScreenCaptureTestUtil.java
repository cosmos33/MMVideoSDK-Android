package com.mm.sdkdemo.utils;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.mm.mediasdk.MoMediaManager;
import com.mm.mediasdk.filters.getter.impl.FilterInstanceGetter;
import com.mm.mediasdk.gl.offscreen.IOffScreenSegmentRenderProcess;
import com.mm.mediasdk.gl.offscreen.OffscreenRenderer;
import com.mm.mmutil.task.MomoMainThreadExecutor;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import project.android.imageprocessing.filter.BasicFilter;

/**
 * Created on 2019-09-24.
 *
 * @author jianxi[mabeijianxi@gmail.com]
 */
public class OffScreenCaptureTestUtil {
    public static void testCapture(Bitmap sourceBitmap, Handler.Callback callback) {

        Queue<BasicFilter> basicFilters = new LinkedList<>();

        int[] ints = new int[sourceBitmap.getWidth() * sourceBitmap.getHeight()];
        Arrays.fill(ints, 0xffff0000);
        Bitmap bitmap = Bitmap.createBitmap(ints, 0, sourceBitmap.getWidth(), sourceBitmap.getWidth(), sourceBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        BasicFilter segmentWithBgBitmapFilter0 = FilterInstanceGetter.getSegmentWithBgBitmapFilter(bitmap);
        basicFilters.offer(segmentWithBgBitmapFilter0);

        ints = new int[sourceBitmap.getWidth() * sourceBitmap.getHeight()];
        Arrays.fill(ints, 0xffffff00);
        bitmap = Bitmap.createBitmap(ints, 0, sourceBitmap.getWidth(), sourceBitmap.getWidth(), sourceBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        BasicFilter segmentWithBgBitmapFilter1 = FilterInstanceGetter.getSegmentWithBgBitmapFilter(bitmap);
        basicFilters.offer(segmentWithBgBitmapFilter1);

        BasicFilter gaussianBlurFilter = FilterInstanceGetter.getGaussianBlurFilter(13);
        basicFilters.offer(FilterInstanceGetter.getSegmentWithBgFiltersFilter(gaussianBlurFilter));

        BasicFilter circleDotFilter = FilterInstanceGetter.getCircleDotFilter(new PointF(20, 20));
        basicFilters.offer(FilterInstanceGetter.getSegmentWithBgFiltersFilter(circleDotFilter));

        BasicFilter comicInkFilter = FilterInstanceGetter.getComicInkFilter(new File(Environment.getExternalStorageDirectory(), "lookup.png").toString());
        basicFilters.offer(FilterInstanceGetter.getSegmentWithBgFiltersFilter(comicInkFilter));


        BasicFilter comicTownFilter = FilterInstanceGetter.getComicTownFilter();
        basicFilters.offer(FilterInstanceGetter.getSegmentWithBgFiltersFilter(comicTownFilter));


        BasicFilter colorHalftoneFilterFilter = FilterInstanceGetter.getColorHalftoneFilterFilter();
        basicFilters.offer(FilterInstanceGetter.getSegmentWithBgFiltersFilter(colorHalftoneFilterFilter));

        IOffScreenSegmentRenderProcess offScreenSegmentRenderProcess = MoMediaManager.createOffScreenSegmentRenderProcess();
        offScreenSegmentRenderProcess.init(sourceBitmap);

        ArrayList<Bitmap> bitmaps = new ArrayList<>();

        loopCapture(offScreenSegmentRenderProcess, basicFilters, bitmaps, callback);

    }

    private static void loopCapture(final IOffScreenSegmentRenderProcess glOffScreenRenderProcess, final Queue<BasicFilter> basicFilters, final ArrayList<Bitmap> bitmaps, final Handler.Callback callback) {
        if (basicFilters.size() > 0) {
            glOffScreenRenderProcess.switchInnerFilter(basicFilters.poll());
            glOffScreenRenderProcess.capture(new OffscreenRenderer.OnCaptureFrameListener() {
                @Override
                public void onCaptureFrame(Bitmap bitmap) {
                    bitmaps.add(bitmap);
                    loopCapture(glOffScreenRenderProcess, basicFilters, bitmaps, callback);
                }
            });
        } else {
            MomoMainThreadExecutor.post(new Runnable() {
                @Override
                public void run() {
                    glOffScreenRenderProcess.release();
                }
            });
            Message message = Message.obtain();
            message.obj = bitmaps;
            callback.handleMessage(message);
        }
    }
}
