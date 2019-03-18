package com.mm.sdkdemo.widget.videorangebar;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.IntRange;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.immomo.mmutil.log.Log4Android;
import com.mm.sdkdemo.R;

import java.security.InvalidParameterException;
import java.util.List;

/**
 * Project MomoDemo
 * Package com.mm.momo.view
 * Created by tangyuchun on 2/14/17.
 */

public class VideoRangeBar extends HorizontalScrollView {
    public VideoRangeBar(Context context) {
        super(context);
        init(context);
    }

    public VideoRangeBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VideoRangeBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VideoRangeBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        rangeSelectorView = new VideoRangeSelectorView(context);

        //额外的高度，选框选中状态下，边框比图片高度稍微高一些
        extraHeight = getResources().getDimensionPixelSize(R.dimen.vrb_range_border_width) * 2;
        rangeSelectorView.setExtraHeight(extraHeight);

        addView(rangeSelectorView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);

        setHorizontalFadingEdgeEnabled(false);
    }

    /**
     * 缩略图的尺寸
     */
    private int imageWidth, imageHeight;
    /**
     * 左右两端空白尺寸
     */
    private int emptyHeaderFooterWidth;
    private VideoRangeSelectorView rangeSelectorView;
    private LinearLayout thumbnailLayout;
    private View emptyHeaderView, emptyFooterView;
    /**
     * 是否可以触摸 在自动滚动时，不能手动再滚动 将canTouch设置为false，即可禁止手动滚动
     */
    private boolean canTouch = true;
    private int extraHeight;
    private int imageCount = 0;//展示的总图片个数
    private long perImageDurationInMs = 1000;//一张图片代表的时长

    public void setImageSize(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new InvalidParameterException("请先设置图片尺寸");
        }
        this.imageWidth = width;
        this.imageHeight = height;
    }

    public void setEmptyHeaderFooterWidth(int emptyHeaderFooterWidth) {
        this.emptyHeaderFooterWidth = emptyHeaderFooterWidth;
        rangeSelectorView.setEmptyHeaderFooterWidth(emptyHeaderFooterWidth);
    }

    public void appendImageList(Bitmap... newImages) {
        if (imageWidth <= 0 || imageHeight <= 0) {
            throw new InvalidParameterException("请先设置图片尺寸");
        }
        showImages(true, newImages);
    }

    public void setImageList(Bitmap... bmps) {
        if (imageWidth <= 0 || imageHeight <= 0) {
            throw new InvalidParameterException("请先设置图片尺寸");
        }
        showImages(false, bmps);
    }

    public void setCancelSelectOnTouchOutside(boolean cancelSelectedByClick) {
        this.rangeSelectorView.setCancelSelectOnTouchOutside(cancelSelectedByClick);
    }

    /**
     * 设置视频总时长 会在此处初始化整个view
     *
     * @param totalVideoDurationInMs 视频总时长
     * @param imageCount             展示的缩略图个数 参数必须是 imageCount>= totalVideoDurationInMs/perImageDuration
     * @param perImageDurationInMs   一张完整图片代表的视频长度，比如每隔5s取一次缩略图，则 perImageDuration=5000
     */
    public void setTotalVideoDurationInMs(long totalVideoDurationInMs, int imageCount, long perImageDurationInMs) {
        this.rangeSelectorView.setTotalVideoDurationInMs(totalVideoDurationInMs);
        this.imageCount = imageCount;
        this.perImageDurationInMs = perImageDurationInMs;
        setupViews(totalVideoDurationInMs);
    }

    private void setupViews(long totalVideoDurationInMs) {
        //添加 emptyHeader
        setupEmptyHeaderView();
        //添加 中间

        int wholeCount = (int) (totalVideoDurationInMs / perImageDurationInMs);//完整的图片个数
        if (wholeCount > imageCount) {
            throw new InvalidParameterException("参数错误，imageCount设置错误，imageCount必须>= totalVideoDurationInMs/perImageDuration");
        }
        int thumbTotalWidth = wholeCount * imageWidth;
        if (imageCount != wholeCount) {
            long lastDuration = totalVideoDurationInMs - (wholeCount * perImageDurationInMs);//最后剩余的视频长度
            int lastImageWidth = (int) ((lastDuration / (float) perImageDurationInMs) * imageWidth);
            thumbTotalWidth += lastImageWidth;
        }
        if (thumbnailLayout == null) {
            thumbnailLayout = new LinearLayout(getContext());
            thumbnailLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(thumbTotalWidth, imageHeight + extraHeight);
            lp.gravity = Gravity.CENTER_VERTICAL;
            thumbnailLayout.setLayoutParams(lp);
            rangeSelectorView.addView(thumbnailLayout);
        }
        //添加 emptyFooter
        setupEmptyFooterView();
    }

    private void setupEmptyHeaderView() {
        if (emptyHeaderFooterWidth > 0) {
            int index = rangeSelectorView.indexOfChild(emptyHeaderView);
            if (index >= 0) {
                ViewGroup.LayoutParams lp = emptyHeaderView.getLayoutParams();
                lp.width = emptyHeaderFooterWidth;
                lp.height = imageHeight;
            } else {
                emptyHeaderView = buildEmptyHeaderFooterView();
                rangeSelectorView.addView(emptyHeaderView);
            }
        } else {
            rangeSelectorView.removeView(emptyHeaderView);
            emptyHeaderView = null;
        }
    }

    private void setupEmptyFooterView() {
        if (emptyHeaderFooterWidth > 0) {
            int index = rangeSelectorView.indexOfChild(emptyFooterView);
            if (index >= 0) {
                ViewGroup.LayoutParams lp = emptyFooterView.getLayoutParams();
                lp.width = emptyHeaderFooterWidth;
                lp.height = imageHeight;
            } else {
                emptyFooterView = buildEmptyHeaderFooterView();
                rangeSelectorView.addView(emptyFooterView);
            }
        } else {
            rangeSelectorView.removeView(emptyFooterView);
            emptyFooterView = null;
        }
    }

    public void addRangeBarListener(RangeBarListener listener) {
        rangeSelectorView.addOnSelectedRangeChangedListener(listener);
    }

    public void removeRangeBarListener(RangeBarListener listener) {
        rangeSelectorView.removeOnSelectedRangeChangedListener(listener);
    }

    public void setCanTouch(boolean canTouch) {
        this.canTouch = canTouch;
    }

    public int getRangeCount() {
        return rangeSelectorView.getRangeCount();
    }

    /**
     * 添加一个选区
     *
     * @param start       开始时间
     * @param end         结束时间
     * @param speed       速度 如果不是调速的话，传入 1即可，代表不变速
     * @param playAddAnim 是否在添加时显示一个动画效果
     * @param selected    是否将添加的选区选中
     * @param minDuration 最短时长
     * @param maxDuration 最长时长
     * @param type        选区类型 参考{@link VideoRange.RangeType}
     * @return
     */
    public VideoRange addRange(long start,
                               long end,
                               float speed,
                               boolean playAddAnim,
                               boolean selected,
                               long minDuration,
                               long maxDuration,
                               @IntRange(from = VideoRange.RangeType.TYPE_NORMAL, to = VideoRange.RangeType.TYPE_FIXED_LENGTH) int type) {
        return rangeSelectorView.addRange(start,
                end,
                speed,
                playAddAnim,
                selected,
                minDuration,
                maxDuration,
                type);
    }

    public void removeRange(VideoRange range) {
        if (rangeSelectorView != null) {
            rangeSelectorView.removeRange(range);
        }
    }

    public void clearAllRanges() {
        rangeSelectorView.clearAllRanges();
    }

    public void cancelSelectedRange() {
        rangeSelectorView.cancelSelectedRange();
    }

    private void showImages(boolean append, Bitmap... images) {
        if (images == null) {
            if (thumbnailLayout != null) {
                thumbnailLayout.removeAllViews();
            }
            invalidate();
            return;
        }

        long totalVideoDurationInMs = rangeSelectorView.getTotalVideoDurationInMs();
        if (totalVideoDurationInMs <= 0) {
            throw new InvalidParameterException("参数错误，totalVideoDurationInMs 必须大于0");
        }

        int wholeImageCount = (int) (totalVideoDurationInMs / perImageDurationInMs);//完整图片的个数
        int totalImageCount = (int) Math.ceil(((float) totalVideoDurationInMs / perImageDurationInMs));//总的图片个数
        /**
         * 最后一段的时长 视频按1s切割，最后剩余的不足1s的时长
         */
        long lastDuration = totalVideoDurationInMs - perImageDurationInMs * (wholeImageCount);
        log("showImages 总图片数 " + totalImageCount + "   最后一段长度 " + lastDuration);

        if (!append) {
            thumbnailLayout.removeAllViews();
        }
        int curImageCount = thumbnailLayout.getChildCount();//减去 emptyHeaderView 就是目前图片的数量
        //要处理最后一张图片的尺寸，最后一张有可能不够1s，所以宽度不应该达到
        for (int i = 0; i < images.length; i++) {
            ImageView view;
            //存在最后一段不完整图片
            if (lastDuration > 0 && curImageCount == totalImageCount - 1) {
                log("showImages 是最后一张图片 " + totalImageCount);
                view = buildImageItemView((float) lastDuration / perImageDurationInMs);
            } else {
                view = buildImageItemView(1f);
            }
            view.setImageBitmap(images[i]);
            thumbnailLayout.addView(view);
            curImageCount++;
        }
    }

    /**
     * 指定的时间戳是否在选框范围内
     *
     * @param time
     * @return 如果在选框范围内，则返回该选框，否则返回 null
     */
    public VideoRange inVideoRange(long time) {
        return rangeSelectorView.inVideoRange(time);
    }

    /**
     * 该选区是否选中状态
     *
     * @param range
     * @return
     */
    public boolean isRangeSelected(VideoRange range) {
        return range.isSelected();
    }

    public VideoRange getSelectedRange() {
        return rangeSelectorView.getSelectedRange();
    }

    public List<VideoRange> getAllRanges() {
        return rangeSelectorView.getAllRanges();
    }

    private View buildEmptyHeaderFooterView() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(emptyHeaderFooterWidth, imageHeight);
        lp.gravity = Gravity.CENTER_VERTICAL;
        View view = new View(getContext());
        view.setLayoutParams(lp);
        return view;
    }

    private ImageView buildImageItemView(float percent) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) (imageWidth * percent), imageHeight);
        lp.gravity = Gravity.CENTER_VERTICAL;
        ImageView view = new ImageView(getContext());
        view.setLayoutParams(lp);
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return view;
    }

    /**
     * 获得视频对应的View的宽度
     *
     * @return
     */
    public int getWidthOfVideo() {
        return rangeSelectorView.getWidthOfVideo();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!canTouch) {
            return false;
        }
        if (rangeSelectorView.handleTouchEvent(ev, getScrollX())) {
            return true;
        }
        return super.onTouchEvent(ev);
    }

    private OnScrollListener mOnScrollListener;

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnScrollListener != null) {
            int x = getScrollX();
            //计算滚动对应的
            mOnScrollListener.onScroll(getScrollX());
        }
    }

    private void log(String msg) {
        if (Log4Android.getInstance().isDebug()) {
            Log.d("VideoRangeBar", "tang---" + msg);
        }
    }

    public void scrollToTimestamp(long timestamp, boolean smooth) {
        if (timestamp > rangeSelectorView.getTotalVideoDurationInMs()) {
            log("scrollToTimestamp error " + timestamp + "/" + rangeSelectorView.getTotalVideoDurationInMs());
            return;
        }
        float percent = (float) timestamp / rangeSelectorView.getTotalVideoDurationInMs();
        int distance = (int) (percent * rangeSelectorView.getWidthOfVideo());

        //        log("scrollToTimestamp scrollX " + distance);
        if (smooth) {
            smoothScrollTo(distance, 0);
        } else {
            scrollTo(distance, 0);
        }
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        mOnScrollListener = onScrollListener;
    }

    public interface OnScrollListener {
        void onScroll(int scrollX);
    }
}