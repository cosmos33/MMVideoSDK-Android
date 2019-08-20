package com.mm.base_business.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;

/**
 * Created by tangyuchun on 2/26/16.
 */
public class CanvasUtils {
    public static void drawRoundRect(Canvas canvas, Paint paint, RectF rectF, float radius) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(rectF, radius, radius, paint);
        } else {
            Path path = new Path();
            path.moveTo(rectF.left + radius, rectF.top);
            path.lineTo(rectF.right - radius, rectF.top);
            path.quadTo(rectF.right, rectF.top, rectF.right, rectF.top + radius);

            path.lineTo(rectF.right, rectF.bottom - radius);
            path.quadTo(rectF.right, rectF.bottom, rectF.right - radius, rectF.bottom);

            path.lineTo(rectF.left + radius, rectF.bottom);
            path.quadTo(rectF.left, rectF.bottom, rectF.left, rectF.bottom - radius);

            path.lineTo(rectF.left, rectF.top + radius);
            path.quadTo(rectF.left, rectF.top, rectF.left + radius, rectF.top);
            canvas.drawPath(path, paint);
        }
    }

    /**
     * 是否支持{@link Canvas#clipPath(Path)}
     * 若开启了硬件加速，并且系统版本<18 不支持{@link Canvas#clipPath(Path)}
     *
     * @param canvas
     * @return
     */
    public static boolean isSupportClippath(Canvas canvas) {
        boolean res1 = canvas.isHardwareAccelerated();
        boolean res2 = Build.VERSION.SDK_INT >= 18;
//        log.d(" xfyxfy--- CanvasSupportUtils #isSupportClippath --- isHardwareAccelerated : " + res1 + " SDK_INT >= 18 : " + res2);
        return !res1 || res2;
    }
}
