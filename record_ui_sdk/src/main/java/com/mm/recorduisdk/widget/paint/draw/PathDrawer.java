package com.mm.recorduisdk.widget.paint.draw;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import java.util.List;

public class PathDrawer {
    private Paint gesturePaint;
    private Paint gestureEasePaint;
    private PorterDuffXfermode eraserMode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

    public enum PenType {PEN, ERASER, Shader}

    public PathDrawer() {
        initGesturePaint();
    }

    public void onDraw(Canvas canvas, SerializablePath currentDrawingPath, List<SerializablePath> paths,boolean isTouchUp) {
        drawGestures(canvas, paths);
        if (currentDrawingPath != null && !isTouchUp) {
            if (currentDrawingPath.getPenType() != PenType.ERASER) {
                drawGesture(canvas, currentDrawingPath);
            } else {
                 drawEaseGesture(canvas, currentDrawingPath);
            }
        }
    }

    public void drawEasePutGestures(Canvas canvas, List<SerializablePath> paths) {
        for (SerializablePath path : paths) {
            if (path.getPenType() == PenType.ERASER) {
                drawGesture(canvas, path);
            }
        }
    }

    public void drawGesturesOnly(Canvas canvas, List<SerializablePath> paths) {
        for (SerializablePath path : paths) {
            if (path.getPenType() == PenType.ERASER) {
                drawEaseGestureOnly(canvas, path);
            } else {
                drawGesture(canvas, path);
            }
        }
    }

    public void drawGestures(Canvas canvas, List<SerializablePath> paths) {
        for (SerializablePath path : paths) {
            if (path.getPenType() == PenType.ERASER) {
                drawEaseGestureOnly(canvas, path);
            } else {
                drawGesture(canvas, path);
            }
        }
    }

    public Bitmap obtainEaseBitmap(Bitmap createdBitmap, List<SerializablePath> paths) {
        Canvas composeCanvas = new Canvas(createdBitmap);
        drawEasePutGestures(composeCanvas, paths);
        return createdBitmap;
    }

    public Bitmap obtainBitmap(Bitmap createdBitmap, List<SerializablePath> paths) {
        Canvas composeCanvas = new Canvas(createdBitmap);
        drawGesturesOnly(composeCanvas, paths);
        return createdBitmap;
    }

    private void drawEaseGestureOnly(Canvas canvas, SerializablePath path) {
        gestureEasePaint.setStrokeWidth(path.getWidth());
        gestureEasePaint.setColor(path.getColor());
        canvas.drawPath(path, gestureEasePaint);
    }

    private void drawEaseGesture(Canvas canvas, SerializablePath path) {
        gestureEasePaint.setStrokeWidth(path.getWidth());
        gestureEasePaint.setColor(path.getColor());
        gesturePaint.setStrokeWidth(path.getWidth());
        gesturePaint.setColor(path.getColor());
        canvas.drawPath(path, gestureEasePaint);
        canvas.drawPath(path, gesturePaint);
    }

    private void drawGesture(Canvas canvas, SerializablePath path) {
        gesturePaint.setStrokeWidth(path.getWidth());
        gesturePaint.setColor(path.getColor());
        gesturePaint.setShader(path.getShader());
        canvas.drawPath(path, gesturePaint);
    }

    private void initGesturePaint() {
        gesturePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
        gesturePaint.setStyle(Paint.Style.STROKE);
        gesturePaint.setStrokeJoin(Paint.Join.ROUND);
        gesturePaint.setStrokeCap(Paint.Cap.ROUND);

        gestureEasePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
        gestureEasePaint.setStyle(Paint.Style.STROKE);
        gestureEasePaint.setStrokeJoin(Paint.Join.ROUND);
        gestureEasePaint.setFilterBitmap(false);

        gestureEasePaint.setMaskFilter(new MaskFilter());
        gestureEasePaint.setStrokeCap(Paint.Cap.ROUND);
        gestureEasePaint.setXfermode(eraserMode);
        gestureEasePaint.setAlpha(0);
    }
}
