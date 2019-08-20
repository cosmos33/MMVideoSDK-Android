package com.mm.recorduisdk.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.mm.mediasdk.utils.UIUtils;
import com.mm.recorduisdk.R;

/**
 * Created by wangrenguang on 2017/11/16.
 * 录制页面露脸提示view
 */

public class FaceTipView extends TextView {
    private static final int DEFAULT_VIEW_WIDTH = UIUtils.getPixels(168);
    private static final int DEFAULT_VIEW_HEIGHT = UIUtils.getPixels(168);

    private static final int ANGLE = UIUtils.getPixels(14);

    private int mWidth;
    private int mHeight;

    private Paint mPaint;

    private TextPaint mTextPaint;

    private boolean isShowBg;


    public FaceTipView(Context context) {
        super(context);
        init();
    }

    public FaceTipView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FaceTipView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi (api = Build.VERSION_CODES.LOLLIPOP)
    public FaceTipView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mTextPaint = new TextPaint();
        mTextPaint.setColor(UIUtils.getColor(R.color.white));
        mTextPaint.setAntiAlias(false);

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(UIUtils.getColor(R.color.white));
        mPaint.setAntiAlias(false);
        mPaint.setStrokeWidth(UIUtils.getPixels(3));
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureDimension(DEFAULT_VIEW_WIDTH, widthMeasureSpec);
        int height = measureDimension(DEFAULT_VIEW_HEIGHT, heightMeasureSpec);
        mWidth = width;
        mHeight = height;
        setMeasuredDimension(width, height);
    }

    protected int measureDimension(int defaultSize, int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
            case MeasureSpec.AT_MOST:
                result = specSize;
                break;
            default:
                result = defaultSize;
                break;
        }
        return result;
    }

    public void showBg(boolean isShow) {
        isShowBg = isShow;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isShowBg) {
            return;
        }
        canvas.save();
        // 左上角
        canvas.drawLine(0, 0, 0, ANGLE, mPaint);
        canvas.drawLine(0, 0, ANGLE, 0, mPaint);
        // 左下角
        canvas.drawLine(0, UIUtils.getPixels(154), 0, DEFAULT_VIEW_WIDTH, mPaint);
        canvas.drawLine(0, DEFAULT_VIEW_WIDTH, ANGLE, DEFAULT_VIEW_WIDTH, mPaint);
        //右上角
        canvas.drawLine(UIUtils.getPixels(154), 0, DEFAULT_VIEW_WIDTH, 0, mPaint);
        canvas.drawLine(DEFAULT_VIEW_WIDTH, 0, DEFAULT_VIEW_WIDTH, ANGLE, mPaint);
        //右下角
        canvas.drawLine(DEFAULT_VIEW_WIDTH, UIUtils.getPixels(154), DEFAULT_VIEW_WIDTH, DEFAULT_VIEW_WIDTH,
                mPaint);
        canvas.drawLine(UIUtils.getPixels(154), DEFAULT_VIEW_WIDTH, DEFAULT_VIEW_WIDTH, DEFAULT_VIEW_WIDTH, mPaint);

        canvas.restore();
    }
}
