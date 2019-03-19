package com.mm.sdkdemo.recorder.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mm.mediasdk.dynamicresources.DynamicResourceConstants;
import com.mm.mediasdk.dynamicresources.DynamicResourceManager;
import com.mm.mmutil.MD5Utils;
import com.mm.mmutil.log.Log4Android;
import com.mm.mmutil.task.ThreadUtils;
import com.mm.mmutil.toast.Toaster;
import com.immomo.moment.mediautils.VideoDataRetrieverBySoft;
import com.mm.mediasdk.utils.UIUtils;
import com.mm.sdkdemo.R;
import com.mm.sdkdemo.config.Configs;
import com.mm.sdkdemo.recorder.MediaConstants;
import com.mm.sdkdemo.recorder.adapter.CoverListAdapter;
import com.mm.sdkdemo.recorder.model.Video;
import com.mm.sdkdemo.utils.SaveImageManager;
import com.mm.sdkdemo.utils.VideoUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wangduanqing
 */
public class SelectMomentCoverActivity extends BaseFullScreenActivity implements View.OnClickListener, SaveImageManager.Callback {

    private static final String CACHE_PRE = "cache_thumb_";
    private static final String CACHE_SUFFIX = ".jpg_";

    private String videoPath;
    private String outputCoverPath;

    private ImageView ivBgCover, ivPreview, ivProgress;
    private View progressLayout, previewLayout;
    private RecyclerView recyclerView;

    private List<Bitmap> bmpList = new ArrayList<>();
    /**
     * 指示当前展示的位置，避免频繁刷新图片
     */
    private int currentIndex = -1;
    private CoverListAdapter adapter;

    private BitmapFactory.Options decodeThumbOptions;
    /**
     * 抽帧相关变量
     */
    private VideoDataRetrieverBySoft videoDataRetrieve;
    private File coverCacheDir = null;

    private int thumbCount = 0;

    private int frameCount = 10;

