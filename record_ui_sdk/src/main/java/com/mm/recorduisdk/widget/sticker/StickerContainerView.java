package com.mm.recorduisdk.widget.sticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mm.mediasdk.utils.UIUtils;
import com.mm.recorduisdk.R;
import com.mm.recorduisdk.recorder.sticker.StickerEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhoukai on 1/20/16.
 */
public class StickerContainerView extends RelativeLayout {
    private static final long MAX_CLICK_TIME = 200;
    //定义三种模式：none、drag、zoom
    public static final int mode_none = 0;
    public static final int mode_drag = 1;
    public static final int mode_zoom = 2;
    public static final int mode_drag_left = 3;
    //当前操作模式
    private int mMode = mode_none;

    List<StickerView> mViews;

    StickerView mCurrentView;

    private View coverTopView, coverBottomView;

    private int width, height;
    private int childTopMargin = 0, childLeftMargin = 0;
    private int filterColor = 0x33000000;
    private boolean showToast = true;

    public ImageView deleteBtn;

    private boolean isInDeleteMode = false;

    public boolean isDynamicSticker = false;

    public Rect showRect = null;

    /**
     * 是否可以操作贴纸
     */
    private boolean isCanEdit = true;

    public StickerEditListener stickerEditListener;
    private boolean isClikeDelete = false;

    public StickerContainerView(Context context) {
        super(context);
    }

