package com.mm.recorduisdk.recorder.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.immomo.moment.config.MRecorderActions;
import com.immomo.moment.mediautils.VideoDataRetrieverBySoft;
import com.immomo.moment.mediautils.cmds.EffectModel;
import com.immomo.moment.mediautils.cmds.TimeRangeScale;
import com.immomo.moment.mediautils.cmds.VideoCut;
import com.immomo.moment.mediautils.cmds.VideoEffects;
import com.mm.base_business.base.BaseFragment;
import com.mm.base_business.utils.UIHandler;
import com.mm.mediasdk.IVideoProcessor;
import com.mm.mediasdk.MoMediaManager;
import com.mm.mediasdk.utils.UIUtils;
import com.mm.mmutil.log.Log4Android;
import com.mm.mmutil.task.MomoTaskExecutor;
import com.mm.mmutil.toast.Toaster;
import com.mm.recorduisdk.R;
import com.mm.recorduisdk.recorder.MediaConstants;
import com.mm.recorduisdk.recorder.model.Video;
import com.mm.recorduisdk.utils.VideoUtils;
import com.mm.recorduisdk.widget.seekbar.OnSeekChangeListener;
import com.mm.recorduisdk.widget.seekbar.SeekParams;
import com.mm.recorduisdk.widget.seekbar.TickSeekBar;
import com.mm.recorduisdk.widget.videorangebar.RangeBarListener;
import com.mm.recorduisdk.widget.videorangebar.VideoRange;
import com.mm.recorduisdk.widget.videorangebar.VideoRangeBar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Project momodev
 * Package com.mm.momo.moment.fragment
 * Created by tangyuchun on 2/16/17.
 */

public class VideoSpeedAdjustFragment extends BaseFragment implements View.OnClickListener {

    private static final float[] speedValueArray = {2f, 1.5f, 1f, 0.5f, 0.2f};

    private static final long MIN_RANGE_DURATION = 2 * 1000;//最短2s的选区
    private static final long MAX_RANGE_DURATION = 5 * 60 * 1000;
    private final int DEFAULT_SPEED_INDEX = 2;
    private final int MAX_RANGE_COUNT = 5;

    private String videoPath;
    private long videoDuration = -1L;
    private int videoWidth, videoHeight, videoRotate;

    private FrameLayout surfaceLayout;
    private SurfaceView surfaceView;
    private TickSeekBar slideBar;
    private VideoRangeBar rangeBar;
    private TextView tvTime;
    private View btnPlay;

    private IVideoProcessor process;

    /**
     * 是否正在播放
     */
    private boolean isPlaying = false;

    /**
     * 是否需要调用prepare
     */
    private boolean isNeedPrepare = false;
    /**
     * 开始播放的位置 onPlaying()回调来的时间戳是相对于播放七点的，所以需要把播放起点纪录下来
     */
    private long startPlayPosMS = 0L;
    private long endPlayPosMS = 0;
    /**
     * 抽帧相关变量
     */
    VideoDataRetrieverBySoft videoDataRetrieve = new VideoDataRetrieverBySoft();
    List<VideoDataRetrieverBySoft.Node> videoNodes = new ArrayList<>();

    private int thumbnailWidth, thumbnailHeight;
    private List<TimeRangeScale> initTimeRanges = null;

    private long playDuration = 0;

    @Override
    protected void onLoad() {
        //do nothing
    }

    @Override
    protected boolean isNeedLazyLoad() {
        return false;
    }

