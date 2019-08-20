package com.mm.recorduisdk.widget.sticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.mm.mediasdk.utils.UIUtils;
import com.mm.recorduisdk.R;
import com.mm.recorduisdk.recorder.sticker.StickerEntity;

/**
 * 表情贴纸
 */
public class StickerView extends ImageView {
    private static final String TAG = "StickerView";

    //动态贴纸
    public static final int TYPE_TEXT = 1;
    public static final int TYPE_IMG = 2;
    private float scale = 1;
    private float angle = 0;
    float tempAngle = 720;

    private Bitmap mBitmap;
    private Paint localPaint;
    private int mScreenwidth, mScreenHeight;
    private static final float BITMAP_SCALE = 0.7f;
    private String text = null;
    private int chosenTextColorIndex = 0;

    //用于更新动态贴纸的坐标
    private onUpdateViewListener onUpdateViewListener;
    private Paint mBorderPaint;


    private boolean isShowEditBorder;
    private Bitmap mDeleteStickerBitmap;

    public void setOnUpdateViewListener(StickerView.onUpdateViewListener onUpdateViewListener) {
        this.onUpdateViewListener = onUpdateViewListener;
    }

    /**
     * 对角线的长度
     */

    private Matrix matrix = new Matrix();

    private Matrix matrixStart = new Matrix();
    /**
     * 是否在编辑模式
     */
    private float MIN_SCALE = 0.5f;

    private float MAX_SCALE = 1.2f;

    //双指缩放时的初始距离
    private float oldDis;

    private long stickerId;

    private DisplayMetrics dm;

    private int MIN_WIDTH = UIUtils.getPixels(100);
    private int MAX_WIDTH = UIUtils.getScreenWidth();

    //水平镜像
    private boolean isHorizonMirror = false;

    private int mType;
    private StickerEntity mStickerEntity;