    public StickerContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StickerContainerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int ws, int hs) {
        super.onMeasure(ws, hs);
    }

    public void setCanEdit(boolean pCanEdit) {
        isCanEdit = pCanEdit;
    }

    public StickerView addSticker(Bitmap bitmap, int x, int y) {
        return addSticker(bitmap, null, 0, x, y);
    }

    /**
     * 添加贴图，若是文字贴图，text不为null
     *
     * @param bitmap
     * @param text
     * @param chosenColorIndex
     * @param x
     * @param y
     * @return
     */
    public StickerView addSticker(Bitmap bitmap, String text, int chosenColorIndex, int x, int y) {
        if (childTopMargin > 0) {
            y = y * height / UIUtils.getScreenHeight() + childTopMargin;
            final int bh = bitmap.getHeight();
            final int offset = y + bh - height - childTopMargin;
            if (offset > 0)
                y -= offset;
        } else if (childLeftMargin > 0) {
            x = x * width / UIUtils.getScreenWidth() + childLeftMargin;
            final int bw = bitmap.getWidth();
            final int offset = x + bw - width - childLeftMargin;
            if (offset > 0)
                x -= offset;
        }
        StickerView stickerView = new StickerView(getContext());
        stickerView.setBitmap(bitmap, x, y);
        stickerView.setType(StickerView.TYPE_TEXT);
        stickerView.setTextAndColorIndex(text, chosenColorIndex);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(stickerView, getChildCount() - 2, lp);
        mViews.add(stickerView);
        setCurrentEdit(stickerView);
        return stickerView;
    }

    /**
     * 添加动态贴纸
     *
     * @param bitmap
     * @param stickerEntity
     * @param listener
     */
    public void addSticker(Bitmap bitmap, StickerEntity stickerEntity, StickerView.onUpdateViewListener listener) {
        StickerEntity.StickerLocationEntity stickerAttrs = stickerEntity.getLocationScreen();
        if (stickerAttrs == null) {
            return;
        }
        float x = stickerAttrs.getOriginx();
        float y = stickerAttrs.getOriginy();
        float w = stickerAttrs.getWidth();
        float h = stickerAttrs.getHeight();
        float angle = stickerAttrs.getAngle();

        final StickerView stickerView = new StickerView(getContext());
        stickerView.setStickerEntity(stickerEntity);
        stickerView.setStickerId(stickerEntity.getId());
        stickerView.setBitmap(bitmap, x, y, w, h, angle);
        stickerView.setType(StickerView.TYPE_IMG);
        stickerView.setOnUpdateViewListener(listener);
        stickerView.setStickerId(stickerEntity.getId());
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(stickerView, lp);
        mViews.add(stickerView);
        setCurrentEdit(stickerView);
    }

    public void setCurrentEdit(StickerView stickerView) {
        mCurrentView = stickerView;
        bringCurrentViewToFront();
    }

    public int getChildWidth() {
        return width;
    }

    public int getChildHeight() {
        return height;
    }

    public int getChildTopMargin() {
        return childTopMargin;
    }

    public int getChildLeftMargin() {
        return childLeftMargin;
    }

    public void setParams(int width, int height, int leftMargin, int topMargin) {
        this.height = height;
        childTopMargin = topMargin;
        this.width = width;
        childLeftMargin = leftMargin;
        if (topMargin > 0) {
            if (coverTopView == null) {
                coverTopView = createTopBottomView();
//                addView(coverTopView);
            }
            if (coverBottomView == null) {
                coverBottomView = createTopBottomView();
//                addView(coverBottomView);
            }
            coverTopView.setLayoutParams(new LayoutParams(-1, topMargin));
            MarginLayoutParams bottom = new LayoutParams(-1, topMargin);
            bottom.setMargins(0, topMargin + height, 0, 0);
            coverBottomView.setLayoutParams(bottom);
            coverTopView.setVisibility(View.VISIBLE);
            coverBottomView.setVisibility(View.VISIBLE);
        } else if (leftMargin > 0) {
            if (coverTopView == null) {
                coverTopView = createTopBottomView();
//                addView(coverTopView);
            }
            if (coverBottomView == null) {
                coverBottomView = createTopBottomView();
//                addView(coverBottomView);
            }
            coverTopView.setLayoutParams(new LayoutParams(leftMargin, -1));
            MarginLayoutParams bottom = new LayoutParams(leftMargin, -1);
            bottom.setMargins(leftMargin + width, 0, 0, 0);
            coverBottomView.setLayoutParams(bottom);
            coverTopView.setVisibility(View.VISIBLE);
            coverBottomView.setVisibility(View.VISIBLE);
        }
    }

    private View createTopBottomView() {
        View topView = new View(getContext());
        topView.setBackgroundColor(Color.argb(51, 0, 0, 0));
        topView.setVisibility(GONE);

        return topView;
    }

    private void bringCurrentViewToFront() {
        if (mCurrentView != null)
            bringChildToFront(mCurrentView);
        if (coverTopView != null)
            bringChildToFront(coverTopView);
        if (coverBottomView != null)
            bringChildToFront(coverBottomView);
    }

    public void changeToDeleteMode(boolean mode, Animation.AnimationListener listener) {
        if (isInDeleteMode == mode) {
            return;
        } else {
            isInDeleteMode = mode;
        }
        if (isClikeDelete) {
            return;
        }
        if (isInDeleteMode) {
            deleteBtn.setImageResource(R.drawable.ic_moment_edit_delete_sticker_png);
            ScaleAnimation animation = new ScaleAnimation(1.0f, 1.4f, 1.0f, 1.4f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(200);
            animation.setFillAfter(true);
            animation.setAnimationListener(listener);
            deleteBtn.startAnimation(animation);
        } else {
            ScaleAnimation animation = new ScaleAnimation(1.4f, 1.0f, 1.4f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(200);
            animation.setFillAfter(true);
            animation.setAnimationListener(listener);
            deleteBtn.startAnimation(animation);
            deleteBtn.setImageResource(R.drawable.ic_moment_edit_delete_sticker_dark);

        }
    }

    public void setClickDeleteMode(boolean clickDelete) {
        isClikeDelete = clickDelete;
    }

    private long downTime = 0;
    private float mDownX = 0;
    private float mDownY = 0;

    private float mLeftDownX = 0;
    private float mLeftDownY = 0;

    private float mDistance = 0;
    private float mAngle = 0;
    private PointF downPoint = new PointF();
    private PointF moveCenterPoint = new PointF();
    Rect btnDeleteRect = new Rect();

    private Matrix mSavedMatrix = new Matrix();

    public boolean isInRectView(StickerView view, PointF pointF) {
        float x = pointF.x;
        float y = pointF.y;
        //修复偶尔viewRect == null 的bug
        if (null == view || null == view.rootRect || view.getVisibility() != View.VISIBLE) {
            return false;
        }

        return view.rootRect.contains((int) x, (int) y);
    }

    public boolean inInBorderDelteView(StickerView view, PointF pointF) {
        float x = pointF.x;
        float y = pointF.y;
        //修复偶尔viewRect == null 的bug
        if (null == view || null == view.rootRect) {
            return false;
        }
        return scaleRect(view.deleteIconRect, 2f).contains((int) x, (int) y);
    }

    private Rect scaleRect(Rect targetRect, float scale) {
        targetRect.inset(-(int) (targetRect.width() * scale), -(int) (targetRect.height() * scale));
        return targetRect;
    }

    public PointF getCenterPoint(StickerView view) {
        return view.centerPoint;
    }

    public StickerView getActionView(PointF pointF, int mode) {
        if (null == mCurrentView) {
            return null;
        }
        StickerView topView = null;
        if (mode == mode_drag) {
            if (isInRectView(mCurrentView, pointF)) {
                topView = mCurrentView;
            } else {
                for (int i = 0; i < mViews.size(); i++) {
                    StickerView view = mViews.get(i);
                    if (isInRectView(view, pointF)) {
                        topView = view;
                        break;
                    }
                }
            }
        } else {
            int minDistance = UIUtils.getPixels(50);
            for (int i = 0; i < mViews.size(); i++) {
                StickerView view = mViews.get(i);
                float distance = (float) getmDistancePoint(pointF, getCenterPoint(view));
                if (distance < minDistance) {
                    minDistance = (int) distance;
                    topView = view;
                }
            }
            if (minDistance == UIUtils.getPixels(50)) {
                topView = null;
            }
        }
        if (null != topView) {
            mCurrentView = topView;
            bringCurrentViewToFront();
            bringChildToFront(deleteBtn);
        }
        return topView;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mViews = new ArrayList<>();
    }

    public double getmDistancePoint(PointF pointA, PointF pointB) {
        //计算x的变化量
        float deltalx = pointA.x - pointB.x;
        //计算y的变化量
        float deltaly = pointA.y - pointB.y;
        //计算距离
        return Math.sqrt(deltalx * deltalx + deltaly * deltaly);

    }

    //返回两点间的距离
    public float getDistance(MotionEvent event) {
        //计算x的变化量
        float deltalx = event.getX(0) - event.getX(1);
        //计算y的变化量
        float deltaly = event.getY(0) - event.getY(1);
        //计算距离
        return (float) Math.sqrt(deltalx * deltalx + deltaly * deltaly);
    }

    //返回两点的中点
    public void getMidPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    //获得旋转角
    public float getAngle(MotionEvent event) {
        double deltalx = event.getX(0) - event.getX(1);
        double deltaly = event.getY(0) - event.getY(1);
        return (float) Math.atan2(deltalx, deltaly);
    }

    private int lastDistance = 0;
    boolean isOnView = false;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        int action = MotionEventCompat.getActionMasked(motionEvent);
        switch (action) {
            //单点触控处理
            case MotionEvent.ACTION_DOWN:
                //设置当前操作模式为drag
                downTime = System.currentTimeMillis();
                mMode = mode_drag;
                //获取当前坐标
                mDownX = motionEvent.getX();
                mDownY = motionEvent.getY();
                if (null != getActionView(new PointF(mDownX, mDownY), mMode)) {
                    if (mCurrentView != null) {
                        mSavedMatrix.set(mCurrentView.getCurMatrix());
                    }
                    isOnView = true;
                } else {
                    isOnView = false;
                }
                break;
        }
        return isOnView;
    }

    private float lastScale;
    private float lastAngle;
    private float lastX;
    private float lastY;

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!isCanEdit) {
            return false;
        }
        if (stickerEditListener != null)
            stickerEditListener.onTouch();
        int action = MotionEventCompat.getActionMasked(motionEvent);
        switch (action) {
            //多点触控处理
            case MotionEvent.ACTION_POINTER_DOWN:
                //获取两点间距离
                mDistance = getDistance(motionEvent);
                lastDistance = (int) mDistance;
                //获取旋转角
                mAngle = getAngle(motionEvent);
                //获取中点
                getMidPoint(downPoint, motionEvent);
                if (mMode == mode_drag_left && mCurrentView != null) {
                    mSavedMatrix.set(mCurrentView.getCurMatrix());
                    mMode = mode_zoom;
                    isOnView = true;
                } else {
                    mMode = mode_zoom;
                    if (null != getActionView(downPoint, mMode)) {
                        if (null != mCurrentView) {
                            mSavedMatrix.set(mCurrentView.getCurMatrix());
                        }
                        isOnView = true;
                    } else {
                        isOnView = false;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //缩放处理
                if (isOnView) {
                    if (null != stickerEditListener) {
                        stickerEditListener.beginEdit();
                        if (!isClikeDelete && deleteBtn.getVisibility() != VISIBLE) {
                            deleteBtn.setVisibility(VISIBLE);
                        }
                        if (btnDeleteRect.left == 0) {
                            deleteBtn.getGlobalVisibleRect(btnDeleteRect);
                        }
                    }
                    if (mMode == mode_zoom) {
                        getMidPoint(moveCenterPoint, motionEvent);
                        //获取缩放比率
                        float mscale = getDistance(motionEvent) / mDistance;
                        lastDistance = (int) mDistance;
                        //获取旋转角，这里可以不用
                        float angle = mAngle - getAngle(motionEvent);
                        //以中点为中心，进行缩放
                        float deltalx = moveCenterPoint.x - downPoint.x;
                        float deltaly = moveCenterPoint.y - downPoint.y;
                        lastScale = mscale;
                        lastAngle = angle;
                        lastX = deltalx;
                        lastY = deltaly;
                        showToast();
                        if (mCurrentView != null) {
                            mCurrentView.setPostScale(mSavedMatrix, (float) Math.toDegrees(angle), mscale, mscale, deltalx, deltaly);
                        }
                    } else if (mMode == mode_drag)//平移处理
                    {
                        //计算平移量
                        float deltalx = motionEvent.getX() - mDownX;
                        float deltaly = motionEvent.getY() - mDownY;
                        showToast();
                        //平移
                        if (mCurrentView != null) {
                            mCurrentView.setNewMatrix(mSavedMatrix, deltalx, deltaly);
                        }
                        if (null != stickerEditListener) {
                            stickerEditListener.editingStickerView(mCurrentView);
                            if (isDeleteArea((int) motionEvent.getX(), (int) motionEvent.getY())) {
                                changeToDeleteMode(true, null);
                            } else {
                                changeToDeleteMode(false, null);
                            }
                        }
                    } else if (mMode == mode_drag_left)//平移处理
                    {
                        float x = motionEvent.getX() - mLeftDownX;
                        float y = motionEvent.getY() - mLeftDownY;
                        //获取缩放比率
                        float mscale = lastScale;
                        lastDistance = (int) mDistance;
                        float angle = lastAngle;
                        float deltalx = lastX + x;
                        float deltaly = lastY + y;
                        showToast();
                        if (mCurrentView != null) {
                            mCurrentView.setPostScale(mSavedMatrix, (float) Math.toDegrees(angle), mscale, mscale, deltalx, deltaly);
                        }
                        if (null != stickerEditListener) {
                            stickerEditListener.editingStickerView(mCurrentView);
                            if (isDeleteArea((int) motionEvent.getX(), (int) motionEvent.getY())) {
                                changeToDeleteMode(true, null);
                            } else {
                                changeToDeleteMode(false, null);
                            }
                        }
                    }

                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                final long upTime = System.currentTimeMillis();
                if (upTime - downTime <= MAX_CLICK_TIME && isOnView) {
                    if (isClikeDelete && mCurrentView != null && mCurrentView.isShowEditBorder() && inInBorderDelteView(mCurrentView, new PointF(mDownX, mDownY))) {
                        changeToDeleteMode(true, null);
                    }
                    onStickerClick();
                    break;
                }

                mDistance = lastDistance;
                if (null != mCurrentView) {
                    mSavedMatrix.set(mCurrentView.getCurMatrix());
                }
                mLeftDownY = 0;
                mLeftDownX = 0;
                isOnView = false;
                mMode = mode_none;
                if (null != stickerEditListener) {
                    endEditSticker();
                }

                if (null != mCurrentView) {
                    mCurrentView.tempAngle = 720;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mDistance = lastDistance;
                int actionIndex = motionEvent.getActionIndex();
                if (actionIndex == 0) {
                    mLeftDownX = motionEvent.getX(1);
                    mLeftDownY = motionEvent.getY(1);
                } else {
                    mLeftDownX = motionEvent.getX(0);
                    mLeftDownY = motionEvent.getY(0);
                }
                mMode = mode_drag_left;
                break;
        }
        return true;
    }

    Matrix matrix;
    RectF rectF;

    private boolean isDeleteArea(int fingerX, int fingerY) {

        boolean flag = false;
        flag = btnDeleteRect.contains(fingerX, fingerY);

        if (null == showRect || null == mCurrentView || null == mCurrentView.centerPoint) {
            return flag;
        }
        flag = flag | !showRect.contains((int) mCurrentView.centerPoint.x, (int) mCurrentView.centerPoint.y);

        return flag;
    }

    private void showToast() {
        //8.0改版，干掉所有贴纸的toast提示。
        return;
        //        if (mCurrentView == null)
        //            return;
        //        if (childLeftMargin > 0 || childTopMargin > 0) {
        //            if (showToast) {
        //                Rect r = mCurrentView.viewRect;
        //                if (childTopMargin > 0) {
        //                    final int top = childTopMargin;
        //                    final int bottom = top + height;
        //                    final int vh = r.centerY();
        //                    if (vh <= top || vh >= bottom) {
        //                        Toaster.show(R.string.moment_edit_video_sticker_out);
        //                        showToast = false;
        //                    }
        //                }
        //            }
        //        }
    }

    private void endEditSticker() {
        if (isInDeleteMode) {
            mViews.remove(mCurrentView);
            removeView(mCurrentView);

            if (null != stickerEditListener) {
                stickerEditListener.onDeleteSticker(mCurrentView);
            }
            if (mViews.size() == 0) {
                mCurrentView = null;
            } else {
                //bug fixed 动态贴纸放到最大后，无法移动贴纸。
                mCurrentView = mViews.get(mViews.size() - 1);
            }
            if (!isClikeDelete) {
                changeToDeleteMode(false, new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        endEditIndeed();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            } else {
                changeToDeleteMode(false, null);
                endEditIndeed();
            }

        } else {
            endEditIndeed();
        }

    }

    public void endEditIndeed() {
        deleteBtn.clearAnimation();
        deleteBtn.setVisibility(GONE);
        stickerEditListener.endEdit();
    }

    private void onStickerClick() {
        if (mCurrentView != null && stickerEditListener != null) {

            if (stickerEditListener != null) {
                stickerEditListener.onStickerClick(mCurrentView);
            }
        }
    }

    public void removeStickerViewById(int stickerId) {

        StickerView needDeleteSticker = null;
        for (StickerView stickerView : mViews) {
            StickerEntity stickerEntity = stickerView.getStickerEntity();
            if (stickerEntity != null && stickerId == stickerEntity.getId()) {
                needDeleteSticker = stickerView;
                break;
            }
        }

        if (needDeleteSticker == null) {
            return;
        }
        mViews.remove(needDeleteSticker);
        removeView(needDeleteSticker);
        if (null != stickerEditListener) {
            stickerEditListener.onDeleteSticker(needDeleteSticker);
        }
        if (mViews.size() == 0) {
            mCurrentView = null;
        } else {
            //bug fixed 动态贴纸放到最大后，无法移动贴纸。
            mCurrentView = mViews.get(mViews.size() - 1);
        }
    }

    public void setInSaveMode(boolean isSaveMode) {
        if (mCurrentView == null) {
            return;
        }
    }

    public boolean hasImageSticker() {
        if (mViews != null && mViews.size() > 0) {
            for (StickerView stickerView : mViews) {
                if (stickerView.getType() == StickerView.TYPE_IMG) {
                    return true;
                }
            }
        }
        return false;
    }

    public void checkStickerNeedShow(long currentPlayingTime) {
        if (mViews != null) {
            for (StickerView stickerView : mViews) {
                if (stickerView.getStickerEntity() == null) {
                    continue;
                }
                Long startShowTime = stickerView.getStickerEntity().getStartShowTime();
                Long endShowTime = stickerView.getStickerEntity().getEndShowTime();
                if (startShowTime != null && endShowTime != null) {
                    if (startShowTime <= currentPlayingTime && endShowTime >= currentPlayingTime) {
                        if (stickerView.getVisibility() != VISIBLE) {
                            stickerView.setVisibility(VISIBLE);
                        }
                    } else if (stickerView.getVisibility() != GONE) {
                        stickerView.setVisibility(GONE);
                    }

                } else if (stickerView.getVisibility() != VISIBLE) {
                    stickerView.setVisibility(VISIBLE);
                }
            }
        }
    }


    public void clearAllShowClickEditState() {
        if (mViews != null) {
            for (StickerView stickerView : mViews) {
                stickerView.setShowEditBorder(false);
            }
        }
    }

    public interface StickerEditListener {
        void onTouch();

        void beginEdit();

        void editingStickerView(StickerView stickerView);

        void endEdit();

        void onDeleteSticker(StickerView stickerView);

        void onStickerClick(StickerView view);

    }

    public StickerView getCurrentEditView() {
        return mCurrentView;
    }

    public boolean hasStickers() {
        return null != mViews && mViews.size() > 0;
    }

    /**
     * 获取当前动态贴纸数量
     *
     * @return
     */
    public int getStickerCount() {
        if (null == mViews) {
            return 0;
        }

        return mViews.size();
    }

}