    @Override
    protected int getLayout() {
        return R.layout.frag_video_speed_adjust;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            videoPath = bundle.getString(MediaConstants.KEY_VIDEO_PATH);
            File file = new File(videoPath);
            if (!file.exists() || file.length() <= 0) {
                Toaster.showInvalidate("视频录制错误，请重试");
                getActivity().finish();
                return;
            }
            //从上次编辑参数中提取调速的参数
            EffectModel mInitEffectModel = (EffectModel) bundle.getSerializable(MediaConstants.KEY_VIDEO_SPEED_PARAMS);
            if (mInitEffectModel != null) {
                VideoEffects effects = mInitEffectModel.getVideoEffects();
                if (effects != null && effects.getTimeRangeScales() != null) {
                    initTimeRanges = effects.getTimeRangeScales();
                }
            }
        }
    }

    @Override
    protected void initViews(View contentView) {
        surfaceLayout = findViewById(R.id.video_Speed_surface_placeholder);
        tvTime = findViewById(R.id.moment_speed_time);
        slideBar = findViewById(R.id.moment_speed_slideindicatorbar);
        rangeBar = findViewById(R.id.moment_speed_video_range_bar);
        btnPlay = findViewById(R.id.moment_speed_btn_play);

        surfaceLayout.setOnClickListener(this);
        findViewById(R.id.moment_speed_btn_close).setOnClickListener(this);
        findViewById(R.id.moment_speed_btn_ok).setOnClickListener(this);
        //给此View设置一个点击事件，用于屏蔽 SurfaceView的点击区域
        findViewById(R.id.video_speed_bottom_layout).setOnClickListener(this);

        slideBar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {

            }

            @Override
            public void onStartTrackingTouch(TickSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(TickSeekBar seekBar) {
                float speed = seekBar.getProgress();
                if (speed == 1.0f) {
                    VideoRange range = rangeBar.getSelectedRange();
                    if (range != null) {
                        removeRange(range);
                        refreshSlideBar(true, true);
                        //                        showSpeedText(speedTextArray[DEFAULT_SPEED_INDEX]);
                    }
                } else {
                    //如果是在选区之内，则调整选区速度
                    long timestamp = getIndicatorLineTimestampInVideo();
                    VideoRange range = rangeBar.inVideoRange(timestamp);
                    if (range == null) {
                        if (rangeBar.getRangeCount() >= MAX_RANGE_COUNT) {
                            Toaster.showInvalidate("变速已达上限");
                            refreshSlideBar(false, true);
                            //取消高亮的选区
                            rangeBar.cancelSelectedRange();
                        } else {
                            //不在选框之内，则可以新增一个选框
                            addRange(timestamp, speed);
                        }
                    } else {
                        //选中的选区才调整速度
                        if (rangeBar.isRangeSelected(range)) {
                            //修改选框速度即可
                            range.setSpeed(1 / speed);
                            //                            showSpeedText(speedTextArray[indexOfIndicator]);
                        }
                    }
                }
            }
        });

        //        slideBar.setIndicators(speedTextArray);
        //        slideBar.setIndicatorBuilder(new SlideIndicatorBuilder() {
        //            @Override
        //            public View buildIndicator(SlideIndicatorBar parent) {
        //                tvSlideIndicator = new TextView(getContext());
        //                int size = UIUtils.getPixels(33f);
        //                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(size, size);
        //                tvSlideIndicator.setLayoutParams(lp);
        //                tvSlideIndicator.setTextSize(10);
        //                tvSlideIndicator.setGravity(Gravity.CENTER);
        //                tvSlideIndicator.setTextColor(UIUtils.getColor(R.color.moment_slide_indicator_text_color));
        //                tvSlideIndicator.setBackgroundResource(R.drawable.bg_moment_slide_indicator);
        //                tvSlideIndicator.setText(speedTextArray[DEFAULT_SPEED_INDEX]);
        //                return tvSlideIndicator;
        //            }
        //        });
        //        slideBar.addIndicatorSlideListener(new SlideIndicatorBar.OnIndicatorSlideListener() {
        //            @Override
        //            public void onIndicatorSliding(View indicator, int indexOfIndicator) {
        //                Log4Android.getInstance().i("tang------onIndicatorSliding " + indexOfIndicator);
        //                if (currentIndex != indexOfIndicator) {
        //                    currentIndex = indexOfIndicator;
        //                    tvSlideIndicator.setText(speedTextArray[indexOfIndicator]);
        //                }
        //            }
        //
        //            @Override
        //            public void onIndicatorSettled(View indicator, int indexOfIndicator) {
        //                currentIndex = indexOfIndicator;
        //                //当变速档发生切换后
        //                handler.sendEmptyMessage(MyHandler.ACTION_UPDATE_INDICATOR_TEXT);
        //                Log4Android.getInstance().i("tang------onIndicatorSettled " + indexOfIndicator + "   " + speedTextArray[indexOfIndicator]);
        //                //切换回原速度后，选中的选区就取消
        //                if (indexOfIndicator == DEFAULT_SPEED_INDEX) {
        //                    VideoRange range = rangeBar.getSelectedRange();
        //                    if (range != null) {
        //                        removeRange(range);
        //                        refreshSlideBar(true, true);
        //                        showSpeedText(speedTextArray[DEFAULT_SPEED_INDEX]);
        //                    }
        //                } else {
        //                    //如果是在选区之内，则调整选区速度
        //                    long timestamp = getIndicatorLineTimestampInVideo();
        //                    VideoRange range = rangeBar.inVideoRange(timestamp);
        //                    if (range == null) {
        //                        if (rangeBar.getRangeCount() >= MAX_RANGE_COUNT) {
        //                            Toaster.showInvalidate("变速已达上限");
        //                            refreshSlideBar(false, true);
        //                            //取消高亮的选区
        //                            rangeBar.cancelSelectedRange();
        //                        } else {
        //                            //不在选框之内，则可以新增一个选框
        //                            addRange(timestamp, indexOfIndicator);
        //                        }
        //                    } else {
        //                        //选中的选区才调整速度
        //                        if (rangeBar.isRangeSelected(range)) {
        //                            //修改选框速度即可
        //                            range.setSpeed(speedValueArray[indexOfIndicator]);
        //                            showSpeedText(speedTextArray[indexOfIndicator]);
        //                        }
        //                    }
        //                }
        //            }
        //        });
        //        //默认在中间位置
        //        slideBar.setCurrentIndicatorIndex(DEFAULT_SPEED_INDEX);
        //列表的左右空白长度必须为屏幕一半，这样可以严格限制滚动区域在视频时间范围内
        rangeBar.setEmptyHeaderFooterWidth(UIUtils.getScreenWidth() / 2);

        thumbnailHeight = UIUtils.getDimensionPixelSize(R.dimen.video_range_bar_item_height);
        thumbnailWidth = UIUtils.getDimensionPixelSize(R.dimen.video_range_bar_item_width);
        rangeBar.setImageSize(thumbnailWidth, thumbnailHeight);

        View indicatorLine = findViewById(R.id.moment_speed_video_range_bar_line);
        ViewGroup.LayoutParams lp = indicatorLine.getLayoutParams();
        if (lp != null) {
            lp.height = thumbnailHeight + UIUtils.getPixels(8f);
        }

        rangeBar.setOnScrollListener(new VideoRangeBar.OnScrollListener() {
            //纪录滚动时的速度，避免频繁刷新View
            private float curScrollSpeed = 0f;
            /**
             * 中线指的时间 单位s  避免频繁刷新时间
             */
            private int curTimeInSecond = 0;

            @Override
            public void onScroll(int scrollX) {
                if (videoDuration <= 0 || scrollX < 0) {
                    return;
                }
                long timestamp = getIndicatorLineTimestampInVideo();

                //没有激活变速区时，拖曳视频锚点到任何一帧，五档调速栏应显示对应的速度
                if (!isPlaying) {
                    VideoRange rage = rangeBar.inVideoRange(timestamp);
                    if (rage != null) {
                        float speed = rage.getSpeed();
                        if (curScrollSpeed != speed) {
                            curScrollSpeed = speed;
                            slideBar.setProgress(1 / speed);
                            //                                    tvSlideIndicator.setText(speedTextArray[i]);
                            //                                    slideBar.setCurrentIndicatorIndex(i);
                        }
                    } else {
                        if (curScrollSpeed != speedValueArray[DEFAULT_SPEED_INDEX]) {
                            curScrollSpeed = speedValueArray[DEFAULT_SPEED_INDEX];
                            slideBar.setProgress(1);
                            //                            tvSlideIndicator.setText(speedTextArray[DEFAULT_SPEED_INDEX]);
                            //                            slideBar.setCurrentIndicatorIndex(DEFAULT_SPEED_INDEX);
                        }
                    }
                }
                int second = (int) (timestamp / 1000);
                if (curTimeInSecond != second) {
                    curTimeInSecond = second;
                    tvTime.setText(getSecondText(curTimeInSecond));
                }
            }
        });
        rangeBar.addRangeBarListener(new RangeBarListener() {
            /**
             * 当选中的选框发生切换时
             * @param selectedRange 可能为空，当 ==null 时，代表没有选中的选框
             */
            @Override
            public void onSelectedRangeSwitched(VideoRange selectedRange) {
                if (selectedRange != null) {
                    //当选中的选框发生切换
                    refreshRangeTimeText(selectedRange);
                    //同时也要刷新 SlideIndicatorBar
                    //刷新 速度控件
                    float speed = selectedRange.getSpeed();
                    slideBar.setProgress(1 / speed);
                    refreshSlideBar(true, false);
                }
            }

            /**
             *当选中的选框发生变化时  包括位置移动，时间戳变化都会触发
             * @param range
             */
            @Override
            public void onRangeMoving(VideoRange range) {
                refreshRangeTimeText(range);
            }

            @Override
            public void onRangeMoveStopped(VideoRange range) {
                Log4Android.getInstance().i("tang----停止移动选区");
            }
        });
    }

    private String getSecondText(long second) {
        if (second < 10) {
            return "00:0" + second;
        }
        return "00:" + second;
    }

    private void removeRange(VideoRange range) {
        //如果正在播放，则停止播放
        if (process != null && process.isPlaying()) {
            pausePlay();
        }
        if (range != null) {
            rangeBar.removeRange(range);
        }
    }

    private void addRange(long start, float speed) {
        //添加选区时，停止滚动
        VideoRange range = rangeBar.addRange(
                start,
                start + getMinRangeDuration(),
                1 / speed,
                true,
                true,
                getMinRangeDuration(),
                MAX_RANGE_DURATION,
                VideoRange.RangeType.TYPE_NORMAL);
        if (range != null) {
            //选区不能拖动
            range.setCanMove(false);
            //            showSpeedText(speedTextArray[selectSpeedIndex]);
            pausePlay();
        } else {
            Toaster.showInvalidate("当前位置无足够空间");
        }
    }

    private void pausePlay() {
        isPlaying = false;
        try {
            if (process != null) {
                process.pause();
            }
        } catch (Exception ex) {
            Log4Android.getInstance().e(ex);
        }
        playDuration = 0;
        rangeBar.setCanTouch(true);
        refreshViewVisible(slideBar, true, true);
    }

    private void stopPlay() {
        initSurfaceHolder = false;
        if (process != null) {
            process.pause();
        }
        isPlaying = false;
        playDuration = 0;
        rangeBar.setCanTouch(true);

        removeSurfaceView();
    }

    private void startPlay(long start, long end, float speed, boolean isResume) {
        Log4Android.getInstance().i("tang----startPlay start:" + start + " end:" + end + "  speed:" + speed);

        VideoEffects videoEffects = new VideoEffects();
        videoEffects.setVideoCuts(new VideoCut(videoPath, start, end));
        if (speed != 1.0f) {
            videoEffects.setTimeRangeScales(new TimeRangeScale(0, end - start, speed));
        }
        //先暂停
        //        pausePlay();
        rangeBar.scrollToTimestamp(start, true);

        startPlayPosMS = start;
        endPlayPosMS = end;
        isPlaying = true;

        if (isResume) {
            boolean prepare = true;
            if (isFirstResume || isNeedPrepare) {
                prepare = process.prepareVideo(videoPath, null, 0, 0, 100, 0);
                process.setVideoEffect(videoEffects);
                isNeedPrepare = false;
            } else {
                process.resume();
            }
            if (prepare) {
                //禁止掉手动滚动进度条
                playDuration = 0;
                rangeBar.setCanTouch(false);
                refreshViewVisible(btnPlay, false, false);
            }
        } else {
            process.setVideoEffect(videoEffects);
            process.updateEffect(0, true);
            refreshViewVisible(slideBar, false, false);

        }
    }

    private void refreshSlideBar(boolean show, boolean playAnim) {
        if (playAnim) {
            if (show) {
                if (slideBar.getVisibility() != View.VISIBLE) {
                    AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
                    alpha.setDuration(200);
                    slideBar.clearAnimation();
                    slideBar.startAnimation(alpha);
                    slideBar.setVisibility(View.VISIBLE);
                }
            } else {
                if (slideBar.getVisibility() == View.VISIBLE) {
                    AlphaAnimation alpha = new AlphaAnimation(1f, 0f);
                    alpha.setDuration(200);
                    slideBar.clearAnimation();
                    slideBar.startAnimation(alpha);
                    slideBar.setVisibility(View.INVISIBLE);
                }
            }
        } else {
            slideBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private boolean showingSpeedText = false;

    private Runnable showPlayButtonCallback = new Runnable() {
        @Override
        public void run() {
            if (showingSpeedText) {
                return;
            }
            refreshViewVisible(btnPlay, true, true);
        }
    };

    /**
     * 计算当前游标对应在视频中的时间戳
     *
     * @return
     */
    private long getIndicatorLineTimestampInVideo() {
        float percent = (float) rangeBar.getScrollX() / rangeBar.getWidthOfVideo();
        return (long) (percent * videoDuration);
    }

    private void refreshRangeTimeText(VideoRange range) {
        if (range != null) {
            tvTime.setText(getSecondText(range.getStartTime() / 1000) + "   " + getSecondText(range.getEndTime() / 1000));
        }
    }

    private void removeSurfaceView() {
        surfaceLayout.removeAllViews();
        surfaceView = null;
    }

    private void initSurfaceView() {
        if (process != null) {
            process.release();
            process = null;
        }
        if (surfaceView == null) {
            surfaceView = new SurfaceView(getContext());
            surfaceLayout.removeAllViews();
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            lp.gravity = Gravity.CENTER;
            surfaceLayout.addView(surfaceView, lp);
        }

        if (TextUtils.isEmpty(videoPath) || !new File(videoPath).exists()) {
            Toaster.showInvalidate("视频文件错误，请重新录制");
            getActivity().finish();
            return;
        }
        initSurfaceHolder = false;
        surfaceView.getHolder().addCallback(buildSurfaceCallback());

        if (process == null) {
            process = MoMediaManager.createVideoProcessor();
            // 2/23/17 需要监听播放暂停事件
            process.setPlayingStatusListener(new MRecorderActions.OnPlayingStatusListener() {

                @Override
                public void onPlayingPtsMs(long ptsMs) {
                    if (!isPlaying) {
                        return;
                    }
                    final long pts = ptsMs;
                    //播放进度回调
                    if (Looper.getMainLooper() == Looper.myLooper()) {
                        VideoSpeedAdjustFragment.this.onPlaying(startPlayPosMS + ptsMs);
                    } else {
                        rangeBar.post(new Runnable() {
                            @Override
                            public void run() {
                                VideoSpeedAdjustFragment.this.onPlaying(startPlayPosMS + pts);
                            }
                        });
                    }
                }

                @Override
                public void onPlayingPaused() {
//                    Log4Android.getInstance().i("tang------onPlayingPaused 播放暂停");
                    isPlaying = false;
                    rangeBar.post(new Runnable() {
                        @Override
                        public void run() {
                            playDuration = 0;
                            rangeBar.setCanTouch(true);
                            if (!showingSpeedText && !isPlaying) {
                                refreshViewVisible(btnPlay, true, true);
                            }
                            refreshViewVisible(slideBar, true, true);
                        }
                    });
                }

                @Override
                public void onPlayingProgress(float progress) {
                    if (progress >= 1 && endPlayPosMS == videoDuration) {
                        if (Looper.getMainLooper() == Looper.myLooper()) {
                            VideoSpeedAdjustFragment.this.onPlaying(videoDuration);
                        } else {
                            rangeBar.post(new Runnable() {
                                @Override
                                public void run() {
                                    VideoSpeedAdjustFragment.this.onPlaying(videoDuration);
                                }
                            });
                        }
                    }
                }

                @Override
                public void onPlayingFinished() {

                }
            });
        }
        setSurfaceViewLp();
    }

    /**
     * 设置surfaceView大小
     */
    private void setSurfaceViewLp() {
        int surfaceW, surfaceH;
        int screenW = UIUtils.getScreenWidth();
        int screenH = UIUtils.getScreenHeight();

        int w = videoWidth, h = videoHeight;
        if (videoRotate == 90 || videoRotate == 270) {
            w = videoHeight;
            h = videoWidth;
        }

        float rationW = (float) screenW / w;
        float rationH = (float) screenH / h;
        if (rationH > rationW) {//按照宽度来处理
            surfaceW = screenW;
            surfaceH = (int) (rationW * h);
        } else {
            surfaceH = screenH;
            surfaceW = (int) (rationH * w);
        }
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) surfaceView.getLayoutParams();
        if (lp == null) {
            lp = new FrameLayout.LayoutParams(surfaceW, surfaceH);
            surfaceView.setLayoutParams(lp);
        } else {
            lp.width = surfaceW;
            lp.height = surfaceH;
        }
    }

    /**
     * 刷新 VideoRangeBar进度
     *
     * @param pstInMs
     */
    private void onPlaying(final long pstInMs) {
        if (playDuration == videoDuration)
            return;
        playDuration = pstInMs;
        rangeBar.setCanTouch(false);
        rangeBar.scrollToTimestamp(pstInMs, true);
    }

    private boolean initSurfaceHolder = false;

    private SurfaceHolder.Callback buildSurfaceCallback() {
        return new SurfaceHolder.Callback() {
            public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
                Log4Android.getInstance().i("tang----surfaceChanged");
                if (process != null && !initSurfaceHolder) {
                    initSurfaceHolder = true;
                    process.addScreenSurface(surfaceView.getHolder());
                    process.startPreview();
                }
            }

            public void surfaceCreated(SurfaceHolder holder) {
            }

            public void surfaceDestroyed(SurfaceHolder holder) {
                if (process != null) {
                    process.stopPreview();
                    isNeedPrepare = true;
                }
            }
        };
    }

    private boolean initParams() {
        boolean result = false;
        final Video v = new Video(videoPath);
        if (VideoUtils.getVideoMetaInfo(v)) {
            videoRotate = v.rotate;
            videoDuration = v.length;
            videoWidth = v.getWidth();
            videoHeight = v.height;
            rangeBar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    rangeBar.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    prepareInitRanges();
                }
            });
        }
        if (videoDuration <= 0) {
            Toaster.show("获取视频信息错误");
            getActivity().setResult(Activity.RESULT_CANCELED);
            getActivity().finish();
        } else {
            result = true;
        }
        return result;
    }

    /**
     * 添加传入的选区
     */
    private void prepareInitRanges() {
        if (initTimeRanges == null) {
            return;
        }
        for (TimeRangeScale scale : initTimeRanges) {
            if (scale == null || scale.getStart() < 0 || scale.getEnd() > videoDuration || scale.getSpeed() == 1f) {
                continue;
            }
            rangeBar.addRange(
                    scale.getStart(),
                    scale.getEnd(),
                    scale.getSpeed(),
                    false,
                    false,
                    getMinRangeDuration(),
                    MAX_RANGE_DURATION,
                    VideoRange.RangeType.TYPE_NORMAL
            );
        }
    }

    private boolean isFirstResume = true;

    @Override
    public void onResume() {
        Log4Android.getInstance().i("tang-----onResume");
        super.onResume();
        //第一次进入才从最开始播放
        if (isFirstResume) {
            if (!initParams()) return;
            initRetriever();
        }
        initSurfaceView();
        startPlay(0, videoDuration, speedValueArray[DEFAULT_SPEED_INDEX], true);
        isFirstResume = false;
    }

    @Override
    public void onPause() {
        Log4Android.getInstance().i("tang-----onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log4Android.getInstance().i("tang-----onStop");
        stopPlay();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        //去掉延迟动画
        btnPlay.removeCallbacks(showPlayButtonCallback);
        stopPlay();
        MomoTaskExecutor.cancleAllTasksByTag(this.hashCode());
        if (process != null) {
            process.release();
        }
        super.onDestroy();
    }

    /**
     * 确保页面被回收时，视频路径能够保存下来
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            if (!TextUtils.isEmpty(videoPath)) {
                outState.putString(MediaConstants.KEY_VIDEO_PATH, videoPath);
            }
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            String path = savedInstanceState.getString(MediaConstants.KEY_VIDEO_PATH);
            if (!TextUtils.isEmpty(path)) {
                videoPath = path;
            }
        }
    }

    @Override
    public void onClick(View v) {

        if (v == surfaceLayout) {
            if (process != null) {
                if (process.isPlaying()) {
                    pausePlay();
                } else {
                    refreshViewVisible(btnPlay, false, true);
                    refreshViewVisible(slideBar, false, true);
                    //如果在选区上，则从该选区开头播放
                    VideoRange selectedRange = rangeBar.getSelectedRange();
                    if (selectedRange != null) {
                        //滚动到位置
                        startPlay(selectedRange.getStartTime(), selectedRange.getEndTime(), selectedRange.getSpeed(), false);
                    } else {
                        long timestamp = getIndicatorLineTimestampInVideo();
                        //此处 —150是为了避免视频播放结束了，但是仍然没有移动到最后面的情况
                        if (timestamp >= videoDuration - 150) {
                            timestamp = 0L;
                        }
                        //按照原速从头 timestamp 处开始播放
                        startPlay(timestamp, videoDuration, speedValueArray[DEFAULT_SPEED_INDEX], false);
                    }
                }
            }
        } else if (v == findViewById(R.id.moment_speed_btn_close)) {
            onCancel();
        } else if (v == findViewById(R.id.moment_speed_btn_ok)) {
            onFinished();
        }
    }

    private void onFinished() {
        long duration = calculateVideoFinalDuaration();
        if (duration < MediaConstants.MIN_VIDEO_DURATION) {
            Toaster.showInvalidate("变速后的视频不能小于2秒，请重新调整速度");
            return;
        }
        EffectModel effectModel = new EffectModel();
        effectModel.setMediaPath(videoPath);

        if (rangeBar.getRangeCount() > 0) {
            VideoEffects videoEffects = new VideoEffects();
            List<TimeRangeScale> scales = new ArrayList<>();
            for (VideoRange range : rangeBar.getAllRanges()) {
                if (range != null && range.getSpeed() != 1.0f) {
                    scales.add(new TimeRangeScale(
                            range.getStartTime(),
                            range.getEndTime(),
                            range.getSpeed())
                    );
                }
            }
            videoEffects.setTimeRangeScales(scales);
            effectModel.setVideoEffects(videoEffects);
        }

        Activity activity = getActivity();
        Intent data = new Intent();
        data.putExtra(MediaConstants.KEY_VIDEO_SPEED_PARAMS, effectModel);
        activity.setResult(Activity.RESULT_OK, data);

        if (Log4Android.getInstance().isDebug()) {
            Log4Android.getInstance().i("tang----最终的变速参数 " + EffectModel.toEffectCmd(effectModel));
        }
        activity.finish();
    }

    /**
     * 计算视频最终的长度
     *
     * @return
     */
    private long calculateVideoFinalDuaration() {
        if (rangeBar.getRangeCount() == 0) {
            return videoDuration;
        }
        long rangeRealDuration = 0L;
        long rangeOriginDuration = 0L;
        for (VideoRange range : rangeBar.getAllRanges()) {
            rangeRealDuration += range.getDuration() * range.getSpeed();
            rangeOriginDuration += range.getDuration();
        }
        Log4Android.getInstance().i("calculateVideoFinalDuration " + rangeOriginDuration + "   " + rangeRealDuration);
        return videoDuration + rangeRealDuration - rangeOriginDuration;
    }

    private void onCancel() {
        getActivity().setResult(Activity.RESULT_CANCELED);
        getActivity().finish();
    }

    private void refreshViewVisible(View view, boolean visible, boolean anim) {
        if (!anim) {
            view.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        } else {
            if (visible) {
                if (view.getVisibility() != View.VISIBLE) {
                    AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
                    alpha.setDuration(200);
                    view.clearAnimation();
                    view.startAnimation(alpha);
                    view.setVisibility(View.VISIBLE);
                }
            } else {
                if (view.getVisibility() == View.VISIBLE) {
                    AlphaAnimation alpha = new AlphaAnimation(1f, 0f);
                    alpha.setDuration(200);
                    view.clearAnimation();
                    view.startAnimation(alpha);
                    view.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    private long getVideoThumbnailStep(long videoDuration) {
        if (videoDuration <= 10000) {
            return 1000;
        }
        if (videoDuration <= 60000) {
            return 2000;
        }
        if (videoDuration <= 2 * 60 * 1000)
            return 5000;
        return 10000;
    }

    private long getMinRangeDuration() {
        if (videoDuration <= 60000) {
            return MIN_RANGE_DURATION;
        }
        if (videoDuration <= 2 * 60 * 1000)
            return MIN_RANGE_DURATION * 5;
        return MIN_RANGE_DURATION * 10;
    }

    private void initRetriever() {
        videoDataRetrieve.init(videoPath);

        long step = getVideoThumbnailStep(videoDuration);
        int imageCount = (int) (videoDuration / step);

        rangeBar.setTotalVideoDurationInMs(videoDuration, imageCount, step);

        for (int i = 0; i < imageCount; i++) {
            //每1s取一次缩略图 注意此处的参数是微秒
            VideoDataRetrieverBySoft.Node node = new VideoDataRetrieverBySoft.Node(i * step * 1000, 0);
            videoNodes.add(node);
        }
        MomoTaskExecutor.executeTask(MomoTaskExecutor.EXECUTOR_TYPE_USER,
                this.hashCode(),
                new VideoThumbnailTask());
    }

    class VideoThumbnailTask extends MomoTaskExecutor.Task<Void, Void, Boolean> {
        private long startThumbnailTaskTime = 0L;

        @Override
        protected void onPreTask() {
            super.onPreTask();
            Log4Android.getInstance().i("VideoThumbnailTask, task start");
            startThumbnailTaskTime = System.currentTimeMillis();
        }

        @Override
        protected Boolean executeTask(Void... params) {
            startThumbnailTaskTime = System.currentTimeMillis();
            try {
                if (videoDataRetrieve != null) {
                    List<VideoDataRetrieverBySoft.Node> nodes = new ArrayList<>();
                    for (int i = 0; i < videoNodes.size(); i++) {
                        nodes.add(videoNodes.get(i));
                        if (i % 5 == 0) {//每隔5s取一次 分段取
                            getThumbnail(nodes);
                            nodes.clear();
                        }
                    }
                    getThumbnail(nodes);
                }
            } catch (Exception e) {
                Log4Android.getInstance().e(e);
                return false;
            }
            return true;
        }

        @Override
        protected void onTaskSuccess(Boolean reuslt) {
            super.onTaskSuccess(reuslt);
            if (reuslt) {
                Log4Android.getInstance().i("VideoThumbnailTask, Task Success:task run time:" + (System.currentTimeMillis() - startThumbnailTaskTime));
            } else {
                Log4Android.getInstance().i("VideoThumbnailTask, Task Fail:task run time:" + (System.currentTimeMillis() - startThumbnailTaskTime));
            }
            if (videoDataRetrieve != null) {
                videoDataRetrieve.release();
                videoDataRetrieve = null;
            }
        }
    }

    /**
     * 获得视频截图，注意该方法为阻塞方法
     * 在获取缩略图后根据当前分辨率大小进行适当压缩
     *
     * @param nodes
     */
    private void getThumbnail(List<VideoDataRetrieverBySoft.Node> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }
        videoDataRetrieve.getImageByList(nodes);
        try {
            Bitmap[] bitmaps = new Bitmap[nodes.size()];
            int index = 0;
            Matrix matrix = null;
            for (VideoDataRetrieverBySoft.Node node : nodes) {
                if (node.bmp == null) {
                    index++;
                    continue;
                }
                //需要对缩略图做旋转
                int width = node.bmp.getWidth();
                int height = node.bmp.getHeight();
                if (matrix == null) {
                    matrix = new Matrix();
                    final float sx = thumbnailWidth / (float) width;
                    final float sy = thumbnailHeight / (float) height;
                    matrix.setScale(sx, sy);
                    //旋转图片
                    matrix.setRotate(videoRotate);
                }
                bitmaps[index] = Bitmap.createBitmap(node.bmp, 0, 0, width, height, matrix, true);
                index++;
            }

            Message msg = handler.obtainMessage();
            msg.what = MyHandler.ACTION_UPDATE_RANGE_BAR;
            msg.obj = bitmaps;
            handler.sendMessage(msg);
        } catch (Exception e) {
            Log4Android.getInstance().e(e);
        }
    }

    private void onGetThumbnails(Bitmap[] bmps) {
        if (bmps == null) {
            return;
        }
        rangeBar.appendImageList(bmps);
    }

    private MyHandler handler = new MyHandler(this);

    private static class MyHandler extends UIHandler<VideoSpeedAdjustFragment> {
        private static final int ACTION_UPDATE_RANGE_BAR = 0x11;
        private static final int ACTION_UPDATE_INDICATOR_TEXT = 0x12;

        public MyHandler(VideoSpeedAdjustFragment cls) {
            super(cls);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (getRef() == null) {
                return;
            }
            switch (msg.what) {
                case ACTION_UPDATE_RANGE_BAR:
                    getRef().onGetThumbnails((Bitmap[]) msg.obj);
                    break;
                case ACTION_UPDATE_INDICATOR_TEXT:
                    break;
                default:
                    break;
            }

        }
    }
}
