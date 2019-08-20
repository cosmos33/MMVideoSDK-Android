package com.mm.recorduisdk.widget.paint;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mm.mediasdk.utils.UIUtils;
import com.mm.recorduisdk.R;
import com.mm.recorduisdk.utils.AnimationUtil;
import com.mm.recorduisdk.widget.AnimCheckableGroupView;
import com.mm.recorduisdk.widget.IAnimView;
import com.mm.recorduisdk.widget.paint.draw.PathDrawer;

/**
 * 时刻编辑页面 涂鸦的View 负责涂鸦功能
 * Project momodev
 * Package com.mm.momo.moment.view.paint
 * Created by tangyuchun on 7/26/16.
 */
public class PaintPanelView extends RelativeLayout {
    private final int COLOR_PURPLE = 0xFF572EB3;
    private final int COLOR_GREEN = 0xFF03E7A9;
    private final int COLOR_RED = 0xFFF3342C;
    private final int COLOR_BLUE = 0xFF195AFF;
    private final int COLOR_YELLOW = 0xFFFFFC19;

    private View undoView, okView, closeView;
    private AnimCheckableGroupView acgView;
    public ImageView maskView;
    public DrawableView drawableView;

    private int paintColors[];
    private Bitmap maskBitMap;
    private DrawableViewConfig config = new DrawableViewConfig();
    LayoutParams imageParams;

    private int lastPathSize = 0;

    private boolean hasMosaic = true;

    public PaintPanelView(Context context) {
        super(context);
    }