    public StickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        stickerId = 0;
        init();
    }

    public StickerView(Context context) {
        super(context);
        stickerId = 0;
        init();
    }

    public StickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        stickerId = 0;
        init();
    }

    private void init() {
        centerPoint = new PointF();
        localPaint = new Paint();
        localPaint.setAntiAlias(true);
        dm = getResources().getDisplayMetrics();
        mScreenwidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
    }

    public StickerEntity getStickerEntity() {
        return mStickerEntity;
    }

    public void setStickerEntity(StickerEntity stickerEntity) {
        mStickerEntity = stickerEntity;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public long getStickerId() {
        return stickerId;
    }

    public void setStickerId(long stickerId) {
        this.stickerId = stickerId;
    }

    public Rect viewRect;
    public Rect rootRect = new Rect();
    public Rect contentViewRect = new Rect();
    public Rect deleteIconRect = new Rect();

    public PointF centerPoint;

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap != null) {
            float[] arrayOfFloat = new float[9];
            matrix.getValues(arrayOfFloat);
            float f1 = 0.0F * arrayOfFloat[0] + 0.0F * arrayOfFloat[1] + arrayOfFloat[2];
            float f2 = 0.0F * arrayOfFloat[3] + 0.0F * arrayOfFloat[4] + arrayOfFloat[5];
            float f3 = arrayOfFloat[0] * this.mBitmap.getWidth() + 0.0F * arrayOfFloat[1] + arrayOfFloat[2];
            float f4 = arrayOfFloat[3] * this.mBitmap.getWidth() + 0.0F * arrayOfFloat[4] + arrayOfFloat[5];
            float f5 = 0.0F * arrayOfFloat[0] + arrayOfFloat[1] * this.mBitmap.getHeight() + arrayOfFloat[2];
            float f6 = 0.0F * arrayOfFloat[3] + arrayOfFloat[4] * this.mBitmap.getHeight() + arrayOfFloat[5];
            float f7 = arrayOfFloat[0] * this.mBitmap.getWidth() + arrayOfFloat[1] * this.mBitmap.getHeight() + arrayOfFloat[2];
            float f8 = arrayOfFloat[3] * this.mBitmap.getWidth() + arrayOfFloat[4] * this.mBitmap.getHeight() + arrayOfFloat[5];
            if (viewRect == null)
                viewRect = new Rect();
            viewRect.left = (int) Math.min(Math.min(f5, f7), Math.min(f1, f3));
            viewRect.top = (int) Math.min(Math.min(f6, f8), Math.min(f2, f4));
            viewRect.right = (int) Math.max(Math.max(f5, f7), Math.max(f1, f3));
            viewRect.bottom = (int) Math.max(Math.max(f6, f8), Math.max(f2, f4));
            centerPoint.set(viewRect.centerX(), viewRect.centerY());
            //            if (viewRect.width() <= MIN_WIDTH) {
            //                viewRect.left = (int) (centerPoint.x - MIN_WIDTH / 2.0f);
            //                viewRect.right = (int) (centerPoint.x + MIN_WIDTH / 2.0f);
            //                viewRect.top = (int) (centerPoint.y - MIN_WIDTH / 2.0f);
            //                viewRect.bottom = (int) (centerPoint.y + MIN_WIDTH / 2.0f);
            //                centerPoint.set(viewRect.centerX(), viewRect.centerY());
            //            }
            //
            //            if (viewRect.width() >= MAX_WIDTH) {
            //                viewRect.left = (int) (centerPoint.x - MAX_WIDTH / 2.0f);
            //                viewRect.right = (int) (centerPoint.x + MAX_WIDTH / 2.0f);
            //                viewRect.top = (int) (centerPoint.y - MAX_WIDTH / 2.0f);
            //                viewRect.bottom = (int) (centerPoint.y + MAX_WIDTH / 2.0f);
            //                centerPoint.set(viewRect.centerX(), viewRect.centerY());
            //            }
            //canvas.save();
            //LogUtil.d("centerPoint" + centerPoint.x +":" +centerPoint.y);
            rootRect.set(viewRect);
            scaleRect(rootRect, 0.25f);

            canvas.drawBitmap(mBitmap, matrix, localPaint);
            if (isShowEditBorder) {
                drawBorder(canvas);
            }
            if (null != onUpdateViewListener) {
                //更新动态贴纸的坐标
                onUpdateViewListener.onUpdateView(centerPoint, stickerId, viewRect.width() / (float) this.mBitmap.getWidth(), (angle % 360));

            }

            //删除在右上角
            // canvas.restore();
        }
    }

    private void drawBorder(Canvas canvas) {
        canvas.save();
        initBorderPaintIfNeed();
        contentViewRect.set(viewRect);

        canvas.drawRect(scaleRect(contentViewRect, 0.2f), mBorderPaint);

//        drawDeleteIcon(canvas);

        canvas.restore();
    }

    private void drawDeleteIcon(Canvas canvas) {
        if (mDeleteStickerBitmap == null) {
            mDeleteStickerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_sticker_delete);
        }
        deleteIconRect.set(contentViewRect.right - mDeleteStickerBitmap.getWidth() / 2, contentViewRect.top - mDeleteStickerBitmap.getHeight() / 2, contentViewRect.right + mDeleteStickerBitmap.getWidth() / 2, contentViewRect.top + mDeleteStickerBitmap.getHeight() / 2);
        canvas.drawBitmap(mDeleteStickerBitmap, deleteIconRect.left, deleteIconRect.top, localPaint);
    }

    private void initBorderPaintIfNeed() {
        if (mBorderPaint == null) {
            mBorderPaint = new Paint();
            mBorderPaint.setStyle(Paint.Style.STROKE);
            mBorderPaint.setAntiAlias(true);
            mBorderPaint.setStrokeWidth(UIUtils.getPixels(2));
            mBorderPaint.setColor(Color.parseColor("#ffffff"));
            mBorderPaint.setPathEffect(new DashPathEffect(new float[]{4, 4}, 0));
        }
    }

    public void setShowEditBorder(boolean show) {
        if (isShowEditBorder != show) {
            isShowEditBorder = show;
            invalidate();
        }
    }

    public boolean isShowEditBorder() {
        return isShowEditBorder;
    }

    private Rect scaleRect(Rect targetRect, float scale) {
        targetRect.inset(-(int) (targetRect.width() * scale), -(int) (targetRect.height() * scale));
        return targetRect;
    }

    public void setFilter(boolean filter, int color) {
        localPaint.setFilterBitmap(filter);
        if (filter)
            localPaint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP));
        else
            localPaint.setColorFilter(null);
    }

    public boolean isFilter() {
        return localPaint.isFilterBitmap();
    }

    @Override
    public void setImageResource(int resId) {
        setBitmap(BitmapFactory.decodeResource(getResources(), resId));
    }

    /**
     * 制定图片的放置位置
     *
     * @param bitmap
     * @param x
     * @param y
     */
    public void setBitmap(Bitmap bitmap, int x, int y) {
        matrix.reset();
        mBitmap = bitmap;
        initBitmaps();

        //        float initScale = 1;
        //        matrix.postScale(initScale, initScale, w / 2, h / 2);

        matrix.postTranslate(x, y);
        invalidate();
    }

    public void setBitmap(Bitmap bitmap, float transX, float transY, float width, float height, float degress) {
        matrix.reset();
        mBitmap = bitmap;
        initBitmaps();
        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();

        //        float w = width;
        //        float h = height;

        float initScale = 1;
        float sx = width / w;
        float sy = height / h;
        matrix.postScale(sx, sy);
        //Y坐标为 （顶部操作栏+正方形图）/2
        //        matrix.postTranslate(mScreenwidth / 2 - w / 2, (mScreenHeight) / 2 - h / 2);
        matrix.postRotate(degress);
        matrix.postTranslate(transX, transY);
        scale = 1;
        if (tempAngle == 720) {
            tempAngle = angle;
        }
        angle = tempAngle + degress;
        invalidate();
    }

    public void setTextAndColorIndex(String text, int colorIndex) {
        this.text = text;
        this.chosenTextColorIndex = colorIndex;
    }

    public boolean isText() {
        return !TextUtils.isEmpty(text);
    }

    public String getText() {
        return text;
    }

    public int getChosenTextColorIndex() {
        return chosenTextColorIndex;
    }

    /**
     * 默认放置在屏幕中央
     *
     * @param bitmap
     */
    public void setBitmap(Bitmap bitmap) {
        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();
        setBitmap(bitmap, mScreenwidth / 2 - w / 2, (mScreenHeight) / 2 - h / 2);
    }

    public void changeBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        initBitmaps();
        invalidate();
    }

    private void initBitmaps() {
        //当图片的宽比高大时 按照宽计算 缩放大小根据图片的大小而改变 最小为图片的1/8 最大为屏幕宽
        if (mBitmap.getWidth() >= mBitmap.getHeight()) {
            float minWidth = mScreenwidth / 8;
            if (mBitmap.getWidth() < minWidth) {
                MIN_SCALE = 1f;
            } else {
                MIN_SCALE = 1.0f * minWidth / mBitmap.getWidth();
            }

            if (mBitmap.getWidth() > mScreenwidth) {
                MAX_SCALE = 1;
            } else {
                MAX_SCALE = 1.0f * mScreenwidth / mBitmap.getWidth();
            }
        } else {
            //当图片高比宽大时，按照图片的高计算
            float minHeight = mScreenwidth / 8;
            if (mBitmap.getHeight() < minHeight) {
                MIN_SCALE = 1f;
            } else {
                MIN_SCALE = 1.0f * minHeight / mBitmap.getHeight();
            }

            if (mBitmap.getHeight() > mScreenwidth) {
                MAX_SCALE = 1;
            } else {
                MAX_SCALE = 1.0f * mScreenwidth / mBitmap.getHeight();
            }
        }
    }

    public float centerX, centerY;

    //    public Rect beginRect;

    public Matrix getCurMatrix() {
        centerX = centerPoint.x;
        centerY = centerPoint.y;
        //        beginRect = new Rect();
        //        beginRect.left = viewRect.left;
        //        beginRect.top = viewRect.top;
        //        beginRect.right = viewRect.right;
        //        beginRect.bottom = viewRect.bottom;
        return matrix;
    }

    public void setPostScale(Matrix newMatrix, float roate, float sx, float sy, float x, float y) {
        if (newMatrix == null || null == viewRect) {
            return;
        }
        if (getType() == TYPE_TEXT) {
            postForTextSticker(newMatrix, roate, sx, sy, x, y);
        } else {
            postForDynamicSticker(newMatrix, roate, sx, sy, x, y);
        }
    }

    private void postForTextSticker(Matrix newMatrix, float roate, float sx, float sy, float x, float y) {
        matrix.set(newMatrix);
        matrix.postRotate(roate, centerX, centerY);
        matrix.postScale(sx, sy, centerX, centerY);
        matrix.postTranslate(x, y);
        scale = sx;
        if (tempAngle == 720) {
            tempAngle = angle % 360;
        }
        angle = tempAngle + roate;
        //        angle = roate;

        invalidate();
    }

    private void postForDynamicSticker(Matrix newMatrix, float roate, float sx, float sy, float x, float y) {
        //限制动态贴纸最大以及最小的尺寸，以及fixed bug
        if (viewRect.width() <= MIN_WIDTH && sx < 1f) {
            sx = 1f;
            sy = 1f;
        } else if (viewRect.width() >= MAX_WIDTH && sx > 1f) {
            sx = 1f;
            sy = 1f;
        } else {
            matrix.set(newMatrix);
            matrix.postScale(sx, sy, centerX, centerY);
            //        matrix.postTranslate(x, y);
            scale = sx;
            if (tempAngle == 720) {
                tempAngle = angle % 360;
            }
            angle = tempAngle + roate;
        }

        //        Log4Android.getInstance().i("zhutao-roate============================" + roate);
        invalidate();
    }

    public void setNewMatrix(Matrix newMatrix, float x, float y) {
        matrix.set(newMatrix);
        matrix.postTranslate(x, y);
        invalidate();
    }

    public interface onUpdateViewListener {
        void onUpdateView(PointF centerPoint, long stickerId, float scale, float angle);
    }
}
