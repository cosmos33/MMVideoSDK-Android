package com.mm.sdkdemo.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.mm.mediasdk.utils.ImageUtil;
import com.mm.mmutil.app.AppContext;
import com.mm.mmutil.log.Log4Android;
import com.mm.mediasdk.utils.UIUtils;
import com.mm.sdkdemo.R;

import static android.view.View.MeasureSpec;
import static android.view.View.MeasureSpec.AT_MOST;

/**
 * Created by XiongFangyu on 2017/3/16.
 */
public class MicroVideoWaterMarkHelper {

    private static final int LAYOUT_WATER_MARK = R.layout.water_mark_layout;
    private static final float MARGIN_TOP = 0.0915f;
    private static final float HEIGHT = 0.0284f;
    private static final float MARGIN_RIGHT = 0.032f;
    private static final float WIDTH = 0.28f;
    private static final boolean USE_WIDTH = true;

    /**
     * 给已有需合成进视频的图片添加水印
     * 若图片大小大于视频长宽，则缩放
     * @param blend
     * @param w 视频宽
     * @param h 视频长
     * @return
     */
    public static final Bitmap getBlendBitmapByVideoSize(Bitmap blend, int w, int h) {
        if (blend != null && !blend.isRecycled()) {
            Bitmap wartermark = getWaterMarkBitmap("123456");
            Bitmap scaleBlend = blend;
            if (wartermark != null) {
                int bw = blend.getWidth();
                int bh = blend.getHeight();
                final Rect rect = new Rect();
                if (bw > w || bh > h) {
                    scaleBlend = ImageUtil.zoomBitmap(blend, w, h);
                    bw = scaleBlend.getWidth();
                    bh = scaleBlend.getHeight();
                }
                int rw, rh;
                int t = (int) (bh * MARGIN_TOP);
                int r = bw - (int) (bw * MARGIN_RIGHT);
                if (USE_WIDTH) {
                    rw = (int) (bw * WIDTH);
                    rh = rw * wartermark.getHeight() / wartermark.getWidth();
                } else {
                    rh = (int) (bh * HEIGHT);
                    rw = rh * wartermark.getWidth() / wartermark.getHeight();
                }

                rect.set(r - rw, t, r, t + rh);
                Canvas canvas = new Canvas(scaleBlend);
                canvas.drawBitmap(wartermark, null, rect, null);
                //                canvas.drawBitmap(wartermark, 0, 0, null);
                canvas.setBitmap(null);
                return scaleBlend;
            }
        }
        return blend;
    }

    public static final Bitmap getWaterMarkBitmap(String momoid) {
        View view = LayoutInflater.from(AppContext.getContext()).inflate(LAYOUT_WATER_MARK, null);
        TextView tv = (TextView) view.findViewById(R.id.water_text);
        tv.setText(momoid);
        view.measure(MeasureSpec.makeMeasureSpec(UIUtils.getScreenWidth(), AT_MOST),
                     MeasureSpec.makeMeasureSpec(UIUtils.getScreenHeight(), AT_MOST));
        final int mw = view.getMeasuredWidth();
        final int mh = view.getMeasuredHeight();
        view.layout(0, 0, mw, mh);
        try {
            Bitmap result = Bitmap.createBitmap(mw, mh, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            view.draw(canvas);
            canvas.setBitmap(null);
            return result;
        } catch (Exception e) {
            Log4Android.getInstance().e(e);
        }
        return null;
    }
}
