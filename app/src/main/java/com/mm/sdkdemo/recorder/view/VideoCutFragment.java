package com.mm.sdkdemo.recorder.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mm.mdlog.MDLog;
import com.mm.mmutil.log.Log4Android;
import com.mm.mmutil.task.MomoTaskExecutor;
import com.mm.mmutil.toast.Toaster;
import com.immomo.moment.config.MRecorderActions;
import com.immomo.moment.mediautils.VideoDataRetrieverBySoft;
import com.immomo.moment.mediautils.cmds.AudioEffects;
import com.immomo.moment.mediautils.cmds.EffectModel;
import com.immomo.moment.mediautils.cmds.VideoCut;
import com.immomo.moment.mediautils.cmds.VideoEffects;
import com.mm.mediasdk.IVideoProcessor;
import com.mm.mediasdk.MoMediaManager;
import com.mm.mediasdk.utils.UIUtils;
import com.mm.mediasdk.videoprocess.MoVideo;
import com.mm.sdkdemo.R;
import com.mm.sdkdemo.base.BaseFragment;
import com.mm.sdkdemo.bean.VideoRecordDefs;
import com.mm.sdkdemo.config.Configs;
import com.mm.sdkdemo.log.LogTag;
import com.mm.sdkdemo.recorder.MediaConstants;
import com.mm.sdkdemo.recorder.listener.FragmentChangeListener;
import com.mm.sdkdemo.recorder.model.Video;
import com.mm.sdkdemo.utils.VideoUtils;
import com.mm.sdkdemo.widget.videorangebar.RangeBarListener;
import com.mm.sdkdemo.widget.videorangebar.VideoRange;
import com.mm.sdkdemo.widget.videorangebar.VideoRangeBar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.mm.sdkdemo.recorder.MediaConstants.KEY_CUT_VIDEO_RESULT;
import static com.mm.sdkdemo.recorder.MediaConstants.KEY_PICKER_VIDEO;
import static com.mm.sdkdemo.recorder.activity.VideoRecordAndEditActivity.GOTO_WHERE;

/**
 * @author wangduanqing
 */
public class VideoCutFragment extends BaseFragment implements View.OnClickListener {
    private ImageView ivBack;
    private ImageView ivOK;
    private RelativeLayout parentLayout;
    private LinearLayout videoLayout;
    private SurfaceView videoSurfaceView;
    private TextView startTimeTv;
    private VideoRangeBar videoRangeBar;
    private ProgressDialog progressDialog;
    private TextView videoCutTime;

    private ImageView cut_btn_play;

    /**
     * 视频相关变量
     **/
    private Video currentVideo;
    private String curVideoPath;
    private boolean isAllowedCut = false;//是否允许裁剪
    private boolean isCutting = false;//是否正在裁剪

    private long cutMaxDuration = MediaConstants.MIN_CUT_VIDEO_DURATION; //1分钟
    private int initMinDuration = 20000;//rangbar初始长度为20s
    private static final int THUMBNAIL_HEIGHT = UIUtils.getPixels(30);//缩略图高度

    private long rangeStartTime = 0;//截取开始时间，毫秒
    private long rangeEndTime = initMinDuration;//截取结束时间，毫秒
    private long scrolloTimestamp;
    //视频超过一分钟弹框提示时间点
    private static final long VIDEO_LENGTH_TIME = MediaConstants.MIN_CUT_VIDEO_DURATION;
    private int videoLengthTime;

    /**
     * 播放相关变量
     */
    boolean notAddHolder = true;
    private IVideoProcessor process = MoMediaManager.createVideoProcessor();
    private boolean processPrepred = false;

    /**
     * 抽帧相关变量
     */
    VideoDataRetrieverBySoft videoDataRetrieve = new VideoDataRetrieverBySoft();
    List<VideoDataRetrieverBySoft.Node> videoNodes = new ArrayList<>();

    long curPosMs = 0l;
    long startPosMs = 0l;
    boolean isPlaying = false;
    boolean isHandPause = false;
    boolean isPlayingFinish = false;