    private SaveImageManager saveImageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_moment_cover);
        Intent intent = getIntent();
        if (intent != null) {
            Video video = intent.getParcelableExtra(MediaConstants.KEY_VIDEO_PATH);
            videoPath = video.path;
            outputCoverPath = intent.getStringExtra(MediaConstants.KEY_OUTPUT_COVER_PATH);
            currentIndex = intent.getIntExtra(MediaConstants.KEY_SELECTED_COVER_POS, -1);
            VideoUtils.getVideoFixMetaInfo(video);
            videoDuration = video.length;
            videoRotate = video.rotate;
            videoWidth = video.getWidth();
            videoHeight = video.height;
            initFrameCount(video.length);
        }
        if (TextUtils.isEmpty(videoPath) || !new File(videoPath).exists()) {
            Toast.makeText(this, "视频文件不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        videoDataRetrieve = new VideoDataRetrieverBySoft();
        initViews();

        saveImageManager = new SaveImageManager();
        saveImageManager.setAutoRecycleBitmap(true);
        saveImageManager.setQuality(100);
        saveImageManager.setCallback(this);
    }

    private void initFrameCount(long duration) {
        if (duration > 0 && duration <= 60000) {
            frameCount = 10;
        } else if (duration > 60000 && duration <= 3 * 60000) {
            frameCount = 20;
        } else
            frameCount = 30;
    }

    private void initCacheCoverDir() {
        if (coverCacheDir != null && coverCacheDir.exists()) {
            return;
        }
        String md5Str = MD5Utils.getMD5(videoPath);
        coverCacheDir = new File(Configs.getDir("coverCache"), md5Str);
        if (!coverCacheDir.exists()) {
            coverCacheDir.mkdirs();
        }
        saveImageManager.setFormatFilePath(coverCacheDir.getAbsolutePath() + File.separator + CACHE_PRE + "%d" + CACHE_SUFFIX);
    }

    private void initViews() {
        progressLayout = findViewById(R.id.select_cover_progress_layout);
        ivProgress = (ImageView) findViewById(R.id.select_cover_progress_icon);

        ivBgCover = (ImageView) findViewById(R.id.select_cover_big);
        previewLayout = findViewById(R.id.select_cover_preview_layout);
        ivPreview = (ImageView) findViewById(R.id.select_cover_preview_image);

        recyclerView = (RecyclerView) findViewById(R.id.select_cover_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(SelectMomentCoverActivity.this, LinearLayoutManager.HORIZONTAL, false));

        //设置下方的小图的尺寸 16:9
        final int itemWidth = UIUtils.getPixels(45f);
        int itemHeight = UIUtils.getPixels(80f);

        //设置预览图片的尺寸
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) previewLayout.getLayoutParams();
        lp.width = UIUtils.getPixels(55f);
        lp.height = UIUtils.getPixels(97f);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;

        adapter = new CoverListAdapter(itemWidth, itemHeight, bmpList);
        adapter.setEmptyHeaderFooterWidth((screenWidth - itemWidth) / 2);
        recyclerView.setAdapter(adapter);

        findViewById(R.id.select_cover_btn_close).setOnClickListener(this);
        findViewById(R.id.select_cover_btn_ok).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_cover_btn_close:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.select_cover_btn_ok:
                boolean result = onSaveCoverBmp(outputCoverPath);
                if (result) {
                    Intent data = new Intent();
                    data.putExtra(MediaConstants.KEY_OUTPUT_COVER_PATH, outputCoverPath);
                    data.putExtra(MediaConstants.KEY_SELECTED_COVER_POS, currentIndex);
                    setResult(RESULT_OK, data);
                } else {
                    setResult(RESULT_CANCELED);
                }
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 保持临时变量
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(MediaConstants.KEY_VIDEO_PATH, videoPath);
        outState.putString(MediaConstants.KEY_OUTPUT_COVER_PATH, outputCoverPath);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String tempVideoPath = savedInstanceState.getString(MediaConstants.KEY_VIDEO_PATH);
        String tempOutputPath = savedInstanceState.getString(MediaConstants.KEY_OUTPUT_COVER_PATH);

        if (TextUtils.isEmpty(tempVideoPath) && TextUtils.isEmpty(tempOutputPath)) {
            videoPath = tempVideoPath;
            outputCoverPath = tempOutputPath;
        }
    }

    private boolean onSaveCoverBmp(String outputPath) {
        try {
            if (largeBmp == null) {
                return false;
            }
            File file = new File(outputPath);
            largeBmp.compress(Bitmap.CompressFormat.JPEG, 85, new FileOutputStream(outputPath));
            return file.exists() && file.length() > 0;
        } catch (Exception ex) {
            Log4Android.getInstance().e(ex);
        }
        return false;
    }

    /**
     * 设置当前展示的图片
     *
     * @param pos
     */
    private void refreshSlideView(int pos, boolean islarge) {
        if (pos < 0 || pos > bmpList.size()) {
            pos = 0;
        }
        currentIndex = pos;

        if (islarge) {
            getLargeFrame(pos);
        } else {
            ivPreview.setImageBitmap(bmpList.get(pos));
        }
    }

    private Bitmap largeBmp = null;

    private void getLargeFrame(final int pos) {
        if (thumbCount > pos) {
            ThreadUtils.execute(ThreadUtils.TYPE_RIGHT_NOW, new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = getLargeThumbByIndex(pos);
                    onGetLargeThumbnails(bitmap);
                }
            });
        }
    }

    private void onGetLargeThumbnails(final Bitmap largeBmp) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (largeBmp != null && !largeBmp.isRecycled()) {
                    //需要先设置ImageView的scaleType  保证竖屏的封面图能够铺满全屏
                    boolean needCrop = false;
                    if (largeBmp.getWidth() < largeBmp.getHeight()) {
                        float ratio = largeBmp.getHeight() / (float) largeBmp.getWidth();
                        needCrop = ratio > 1.6 && ratio < 1.8;
                    }
                    ivBgCover.setScaleType(needCrop ? ImageView.ScaleType.CENTER_CROP : ImageView.ScaleType.FIT_CENTER);

                    ivBgCover.setImageBitmap(largeBmp);
                    ivPreview.setImageBitmap(largeBmp);
                    SelectMomentCoverActivity.this.largeBmp = largeBmp;
                }
            }
        });
    }

    /**
     * 滚动时，实时计算当前对应的位置
     */
    private void onScrollRecyclerView() {
        int pos = calculatePosInList(null);
        refreshSlideView(pos, false);
    }

    /**
     * 根据滚动距离，计算预览的窗口对应在 RecyclerView中的位置
     *
     * @return
     */
    private int calculatePosInList(int[] scrollOffset) {
        if (recyclerView == null || recyclerView.getAdapter() == null) {
            return 0;
        }
        LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
        int first = lm.findFirstVisibleItemPosition();

        CoverListAdapter adapter = (CoverListAdapter) recyclerView.getAdapter();

        int emptyHeader = adapter.getEmptyHeaderFooterWidth();
        int itemWidth = adapter.getItemWidth();

        int scrollX = 0;
        if (first == 0) {
            scrollX = Math.abs(lm.findViewByPosition(0).getLeft());
        } else {
            scrollX += (emptyHeader);
            scrollX += (first - 1) * itemWidth;
            scrollX += Math.abs(lm.findViewByPosition(first).getLeft());
        }
        int distance = scrollX;
        int pos = 0, needScroll = 0;
        if (distance > 0) {
            pos = distance / itemWidth;
            int offset = distance - itemWidth * pos;
            if (offset > 0) {
                //剩余的距离超过item宽度的一半，则算到下一个
                if (offset > itemWidth * 0.5) {
                    pos++;
                    needScroll = itemWidth * pos - distance;
                } else {
                    needScroll = -offset;
                }
            }
            if (scrollOffset != null) {
                scrollOffset[0] = needScroll;
            }
        }
        //        Log4Android.getInstance().d("tang----scrollX " + scrollX + "   " + pos + "   " + needScroll);
        return pos;
    }

    private boolean isFirstResume = true;
    private long videoDuration;
    private int videoWidth, videoHeight, videoRotate;

    @Override
    protected void onResume() {
        super.onResume();
        File file = new File(videoPath);
        if (!file.exists() || file.length() <= 0) {
            finish();
            return;
        }
        initCacheCoverDir();
        if (isFirstResume) {
            isFirstResume = false;
            initParams();
        }
    }

    private void initParams() {
        if (videoDuration <= 0L) {
            Video v = new Video(videoPath);
            VideoUtils.getVideoMetaInfo(v);
            videoRotate = v.rotate;
            videoDuration = v.length;
            videoWidth = v.getWidth();
            videoHeight = v.height;
            initFrameCount(videoDuration);
        }
        initViewParams();
        getThumbnailsForBest();
        saveImageManager.setRotate(videoRotate);
    }

    private boolean getThumbnailFirstTask = true;

    private void onGetThumbnailSuccess(final boolean finish, final Bitmap... bitmaps) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (bitmaps != null) {
                    final int len = bitmaps.length;
                    for (int i = 0; i < len; i++) {
                        final Bitmap b = bitmaps[i];
                        if (b != null && !b.isRecycled()) {
                            bmpList.add(b);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    if (currentIndex <= 0 && getThumbnailFirstTask) {
                        refreshSlideView(0, true);
                    }
                }
                getThumbnailFirstTask = false;
                if (finish) {
                    onGetAllCovers();
                }
            }
        });
    }

    //RecyclerView是否滚动过
    private boolean scrollOnInit = false;

    private void onGetAllCovers() {
        if (progressLayout.getVisibility() == View.VISIBLE) {
            ivProgress.clearAnimation();
            progressLayout.setVisibility(View.GONE);
        }
        refreshSlideView(currentIndex, true);
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentIndex >= 0 && currentIndex < bmpList.size()) {
                    recyclerView.scrollBy(UIUtils.getPixels(45f) * currentIndex, 0);
                    scrollOnInit = true;
                }
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    private boolean scrollFlag = false;

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (scrollOnInit) {
                            scrollOnInit = false;
                            return;
                        }
                        onScrollRecyclerView();
                    }

                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        //当滚动停止后，要让小窗口对应到RecyclerView上，有吸附效果
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            int[] scrollOffset = new int[1];
                            int posInList = calculatePosInList(scrollOffset);
                            Log.e("TEST", "tang------onScrollStateChanged SCROLL_STATE_IDLE " + posInList + "  " + scrollOffset[0]);

                            //如果是最后一位，则不滚动
                            if (!scrollFlag) {
                                scrollFlag = true;
                                recyclerView.smoothScrollBy(scrollOffset[0], 0);
                            } else {
                                scrollFlag = false;
                            }
                            refreshSlideView(posInList, true);
                        }
                    }
                });
            }
        }, 10);
    }

    @Override
    protected void onDestroy() {
        releaseRetriver();
        if (saveImageManager != null)
            saveImageManager.release();
        saveImageManager = null;
        super.onDestroy();
    }

    private int thumbnailWidth, thumbnailHeight;

    private void initViewParams() {
        ivProgress.clearAnimation();
        ivProgress.startAnimation(AnimationUtils.loadAnimation(this, R.anim.loading));

        thumbnailHeight = UIUtils.getDimensionPixelSize(R.dimen.video_range_bar_item_height);
        thumbnailWidth = UIUtils.getDimensionPixelSize(R.dimen.video_range_bar_item_width);
    }

    private void releaseRetriver() {
        if (videoDataRetrieve != null) {
            videoDataRetrieve.release();
            videoDataRetrieve = null;
        }
    }

    private void getThumbnailsForBest() {
        if (checkLocalThumb()) {
            decodeAllThumbFromFile();
            return;
        }
        if (!videoDataRetrieve.initWithType(videoPath, VideoDataRetrieverBySoft.GET_FRAME_TYPE_BY_BEST, frameCount)) {
            Toaster.show("初始化视频失败");
            return;
        }
        videoDataRetrieve.setImageFrameFilterListener(imageFrameFilterListener);

        File faFilePath = DynamicResourceManager.getInstance().getResource(DynamicResourceConstants.ITEM_NAME_MMCV_FA_MODEL);
        File fdFilePath = DynamicResourceManager.getInstance().getResource(DynamicResourceConstants.ITEM_NAME_MMCV_MACE_FD_MODEL);
        List<String> modlePath = new ArrayList<>();
        if (fdFilePath != null && fdFilePath.exists() && faFilePath != null && faFilePath.exists()) {
            modlePath.add(0, fdFilePath.getAbsolutePath());
            modlePath.add(1, faFilePath.getAbsolutePath());
            videoDataRetrieve.setmFaceModeList(modlePath);
        }

        videoDataRetrieve.executeFrameFilter();
    }

    private void decodeAllThumbFromFile() {
        ThreadUtils.execute(ThreadUtils.TYPE_RIGHT_NOW, new Runnable() {
            @Override
            public void run() {
                initThumbnailOption();
                for (int i = 0; i < frameCount; i++) {
                    File f = getThumbnailCacheFile(i);
                    if (f.exists() && f.length() > 0) {
                        try {
                            final Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), decodeThumbOptions);
                            onGetThumbnailSuccess(false, bitmap);
                            thumbCount++;
                        } catch (Exception e) {
                            Log4Android.getInstance().e(e);
                        }
                    }
                }
                onGetThumbnailSuccess(true);
            }
        });
    }

    private boolean checkLocalThumb() {
        if (coverCacheDir != null && coverCacheDir.isDirectory()) {
            File[] list = coverCacheDir.listFiles();
            if (list.length == frameCount)
                return true;
        }
        return false;
    }

    private void initThumbnailOption() {
        if (decodeThumbOptions == null) {
            decodeThumbOptions = new BitmapFactory.Options();
            decodeThumbOptions.inSampleSize = videoWidth / thumbnailWidth;
            if (decodeThumbOptions.inSampleSize <= 0) {
                decodeThumbOptions.inSampleSize = 1;
            }
        }
    }

    private File getThumbnailCacheFile(int index) {
        return new File(coverCacheDir, CACHE_PRE + index + CACHE_SUFFIX);
    }

    private Bitmap getLargeThumbByIndex(int index) {
        File cacheFile = getThumbnailCacheFile(index);
        if (cacheFile.exists() && cacheFile.length() > 0)
            return BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
        return null;
    }

    private VideoDataRetrieverBySoft.ImageFrameFilterListener imageFrameFilterListener = new VideoDataRetrieverBySoft.ImageFrameFilterListener() {
        @Override
        public void doFilterFrame(Bitmap bitmap) {
            if (bitmap != null && !bitmap.isRecycled() && saveImageManager != null) {
                saveImageManager.putBitmap(bitmap, thumbCount);
                thumbCount++;
            }
        }

        @Override
        public void doFilterComplete() {
            onGetThumbnailSuccess(true);
        }

        @Override
        public void doFilterError(Exception e) {
            Log4Android.getInstance().e(e);
        }
    };

    @Override
    public void onSaveSucess(File file, Object... args) {
        initThumbnailOption();
        final Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), decodeThumbOptions);
        onGetThumbnailSuccess(false, bitmap);
    }

    @Override
    public void onSaveError(Throwable t, Object... args) {
        t.printStackTrace();
    }
}