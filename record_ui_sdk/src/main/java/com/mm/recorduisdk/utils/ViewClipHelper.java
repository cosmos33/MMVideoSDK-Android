package com.mm.recorduisdk.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static android.graphics.Path.FillType.INVERSE_EVEN_ODD;

/**
 * Created by xudong on 2018/7/18
 * <p>
 * Warning:https://issuetracker.google.com/issues/111819103
 * <p>
 * In Android O, the paths are drawn as rectangles covering the entire path bounds.
 * The inverse path pixels are drawn with transparent pixels.
 * In Android P, only the paths pixels that have a fill are drawn.
 * <p>
 * The inverse path pixels are not touched/drawn at all, which is faster.
 * Sample.apk draws a rounded rect over an image.
 * The PorterDuff mode is not important at corners,
 * because there is no blending/drawing in Android P.
 */
public final class ViewClipHelper {
    @NonNull
    private final Paint clipPaint;
    @NonNull
    private final Path clipPath = new Path();

    @NonNull
    private final Paint surfaceViewClipPaint;
    @NonNull
    private final Path surfaceViewClipPath = new Path();

    public ViewClipHelper() {
        clipPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        clipPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            clipPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        } else {
            clipPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        }

        surfaceViewClipPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        surfaceViewClipPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        surfaceViewClipPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public void updateClipPath(@NonNull Path clipPath) {
        this.clipPath.set(clipPath);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            this.clipPath.setFillType(INVERSE_EVEN_ODD);
        }

        this.surfaceViewClipPath.set(clipPath);
    }

    public void clip(@NonNull Canvas canvas, @Nullable SuperDrawAction drawAction,
                     boolean containsSurfaceView) {
        if (containsSurfaceView) {
            canvas.drawPath(surfaceViewClipPath, surfaceViewClipPaint);
        }

        int layerId = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(),
                null, Canvas.ALL_SAVE_FLAG);
        if (drawAction != null) {
            drawAction.innerDraw(canvas);
        }

        canvas.drawPath(clipPath, clipPaint);
        canvas.restoreToCount(layerId);
    }

    public interface SuperDrawAction {
        void innerDraw(Canvas canvas);
    }

    private static boolean containsSurfaceView(@NonNull ViewGroup parent, boolean detectOnlyChild) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i = i + 1) {
            if (parent.getChildAt(i) instanceof SurfaceView) {
                return true;
            }
        }
        if (childCount == 1 && detectOnlyChild) {
            View onlyChild = parent.getChildAt(0);
            if (onlyChild instanceof ViewGroup) {
                return containsSurfaceView((ViewGroup) onlyChild, false);
            }
        }
        return false;
    }

    public static boolean containsSurfaceView(@NonNull ViewGroup parent) {
        return containsSurfaceView(parent, true);
    }
}