    public PaintPanelView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PaintPanelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PaintPanelView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void init() {
        paintColors = new int[]{
                COLOR_PURPLE,
                COLOR_GREEN,
                COLOR_RED,
                COLOR_BLUE,
                COLOR_YELLOW
        };

        undoView = findViewById(R.id.moment_paint_panel_undo);
        okView = findViewById(R.id.moment_paint_panel_ok);
        closeView = findViewById(R.id.moment_paint_panel_btn_close);
        acgView = findViewById(R.id.moment_paint_panel_acgview);

        acgView.addColors(paintColors);
        if (hasMosaic) {
            acgView.addDrawables(getResources().getDrawable(R.drawable.ic_moment_paint_masic));
        }

        final int colorLength = paintColors.length;
        final int mosaicPos = colorLength;
        final int allSize = hasMosaic ? colorLength + 1 : colorLength;
        acgView.setCheck(0, true, false);
        acgView.setCheckOnce(true);
        acgView.setChildCheckListener(new AnimCheckableGroupView.ChildCheckListener() {
            @Override
            public void onChecked(IAnimView v, boolean checked, int pos) {
                if (pos < 0 || pos >= allSize) {
                    return;
                }
                if (hasMosaic && pos == mosaicPos) {
                    setupForMasicPaint();
                } else if (pos < colorLength) {
                    setupForNormalPaint(paintColors[pos]);
                } else {
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) v.getDrawable();
                    setupForBitmapPaint(new BitmapShader(bitmapDrawable.getBitmap(), Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
                }
            }
        });

        drawableView = findViewById(R.id.moment_paint_panel_drawableview);
        maskView = findViewById(R.id.moment_paint_panel_maskview);

        undoView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                undo();
            }
        });
        okView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finishPaint();
            }
        });
        closeView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                drawableView.clearAfter(lastPathSize);
                finishPaint();
            }
        });
        initConfigs();

        if (!isInEditMode()) {
            final int vh = UIUtils.getVirtualBarHeight();
            acgView.setPadding(acgView.getPaddingLeft(),
                               acgView.getPaddingTop(),
                               acgView.getPaddingRight(),
                               acgView.getPaddingBottom() + vh);
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == View.VISIBLE) {
            acgView.setVisibility(View.VISIBLE);
            undoView.setVisibility(canUndo() ? VISIBLE : GONE);
        } else {
            acgView.setVisibility(View.GONE);
        }
    }

    public void showCheckGroup() {
        acgView.show(true);
    }

    private void setupForNormalPaint(int color) {
        config.setStrokeColor(color);
        config.setShowCanvasBounds(true);
        config.setStrokeWidth(UIUtils.getPixels(10));
        config.setPenType(PathDrawer.PenType.PEN);
        config.setMinZoom(1.0f);
        config.setMaxZoom(1.0f);

        drawableView.setPenType(PathDrawer.PenType.PEN);
    }

    private void setupForMasicPaint() {
        config.setStrokeColor(0x30000000);
        config.setPenType(PathDrawer.PenType.ERASER);
        config.setStrokeWidth(UIUtils.getPixels(20));

        drawableView.setPenType(PathDrawer.PenType.ERASER);
    }

    private void setupForBitmapPaint(Shader shader) {
        config.setPenType(PathDrawer.PenType.Shader);
        config.setShader(shader);
        config.setStrokeWidth(UIUtils.getPixels(10));
    }

    private void initConfigs() {
        setupForNormalPaint(paintColors[0]);
        if (imageParams != null) {
            config.setCanvasWidth(imageParams.width);
            config.setCanvasHeight(imageParams.height);
            drawableView.setLayoutParams(imageParams);
        } else {
            config.setCanvasHeight(UIUtils.getScreenHeight());
            config.setCanvasWidth(UIUtils.getScreenWidth());
        }
        drawableView.setConfig(config);
        drawableView.setOnDrawListener(new DrawableView.onDrawListener() {
            @Override
            public void onDraw(Bitmap bitmap) {
                if (mPaintActionListener != null) {
                    mPaintActionListener.onDraw(null, drawableView.obtainEaseBitmap());
                }
            }

            @Override
            public void onDrawBegin() {
                undoView.clearAnimation();
                AnimationUtil.hiddenAlphaAnimation(acgView);
                if (canUndo() && undoView.getVisibility() == VISIBLE)
                    AnimationUtil.hiddenAlphaAnimation(undoView);
                AnimationUtil.hiddenAlphaAnimation(okView);
                setDrawableMode(true);
            }

            @Override
            public void onDrawEnd() {
                AnimationUtil.showAlphaAnimation(acgView);
                AnimationUtil.showAlphaAnimation(okView);
                setDrawableMode(false);
                if (canUndo()) {
                    undoView.setVisibility(VISIBLE);
                    AnimationUtil.showAlphaAnimation(undoView);
                } else {
                    undoView.setVisibility(GONE);
                }
            }
        });
        if (null != maskBitMap) {
            maskView.setImageBitmap(maskBitMap);
        }
    }

    public void setMaskBitmap(Bitmap bitmap) {
        maskBitMap = bitmap;
        if (maskView != null && bitmap != null)
            maskView.setImageBitmap(maskBitMap);
    }

    boolean isInDrawMode = false;

    public void setDrawableMode(boolean isDraw) {
        if (isInDrawMode == isDraw) {
            return;
        } else {
            isInDrawMode = isDraw;
        }
    }

    public void undo() {
        undoView.clearAnimation();
        drawableView.undo();
        if (!drawableView.canUndo()) {
            if (mPaintActionListener != null) {
                mPaintActionListener.onUndo(null, null);
            }
        } else {
            if (mPaintActionListener != null) {
                mPaintActionListener.onUndo(null, drawableView.obtainEaseBitmap());
            }
        }
        undoView.setVisibility(canUndo() ? VISIBLE : GONE);
    }

    public void finishPaint() {
        lastPathSize = drawableView.getPathSize();
        if (mPaintActionListener != null) {
            mPaintActionListener.onFinished(drawableView.obtainBitmap(), drawableView.obtainEaseBitmap());
        }
    }

    /**
     * 是否有涂鸦
     *
     * @return
     */
    public boolean canUndo() {
        return drawableView != null && drawableView.canUndo();
    }

    public void setImageParams(LayoutParams imageParams) {
        this.imageParams = imageParams;
        if (drawableView != null) {
            config.setCanvasWidth(imageParams.width);
            config.setCanvasHeight(imageParams.height);
            drawableView.setLayoutParams(imageParams);
            drawableView.setConfig(config);
        }
    }

    private PaintActionListener mPaintActionListener;

    public void setPaintActionListener(PaintActionListener pPaintActionListener) {
        mPaintActionListener = pPaintActionListener;
    }

    public interface PaintActionListener {
        void onUndo(Bitmap paintBmp, Bitmap easeBitmap);

        void onFinished(Bitmap paintBmp, Bitmap easeBitmap);

        void onDraw(Bitmap paintBmp, Bitmap easeBitmap);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (null != maskBitMap) {
            maskBitMap.recycle();
            maskBitMap = null;
        }
        super.onDetachedFromWindow();
    }

    public void setHasMosaic(boolean hasMosaic) {
        this.hasMosaic = hasMosaic;
    }
}