    private FragmentChangeListener fragmentChangeListener;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initData(getArguments());
    }

    private void initData(Bundle arg) {
        if (null != arg) {
            currentVideo = arg.getParcelable(VideoRecordDefs.KEY_VIDEO);
            VideoUtils.getVideoFixMetaInfo(currentVideo);
            curVideoPath = currentVideo.path;
            videoLengthTime = (int) arg.getLong(VideoRecordDefs.VIDEO_LENGTH_TIME, -1);
            videoLengthTime = arg.getInt(VideoRecordDefs.VIDEO_LENGTH_TIME, videoLengthTime);

            initMinDuration = (int) arg.getLong(VideoRecordDefs.VIDEO_MIN_CUT_TIME, initMinDuration);

            if (currentVideo == null || !VideoUtils.isValidFile(currentVideo.path) ||
                    currentVideo.getWidth() == 0 || currentVideo.height == 0) {
                toast("视频异常，请稍后再试");
                Activity a = getActivity();
                if (a != null && !a.isFinishing())
                    a.finish();
            }
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.video_draft_cut_layout;
    }

    @Override
    protected void initViews(View contentView) {
        ivBack = findViewById(R.id.video_cut_back);
        ivOK = findViewById(R.id.video_cut_ok);
        parentLayout = findViewById(R.id.parentLayout);
        parentLayout.setOnClickListener(this);
        videoLayout = findViewById(R.id.videoLayout);
        videoRangeBar = findViewById(R.id.videoRangeBar);
        View indicatorLine = findViewById(R.id.video_range_bar_line);
        ViewGroup.LayoutParams lp = indicatorLine.getLayoutParams();
        if (lp != null) {
            lp.height = UIUtils.getDimensionPixelSize(R.dimen.video_range_bar_item_height)
                    + UIUtils.getPixels(8f);
        }
        //触摸选框外部，不取消选中状态
        // videoRangeBar.setCancelSelectOnTouchOutside(false);
        videoRangeBar.setEmptyHeaderFooterWidth(UIUtils.getScreenWidth() / 2);

        cut_btn_play = findViewById(R.id.cut_btn_play);
        cut_btn_play.setOnClickListener(this);

        startTimeTv = findViewById(R.id.startTime);
        videoCutTime = this.findViewById(R.id.moment_video_cut_time);
        ivBack.setOnClickListener(this);
        ivOK.setOnClickListener(this);

        startTimeTv.setVisibility(View.GONE);
        int max = (int) VIDEO_LENGTH_TIME;
        if (videoLengthTime > 0) {
            max = videoLengthTime;
        }
        if (initMinDuration < 0) {
            initMinDuration = 20000;
        }
        cutMaxDuration = Math.min(MediaConstants.MIN_CUT_VIDEO_DURATION, max);

        initProcess();
        initVideoRangeBar();
        initSurfaceView();
        initRetriever();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (process == null) {
            if (currentVideo != null) {
                notAddHolder = true;
                process = MoMediaManager.createVideoProcessor();
                initProcess();
                initSurfaceView();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (process != null) {
            process.release();
            process = null;
        }
        videoLayout.removeView(videoSurfaceView);
        videoSurfaceView = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MomoTaskExecutor.cancleAllTasksByTag(this.hashCode());
        VideoUtils.deleteTempFile(curVideoPath);
        videoNodes = null;
        fragmentChangeListener = null;
    }

    private void initProcess() {

        process.setPlayingStatusListener(new MRecorderActions.OnPlayingStatusListener() {
            @Override
            public void onPlayingPaused() {
                cut_btn_play.post(new Runnable() {
                    @Override
                    public void run() {
                        cut_btn_play.setVisibility(View.VISIBLE);
                    }
                });
                isPlaying = false;
            }

            @Override
            public void onPlayingPtsMs(long ptsMs) {
                onPlayingScroll(startPosMs + ptsMs);
            }

            @Override
            public void onPlayingFinished() {
                Log4Android.getInstance().i("yichao------onProcessFinished 播放完毕");
                cut_btn_play.post(new Runnable() {
                    @Override
                    public void run() {
                        cut_btn_play.setVisibility(View.VISIBLE);
                    }
                });
                isPlaying = false;
                isPlayingFinish = true;
            }

            @Override
            public void onPlayingProgress(float progress) {

            }
        });
    }

    private void initVideoRangeBar() {
        videoRangeBar.setImageSize(UIUtils.getPixels(40), UIUtils.getPixels(49));
        videoRangeBar.addRangeBarListener(rangeBarListener);
        videoRangeBar.setOnClickListener(this);
        videoRangeBar.setOnScrollListener(rangeBarScrollistener);
    }

    private VideoRangeBar.OnScrollListener rangeBarScrollistener = new VideoRangeBar.OnScrollListener() {
        /**
         * 中线指的时间 单位s  避免频繁刷新时间
         */
        private int curTimeInSecond = 0;

        @Override
        public void onScroll(int scrollX) {
            if (getVideoLength() <= 0 || scrollX < 0) {
                return;
            }
            scrolloTimestamp = getIndicatorLineTimestampInVideo();
            int second = (int) (scrolloTimestamp / 1000);
            if (curTimeInSecond != second) {
                curTimeInSecond = second;
                videoCutTime.setText(getSecondText(curTimeInSecond));
            }
        }
    };

    private String getSecondText(long second) {
        int m = (int) (second / 60);
        int s = (int) (second % 60);
        String mm = m + "";
        String ss = s >= 10 ? s + "" : "0" + s;
        return mm + ":" + ss;
    }

    private RangeBarListener rangeBarListener = new RangeBarListener() {

        @Override
        public void onSelectedRangeSwitched(VideoRange selectedRange) {

        }

        @Override
        public void onRangeMoving(VideoRange range) {
            long startTime = range.getStartTime();
            long endTime = range.getEndTime();
            if (startTimeTv != null) {
                int length = Math.round((endTime - startTime) / 1000f);
                startTimeTv.setText("你已截取" + length + "s");
            }
        }

        @Override
        public void onRangeMoveStopped(VideoRange range) {
            rangeStartTime = range.getStartTime();//毫秒
            rangeEndTime = range.getEndTime();
            if (rangeEndTime > currentVideo.length) {
                rangeEndTime = currentVideo.length;
            }
            if (rangeStartTime < 0) {
                rangeStartTime = 0;
            }

            isPlaying = false;
            isPlayingFinish = true;
            isHandPause = false;
            if (process != null) {
                process.pause();
            }
            cut_btn_play.post(new Runnable() {
                @Override
                public void run() {
                    cut_btn_play.setVisibility(View.VISIBLE);
                }
            });

        }
    };

    private void initSurfaceView() {
        videoSurfaceView = new SurfaceView(getActivity());
        videoLayout.addView(videoSurfaceView);
        videoSurfaceView.getHolder().addCallback(mSHCallback);
        processPrepred = process.prepareVideo(currentVideo.path, null, 0, 0, 100, 0);
        cut_btn_play.setVisibility(View.GONE);
        isPlaying = true;
        setSurfaceViewLp();
    }

    SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            Log4Android.getInstance().i("surfaceChanged and notAddHolder:" + notAddHolder);
            if (process != null && notAddHolder) {
                notAddHolder = false;
                process.addScreenSurface(videoSurfaceView.getHolder());
                if (processPrepred)
                    process.startPreview();
            }
        }

        public void surfaceCreated(SurfaceHolder holder) {
            Log4Android.getInstance().i("surfaceCreated");

        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            Log4Android.getInstance().i("surfaceDestroyed");
            if (process != null) {
                process.release();
                process = null;
            }
        }
    };

    private void setSurfaceViewLp() {
        int surfaceW;
        int surfaceH;
        if (currentVideo.getWidth() > currentVideo.height) {
            surfaceW = UIUtils.getScreenWidth();
            surfaceH = (surfaceW * currentVideo.height) / currentVideo.getWidth();
        } else {
            surfaceH = UIUtils.getScreenHeight();
            surfaceW = (surfaceH * currentVideo.getWidth()) / currentVideo.height;
        }

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(surfaceW, surfaceH);
        videoSurfaceView.setLayoutParams(lp);
    }

    private void initRetriever() {
        videoDataRetrieve.init(currentVideo.path);
        int step = getStep();
        long frames = currentVideo.length / step;
        videoRangeBar.setTotalVideoDurationInMs(currentVideo.length, (int) frames, step);

        for (int i = 0; i < frames; i++) {
            VideoDataRetrieverBySoft.Node node = new VideoDataRetrieverBySoft.Node(i * 1000 * step, 0);//注意此处的参数是微秒
            videoNodes.add(node);
        }
        VideoThumbnailTask videoThumbnailTask = new VideoThumbnailTask();
        MomoTaskExecutor.executeTask(MomoTaskExecutor.EXECUTOR_TYPE_USER, this.hashCode(), videoThumbnailTask);
    }

    private int getStep() {
        if (currentVideo.length <= 20000) {
            return 2000;
        } else if (currentVideo.length <= 2 * 60000) {
            return 5000;
        }
        return 10000;
    }

    private void onPlayingScroll(long curPos) {
        curPosMs = curPos;
        Log4Android.getInstance().i("tang------onPlaying " + (curPos));
        if (Looper.getMainLooper() == Looper.myLooper()) {
            videoRangeBar.scrollToTimestamp(curPosMs, true);
        } else {
            videoRangeBar.post(new Runnable() {
                @Override
                public void run() {
                    videoRangeBar.scrollToTimestamp(curPosMs, true);
                }
            });
        }
    }

    @Override
    protected void onLoad() {

    }

    private void toast(String msg) {
        Toaster.show(msg, Toaster.LENGTH_LONG);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_cut_back:
                backResult(RESULT_CANCELED, null);
                break;
            case R.id.video_cut_ok:
                if (isAllowedCut && !isCutting) {
                    cutVideo(currentVideo);
                } else {
                    toast("视频缩略图生成中，请稍候");
                }
                break;
            case R.id.videoRangeBar:
                toast("缩略图生成中...");
                break;
            case R.id.parentLayout:
            case R.id.cut_btn_play:
                Log4Android.getInstance().i("yichao ===== isPlaying:" + isPlaying + ", isHandPause:" + isHandPause);
                if (isPlaying) {
                    isPlaying = false;
                    isHandPause = true;
                    if (process != null) {
                        process.pause();
                    }
                } else {
                    cut_btn_play.setVisibility(View.GONE);
                    isPlaying = true;
                    Log4Android.getInstance().i("currentVideo cut play");
                    isPlayingFinish = false;
                    handlePlayVideo();
                    break;
                }
        }
    }

    private void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showProgress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {

                }
            });
            progressDialog.setCancelable(false);
        }
        progressDialog.getWindow().setLayout(UIUtils.getPixels(175), UIUtils.getPixels(50));
        progressDialog.setMessage("视频截取中...");
        if (!progressDialog.isShowing()) {
            showDialog(progressDialog);
        }
    }

    private void cutVideo(final Video video) {
        Log4Android.getInstance().i("cutVideo start");
        isCutting = true;
        VideoUtils.getVideoFixMetaInfo(video);
        showProgress();
        if (process != null)
            process.release();
        process = null;
        final IVideoProcessor cutProcess = MoMediaManager.createVideoProcessor();
        //                final MomoProcess cutProcess = new MomoProcess();
        //截取视频输出路径
        String outVideoPath;
        File tempDir = Configs.getDir("cutVideo");
        if (tempDir == null) {
            return;
        }
        String tempVideoDir = tempDir.getAbsolutePath();
        String fileName = String.valueOf(System.currentTimeMillis());
        outVideoPath = tempVideoDir + File.separator + fileName + ".mp4";
        MDLog.i(LogTag.RECORDER.VIDEO_CUT, outVideoPath);
        //计算压缩宽高
        video.frameRate = MediaConstants.DEFAULT_FRAME_RATE;
        if (video.avgBitrate <= 0)
            video.avgBitrate = MediaConstants.BIT_RATE_FOR_CUT_VIDEO;
        cutProcess.setOutVideoInfo(video.getWidth(), video.height, (int) video.frameRate, video.avgBitrate);
        //        cutProcess.setOutMediaVideoInfo(video.getWidth(), video.height, (int) video.frameRate, video.avgBitrate, true);
        VideoEffects videoEffects = new VideoEffects();
        VideoCut videoCut = new VideoCut();
        videoCut.setMedia(video.path);

        //单位为毫秒，要保证截取的时长小于60000毫秒
        int diff = (int) rangeEndTime - (int) rangeStartTime;
        if (diff > MediaConstants.MAX_LONG_VIDEO_DURATION) {
            rangeEndTime = (int) rangeEndTime - (diff - MediaConstants.MAX_LONG_VIDEO_DURATION);
        }

        videoCut.setStart((int) rangeStartTime);
        videoCut.setEnd((int) rangeEndTime);
        List<VideoCut> videoCuts = new ArrayList<>();
        videoCuts.add(videoCut);
        videoEffects.setVideoCuts(videoCuts);

        EffectModel effectModel = new EffectModel();
        effectModel.setMediaPath(video.path);
        effectModel.setVideoEffects(videoEffects);
        effectModel.setAudioEffects(new AudioEffects());

        String json = EffectModel.toEffectCmd(effectModel);
        Log4Android.getInstance().i(" cut video json:" + json);

        //设置process压缩回调
        cutProcess.setOnStatusListener(new MRecorderActions.OnProcessProgressListener() {
            @Override
            public void onProcessProgress(float progress) {
                if (progress > 1.0f) {
                    progress = 1.0f;
                }
                String str = "正在截取 " + (int) (progress * 100) + "%";
                Message msg = Message.obtain();
                msg.what = ACTION_UPDATE_CUT_PROGRESS;
                msg.obj = str;
                handler.sendMessage(msg);
            }

            @Override
            public void onProcessFinished() {
                if (isActivityValid()) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toast("截取成功");
                            hideProgress();
                            cutProcess.release();
                            backResult(true);
                        }
                    }, 50);
                }
            }
        });

        cutProcess.setOnProcessErrorListener(new MRecorderActions.OnProcessErrorListener() {
            @Override
            public void onErrorCallback(int what, int errorCode, String msg) {
                cutError(cutProcess);

            }
        });

        cutProcess.prepareVideo(video.path, null, 0, 0, 100, 0);
        cutProcess.setVideoEffect(videoEffects);
        //        cutProcess.prepare(json);
        cutProcess.makeVideo(outVideoPath);
        video.path = outVideoPath;
    }

    private void cutError(final IVideoProcessor processor) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                toast("截取失败，请稍后再试");
                hideProgress();
                processor.release();
                VideoUtils.deleteTempFile(curVideoPath);
                backResult(false);
            }
        });
    }

    private boolean isActivityValid() {
        Activity a = getActivity();
        return a != null && !a.isFinishing();
    }

    private void handlePlayVideo() {
        VideoRange selectedRange = videoRangeBar.getSelectedRange();
        if (selectedRange != null) {
            if (scrolloTimestamp >= selectedRange.getStartTime() && scrolloTimestamp <= selectedRange.getEndTime()) {
                startPlay(selectedRange.getStartTime(), selectedRange.getEndTime());
            } else {
                startPlay(getTimestamp(), getVideoLength());
            }
        } else {
            startPlay(getTimestamp(), getVideoLength());
        }
    }

    private long getTimestamp() {
        long timestamp = getIndicatorLineTimestampInVideo();
        //此处 —150是为了避免视频播放结束了，但是仍然没有移动到最后面的情况
        if (timestamp >= getVideoLength() - 150) {
            timestamp = 0L;
        }
        return timestamp;
    }

    private long getIndicatorLineTimestampInVideo() {
        float percent = (float) videoRangeBar.getScrollX() / videoRangeBar.getWidthOfVideo();
        return (long) (getVideoLength() * percent);
    }

    private long getVideoLength() {
        if (currentVideo != null) {
            return currentVideo.length;
        } else {
            return 0L;
        }
    }

    private void startPlay(long startTime, long endTime) {
        VideoCut videoCut = new VideoCut();
        videoCut.setMedia(currentVideo.path);
        videoCut.setStart((int) startTime);
        videoCut.setEnd((int) endTime);
        List<VideoCut> videoCuts = new ArrayList<>();
        videoCuts.add(videoCut);
        videoRangeBar.scrollToTimestamp(startTime, true);
        if (process != null) {
            process.updateEffect(videoCuts, null, startTime, true);
        }
    }

    private void backResult(boolean result) {
        Bundle bundle = new Bundle();
        if (result) {
            currentVideo.isCut = true;
        }
        bundle.putParcelable(KEY_PICKER_VIDEO, currentVideo);
        bundle.putBoolean(KEY_CUT_VIDEO_RESULT, result);
        VideoUtils.getVideoFixMetaInfo(currentVideo);
        backResult(RESULT_OK, bundle);
    }

    /**
     * handler, 主要用于更新rangebar中的缩略图
     */
    private static final int ACTION_UPDATE_RANGEBAR = 1;//更新rangebar
    private static final int ACTION_UPDATE_CUT_PROGRESS = 2;//更新压缩进度

    private final UIHandler handler = new UIHandler(this);

    private static class UIHandler extends com.mm.sdkdemo.utils.UIHandler<VideoCutFragment> {

        public UIHandler(VideoCutFragment cls) {
            super(cls);
        }

        @Override
        public void handleMessage(Message msg) {
            VideoCutFragment fragment = getRef();
            if (fragment == null)
                return;
            Activity a = fragment.getActivity();
            if (a == null || a.isFinishing())
                return;
            if (fragment != null) {
                switch (msg.what) {
                    case ACTION_UPDATE_RANGEBAR:
                        if (fragment.videoRangeBar != null) {
                            fragment.videoRangeBar.appendImageList((Bitmap[]) msg.obj);
                            fragment.isAllowedCut = true;
                            VideoRange range = fragment.videoRangeBar.addRange(
                                    0,
                                    fragment.initMinDuration,
                                    1f,
                                    true,
                                    true,
                                    fragment.initMinDuration,
                                    fragment.cutMaxDuration,
                                    VideoRange.RangeType.TYPE_NORMAL);
                            fragment.startTimeTv.setVisibility(View.VISIBLE);
                            String time = null;
                            if (fragment.initMinDuration >= 60000) {
                                time = fragment.initMinDuration / 60000 + "分钟";
                            } else {
                                time = fragment.initMinDuration / 1000 + "秒";
                            }
                            fragment.startTimeTv.setText(fragment.getString(R.string.video_has_cut_title, time));

                            if (range != null) {
                                //如果在选区上，则从该选区开头播放
                                //选区不能拖动
                                range.setCanMove(false);
                                fragment.handlePlayVideo();
                                fragment.isPlaying = true;
                            }
                        }

                        break;
                    case ACTION_UPDATE_CUT_PROGRESS:
                        if (fragment.progressDialog != null) {
                            if (!fragment.progressDialog.isShowing()) {
                                fragment.showDialog(fragment.progressDialog);
                            }
                            fragment.progressDialog.setMessage((String) msg.obj);
                        }
                        break;
                }
            }

        }
    }

    long startThumbnailTaskTime = 0l;

    class VideoThumbnailTask extends MomoTaskExecutor.Task<Void, Void, Boolean> {

        @Override
        protected void onPreTask() {
            super.onPreTask();
            startThumbnailTaskTime = System.currentTimeMillis();
        }

        @Override
        protected Boolean executeTask(Void... params) {
            try {
                if (videoDataRetrieve != null) {
                    List<VideoDataRetrieverBySoft.Node> nodes = new ArrayList<>();
                    for (int i = 0, l = videoNodes.size(); i < l; i++) {
                        nodes.add(videoNodes.get(i));
                        if (i != 0 && i % 5 == 0) {
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
        protected void onTaskFinish() {
            super.onTaskFinish();
        }

        @Override
        protected void onTaskSuccess(Boolean reuslt) {
            super.onTaskSuccess(reuslt);
            if (reuslt) {
                Log4Android.getInstance().i("VideoThumbnailTask, Task Success:task run time:" + (System.currentTimeMillis() - startThumbnailTaskTime));
            } else {
                backResult(false);
                toast("截取失败，请稍后再试");
                Log4Android.getInstance().i("VideoThumbnailTask, Task Fail:task run time:" + (System.currentTimeMillis() - startThumbnailTaskTime));
            }
        }

        private void getThumbnail(List<VideoDataRetrieverBySoft.Node> nodes) {
            videoDataRetrieve.getImageByList(nodes);
            try {

                for (VideoDataRetrieverBySoft.Node node : nodes) {
                    float sampleSize = node.bmp.getHeight() / THUMBNAIL_HEIGHT;
                    int newW = (int) (node.bmp.getWidth() / sampleSize);
                    int newH = THUMBNAIL_HEIGHT;
                    node.bmp = Bitmap.createScaledBitmap(node.bmp, newW, newH, true);
                    if (currentVideo.rotate != 0) {
                        Matrix matrix = new Matrix();
                        matrix.setRotate(currentVideo.rotate);
                        node.bmp = Bitmap.createBitmap(node.bmp, 0, 0, newW, newH, matrix, true);
                    }
                }
            } catch (Exception e) {
                Log4Android.getInstance().e(e);
            }

            Bitmap[] bitmaps = new Bitmap[nodes.size()];
            for (int i = 0, l = nodes.size(); i < l; i++) {
                bitmaps[i] = nodes.get(i).bmp;
            }
            Message msg = Message.obtain();
            msg.what = ACTION_UPDATE_RANGEBAR;
            msg.obj = bitmaps;
            handler.sendMessage(msg);
        }
    }

    private void backResult(int resultCode, Bundle data) {
        if (data == null)
            data = new Bundle();
        data.putInt(MediaConstants.KEY_RESULT_CODE, resultCode);
        // 当listener是VideoRecordAndEditActivity set的，会用到GOTO_WHERE
        data.putString(GOTO_WHERE, VideoEditFragment.class.getSimpleName());
        if (fragmentChangeListener != null) {
            fragmentChangeListener.change(this, data);
        }
    }

    public void setFragmentChangeListener(FragmentChangeListener fragmentChangeListener) {
        this.fragmentChangeListener = fragmentChangeListener;
    }
}
