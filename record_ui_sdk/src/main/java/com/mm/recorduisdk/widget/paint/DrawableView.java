package com.mm.recorduisdk.widget.paint;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.mm.recorduisdk.widget.paint.draw.PathDrawer;
import com.mm.recorduisdk.widget.paint.draw.SerializablePath;
import com.mm.recorduisdk.widget.paint.gestures.creator.GestureCreator;
import com.mm.recorduisdk.widget.paint.gestures.creator.GestureCreatorListener;
import com.mm.recorduisdk.widget.paint.gestures.scale.GestureScaleListener;
import com.mm.recorduisdk.widget.paint.gestures.scale.GestureScaler;
import com.mm.recorduisdk.widget.paint.gestures.scale.ScalerListener;
import com.mm.recorduisdk.widget.paint.gestures.scroller.GestureScrollListener;
import com.mm.recorduisdk.widget.paint.gestures.scroller.GestureScroller;
import com.mm.recorduisdk.widget.paint.gestures.scroller.ScrollerListener;

import java.util.ArrayList;

public class DrawableView extends View
        implements View.OnTouchListener, ScrollerListener, GestureCreatorListener, ScalerListener {

    private final ArrayList<SerializablePath> paths = new ArrayList<>();

    private GestureScroller gestureScroller;
    private GestureScaler gestureScaler;
    private GestureCreator gestureCreator;
    private int canvasHeight;
    private int canvasWidth;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private PathDrawer pathDrawer;
    private SerializablePath currentDrawingPath;
    private PathDrawer.PenType penType = PathDrawer.PenType.PEN;
    private Bitmap tempBitmap;
    private onDrawListener onDrawListener;
    private int eraserPathCount = 0;

    public PathDrawer.PenType getPenType() {
        return penType;
    }

    public DrawableView(Context context) {
        super(context);
        init();
    }

    public DrawableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DrawableView(Context context, AttributeSet attrs,
                        int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        eraserPathCount = 0;
        gestureScroller = new GestureScroller(this);
        gestureDetector = new GestureDetector(getContext(), new GestureScrollListener(gestureScroller));
        gestureScaler = new GestureScaler(this);
        scaleGestureDetector =
                new ScaleGestureDetector(getContext(), new GestureScaleListener(gestureScaler));
        gestureCreator = new GestureCreator(this);
        pathDrawer = new PathDrawer();
        setOnTouchListener(this);
    }

    public void setPenType(PathDrawer.PenType type) {
        penType = type;
    }

    public void setConfig(DrawableViewConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Paint configuration cannot be null");
        }
        canvasWidth = config.getCanvasWidth();
        canvasHeight = config.getCanvasHeight();
        gestureCreator.setConfig(config);
        gestureScaler.setZooms(config.getMinZoom(), config.getMaxZoom());
        gestureScroller.setCanvasBounds(canvasWidth, canvasHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        gestureScroller.setViewBounds(w, h);
    }

    boolean isTouchUp = false;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        gestureCreator.onTouchEvent(event);
        switch (MotionEventCompat.getActionMasked(event)) {
            case MotionEvent.ACTION_DOWN:
                isTouchUp = false;
                if (null != onDrawListener) {
                    onDrawListener.onDrawBegin();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                isTouchUp = false;
                break;

            case MotionEvent.ACTION_UP:
                isTouchUp = true;
                if (null != onDrawListener) {
                    onDrawListener.onDrawEnd();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                isTouchUp = true;
                if (null != onDrawListener) {
                    onDrawListener.onDrawEnd();
                }
                break;
            case MotionEventCompat.ACTION_POINTER_DOWN:
                isTouchUp = true;
                break;
        }
        invalidate();
        return true;
    }

    public void undo() {
        if (paths.size() > 0) {
            SerializablePath path = paths.remove(paths.size() - 1);
            if (path.getPenType() == PathDrawer.PenType.ERASER)
                eraserPathCount--;
            if (eraserPathCount < 0)
                eraserPathCount = 0;
        }
        invalidate();
    }

    public int getPathSize() {
        return paths.size();
    }

    public boolean canUndo() {
        return paths.size() > 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        pathDrawer.onDraw(canvas, currentDrawingPath, paths, isTouchUp);
    }

    /**
     * 清除从start开始的所有path [start, size)
     * @param start
     */
    public void clearAfter(int start) {
        int size = paths.size();
        if (start == 0) {
            clear();
            return;
        }
        if (size <= start)
            return;
        final int offset = size - start;
        for (int i = 0; i < offset; i++) {
            size--;
            paths.remove(size);
        }
        invalidate();
    }

    public void clear() {
        paths.clear();
        invalidate();
    }

    public Bitmap obtainBitmap(Bitmap createdBitmap) {
        return pathDrawer.obtainBitmap(createdBitmap, paths);
    }

    public Bitmap obtainEaseBitmap(Bitmap createdBitmap) {
        return pathDrawer.obtainEaseBitmap(createdBitmap, paths);
    }

    public Bitmap obtainBitmap() {
        if (paths.size() <= 0)
            return null;
        return obtainBitmap(Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888));
    }

    public Bitmap obtainEaseBitmap() {
        if (eraserPathCount <= 0)
            return null;
        return obtainEaseBitmap(Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888));
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        DrawableViewSaveState state = new DrawableViewSaveState(super.onSaveInstanceState());
        state.setPaths(paths);
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof DrawableViewSaveState)) {
            super.onRestoreInstanceState(state);
        } else {
            DrawableViewSaveState ss = (DrawableViewSaveState) state;
            super.onRestoreInstanceState(ss.getSuperState());
            paths.addAll(ss.getPaths());
        }
    }

    @Override
    public void onViewPortChange(RectF currentViewport) {
        gestureCreator.onViewPortChange(currentViewport);
    }

    @Override
    public void onCanvasChanged(RectF canvasRect) {
        gestureCreator.onCanvasChanged(canvasRect);
    }

    @Override
    public void onGestureCreated(SerializablePath serializablePath) {
        if (serializablePath.getPenType() == PathDrawer.PenType.ERASER)
            eraserPathCount++;
        paths.add(serializablePath);
        if (null != onDrawListener) {
            onDrawListener.onDraw(null);
        }
    }

    @Override
    public void onCurrentGestureChanged(SerializablePath currentDrawingPath) {
        this.currentDrawingPath = currentDrawingPath;
    }

    @Override
    public void onScaleChange(float scaleFactor) {
        gestureScroller.onScaleChange(scaleFactor);
        gestureCreator.onScaleChange(scaleFactor);
    }

    public interface onDrawListener {
        public void onDraw(Bitmap bitmap);

        public void onDrawBegin();

        public void onDrawEnd();

    }

    public DrawableView.onDrawListener getOnDrawListener() {
        return onDrawListener;
    }

    public void setOnDrawListener(DrawableView.onDrawListener onDrawListener) {
        this.onDrawListener = onDrawListener;
    }
}
