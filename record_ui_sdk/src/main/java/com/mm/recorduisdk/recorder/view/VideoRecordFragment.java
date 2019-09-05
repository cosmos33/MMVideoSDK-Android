package com.mm.recorduisdk.recorder.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.core.glcore.config.MRConfig;
import com.core.glcore.config.Size;
import com.cosmos.mdlog.MDLog;
import com.immomo.doki.DokiInitializer;
import com.immomo.doki.filter.makeup.MakeupFilter;
import com.immomo.moment.config.MRecorderActions;
import com.immomo.moment.model.VideoFragment;
import com.mm.base_business.base.BaseFragment;
import com.mm.base_business.utils.DeviceUtils;
import com.mm.mediasdk.utils.UIUtils;
import com.mm.mmutil.app.AppContext;
import com.mm.mmutil.task.MomoMainThreadExecutor;
import com.mm.mmutil.toast.Toaster;
import com.mm.recorduisdk.Constants;
import com.mm.recorduisdk.R;
import com.mm.recorduisdk.bean.MMImageEditParams;
import com.mm.recorduisdk.bean.MMRecorderParams;
import com.mm.recorduisdk.bean.MMVideoEditParams;
import com.mm.recorduisdk.bean.MomentFace;
import com.mm.recorduisdk.bean.TolerantMoment;
import com.mm.recorduisdk.bean.element.Element;
import com.mm.recorduisdk.bean.element.ElementManager;
import com.mm.recorduisdk.config.Configs;
import com.mm.recorduisdk.log.LogTag;
import com.mm.recorduisdk.moment.MomentFacePanelElement;
import com.mm.recorduisdk.moment.MomentFacePanelHelper;
import com.mm.recorduisdk.recorder.MediaConstants;
import com.mm.recorduisdk.recorder.activity.ImageEditActivity;
import com.mm.recorduisdk.recorder.helper.MomentFaceConstants;
import com.mm.recorduisdk.recorder.helper.MomentFaceUtil;
import com.mm.recorduisdk.recorder.helper.MomentPropPanelHelper;
import com.mm.recorduisdk.recorder.helper.MomoRecorderProxy;
import com.mm.recorduisdk.recorder.helper.RecordTipManager;
import com.mm.recorduisdk.recorder.helper.VideoPanelFaceAndSkinManager;
import com.mm.recorduisdk.recorder.listener.FilterSelectListener;
import com.mm.recorduisdk.recorder.listener.FragmentChangeListener;
import com.mm.recorduisdk.recorder.listener.OnFilterDensityChangeListener;
import com.mm.recorduisdk.recorder.listener.RecordOrientationSwitchListener;
import com.mm.recorduisdk.recorder.model.MusicContent;
import com.mm.recorduisdk.recorder.model.Photo;
import com.mm.recorduisdk.recorder.model.Video;
import com.mm.recorduisdk.recorder.musicpanel.edit.MusicPanelHelper;
import com.mm.recorduisdk.recorder.presenter.IRecorder;
import com.mm.recorduisdk.recorder.presenter.RecordPresenter;
import com.mm.recorduisdk.utils.AnimUtils;
import com.mm.recorduisdk.utils.MomentUtils;
import com.mm.recorduisdk.utils.RecordButtonTouchEventHelper;
import com.mm.recorduisdk.utils.ScreenOrientationManager;
import com.mm.recorduisdk.utils.VideoUtils;
import com.mm.recorduisdk.utils.XEngineEventHelper;
import com.mm.recorduisdk.utils.album.AlbumConstant;
import com.mm.recorduisdk.utils.filter.FiltersManager;
import com.mm.recorduisdk.widget.FaceTipView;
import com.mm.recorduisdk.widget.FilterScrollMoreViewPager;
import com.mm.recorduisdk.widget.FocusView;
import com.mm.recorduisdk.widget.MomentBeautyPanelLayout;
import com.mm.recorduisdk.widget.MomentFilterPanelLayout;
import com.mm.recorduisdk.widget.MomentFilterPanelTabLayout;
import com.mm.recorduisdk.widget.MomentRecordProgressView;
import com.mm.recorduisdk.widget.MomentSkinAndFacePanelLayout;
import com.mm.recorduisdk.widget.OrientationTextView;
import com.mm.recorduisdk.widget.RecordPageIndicator;
import com.mm.recorduisdk.widget.ScrollMoreViewPager;
import com.mm.recorduisdk.widget.SlideIndicatorBar;
import com.mm.recorduisdk.widget.SlideIndicatorBuilder;
import com.mm.recorduisdk.widget.VideoAdvancedRecordButton;
import com.mm.recorduisdk.widget.VideoDefaultRecordButton;
import com.mm.recorduisdk.widget.VideoRecordControllerLayout;
import com.momo.mcamera.dokibeauty.MakeupHelper;
import com.momo.mcamera.filtermanager.MMPresetFilter;
import com.momo.mcamera.mask.MaskModel;
import com.momo.mcamera.mask.Sticker;
import com.momo.xeengine.XE3DEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.mm.recorduisdk.recorder.activity.VideoRecordAndEditActivity.BACK_TO_OLD;
import static com.mm.recorduisdk.recorder.activity.VideoRecordAndEditActivity.GOTO_WHERE;

/**
 * Created by wangduanqing on 2019/2/10.
 */
public class VideoRecordFragment extends BaseFragment implements IMomoRecordView, View.OnClickListener, FilterSelectListener {

    public static int cameraType = Camera.CameraInfo.CAMERA_FACING_FRONT;

    // 删除视频 布局
    private ViewStub stubDeleteTip;
    private TextView tvDeleteTip;
    private boolean isFirstFrameShow = true;
    private MMRecorderParams mRecorderParams;
    private DelayStartShooting delayStartShootingTask;
    private ShowDelayTimeTask delayTextTask;
    private DelayStartRecord delayStartRecordTask;
    private View mMakeUp;


    private static final int MIN_BACK_TIME = 400;

    // 拍摄延时时间
    private static final int DELAY_OFF = 0;
    private static final int DELAY_3 = 3;

    private static final int IC_DELAY_OFF = R.drawable.ic_moment_delay_off;
    private static final int IC_DELAY_3 = R.drawable.ic_moment_delay_3;

    private static final int IC_DEFAULT_FLASH_ON = R.drawable.ic_default_video_flash;
    private static final int IC_DEFAULT_FLASH_OFF = R.drawable.ic_default_video_flash_off;
    private static final int REQUEST_CODE_FOR_EDIT_IMAGE = 0X1252;

    private static final String BOTTOM_TEXT_DEFAULT_RECORD = "拍照";
    private static final String BOTTOM_TEXT_ADVANCED_RECORD = "拍视频";

    private static final String DEFAULT_TIP_RECORD = "点击拍照";
    private static final String DEFAULT_TIP_ONLY_VIDEO = "长按录像";

    private static final CharSequence[] speedTextArray = {"极慢", "慢", "标准", "快", "极快"};

    private static final float[] SpeedValueArray = {2f, 1.5f, 1f, 0.5f, 0.25f};

    private static final int DEFAULT_SPEED_INDEX = 2;

    // 默认当前状态为拍摄
    private @MMRecorderParams.RecordTab
    int state = Constants.RecordTab.PHOTO;

    private SurfaceView videoRecordSurfaceView;
    private String restoreVideoPath;
    // 拍摄 、高级拍摄
    private FilterScrollMoreViewPager changeFragmentViewpager;
    private RecordPageIndicator recordPagerIndicator;
    private MomentRecordProgressView videoAdvancedProgressView;
    private ImageView btnClose;

    private ImageView videoDefaultBtnSwitchCamera;
    private ImageView videoDefaultBtnFlash;

    private OrientationTextView videoAdvancedBtnGotoEdit;
    private TextView videoAdvancedBtnDelay;
    private TextView videoSelectMusicTv;
    private ImageView videoAdvancedBtnDelete;
    private View videoHorizontalToolsLayout;
    private View videoVerticalToolsLayout;
    private TextView tvFilterName;

    private View videoControlLayout;
    private View videoFaceContainer, videoSpeed, videoSlimmingContainer;

    private VideoRecordControllerLayout videoRecordControllerLayout;
    private VideoDefaultRecordButton videoDefaultRecordBtn;
    private VideoAdvancedRecordButton videoAdvancedRecordBtn;
    private View cancelDelayBtn;

    private TextView delayText;
    private TextView recordCancelTip;
    private TextView filterNameTip;

    private SlideIndicatorBar slideBar;
    private TextView tvSlideIndicator;

    // 变脸相关
    private MomentFacePanelElement mFacePanelElement;
    // 管理UI单元
    private ElementManager mElementManager;
    // 变脸面板ViewStub对象
    private ViewStub mFacePannelViewStub;

    private MomentBeautyPanelLayout beautyPanelLayout;

    private MomentFilterPanelLayout filterPanel;  // 滤镜布局
    private FaceTipView mStickerTriggerTipView;
    private Fragment[] fragments;

    // 当前使用某个功能具体的position
    private int mCurFilterPos = Configs.DEFAULT_FILTER_INDEX;
    private int mCurFilterBeautyPos = Configs.DEFAULT_BEAUTY;
    private int mCurFilterEyeThinPos = Configs.DEFAULT_BIG_EYE;
    private int mCurFilterSlimmingPos;
    private int mCurFilterLongLegPos;

    private float mCurrentFilterIntensity = 1;


    private boolean isResumed = false;
    private boolean draggingToCancel = false;
    private int flashMode = IRecorder.FLASH_MODE_OFF;
    private boolean isPreviewing = false;
    private boolean goingToEdit = false;
    private long lastBackPressTime = 0;
    private long lastClickTime = 0;

    private Drawable timeBackground;

    @Nullable
    private MusicContent selectedMusic;//真正选中的音乐
    @Nullable
    private MusicContent initMusic;//外部带进来的音乐
    private String mSelectedFaceClassId = null;
    private String mSelectedFaceID;

    //  video视频信息
//    private VideoInfoTransBean transBean;

    private ProgressDialog finishingDialog;

    private float defaultProgress = 0;

    private int chooseDelayTime = DELAY_OFF;
    private int delayTime = DELAY_OFF;

    // 屏幕旋转管理
    private ScreenOrientationManager orientationManager;
    private RecordPresenter mPresenter;
    // fragment切换回调
    private FragmentChangeListener fragmentChangeListener;

    // 视频录制提示View管理
    private RecordTipManager recordTipManager;

    private int noFaceTimes = 0;
    private boolean noFaceSwitch = true;
    // ar变脸
    private boolean mIsArkit = false;
    //是否是3d贴纸
    private boolean mIs3D = false;


    private Bundle arg;

    private float currentSpeed = 1L;
    private int currentSpeedIndex = DEFAULT_SPEED_INDEX;

    private MusicPanelHelper musicPanelHelper;

    private ImageView lastFrameView;

    private MakeupFilter filter;
    private MomentPropPanelHelper mMomentPropPanelHelper;
    private FocusView mFocusView;

    @Override
    protected int getLayout() {
        return R.layout.fragment_video_record;
    }

    private List<MMPresetFilter> filters = new CopyOnWriteArrayList<>();

    @Override
    public void onCreate(Bundle bundle) {
        filters.addAll(FiltersManager.getAllFilters());
        if (getActivity() != null && getActivity().getWindow() != null) {
            getActivity().getWindow().setFormat(PixelFormat.TRANSLUCENT);
            getActivity().getWindow().setBackgroundDrawableResource(R.drawable.ic_moment_theme_bg);
        }
        super.onCreate(bundle);
        arg = getArguments();
        if (arg != null) {
            mRecorderParams = arg.getParcelable(MediaConstants.KEY_RECORD_PARAMS);
            if (mRecorderParams == null) {
                mRecorderParams = arg.getParcelable(MediaConstants.KEY_CACHE_EXTRA_PARAMS);
            }
            restoreVideoPath = arg.getString(MediaConstants.KEY_RESTORE_VIDEO_PATH, null);
            if (mRecorderParams != null) {
                selectedMusic = mRecorderParams.getInitMusic();
                currentSpeedIndex = mRecorderParams.getSpeedIndex();
                currentSpeed = SpeedValueArray[currentSpeedIndex];
                state = mRecorderParams.getGotoTab();
                mSelectedFaceClassId = mRecorderParams.getInitFaceClassId();
                mSelectedFaceID = mRecorderParams.getInitFaceId();

            }

            if (state < 0 || state > Constants.RecordTab.VIDEO) {
                state = Constants.RecordTab.PHOTO;
            }
        }
        if (mRecorderParams == null) {
            mRecorderParams = new MMRecorderParams.Builder().build();
        }
        initMusic = mRecorderParams.getInitMusic();
        recordTipManager = new RecordTipManager(getActivity());

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(MediaConstants.KEY_RECORD_PARAMS, mRecorderParams);
        super.onSaveInstanceState(outState);
    }

    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mRecorderParams = savedInstanceState.getParcelable(MediaConstants.KEY_RECORD_PARAMS);
            initRecordButton();
        }
        super.onViewStateRestored(savedInstanceState);
    }

    private void initRecordButton() {
        videoDefaultRecordBtn.setCanLongPress(false);
        videoDefaultRecordBtn.setCallback(new DefaultCallback());
        videoAdvancedRecordBtn.setCallback(new AdvancedCallback());
    }

    @Override
    protected void initViews(final View contentView) {
        beautyPanelLayout = findViewById(R.id.record_beauty);
        videoRecordSurfaceView = findViewById(R.id.video_record_surfaceView);
        lastFrameView = findViewById(R.id.last_frame);
        changeFragmentViewpager = findViewById(R.id.change_fragment_viewpager);
        mFocusView = findViewById(R.id.focus_view);
        recordPagerIndicator = findViewById(R.id.record_pager_indicator);
        videoAdvancedProgressView = findViewById(R.id.video_advanced_progress_view);
        btnClose = findViewById(R.id.record_btn_close);

        videoDefaultBtnSwitchCamera = findViewById(R.id.video_default_btn_switch_camera);
        videoDefaultBtnFlash = findViewById(R.id.video_default_btn_flash);
        videoHorizontalToolsLayout = findViewById(R.id.video_horizontal_tools_layout);
        videoVerticalToolsLayout = findViewById(R.id.video_vertical_tools_layout);
        videoAdvancedBtnGotoEdit = findViewById(R.id.video_advanced_btn_goto_edit);
        videoAdvancedBtnDelay = findViewById(R.id.video_advanced_btn_delay);
        videoSelectMusicTv = findViewById(R.id.music_name);
        videoAdvancedBtnDelete = findViewById(R.id.video_advanced_btn_delete);
        videoControlLayout = findViewById(R.id.video_control_layout);
        videoDefaultRecordBtn = findViewById(R.id.video_default_record_btn);
        videoAdvancedRecordBtn = findViewById(R.id.video_advanced_record_btn);
        delayText = findViewById(R.id.record_delay_text);
        recordCancelTip = findViewById(R.id.record_cancel_tip);
        cancelDelayBtn = findViewById(R.id.video_advanced_cancel_delay_btn);
        filterNameTip = findViewById(R.id.filter_name_tv);
        tvFilterName = findViewById(R.id.tv_filter_name);
        stubDeleteTip = findViewById(R.id.stub_delete_tip);
        mMakeUp = findViewById(R.id.video_beauty);

        videoRecordControllerLayout = findViewById(R.id.video_record_btn_layout);
        // 变脸面板ViewStub对象
        mFacePannelViewStub = findViewById(R.id.record_face_viewstub);

        videoFaceContainer = findViewById(R.id.video_face_container);
        videoSpeed = findViewById(R.id.speed);
        videoSlimmingContainer = findViewById(R.id.video_meiyan);

        UIUtils.setTopDrawable(videoAdvancedBtnDelay, IC_DELAY_OFF);
        timeBackground = getResources().getDrawable(R.drawable.video_record_time_background);

        mStickerTriggerTipView = findViewById(R.id.record_sticker_trigger_tip);
        if (recordTipManager != null) {
            recordTipManager.setTipView(mStickerTriggerTipView);
        }

        setPreView();
        initRecorder();
        initProgressView();
        initRecordButton();
        initFragments();
        initViewPager();
        initEvent();
        initMarginBottom();
        onPageChange(false);
        processInitMusic();
        tvFilterName.setVisibility(canUseFilterOrFace() ? View.VISIBLE : View.GONE);
        videoFaceContainer.setVisibility(canUseFilterOrFace() ? View.VISIBLE : View.GONE);
        videoSpeed.setVisibility(state == Constants.RecordTab.VIDEO ? View.VISIBLE : View.INVISIBLE);
        videoSlimmingContainer.setVisibility(canUseFilterOrFace() ? View.VISIBLE : View.GONE);
        recordPagerIndicator.setVisibility(View.VISIBLE);

        // 检查变脸模板是否展示
        checkFacePanelInit();

        contentView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                contentView.getViewTreeObserver().removeOnPreDrawListener(this);
                if (videoAdvancedRecordBtn != null) {
                    videoAdvancedRecordBtn.requestLayout();
                }
                return false;
            }
        });
        checkSurfaceSize();
    }

    private void checkSurfaceSize() {
        Size visualSize = mPresenter.getVisualSize();
        videoRecordSurfaceView.getLayoutParams().width = visualSize.getWidth();
        videoRecordSurfaceView.getLayoutParams().height = visualSize.getHeight();
        videoRecordSurfaceView.requestLayout();
    }

    private void setPreView() {
        videoRecordSurfaceView.setBackgroundColor(Color.argb(0x00, 0, 0, 0));//透明背景色
        if (this.getHolder() != null) {
            this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        }
    }

    @Override
    public void hidePreImageView() {
        MomoMainThreadExecutor.post(new Runnable() {
            @Override
            public void run() {
                if (isFirstFrameShow) {
                    AlphaAnimation animation2 = new AlphaAnimation(1, 0);
                    animation2.setDuration(500);
                    animation2.setFillAfter(true);
                    lastFrameView.startAnimation(animation2);
                    isFirstFrameShow = false;
                }
            }
        });

    }

    private void checkFacePanelInit() {
        if (mRecorderParams == null) {
            return;
        }
        if (!TextUtils.isEmpty(mRecorderParams.getInitFaceClassId()) && TextUtils.isEmpty(mRecorderParams.getInitFaceId())) {
            showFacePanel();
            mFacePanelElement.setSelectTab(mRecorderParams.getInitFaceClassId());
        } else if (!TextUtils.isEmpty(mSelectedFaceClassId) && !TextUtils.isEmpty(mSelectedFaceID)) {
            initFacePanel(true);
        }
    }

    private void processInitMusic() {
        //初始化带进来的音乐
        if (initMusic != null) {
            setupInitMusic(initMusic);
        }
    }

    /**
     * 初始化带进来的音乐，需要加入到 Recorder 里面
     */
    private void setupInitMusic(MusicContent initMusic) {
        mPresenter.setPlayMusic(initMusic);
        updateMusicView(initMusic);
        selectedMusic = initMusic;
    }

    private void updateMusicView(MusicContent music) {
        if (music != null) {
            videoSelectMusicTv.setText(music.name);
            videoSelectMusicTv.setActivated(true);
        } else {
            videoSelectMusicTv.setText("配乐");
            videoSelectMusicTv.setActivated(false);
        }
    }

    private void chooseLocalMusic() {
        if (musicPanelHelper == null) {
            int initMusicVolume = 100;
            musicPanelHelper = new MusicPanelHelper(getChildFragmentManager(), getContentView(), initMusicVolume);
            musicPanelHelper.setOnMusicListener(new MusicPanelHelper.OnMusicListener() {

                @Override
                public void onSelect(@NonNull MusicContent music) {
                    selectedMusic = music;
                    boolean result = mPresenter.setPlayMusic(music);
                    if (result) {
                        updateMusicView(music);
                    }
                }

                @Override
                public void onCut(int startTime, int endTime) {
                    selectedMusic.startMillTime = startTime;
                    selectedMusic.endMillTime = endTime;
                    boolean result = mPresenter.setPlayMusic(selectedMusic);
                    if (result) {
                        updateMusicView(selectedMusic);
                    }
                }

                @Override
                public void onVolumeChanged(int percent) {
                }

                @Override
                public void onHide() {
                }

                @Override
                public void pauseVideo() {
                }

                @Override
                public void clearMusic() {
                    updateMusicView(null);
                    selectedMusic = null;
                    mPresenter.setPlayMusic(null);
                }

            });
        }
        musicPanelHelper.show(selectedMusic, false);
        //        Animation anim = AnimUtils.Animations.newToBottonAnimation(300);
        //        anim.setAnimationListener(new OnAnimationEndListener() {
        //            @Override
        //            public void onAnimationEnd(Animation animation) {
        //                toolsLayout.setVisibility(View.GONE);
        //            }
        //        });
        //        toolsLayout.startAnimation(anim);
    }

    private boolean canAddMusic() {
        boolean isRecording = mPresenter != null && mPresenter.isRecording();
        return videoAdvancedProgressView.getCount() == 0 && !isRecording;
    }

    /***********      Music Panel END        ************/

    private void showSpeedView(boolean show) {
        // 问问不显示变速控件
        if (!show && slideBar == null) {
            return;
        }
        if (slideBar == null) {
            ViewStub viewStub = findViewById(R.id.video_speed_vs);
            View speedView = viewStub.inflate();
            slideBar = speedView.findViewById(R.id.video_speed_slideindicatorbar);
            slideBar.setIndicators(speedTextArray);
            slideBar.setIndicatorBuilder(new SlideIndicatorBuilder() {
                @Override
                public View buildIndicator(SlideIndicatorBar parent) {
                    tvSlideIndicator = new TextView(getContext());
                    int size = UIUtils.getPixels(33f);
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(size, size);
                    tvSlideIndicator.setLayoutParams(lp);
                    tvSlideIndicator.setTextSize(10);
                    tvSlideIndicator.setGravity(Gravity.CENTER);
                    tvSlideIndicator.setTextColor(UIUtils.getColor(R.color.white));
                    tvSlideIndicator.setBackgroundResource(R.drawable.bg_moment_slide_indicator);
                    tvSlideIndicator.setText(speedTextArray[currentSpeedIndex]);
                    return tvSlideIndicator;
                }
            });
            slideBar.addIndicatorSlideListener(new SlideIndicatorBar.OnIndicatorSlideListener() {

                @Override
                public void onIndicatorSliding(View indicator, int indexOfIndicator) {
                    updateSelectText(indexOfIndicator);
                }

                @Override
                public void onIndicatorSettled(View indicator, int indexOfIndicator) {
                    currentSpeed = SpeedValueArray[indexOfIndicator];
                    updateSelectText(indexOfIndicator);
                    currentSpeedIndex = indexOfIndicator;
                }

                private void updateSelectText(int indexOfIndicator) {
                    if (currentSpeedIndex != indexOfIndicator) {
                        currentSpeedIndex = indexOfIndicator;
                        tvSlideIndicator.setText(speedTextArray[indexOfIndicator]);
                    }
                }

            });
        }
        if (show) {
            slideBar.setCurrentIndicatorIndex(currentSpeedIndex);
            AnimUtils.Default.fadeWeight(slideBar, true);
        } else {
            beautyPanelLayout.setVisibility(View.GONE);
            slideBar.setVisibility(View.GONE);
        }
    }

    private boolean canUseFilterOrFace() {
        return MomentUtils.isSupportRecord();
    }

    private int getVirtualBarHeight() {
        return 0;
    }

    private void initMarginBottom() {
        final int vbh = getVirtualBarHeight();
        if (vbh > 0) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) recordPagerIndicator.getLayoutParams();
            layoutParams.bottomMargin += vbh;
            recordPagerIndicator.setLayoutParams(layoutParams);

            layoutParams = (ViewGroup.MarginLayoutParams) videoControlLayout.getLayoutParams();
            layoutParams.bottomMargin += vbh;
            videoControlLayout.setLayoutParams(layoutParams);
        }
    }

    @Override
    public void initFlashAndSwitchButton() {
        if (mPresenter != null) {
            if (mPresenter.supporFrontCamera()) {
                videoDefaultBtnSwitchCamera.setVisibility(View.VISIBLE);
            } else {
                videoDefaultBtnSwitchCamera.setVisibility(View.INVISIBLE);
            }
            if (mPresenter.supportFlash() && !isFrontCamera()) {
                videoDefaultBtnFlash.setVisibility(View.VISIBLE);
            } else {
                videoDefaultBtnFlash.setVisibility(View.INVISIBLE);
            }
            setFlashButtonByMode();
        }
    }

    @Override
    public void finish() {
        finishActivity();
    }

    //    @Override
    //    public void refreshPreviewInfo(MultiRecorder.PreviewInfo previewInfo) {
    //        if (testTextHelper == null) {
    //            testTextHelper = new TestTextHelper((ViewStub) findViewById(R.id.record_preview_info_stub));
    //        }
    //        testTextHelper.setCameraPreviewInfo(previewInfo);
    //    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mPresenter.onConfigurationChanged(newConfig);
    }

    private void setFlashButtonByMode() {
        switch (flashMode) {
            case IRecorder.FLAHS_MODE_ON:
                videoDefaultBtnFlash.setImageResource(IC_DEFAULT_FLASH_ON);
                break;
            case IRecorder.FLASH_MODE_OFF:
                videoDefaultBtnFlash.setImageResource(IC_DEFAULT_FLASH_OFF);
                break;
        }
        mPresenter.setFlashMode(flashMode);
    }

    private void initProgressView() {
        videoAdvancedProgressView.setListener(new MomentRecordProgressView.ProgressListener() {
            @Override
            public void onProgress(long ms) {
                if (!draggingToCancel) {
                    int s = (int) (ms / 1000);
                    int m = s / 60;
                    s = s % 60;
                    String mm = m + "";
                    String ss = s >= 10 ? s + "" : "0" + s;
                    recordCancelTip.setText(mm + ":" + ss);
                    if (recordCancelTip.getVisibility() != View.VISIBLE) {
                        recordCancelTip.setVisibility(View.VISIBLE);
                    }
                    recordCancelTip.setBackgroundDrawable(timeBackground);
                }
                if (ms == getMaxDuration()) {
                    onNoRecordTime();
                }
            }
        });
    }

    private void onNoRecordTime() {
        recordCancelTip.setVisibility(View.INVISIBLE);
        if (mPresenter != null) {
            mPresenter.stopRecording();
            refreshView(false);
            resetRecordButton(true);
            if (videoAdvancedProgressView.getCount() == 1) {
                boolean result = mPresenter.finishRecord(onRecordFinishedListener);
                if (result) {
                    onStartFinish();
                }
            }
        }
    }

    private void initRecorder() {
        mPresenter = new RecordPresenter(mRecorderParams);
        if (!TextUtils.isEmpty(restoreVideoPath) && state == Constants.RecordTab.VIDEO) {
            mPresenter.setVideoOutputPath(restoreVideoPath);
        } else {
            restoreVideoPath = new File(Configs.getDir("record", true), System.currentTimeMillis() + "cache.mp4_").getAbsolutePath();
            mPresenter.setVideoOutputPath(restoreVideoPath);
        }
        mPresenter.initWith(getActivity(), this);
        if (recordTipManager != null) {
            recordTipManager.setRecorder(new MomoRecorderProxy(mPresenter));
        }
        if (mRecorderParams.getInitMusic() != null) {
            mPresenter.setPlayMusic(mRecorderParams.getInitMusic());
            videoSelectMusicTv.setText(mRecorderParams.getInitMusic().name);
        }
    }

    private void initFragments() {
        fragments = new Fragment[]{
                PlaceHolderFragment.newInstance("DefaultRecord"),   //普通拍摄页，占位
                PlaceHolderFragment.newInstance("AdvancedRecord"),  //高级拍摄页，占位
        };
    }

    private String getFragmentName(int id) {
        switch (id) {
            case 0:
                return "DefaultRecord";
            case 1:
                return "AdvancedRecord";
            default:
                break;
        }
        return "";
    }

    private void initTabText() {
        if (fragments.length == 2) {
            recordPagerIndicator.setText(BOTTOM_TEXT_DEFAULT_RECORD, BOTTOM_TEXT_ADVANCED_RECORD);
        } else {
            recordPagerIndicator.setText(BOTTOM_TEXT_DEFAULT_RECORD);
        }
    }

    private void initViewPager() {
        changeFragmentViewpager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }

            public String makeFragmentName(ViewGroup parent, long id) {
                return getFragmentName((int) id);
            }
        });
        recordPagerIndicator.setViewPager(changeFragmentViewpager, state);
        if (state != Constants.RecordTab.PHOTO) {
            MomoMainThreadExecutor.post(new Runnable() {
                @Override
                public void run() {
                    onPageChange(state == Constants.RecordTab.VIDEO);
                }
            });
        }

        changeFragmentViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (videoAdvancedRecordBtn == null) {
                    return;
                }
                final float offset = position + positionOffset;
                if (offset <= Constants.RecordTab.PHOTO) {
                    videoAdvancedRecordBtn.switchFromAdvanced();
                    return;
                }
                gotoAdvancedFromDefault();
                if (offset >= Constants.RecordTab.VIDEO) {
                    videoAdvancedRecordBtn.switchToAdvanced();
                } else {
                    videoAdvancedRecordBtn.onSwitchAnim(positionOffset);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (state != position) {
                    state = position;
                    onPageChange(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        boolean enable = (fragments.length > 1);
        changeFragmentViewpager.setEnabled(enable);
        final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                if (isFacePanelShowing()) {
                    hideFacePanel();
                    showBottomVideoControlLayout();
                }
                if (isFilterPanelShow()) {
                    hideFilterPanel();
                    showBottomVideoControlLayout();
                }

                if (slideBar != null && slideBar.getVisibility() == View.VISIBLE) {
                    showSpeedView(false);
                }
                if (beautyPanelLayout.getVisibility() == View.VISIBLE) {
                    beautyPanelLayout.setVisibility(View.GONE);
                    showBottomVideoControlLayout();
                }
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                float x = e.getX();
                float y = e.getY();
                onClickScreen(x, y);
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (delayStartRecordTask != null)
                    return true;
                if (mPresenter != null) {
                    if (mPresenter.supporFrontCamera() && !mPresenter.isRecording()) {
                        if (recordTipManager != null) {
                            recordTipManager.reset();
                        }
                        mPresenter.switchCamera();
                        if (cameraType == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                            cameraType = Camera.CameraInfo.CAMERA_FACING_BACK;
                        } else {
                            cameraType = Camera.CameraInfo.CAMERA_FACING_FRONT;
                            if (flashMode == IRecorder.FLAHS_MODE_ON) {
                                switchFlash();
                            }
                        }
                    }
                }
                return true;
            }
        });
        changeFragmentViewpager.setBeforeCheckEnableTouchListener(new ScrollMoreViewPager.BeforeCheckEnableTouchListener() {

            @Override
            public boolean onTouch(MotionEvent event) {
                mFocusView.feedEvent(event);
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (isFacePanelShowing()) {
                        hideFacePanel();
                        showBottomVideoControlLayout();
                        return true;
                    }
                    if (isFilterPanelShow()) {
                        hideFilterPanel();
                        showBottomVideoControlLayout();
                        return true;
                    }
                    if (slideBar != null && slideBar.getVisibility() == View.VISIBLE) {
                        showSpeedView(false);
                        return true;
                    }
                }
                if (mIsArkit) {
                    boolean consumed = XEngineEventHelper.touchHitTest(event.getX(), event.getY());
                    if (consumed) {
                        XEngineEventHelper.handEvent(event, changeFragmentViewpager);
                    }
                    changeFragmentViewpager.setEnabled(!consumed);
                    return consumed;
                } else {
                    if (mIs3D) {
                        boolean consumed = XEngineEventHelper.touchHitTest(event.getX(), event.getY());
                        if (consumed) {
                            XEngineEventHelper.handEvent(event, changeFragmentViewpager);
                            return true;
                        }
                    }
                    if (mPresenter != null) {
                        mPresenter.feedCameraZoomEvent(event);
                    }
                    return gestureDetector.onTouchEvent(event);
                }
            }
        });
        initTabText();
        changeFragmentViewpager.setOnVerticalFlingListener(new FilterScrollMoreViewPager.VerticalMovingListener() {
            boolean up = false;

            @Override
            public void onMoving(float offset) {
                if (mFocusView.getVisibility() == View.VISIBLE) {
                    return;
                }
                if (mIsArkit) {
                    return;
                }
                if (!canUseFilterOrFace())
                    return;
                if (mPresenter != null && mPresenter.isRecording())
                    return;
                if (isRunningFilterAnim())
                    return;
                up = offset < 0;
                switchFilter(offset);
            }

            @Override
            public void onFling(boolean up) {

            }

            @Override
            public void onUp(float offset) {
                if (mIsArkit) {
                    return;
                }
                if (!canUseFilterOrFace())
                    return;
                if (mPresenter != null && mPresenter.isRecording())
                    return;
                if (isRunningFilterAnim())
                    return;
                if (currentFilterOffset != 0) {
                    switchFilter(offset < 0, true, false, 200);
                }
            }

            @Override
            public void onCancel() {
                if (mIsArkit) {
                    return;
                }
                if (!canUseFilterOrFace())
                    return;
                if (mPresenter != null && mPresenter.isRecording())
                    return;
                if (isRunningFilterAnim())
                    return;
                if (currentFilterOffset != 0) {
                    switchFilter(!up, true, true, 200);
                }
            }
        });
    }


    public boolean isFrontCamera() {
        return mPresenter != null && mPresenter.isFrontCamera();
    }

    /**
     * 跟手滑动滤镜
     * offset < 0 : 往上滑，需要看到下一个滤镜，index 不变，设置-offset (0 ~ 1)
     * offset > 0 : 往下滑，需要看到上一个滤镜，index - 1，设置1-offset (1 ~ 0)
     */
    private void switchFilter(float offset) {
        if (mPresenter != null) {
            int endIndex = mCurFilterPos + (offset < 0 ? 1 : -1);
            final int size = getFilterSize();
            int setIndex = mCurFilterPos;
            currentFilterOffset = offset > 0 ? 1 - offset : -offset;
            mPresenter.changeToFilter(setIndex, offset < 0, currentFilterOffset);
        }
    }

    private float currentFilterOffset = 0;
    private ValueAnimator filterAnim;

    private boolean isRunningFilterAnim() {
        return filterAnim != null && filterAnim.isRunning();
    }

    /**
     * 自动切换滤镜
     *
     * @param up     是否往上滑
     *               true: 需要看到下一个滤镜，index不变，仅设置offset从0（current）到1，最终设置的index+1，设置的offset为0，mCurFilterPos+1
     *               false: 需要看到上一个滤镜，index -1，设置offset从1(current)到0，最终设置index-1，offset为0，mCurFilterPos-1
     * @param smooth 是否平滑过渡
     */
    private void switchFilter(final boolean up, boolean smooth, boolean cancel, long duration) {
        if (mPresenter != null) {
            final int size = getFilterSize();
            int endIndex = mCurFilterPos + (up ? 1 : -1);
            int setIndex = mCurFilterPos;
            if (cancel)
                endIndex = mCurFilterPos;
            if (endIndex < 0) {
                endIndex = size - 1;
                setIndex = size;
            } else if (endIndex >= size) {
                endIndex = 0;
            }
            final int finalEndIndex = endIndex;
            final int finalSetIndex = setIndex;
            if (smooth) {
                int endValue;
                if (cancel) {
                    //cancel时目标值反过来
                    endValue = up ? 0 : 1;
                } else {
                    endValue = up ? 1 : 0;
                }
                if (filterAnim != null && filterAnim.isRunning()) {
                    filterAnim.cancel();
                }
                if (filterAnim == null) {
                    filterAnim = new ValueAnimator();
                }
                filterAnim.setDuration(duration);
                filterAnim.setFloatValues(currentFilterOffset, endValue);
                filterAnim.removeAllUpdateListeners();
                filterAnim.removeAllListeners();
                filterAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float v = (float) animation.getAnimatedValue();
                        if (v >= 1 || v <= 0)
                            return;
                        if (mPresenter != null) {
                            mPresenter.changeToFilter(finalSetIndex, up, v);
                        }
                    }
                });
                filterAnim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        selectFilterPos(finalEndIndex, up);
                        currentFilterOffset = 0;
                    }
                });
                filterAnim.start();
            } else {
                if (mCurFilterPos != endIndex) {
                    mCurFilterPos = endIndex;
                    mPresenter.changeToFilter(mCurFilterPos, up, 0);
                    mCurrentFilterIntensity = 1;
                    if (filterPanel != null) {
                        filterPanel.changeFilterDensityProgress(100);
                    }
                    showFilterName();
                }
            }
        }
    }

    private void selectFilterPos(int pos, boolean up) {
        if (mPresenter != null) {
            mPresenter.changeToFilter(pos, up, 0);
            if (pos != mCurFilterPos) {
                mCurrentFilterIntensity = 1;
                if (filterPanel != null) {
                    filterPanel.changeFilterDensityProgress(100);
                }
            }
        }
        mCurFilterPos = pos;

        showFilterName();
        if (filterPanel != null) {
            filterPanel.showSwitchSelect(mCurFilterPos);
        }
    }

    private int getFilterSize() {
        return filters != null ? filters.size() : 0;
    }

    private final String showFilterTag = "showFilterTag";

    private void showFilterName() {
        if (filters == null || filters.size() <= mCurFilterPos) {
            return;
        }
        final String filterName = filters.get(mCurFilterPos).getName();
        if (TextUtils.isEmpty(filterName)) {
            return;
        }
        filterNameTip.setText(filterName);
        filterNameTip.setVisibility(View.VISIBLE);
        MomoMainThreadExecutor.cancelAllRunnables(showFilterTag);
        MomoMainThreadExecutor.postDelayed(showFilterTag, new Runnable() {
            @Override
            public void run() {
                if (filterNameTip != null) {
                    filterNameTip.setVisibility(View.INVISIBLE);
                }
            }
        }, 1000);
    }

    private void onClickScreen(float x, float y) {

        //        if (isFrontCamera())
        //            return;

        mFocusView.showFocusView(new Point((int) x, (int) y));


        //        Rect focusRect = calculateTapArea(videoRecordSurfaceView.getWidth(), videoRecordSurfaceView.getHeight(), x, y, 1f);
        //        if (mPresenter != null)
        //            mPresenter.focusOnRect(focusRect);
        if (mPresenter != null) {
            mPresenter.focusOnTouch(x, y, videoRecordSurfaceView.getWidth(), videoRecordSurfaceView.getHeight(), true);
        }
    }

    private Animator focusAnim;


    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    private void initEvent() {
        btnClose.setOnClickListener(this);
        videoDefaultBtnSwitchCamera.setOnClickListener(this);
        videoDefaultBtnFlash.setOnClickListener(this);
        videoAdvancedBtnGotoEdit.setOnClickListener(this);
        videoAdvancedBtnDelay.setOnClickListener(this);
        videoSelectMusicTv.setOnClickListener(this);
        videoAdvancedBtnDelete.setOnClickListener(this);
        tvFilterName.setOnClickListener(this);
        videoSlimmingContainer.setOnClickListener(this);
        mMakeUp.setOnClickListener(this);
        videoFaceContainer.setOnClickListener(this);
        videoSpeed.setOnClickListener(this);

        beautyPanelLayout.setBeautySelectListener(new MomentBeautyPanelLayout.BeautySelectListener() {
            @Override
            public void onSelect(File file) {
                if (filter == null) {
                    DokiInitializer.INSTANCE.init(AppContext.getContext());
                    filter = new MakeupFilter();
                    mPresenter.addFilter(filter);
                }
                MakeupHelper.clearMakeupLayer();
                MakeupHelper.parseMakeupResources(file.getAbsolutePath());
            }
        });
        mFocusView.setOnSlideListener(new FocusView.OnSlideListener() {
            @Override
            public void onSlide(float percentage) {
                mPresenter.changeExposureLevel(percentage);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (!v.isEnabled())
            return;
        long now = SystemClock.uptimeMillis();  // 系统开机到当前的时间总数
        if (now - lastClickTime < 500)
            return;
        lastClickTime = now;

        if (v == btnClose) {
            onBackPressed();
        } else if (v == videoDefaultBtnSwitchCamera) {
            hideSpeedView();
            if (mPresenter != null) {
                if (recordTipManager != null)
                    recordTipManager.reset();
                mPresenter.switchCamera();
                if (cameraType == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    cameraType = Camera.CameraInfo.CAMERA_FACING_BACK;
                } else {
                    cameraType = Camera.CameraInfo.CAMERA_FACING_FRONT;
                    if (flashMode == IRecorder.FLAHS_MODE_ON) {
                        switchFlash();
                    }
                }
            }
        } else if (v == videoDefaultBtnFlash) {
            hideSpeedView();
            switchFlash();
        } else if (v == videoAdvancedBtnGotoEdit) {
            if (mPresenter != null) {
                boolean result = mPresenter.finishRecord(onRecordFinishedListener);
                if (result) {
                    onStartFinish();
                }
            }
        } else if (v == videoAdvancedBtnDelay) {
            hideSpeedView();
            switchDelay();
        } else if (v == videoSelectMusicTv) {
            hideSpeedView();
            if (isFilterPanelShow()) {
                hideFilterPanel();
            }
            if (isFacePanelShowing()) {
                hideFacePanel();
            }
            chooseLocalMusic();
        } else if (v == videoAdvancedBtnDelete) {
            hideSpeedView();
            switchDelete();
        } else if (v == tvFilterName) {
            hideSpeedView();
            if (isFacePanelShowing()) {
                hideFacePanel();
            }
            switchFilterPanel(MomentFilterPanelTabLayout.ON_CLICK_FILTER);
        } else if (v == videoSlimmingContainer) {
            hideSpeedView();
            if (isFacePanelShowing()) {
                hideFacePanel();
            }
            switchFilterPanel(MomentFilterPanelTabLayout.ON_CLICK_FACE);
        } else if (v == videoFaceContainer) {
            hideSpeedView();
            if (isFilterPanelShow()) {
                hideFilterPanel();
            }
            switchFacePanel();
        } else if (v == mMakeUp) {
            hideSpeedView();
            if (isFacePanelShowing()) {
                hideFacePanel();
            }
            if (isFilterPanelShow()) {
                hideFilterPanel();
            }
            if (beautyPanelLayout.getVisibility() == View.VISIBLE) {
                beautyPanelLayout.setVisibility(View.GONE);
            } else {
                beautyPanelLayout.setVisibility(View.VISIBLE);
            }
        } else if (v == videoSpeed) {
            if (beautyPanelLayout.getVisibility() == View.VISIBLE) {
                beautyPanelLayout.setVisibility(View.GONE);
            }
            if (null == slideBar || slideBar.getVisibility() == View.GONE) {
                showSpeedView(true);
            } else {
                showSpeedView(false);
            }
        }
    }

    private void hideSpeedView() {
        if (beautyPanelLayout.getVisibility() == View.VISIBLE) {
            beautyPanelLayout.setVisibility(View.GONE);
        }
        if (slideBar != null && slideBar.getVisibility() == View.VISIBLE) {
            showSpeedView(false);
        }
    }

    private void switchFlash() {
        if (mPresenter == null)
            return;
        flashMode--;
        if (flashMode < IRecorder.FLASH_MODE_OFF) {
            flashMode = IRecorder.FLAHS_MODE_ON;
        }
        setFlashButtonByMode();
    }

    @Override
    protected void onActivityResultReceived(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_FOR_EDIT_IMAGE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                backPhoto(data, (MMImageEditParams) data.getParcelableExtra(MediaConstants.KEY_IMAGE_EDIT_PARAMS), resultCode);
            }
        }
    }

    private void backPhoto(final Intent intent, MMImageEditParams imageEditParams, final int resultCode) {
        final Activity a = getActivity();
        if (a != null && !a.isFinishing()) {
            intent.removeExtra(AlbumConstant.KEY_RESULT_IMAGE_EDIT);
            ArrayList<Photo> p = new ArrayList<>(1);
            p.add(imageEditParams.getPhoto());
            intent.putExtra(MediaConstants.EXTRA_KEY_IMAGE_DATA, p);
            if (mRecorderParams != null && mRecorderParams.getFinishGotoInfo() != null) {
                if (mRecorderParams.getFinishGotoInfo().getExtraBundle() != null) {
                    intent.putExtras(mRecorderParams.getFinishGotoInfo().getExtraBundle());
                }
                if (!TextUtils.isEmpty(mRecorderParams.getFinishGotoInfo().getGotoActivityName())) {
                    intent.setComponent(new ComponentName(a, mRecorderParams.getFinishGotoInfo().getGotoActivityName()));
                    a.startActivity(intent);
                    if (mRecorderParams.getFinishGotoInfo().isFinishCurrentActivity()) {
                        a.finish();
                    }
                    return;
                }
            }
            MomoMainThreadExecutor.post(new Runnable() {
                @Override
                public void run() {
                    a.setResult(resultCode, intent);
                    a.finish();
                }
            });

        }
    }

    private void addMusicToRecorder(MusicContent music) {
        if (canAddMusic()) {
            //todo 外部带的音乐自动放入到我的下面，且开始下载开始播放
            videoSelectMusicTv.setActivated(true);
            if (mPresenter != null) {
                mPresenter.setPlayMusic(music);
            }
        }
    }

    private void switchFilterPanel(int tabPosition) {
        if (isFilterPanelShow()) {
            hideFilterPanel();
            showBottomVideoControlLayout();
        } else {
            showFilterPanel(tabPosition);
        }
    }

    private boolean isFilterPanelShow() {
        return filterPanel != null && filterPanel.getVisibility() == View.VISIBLE;
    }

    private void showSpeedViewWithAni() {
        if (slideBar == null || state != Constants.RecordTab.VIDEO) {
            return;
        }
        AnimUtils.Default.showFromBottom(slideBar, 400);
    }

    private void showFilterPanel(int tabPosition) {
        initFilterPanel(tabPosition);
        if (filterPanel.getVisibility() != View.VISIBLE) {
            AnimUtils.Default.showFromBottom(filterPanel, 400);
        }
        hideSpeedView();
        hideBottomVideoControlLayout();
        recordPagerIndicator.setVisibility(View.INVISIBLE);
    }

    private void initFilterPanel(int tabPosition) {
        if (filterPanel == null) {
            ViewStub vs = findViewById(R.id.moment_record_filter_viewstub);
            if (vs == null) return;
            filterPanel = (MomentFilterPanelLayout) vs.inflate();
            filterPanel.setFilterSelectListener(this);
            filterPanel.setFilterDensityChangeListener(new OnFilterDensityChangeListener() {
                @Override
                public void onFilterDensityChange(int density) {
                    mCurrentFilterIntensity = density / 100.0f;
                    mPresenter.setFilterIntensity(mCurrentFilterIntensity);
                }
            });
            final int vbh = getVirtualBarHeight();
            if (vbh > 0) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) filterPanel.getLayoutParams();
                params.height += vbh;
                filterPanel.setLayoutParams(params);
                filterPanel.setPadding(0, 0, 0, vbh);
            }
        }
        filterPanel.switchTabPanel(tabPosition, filters, mCurFilterPos, mCurFilterBeautyPos, mCurFilterEyeThinPos, mCurFilterSlimmingPos, mCurFilterLongLegPos);
    }

    private void hideBottomVideoControlLayout() {
        if (videoControlLayout.getVisibility() == View.VISIBLE) {
            AnimUtils.Default.hideToBottom(videoControlLayout, false, 400);
        }
    }

    private void hideFilterPanel() {
        Animation anim = videoControlLayout.getAnimation();
        if (anim != null) {
            anim.cancel();
        }
        if (recordPagerIndicator.getVisibility() != View.VISIBLE) {
            recordPagerIndicator.setVisibility(View.VISIBLE);
        }
        if (filterPanel != null) {
            AnimUtils.Default.hideToBottom(filterPanel, true, 400);
        }
    }

    private void switchFacePanel() {
        if (isFacePanelShowing()) {
            hideFacePanel();
            showBottomVideoControlLayout();
        } else {
            showFacePanel();
        }
    }

    private void switchDelete() {
        if (videoAdvancedBtnDelete.isActivated()) {
            if (tvDeleteTip != null) {
                tvDeleteTip.setVisibility(View.GONE);
            }
            videoAdvancedProgressView.removeLast();
            videoAdvancedBtnDelete.setActivated(false);
            //需要告诉 recorder删除
            //https://www.fabric.io/momo6/android/apps/com.mm.momo/issues/58d2c9c80aeb16625bb6fa1a?time=last-seven-days
            if (mPresenter != null) {
                mPresenter.removeLast();
            } else {
                videoAdvancedProgressView.clear();
            }
            if (videoAdvancedProgressView.getCount() <= 0) {
                if (mPresenter != null)
                    mPresenter.setStartRecorded(false);
                if (selectedMusic != null) {
                    addMusicToRecorder(selectedMusic);
                }
            }
            refreshDeleteFinishVisibility(false);
            recordCancelTip.setVisibility(View.INVISIBLE);
        } else {
            if (tvDeleteTip == null) {
                tvDeleteTip = (TextView) stubDeleteTip.inflate();
            }
            tvDeleteTip.setVisibility(View.VISIBLE);
            videoAdvancedBtnDelete.setActivated(true);
            videoAdvancedProgressView.markLastDeleting();
        }
    }

    private void switchDelay() {
        switch (chooseDelayTime) {
            case DELAY_OFF:
                chooseDelayTime = DELAY_3;
                UIUtils.setTopDrawable(videoAdvancedBtnDelay, IC_DELAY_3);
                delayText.setText("" + DELAY_3);
                break;
            case DELAY_3:
                chooseDelayTime = DELAY_OFF;
                UIUtils.setTopDrawable(videoAdvancedBtnDelay, IC_DELAY_OFF);
                delayText.setText("OFF");
                break;
        }
        boolean canLongPress = chooseDelayTime <= DELAY_OFF;
        videoAdvancedRecordBtn.setCanLongPress(canLongPress);
        videoAdvancedRecordBtn.setTouchBack(!canLongPress);
        playDelayTextAnim(300);
    }

    @Override
    protected void onLoad() {
        orientationManager = ScreenOrientationManager.getInstance(getContext().getApplicationContext());
        RecordOrientationSwitchListener listener = new RecordOrientationSwitchListener();
        if (mPresenter != null)
            listener.setMrConfig(mPresenter.getMRConfig());


        listener.setNormalRotationViews(videoFaceContainer, videoSpeed, tvFilterName, videoSlimmingContainer, btnClose,
                videoDefaultBtnSwitchCamera, videoDefaultBtnFlash,
                videoAdvancedBtnDelay, videoSelectMusicTv,
                videoAdvancedBtnDelete, mMakeUp);
        listener.setFinishBtn(videoAdvancedBtnGotoEdit);
        orientationManager.setAngleChangedListener(listener);
        orientationManager.start();
    }

    private void startPreview() {
        if (isPreviewing)
            return;
        if (mPresenter != null) {
            isPreviewing = true;
            mPresenter.onResume();
            if (mPresenter.prepare()) {
                mPresenter.changeToFilter(mCurFilterPos, false, 0);
                mPresenter.setFilterIntensity(mCurrentFilterIntensity);
                initFlashAndSwitchButton();
                mPresenter.startPreview();
                mPresenter.initFilter(filters);
                onBeautyTabSelect(Configs.DEFAULT_BEAUTY, MomentFilterPanelLayout.TYPE_BEAUTY);
                onBeautyTabSelect(Configs.DEFAULT_BIG_EYE, MomentFilterPanelLayout.TYPE_EYE_AND_THIN);
                if (filter != null) {
                    mPresenter.addFilter(filter);
                    beautyPanelLayout.clickCurrentTab();
                }
            } else {
                Toaster.show("相机打开失败，请检查系统相机是否可用");
                finishActivity();
            }
        }
    }

    private boolean firstResume = true;

    @Override
    public void onResume() {
        super.onResume();
        startPreview();
        isResumed = true;
        if (!firstResume) {
            onPageChange(false);
        }
        firstResume = false;
        setOrientationManager();
        if (null != musicPanelHelper) {
            musicPanelHelper.onResume();
        }
        videoDefaultRecordBtn.setEnabled(true);
    }

    @Override
    public void onPause() {
        isResumed = false;
        super.onPause();
        if (delayStartRecordTask != null) {
            cancelDelayTask();
        }
        // 记录已经被初始化过
        recordCancelTip.setVisibility(View.INVISIBLE);
        if (mPresenter != null) {
            if (state == Constants.RecordTab.PHOTO && mPresenter.isRecording()) {
                videoDefaultRecordBtn.onLongPressUp();
            }
            mPresenter.onPause();
        }
        //        closeDialog();
        isPreviewing = false;

        if (null != musicPanelHelper) {
            musicPanelHelper.onPause();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (musicPanelHelper != null) {
            musicPanelHelper.onDestory();
        }
        if (mElementManager != null) {
            mElementManager.onDestroy();
        }

        if (changeFragmentViewpager != null) {
            changeFragmentViewpager.setOnVerticalFlingListener(null);
            changeFragmentViewpager.setBeforeCheckEnableTouchListener(null);
        }
        if (mPresenter != null) {
            mPresenter.release();
        }
        if (recordPagerIndicator != null)
            recordPagerIndicator.release();
        if (videoAdvancedProgressView != null)
            videoAdvancedProgressView.release();
        if (videoDefaultRecordBtn != null)
            videoDefaultRecordBtn.release();
        if (videoAdvancedRecordBtn != null)
            videoAdvancedRecordBtn.release();
        if (slideBar != null) {
            slideBar.clearIndicatorSlideListener();
        }
        fragmentChangeListener = null;
        if (recordTipManager != null) {
            recordTipManager.release(getActivity());
        }
        recordTipManager = null;
        if (mPresenter != null && state == Constants.RecordTab.PHOTO) {
            mPresenter.clearTempFiles();
        }
        ScreenOrientationManager.release();

        MomoMainThreadExecutor.cancelAllRunnables(getTaskTag());
        //8.7.3版本去掉新3d引擎代码，后续版本开放.
        //        XE3DEngine.getInstance().endEngine();
        XE3DEngine.getInstance().clearEvent();
    }

    @Override
    public boolean onBackPressed() {
        hideSpeedView();
        long now = SystemClock.uptimeMillis();
        if (now - lastBackPressTime < MIN_BACK_TIME) {
            return true;
        }
        lastBackPressTime = now;
        if (musicPanelHelper != null && musicPanelHelper.onBackPress()) {
            return true;
        }
        if (isFacePanelShowing()) {
            hideFacePanel();
            showBottomVideoControlLayout();
            return true;
        }
        if (isFilterPanelShow()) {
            hideFilterPanel();
            showBottomVideoControlLayout();
            return true;
        }
        if (delayStartRecordTask != null || delayTextTask != null) {
            return true;
        }
        if (mPresenter != null) {
            if (mPresenter.isTakingPhoto())
                return true;
            if (mPresenter.isRecording()) {
                // 问问录制时  点击back 需要清除录制
                stopRecording(false, state == Constants.RecordTab.VIDEO);
                return true;
            } else if (getCount() > 0) {
                showCloseDialog();
                return true;
            }
        }
        //退出页面时，删除临时文件
        if (mPresenter != null) {
            mPresenter.clearTempFiles();
        }
        //        MusicUtils.release();

        if (fragmentChangeListener != null) {
            if (arg == null) {
                arg = new Bundle();
            }
            if (getArguments() != null && getArguments().getParcelable(MediaConstants.KEY_CACHE_EXTRA_PARAMS) != null) {
                arg.putParcelable(MediaConstants.KEY_CACHE_EXTRA_PARAMS, getArguments().getParcelable(MediaConstants.KEY_CACHE_EXTRA_PARAMS));
            }
            arg.putString(GOTO_WHERE, BACK_TO_OLD);
            fragmentChangeListener.change(this, arg);
            return true;
        }

        finishActivity();

        return super.onBackPressed();
    }

    private void showCloseDialog() {
        if (!checkActivityStatus())
            return;
        AlertDialog mCloseDialog = new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("是否放弃录制").setNegativeButton("取消", null).setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mPresenter != null)
                    mPresenter.clearTempFiles();
                //                MusicUtils.release();
                dialog.dismiss();
                finishActivity();
            }
        }).create();
        showDialog(mCloseDialog);
    }

    private boolean isFacePanelShowing() {
        return mMomentPropPanelHelper != null && mMomentPropPanelHelper.isShown();
    }

    private void showFacePanel() {
        initFacePanel(true);
        mMomentPropPanelHelper.show();
        hideSpeedView();
        hideBottomVideoControlLayout();
    }

    private void hideFacePanel() {
        if (mMomentPropPanelHelper != null) {
            mMomentPropPanelHelper.hide();
        }
    }

    private void showBottomVideoControlLayout() {
        AnimUtils.Default.showFromBottom(videoControlLayout, 400);
    }

    private void showRecordTip() {
        String msg = null;
        if (state == Constants.RecordTab.PHOTO) {
            msg = DEFAULT_TIP_RECORD;
        }
        recordCancelTip.setVisibility(View.VISIBLE);
        recordCancelTip.setText(msg);
        recordCancelTip.setBackgroundDrawable(null);
    }

    private void onPageChange(boolean anim) {
        showRecordTip();
        switch (state) {
            case Constants.RecordTab.PHOTO:
                videoSelectMusicTv.setVisibility(View.GONE);
                videoSpeed.setVisibility(View.INVISIBLE);
                //                videoAdvancedBtnDelay.setVisibility(View.INVISIBLE);
                //                videoSelectMusicTv.setVisibility(View.GONE);
                videoDefaultBtnSwitchCamera.setVisibility(View.VISIBLE);
                if (!isFrontCamera()) {
                    videoDefaultBtnFlash.setVisibility(View.VISIBLE);
                }
                videoAdvancedProgressView.setVisibility(View.GONE);
                if (!isFilterPanelShow()) {
                    videoControlLayout.setVisibility(View.VISIBLE);
                    if (canUseFilterOrFace()) {
                        videoFaceContainer.setVisibility(View.VISIBLE);
                        tvFilterName.setVisibility(View.VISIBLE);
                        videoSlimmingContainer.setVisibility(View.VISIBLE);
                    }
                }
                if (isResumed) {
                    startPreview();
                }
                if (!anim) {
                    btnClose.setVisibility(View.VISIBLE);
                    videoRecordControllerLayout.setVisibility(View.VISIBLE);
                    videoAdvancedRecordBtn.setVisibility(View.GONE);
                }
                break;
            case Constants.RecordTab.VIDEO:
                //                videoAdvancedBtnDelay.setVisibility(View.VISIBLE);
                //                videoSelectMusicTv.setVisibility(View.VISIBLE);
                videoSelectMusicTv.setVisibility(View.VISIBLE);
                videoDefaultBtnSwitchCamera.setVisibility(View.VISIBLE);
                videoAdvancedProgressView.setVisibility(View.VISIBLE);
                videoSpeed.setVisibility(View.VISIBLE);
                if (!isFrontCamera()) {
                    videoDefaultBtnFlash.setVisibility(View.VISIBLE);
                }
                btnClose.setVisibility(View.VISIBLE);
                if (!isFilterPanelShow()) {
                    videoControlLayout.setVisibility(View.VISIBLE);
                    if (canUseFilterOrFace()) {
                        videoFaceContainer.setVisibility(View.VISIBLE);
                        tvFilterName.setVisibility(View.VISIBLE);
                        videoSlimmingContainer.setVisibility(View.VISIBLE);
                    }
                }
                if (isResumed) {
                    startPreview();
                }
                if (!anim) {
                    videoRecordControllerLayout.setVisibility(View.GONE);
                    videoAdvancedRecordBtn.setVisibility(View.VISIBLE);

                    MomoMainThreadExecutor.post(new Runnable() {
                        @Override
                        public void run() {
                            videoAdvancedRecordBtn.switchToAdvanced();
                        }
                    });
                }
                break;
            default:
                break;
        }
    }

    private void showVideoToolsLayout(boolean visiable) {
        if (visiable) {
            if (canUseFilterOrFace()) {
                tvFilterName.setVisibility(View.VISIBLE);
                mMakeUp.setVisibility(View.VISIBLE);
                videoFaceContainer.setVisibility(View.VISIBLE);
                videoSpeed.setVisibility(state == Constants.RecordTab.PHOTO ? View.INVISIBLE : View.VISIBLE);
                videoSlimmingContainer.setVisibility(View.VISIBLE);
            }
            if (!isFrontCamera()) {
                videoDefaultBtnFlash.setVisibility(View.VISIBLE);
            }
            //            if (state == Constants.RecordTab.VIDEO) {
            videoAdvancedBtnDelay.setVisibility(View.VISIBLE);
            videoSelectMusicTv.setVisibility(View.VISIBLE);
            //            }
            videoDefaultBtnSwitchCamera.setVisibility(View.VISIBLE);
        } else {
            videoSelectMusicTv.setVisibility(View.INVISIBLE);
            tvFilterName.setVisibility(View.INVISIBLE);
            mMakeUp.setVisibility(View.INVISIBLE);
            videoFaceContainer.setVisibility(View.INVISIBLE);
            videoSpeed.setVisibility(View.INVISIBLE);
            videoDefaultBtnFlash.setVisibility(View.INVISIBLE);
            videoAdvancedBtnDelay.setVisibility(View.INVISIBLE);
            videoDefaultBtnSwitchCamera.setVisibility(View.INVISIBLE);
            videoSlimmingContainer.setVisibility(View.INVISIBLE);
            if (null != slideBar) {
                slideBar.setVisibility(View.GONE);
            }
        }
    }

    private void gotoAdvancedFromDefault() {
        videoRecordControllerLayout.setVisibility(View.INVISIBLE);
        videoAdvancedRecordBtn.setVisibility(View.VISIBLE);

    }

    @Override
    public SurfaceHolder getHolder() {
        return videoRecordSurfaceView.getHolder();
    }

    @Override
    public void initFacePanel(boolean hasInitFace) {
        if (hasInitFace && mFacePanelElement == null && mFacePannelViewStub != null) {
            mMomentPropPanelHelper = new MomentPropPanelHelper();
            mMomentPropPanelHelper.initView(mFacePannelViewStub);

            mFacePanelElement = mMomentPropPanelHelper.getFacePanelElement();

            mFacePannelViewStub = null;
            mFacePanelElement.setFaceDataManager(MomentFaceUtil.createMomentFaceDataManager(MomentFaceConstants.MOMENT_FACE));
            //适配底部虚拟导航栏
            final int navigationBarHeight = getVirtualBarHeight();
            final String dmode = DeviceUtils.getModle();
            if (navigationBarHeight > 0 && !DeviceUtils.isMIUI()
                    //魅族MX5、华为P10，虽能获取到导航栏高度，但实际没有导航栏
                    && !TextUtils.equals(dmode, "MX5")
                    && !TextUtils.equals(dmode, "MX3")
                    && !TextUtils.equals(dmode, "VKY-AL00")) {
                //高度加上navigationBar的高度
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mFacePanelElement.getLayoutParams();
                params.bottomMargin = navigationBarHeight;
            }

            mFacePanelElement.setMomentFacePanelHelper(new MomentFacePanelHelper() {
                @Override
                protected boolean loadMaskWhenClick(MomentFace face) {
                    return mPresenter != null;
                }

                @Override
                public void onMaskLoadSuccess(MaskModel maskModel, MomentFace face) {
                    super.onMaskLoadSuccess(maskModel, face);
                    if (maskModel == null) {
                        return;
                    }

                    if (face.isOverlap()) {
                        mPresenter.addCustomTypeMaskModel(maskModel);
                    } else {
                        if (recordTipManager != null)
                            recordTipManager.reset();
                        mIs3D = is3d(maskModel);
                        mSelectedFaceID = face.getId();
                        mSelectedFaceClassId = face.getClassId();
                        mPresenter.addMaskModel(maskModel);

                    }
//                    if (maskModel.getFilterDisable() != null && maskModel.getFilterDisable().booleanValue()) {
//                        selectFilterPos(0, false);
//                    }
                }

                private boolean is3d(MaskModel maskModel) {
                    for (Sticker sticker : maskModel.getStickers()) {
                        if ("3d".equals(sticker.getLayerType())) {
                            return true;
                        }
                    }
                    return false;
                }

                @Override
                protected void onItemSelected(MomentFace face) {
                    mIsArkit = face.isArkit();
                    if (mIsArkit && mPresenter.isFrontCamera()) {
                        mPresenter.switchCamera();
                        cameraType = Camera.CameraInfo.CAMERA_FACING_BACK;
                    }
                }

                @Override
                protected void onClear() {
                    if (mPresenter == null) {
                        return;
                    }
                    mPresenter.clearFace();
                    mIsArkit = false;
                    mSelectedFaceID = null;
                    mSelectedFaceClassId = null;
                    if (recordTipManager != null) {
                        recordTipManager.setAdditionalInfo(null);
                        recordTipManager.reset();
                        recordTipManager.onClearMask();
                    }
                    onBeautyTabSelect(mCurFilterEyeThinPos
                            , MomentFilterPanelLayout.TYPE_EYE_AND_THIN);
                }
            });

            mElementManager = new ElementManager(getActivity(), Collections.singletonList((Element) mFacePanelElement));
            mElementManager.onCreate();
            mFacePanelElement.loadFaceData();
            if (!TextUtils.isEmpty(mRecorderParams.getInitFaceId()) && mFacePanelElement != null) {
                TolerantMoment tolerantMoment = new TolerantMoment(mSelectedFaceClassId, mSelectedFaceID);
                mFacePanelElement.setTolerantMoment(tolerantMoment);
            }
//            todo wujian
/*            mFacePanelElement.setPanelRecordBtnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideFacePanel();
                    showBottomVideoControlLayout();
                    if (state == Constants.RecordTab.PHOTO) {
                        if (videoDefaultRecordBtn.getCallback() != null) {
                            videoDefaultRecordBtn.getCallback().onClick();
                        }
                    } else {
                        if (videoAdvancedRecordBtn.getCallback() != null) {
                            videoAdvancedRecordBtn.getCallback().onClick();
                        }
                    }
                }
            });*/

            mMomentPropPanelHelper.setOnResetClickListener(new MomentPropPanelHelper.OnClickListener() {
                @Override
                public void onResetClick() {
                    showFaceResetDialog();
                }

                @Override
                public void onRecordClick() {
                    hideFacePanel();
                    showBottomVideoControlLayout();
                    if (state == Constants.RecordTab.PHOTO) {
                        if (videoDefaultRecordBtn.getCallback() != null) {
                            videoDefaultRecordBtn.getCallback().onClick();
                        }
                    } else {
                        if (videoAdvancedRecordBtn.getCallback() != null) {
                            videoAdvancedRecordBtn.getCallback().onClick();
                        }
                    }
                }
            });
        }
    }

    private void showFaceResetDialog() {
        if (!checkActivityStatus())
            return;
        AlertDialog dialog = new AlertDialog.Builder(this.getActivity())
                .setTitle("提示")
                .setMessage("确认恢复默认效果吗?")
                .setPositiveButton("确认", new DialogInterface
                        .OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mMomentPropPanelHelper.getFacePanelElement().clearSelectedItem();

                        mPresenter.clearFace();
                        mSelectedFaceID = null;
                        mSelectedFaceClassId = null;
                        if (recordTipManager != null) {
                            recordTipManager.setAdditionalInfo(null);
                            recordTipManager.reset();
                            recordTipManager.onClearMask();
                        }
                        onBeautyTabSelect(mCurFilterEyeThinPos
                                , MomentFilterPanelLayout.TYPE_EYE_AND_THIN);

                        recordTipManager.reset();
                        dialog.dismiss();
                    }
                }).create();

        showDialog(dialog);
    }

    private void setCurrentFilterIndex(int pos) {
        if (mCurFilterPos != pos) {
            mCurFilterPos = pos;
            showFilterName();
        }
    }

    @Override
    public void onCameraSet() {
        if (isDetached() || isRemoving()) {
            return;
        }
        recordTipManager.onCameraSet();
        //        onClickScreen(videoRecordSurfaceView.getWidth() / 2, videoRecordSurfaceView.getHeight() / 2);
    }

    //    @Override
    //    public long getLastDuration() {
    //        switch (state) {
    //            case Constants.RecordTab.PHOTO:
    //                return (long) (defaultProgress * getMaxDuration());
    //            case Constants.RecordTab.VIDEO:
    //                return videoAdvancedProgressView.getLastSliceDuration();
    //        }
    //        return 0;
    //    }

    @Override
    public long getMinDuration() {
        return mRecorderParams == null ? MediaConstants.MIN_VIDEO_DURATION : mRecorderParams.getMinDuration();
    }

    @Override
    public void removeLast() {
        videoAdvancedProgressView.stopRecord();
        videoAdvancedProgressView.removeLast();
    }

    @Override
    public int getCount() {
        return videoAdvancedProgressView.getCount();
    }

    //    @Override
    //    public void clearProgress() {
    //        videoAdvancedProgressView.clear();
    //        //上传预览入口：未拍摄成功（未跳转），显示
    //        videoRecordAlbumPreviewIv.setVisibility(isShowAlbumPreview(View.VISIBLE));
    //    }

    @Override
    public void refreshView(boolean isRecording) {
        if (goingToEdit)
            return;
        videoAdvancedBtnGotoEdit.setEnabled(true);
        btnClose.setEnabled(true);
        showVideoToolsLayout(!isRecording);
        if (isRecording) {
            cancelDelayBtn.setVisibility(View.GONE);
            switch (state) {
                case Constants.RecordTab.VIDEO:
                    videoAdvancedRecordBtn.setTouchBack(true);
                    //                    hideAdvancedToolsLayout(300);
                    break;
                case Constants.RecordTab.PHOTO:
                    btnClose.setVisibility(View.GONE);
                    if (mPresenter != null && mPresenter.supporFrontCamera()) {
                        AnimUtils.Default.fadeWeight(videoDefaultBtnSwitchCamera, false);
                    }
                    break;
            }
            videoAdvancedBtnDelete.setVisibility(View.INVISIBLE);
            recordPagerIndicator.setVisibility(View.GONE);
        } else {
            videoAdvancedProgressView.stopRecord();
            videoAdvancedBtnDelay.setEnabled(true);
            btnClose.setVisibility(View.VISIBLE);
            switch (state) {
                case Constants.RecordTab.VIDEO:
                    videoAdvancedRecordBtn.setTouchBack(false);
                    //                    showAdvancedToolsLayout(300);
                    break;
                case Constants.RecordTab.PHOTO:
                    if (mPresenter != null && mPresenter.supporFrontCamera()) {
                        AnimUtils.Default.fadeWeight(videoDefaultBtnSwitchCamera, true);
                    }
                    break;
            }

            recordPagerIndicator.setVisibility(View.VISIBLE);

        }
        refreshDeleteFinishVisibility(isRecording);
        if (tvDeleteTip != null && tvDeleteTip.getVisibility() == View.VISIBLE) {
            tvDeleteTip.setVisibility(View.GONE);
        }
    }

    private void refreshDeleteFinishVisibility(boolean isRecording) {
        if (isResumed) {
            showVideoToolsLayout(!isRecording);
        }
        if (isRecording) {
            switch (state) {
                case Constants.RecordTab.VIDEO:
                    videoAdvancedBtnDelete.setActivated(false);
                    if (videoAdvancedBtnGotoEdit.getVisibility() == View.VISIBLE) {
                        AnimUtils.Default.fadeWeight(videoAdvancedBtnGotoEdit, false);
                    }
                    if (videoAdvancedProgressView.getVisibility() != View.VISIBLE) {
                        AnimUtils.Default.fadeWeight(videoAdvancedProgressView, true);
                    }
                    break;
                case Constants.RecordTab.PHOTO:

                    break;
            }
        } else {
            switch (state) {
                case Constants.RecordTab.VIDEO:
                    boolean showDelete = videoAdvancedProgressView.getCount() > 0;
                    if (showDelete) {
                        videoAdvancedBtnDelete.setVisibility(View.VISIBLE);

                        changeFragmentViewpager.setEnabled(false);
                        recordPagerIndicator.setEnabled(false);
                        videoSelectMusicTv.setEnabled(false);
                        videoSelectMusicTv.setAlpha(0.5f);
                    } else {
                        changeFragmentViewpager.setEnabled(true);
                        recordPagerIndicator.setEnabled(true);
                        videoAdvancedBtnDelete.setVisibility(View.INVISIBLE);
                        AnimUtils.Default.fadeWeight(videoAdvancedProgressView, false);
                        videoSelectMusicTv.setEnabled(true);
                        videoSelectMusicTv.setAlpha(1f);
                    }
                    if (isHasVideo()) {
                        showFinishBtn();
                    } else {
                        hideFinishBtn();
                    }

                    break;
                case Constants.RecordTab.PHOTO:

                    break;
            }
        }
        setOrientationManager();
    }

    private void setOrientationManager() {
        if (orientationManager == null) {
            return;
        }
        if (!isResumed) {
            if (orientationManager != null && orientationManager.isListening())
                orientationManager.stop();
        } else {
            if (mPresenter != null && (mPresenter.isStartRecorded()) || mPresenter.isRecording()) {
                if (orientationManager != null && orientationManager.isListening())
                    orientationManager.stop();
            } else if (orientationManager != null && !orientationManager.isListening()) {
                orientationManager.start();
            }
        }
    }

    private void showFinishBtn() {
        float alpha = videoAdvancedProgressView.getRecordDuration() < getMinDuration() ? 0.3f : 1;
        videoAdvancedBtnGotoEdit.setVisibility(View.VISIBLE);
        videoAdvancedBtnGotoEdit.setAlpha(alpha);
    }

    private void hideFinishBtn() {
        AnimUtils.Default.fadeWeight(videoAdvancedBtnGotoEdit, false);
    }

    @Override
    public void resetRecordButton(boolean needAnim) {
        switch (state) {
            case Constants.RecordTab.PHOTO:
                videoDefaultRecordBtn.reset();
                break;
            case Constants.RecordTab.VIDEO:
                videoAdvancedRecordBtn.reset(needAnim);
                break;
        }
    }

    @Override
    public void restoreByFragments(List<VideoFragment> fragments) {
        if (state == Constants.RecordTab.VIDEO) {
            videoAdvancedProgressView.setMaxDuration(getMaxDuration());
            videoAdvancedProgressView.restoreKeepedSlice(fragments);
            if (videoAdvancedProgressView.getCount() > 0) {
                videoAdvancedProgressView.setVisibility(View.VISIBLE);
                refreshDeleteFinishVisibility(false);
            }
            videoAdvancedBtnDelete.setActivated(false);
        }
    }

    @Override
    public void onStartFinish() {
        String text = null;
        if (videoAdvancedProgressView.getCount() > 1)
            text = "正在处理 0%";
        else
            text = "正在处理";
        finishingDialog = new ProgressDialog(getContext());
        finishingDialog.setMessage(text);
        finishingDialog.getWindow().setLayout(UIUtils.getPixels(190), UIUtils.getPixels(50));
        finishingDialog.setCancelable(false);
        finishingDialog.setCanceledOnTouchOutside(false);
        showDialog(finishingDialog);
    }

    public boolean isVideoDurationValid() {
        boolean result = getRecordDuration() >= getMinDuration();
        if (!result) {
            Toaster.showInvalidate("视频时长最短需要" + getMinDuration() / 1000 + "s");
        }
        return result;
    }

    public boolean isHasVideo() {
        return videoAdvancedProgressView.getRecordDuration() > 0;
    }

    public void onTakePhoto(String path, Exception e) {
        if (e == null) {
            gotoEditImage(path);
        } else {
            videoDefaultRecordBtn.setEnabled(true);
            MDLog.printErrStackTrace(LogTag.RECORDER.RECORD, e);
            Toaster.show("拍照失败");
        }
        changeFragmentViewpager.setEnabled(fragments.length > 1);
        recordPagerIndicator.setEnabled(true);
    }

    private void gotoEditImage(String path) {
        Photo photo = new Photo(0, path);
        photo.isTakePhoto = true;
        photo.tempPath = path;
        photo.isFromCamera = true;
        photo.isOriginal = true;
        // 数据打点
        Intent intent = new Intent(getActivity(), ImageEditActivity.class);
//        todo wujian 完成修改
        intent.putExtra(AlbumConstant.KEY_FINISH_TEXT, "完成");


        MMImageEditParams editParams = new MMImageEditParams.Builder(photo)
                .setFinishGotoInfo(mRecorderParams.getFinishGotoInfo())
                .setOutputPath(mRecorderParams.getPhotoOutputPath())
                .build();
        intent.putExtra(MediaConstants.KEY_IMAGE_EDIT_PARAMS, editParams);

        startActivityForResult(intent, REQUEST_CODE_FOR_EDIT_IMAGE);
        getActivity().overridePendingTransition(0, 0);
    }

    public void onError(final int what, final int extra) {
        MDLog.e(LogTag.RECORDER.RECORD, "video record error, what: %d, extra: %d", what, extra);
        MomoMainThreadExecutor.post(getTaskTag(), new Runnable() {
            @Override
            public void run() {
                switch (what) {
                    case -302:
                        cancelRecord();
                        Toaster.show("录音失败，请为陌陌开启录音权限");
                        break;
                    default:
                        if (extra == 100) {
                            break;
                        }
                        cancelRecord();
                        Toaster.show("录制失败，请重试");
                        break;
                }
            }
        });
    }

    private void onRecordFinish(String path, boolean valid) {
        Video video = new Video(0, path);
        VideoUtils.initVideo(video);
        if (valid) {
            gotoEditVideo(generateExtra(video));
        } else {
            closeDialog();
        }
    }

    private void gotoEditVideo(final Bundle extra) {
        goingToEdit = true;
        MomoMainThreadExecutor.post(getTaskTag(), new Runnable() {
            @Override
            public void run() {
                closeDialog();
                hideViews();
                clearFragments();
                if (fragmentChangeListener != null) {
                    extra.putString(GOTO_WHERE, VideoEditFragment.class.getSimpleName());
                    fragmentChangeListener.change(VideoRecordFragment.this, extra);
                }
            }
        });
        //        MusicUtils.release();
    }

    private void clearFragments() {
        if (!isAdded()) {
            return;
        }
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        for (int i = 0, l = fragments.length; i < l; i++) {
            Fragment f = fm.findFragmentByTag(getFragmentName(i));
            if (f != null)
                ft.remove(f);
        }
        ft.commitAllowingStateLoss();
        View v = getContentView();
        if (v != null && v instanceof ViewGroup) {
            ((ViewGroup) v).removeView(changeFragmentViewpager);
        }
    }

    private void hideViews() {
        final int state = View.INVISIBLE;
        if (videoHorizontalToolsLayout != null)
            videoHorizontalToolsLayout.setVisibility(state);
        if (videoVerticalToolsLayout != null) {
            videoVerticalToolsLayout.setVisibility(state);
        }
        if (videoAdvancedBtnGotoEdit != null)
            videoAdvancedBtnGotoEdit.setVisibility(state);
        if (btnClose != null)
            btnClose.setVisibility(state);
        if (videoControlLayout != null)
            videoControlLayout.setVisibility(state);
        if (videoRecordSurfaceView != null)
            videoRecordSurfaceView.setVisibility(state);
        if (recordPagerIndicator != null)
            recordPagerIndicator.setVisibility(state);
    }

    private Bundle generateExtra(Video v) {
        Bundle extra = new Bundle();
        v.isChosenFromLocal = false;
        MusicContent playMusic = null;
        if (mPresenter != null) {
            MRConfig mrConfig = mPresenter.getMRConfig();
            if (mrConfig != null) {
                v.rotate = mrConfig.getVideoRotation();
            }
            playMusic = mPresenter.getPlayMusic();
            v.avgBitrate = mPresenter.getAvgBitrate();
        }
        v.playingMusic = playMusic;

        MMRecorderParams.Builder recorderParamsBuilder = new MMRecorderParams.Builder(mRecorderParams);

        recorderParamsBuilder.setInitMusic(playMusic);
        if (mRecorderParams == null || mRecorderParams.isEnableAudioRecorder() && mPresenter.getCurrentMaskModel() != null) {
            v.soundPitchMode = mPresenter.getCurrentMaskModel().getSoundPitchMode();
        }
        // 内置拍摄器拍摄的视频  默认是已经转码视频
        v.hasTranscoding = true;

        recorderParamsBuilder.setGotoTab(state);
        recorderParamsBuilder.setCameraType(mPresenter.isFrontCamera() ? Constants.CameraType.FRONT : Constants.CameraType.BACK);
        recorderParamsBuilder.setInitFaceId(mSelectedFaceID);
        recorderParamsBuilder.setInitFaceClassId(mSelectedFaceClassId);
        recorderParamsBuilder.setSpeedIndex(currentSpeedIndex);

        extra.putBoolean(MediaConstants.KEY_SKIP_SWITCH_FACE, mIsArkit);
        extra.putParcelable(MediaConstants.KEY_CACHE_EXTRA_PARAMS, recorderParamsBuilder.build());
        extra.putParcelable(MediaConstants.KEY_VIDEO_EDIT_PARAMS, new MMVideoEditParams.Builder(v)
                .setFinishGotoInfo(mRecorderParams.getFinishGotoInfo())
                .setOutputPath(mRecorderParams.getVideoOutputPath())
                .build());

        return extra;
    }

    private void onRecordProcess(int progress) {
        if (finishingDialog != null) {
            final String str = "正在处理 " + progress + "%";
            if (Looper.getMainLooper() == Looper.myLooper()) {
                finishingDialog.setMessage(str);
            } else {
                MomoMainThreadExecutor.post(getTaskTag(), new Runnable() {
                    @Override
                    public void run() {
                        finishingDialog.setMessage(str);
                    }
                });
            }
        }
    }

    private void onProcessError(String errMsg) {
        closeDialog();
        Toaster.showInvalidate("录制错误，请确保磁盘空间足够，且打开录制音频权限");
        videoAdvancedProgressView.clear();
        refreshView(false);
    }

    @Override
    public void onMaskModelSet(MaskModel model) {
        if (recordTipManager != null) {
            if (model.getAdditionalInfo() == null) {
                recordTipManager.reset();
            } else {
                recordTipManager.setAdditionalInfo(model.getAdditionalInfo());
            }
        }

    }

    public void faceDetected(boolean hasFace) {
        if (!isFrontCamera()) {
            noFaceTimes = hasFace ? 0 : noFaceTimes + 1;

            float value[] = {0.0f, 0.0f};

            if (noFaceTimes == 5) {
                noFaceSwitch = true;

                if (mPresenter != null) {
                    mPresenter.setItemSelectSkinLevel(value);
//                    mPresenter.focusOnTouch(videoRecordSurfaceView.getWidth() / 2, videoRecordSurfaceView.getHeight() / 2, videoRecordSurfaceView.getWidth(), videoRecordSurfaceView.getHeight(),false);
                }

            }

            if (noFaceTimes > 100) {
                noFaceTimes = 100;
            }

            if (hasFace && noFaceSwitch) {
                noFaceSwitch = false;
                int selectPos = 0;
                value = VideoPanelFaceAndSkinManager.getInstance().getFaceSkinLevel(selectPos, MomentFilterPanelLayout.TYPE_BEAUTY);
                if (mPresenter != null) {
                    mPresenter.setItemSelectSkinLevel(value);
                }
            }
        }

        if (recordTipManager != null) {
            recordTipManager.onFaceDetected(hasFace);
        }

    }

    public void setFragmentChangeListener(FragmentChangeListener fragmentChangeListener) {
        this.fragmentChangeListener = fragmentChangeListener;
    }

    public void playStateChanged(int soundId, boolean play) {
        if (recordTipManager != null)
            recordTipManager.playStateChanged(play);
    }

    private void finishActivity() {
        Activity a = getActivity();
        if (a != null) {
            a.setResult(Activity.RESULT_CANCELED);
            a.finish();
        }
    }

    private boolean checkActivityStatus() {
        Activity a = getActivity();
        return a != null && !a.isFinishing();
    }

    private long getMaxDuration() {
        return mRecorderParams == null ? MediaConstants.MAX_VIDEO_DURATION : mRecorderParams.getMaxDuration();
/*        switch (state) {
            case Constants.RecordTab.VIDEO:
                return MediaConstants.MAX_VIDEO_DURATION + MediaConstants.MOMENT_DURATION_EXPAND;
            case Constants.RecordTab.PHOTO:
                return MediaConstants.DEFUALT_RECORD_DURATION + MediaConstants.MOMENT_DURATION_EXPAND;
        }
        return 0;*/
    }

    @Override
    public long getRecordDuration() {
        switch (state) {
            case Constants.RecordTab.VIDEO:
                return videoAdvancedProgressView.getRecordDuration();
            case Constants.RecordTab.PHOTO:
                return (long) (defaultProgress * getMaxDuration());
        }
        return 0;
    }

    private long checkLeftDuration() {
        final long maxDuration = getMaxDuration();
        //已经录制的时长
        long recordedDuration = getRecordDuration();
        long leftDuration = maxDuration - recordedDuration;

        if (leftDuration <= 0) {
            Toaster.showInvalidate("已经录制完成");
            videoAdvancedRecordBtn.reset(false);
            return 0;
        }
        return leftDuration;
    }

    private void checkDelayAndStartRecord() {
        final long leftDuration = checkLeftDuration();
        if (leftDuration <= 0)
            return;
        delayTime = chooseDelayTime;
        if (chooseDelayTime > DELAY_OFF) {
            if (delayStartRecordTask != null) {
                cancelDelayTask();
                return;
            }
            cancelDelayBtn.setVisibility(View.VISIBLE);
            videoAdvancedBtnDelete.setVisibility(View.INVISIBLE);
            showVideoToolsLayout(false);
            recordPagerIndicator.setVisibility(View.GONE);
            btnClose.setEnabled(false);
            videoAdvancedBtnDelay.setEnabled(false);
            delayTextTask = new ShowDelayTimeTask();
            delayTextTask.run();
            delayStartRecordTask = new DelayStartRecord();
            MomoMainThreadExecutor.postDelayed(getTaskTag(), delayStartRecordTask, chooseDelayTime * 1000);
            AnimUtils.Default.hideToTop(btnClose, true, 300);
            if (videoAdvancedBtnGotoEdit.getVisibility() == View.VISIBLE)
                AnimUtils.Default.hideToTop(videoAdvancedBtnGotoEdit, false, 300);
            videoAdvancedRecordBtn.setTouchBack(true);
        } else {
            startAnimToStartRecord(false);
        }
    }

    private void cancelDelayTask() {
        recordPagerIndicator.setVisibility(View.VISIBLE);
        videoAdvancedRecordBtn.setTouchBack(!videoAdvancedRecordBtn.canLongPress());
        changeFragmentViewpager.setEnabled(true);
        cancelDelayBtn.setVisibility(View.GONE);
        showVideoToolsLayout(true);
        //        showAdvancedToolsLayout(300);
        AnimUtils.Default.showFromTop(btnClose, 300);
        if (videoAdvancedBtnGotoEdit.getVisibility() == View.INVISIBLE)
            AnimUtils.Default.showFromTop(videoAdvancedBtnGotoEdit, 300);
        videoAdvancedBtnDelay.setEnabled(true);
        btnClose.setEnabled(true);
        if (delayTextTask != null) {
            delayTextTask.cancel = true;
            MomoMainThreadExecutor.cancelSpecificRunnable(getTaskTag(), delayTextTask);
            delayTextTask = null;
        }
        if (delayStartRecordTask != null)
            MomoMainThreadExecutor.cancelSpecificRunnable(getTaskTag(), delayStartRecordTask);
        delayStartRecordTask = null;
        if (delayStartShootingTask != null)
            MomoMainThreadExecutor.cancelSpecificRunnable(getTaskTag(), delayStartShootingTask);
        delayStartShootingTask = null;
    }

    private Object getTaskTag() {
        return "VideoRecordFragment";
    }

    private void startRecording(boolean advanced) {
        if (initMusic != null && TextUtils.isEmpty(initMusic.path)) {
            Toaster.show("音乐缓冲中,请稍候！");
            return;
        }
        if (mPresenter != null) {
            mPresenter.setSpeed(currentSpeed);
            mPresenter.startRecording();
            if (advanced) {
                videoAdvancedProgressView.setMaxDuration(getMaxDuration());
                videoAdvancedProgressView.startRecord(currentSpeed);
            } else {
                videoRecordControllerLayout.showRecordingFace(true);
                videoDefaultRecordBtn.setProgress(1, getMaxDuration());
                videoDefaultRecordBtn.small2Big();
                videoDefaultRecordBtn.requestLayout();
            }
            refreshView(true);
        }
    }

    private boolean isRecording;

    @Override
    public void onRecordStarted() {
        isRecording = true;
    }

    @Override
    public void onRecordPaused() {
        isRecording = false;
    }

    private void stopRecording(boolean advanced) {
        stopRecording(false, advanced);
    }

    private void stopRecording(boolean cancel, boolean advanced) {
        recordCancelTip.setVisibility(View.INVISIBLE);
        if (mPresenter != null) {
            if (advanced) {
                videoAdvancedProgressView.stopRecord();
            } else {
                videoRecordControllerLayout.showRecordingFace(false);
            }
            mPresenter.stopRecording();
            refreshView(false);
            resetRecordButton(true);
            if (!advanced) {
                boolean result = mPresenter.finishRecord(onRecordFinishedListener);
                if (result) {
                    onStartFinish();
                }
            }

        }
    }

    private boolean canRecordVideo(boolean toast) {
        return true;
    }

    //    private boolean checkRecommendFaceReady() {
    //        return videoRecordControllerLayout != null &&
    //                videoRecordControllerLayout.checkFaceReady();
    //    }

    private class DefaultCallback extends BaseCallback implements VideoDefaultRecordButton.Callback {
        private boolean delayRecordMode;

        @Override
        public void onLongPressed() {
        }

        @Override
        public void onDragIn() {
            if (!delayRecordMode) {
                super.onDragIn();
            }
        }

        @Override
        public void onDragOut() {
            if (!delayRecordMode) {
                super.onDragOut();
            }
        }

        @Override
        public void onCancel() {
            delayRecordMode = false;
            super.onCancel();
            showRecordTip();
        }

        @Override
        public void onClick() {
            super.onClick();
        }

        @Override
        public void onRecordAnimEnd() {
            delayRecordMode = false;
            if (!canRecordVideo(false)) {
                return;
            }
            startRecording(false);
        }

        @Override
        public void onProgress(float progress) {
            defaultProgress = progress;
        }

        @Override
        public void onProgressEnd() {
            delayRecordMode = false;
            if (!canRecordVideo(false)) {
                return;
            }
            if (mPresenter != null && mPresenter.isRecording()) {
                stopRecording(false);
            }
        }

        @Override
        protected boolean isDefault() {
            return true;
        }
    }

    private class AdvancedCallback extends BaseCallback implements VideoAdvancedRecordButton.Callback {

        @Override
        protected boolean isDefault() {
            return false;
        }

        @Override
        public void onSwitchAnimEnd() {
            videoAdvancedRecordBtn.setVisibility(View.INVISIBLE);
            videoRecordControllerLayout.setVisibility(View.VISIBLE);
            //            videoRecordControllerLayout.refresh();
        }

        @Override
        public void onStartRecording() {
            XE3DEngine.getInstance().queueEvent(new Runnable() {
                @Override
                public void run() {
                    XE3DEngine.getInstance().enableRecording(true);
                }
            });
            startRecording(true);
        }

        @Override
        public void onStopRecording() {
            XE3DEngine.getInstance().queueEvent(new Runnable() {
                @Override
                public void run() {
                    XE3DEngine.getInstance().enableRecording(true);
                }
            });
            if (!canRecordVideo(false)) {
                return;
            }
            if (mPresenter != null && mPresenter.isRecording()) {
                stopRecording(true);
            }

        }
    }

    private abstract class BaseCallback implements RecordButtonTouchEventHelper.Callback {

        protected abstract boolean isDefault();

        @Override
        public void onLongPressed() {
            if (!canRecordVideo(true)) {
                return;
            }
            draggingToCancel = false;
            //            if (isDefault()) {
            //                cancelShowRecordTip = true;
            //                recordCancelTip.setVisibility(View.INVISIBLE);
            //                videoDefaultRecordBtn.startAnimToRecord();
            //            } else {
            recordCancelTip.setVisibility(View.INVISIBLE);
            checkDelayAndStartRecord();
            //            }
        }

        @Override
        public void onDragIn() {
            if (!canRecordVideo(false)) {
                return;
            }
            if (mPresenter != null && mPresenter.isRecording()) {
                draggingToCancel = false;
                recordCancelTip.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onDragOut() {
            if (!canRecordVideo(false)) {
                return;
            }
            if (mPresenter != null && mPresenter.isRecording()) {
                draggingToCancel = true;
                recordCancelTip.setBackgroundDrawable(null);
                recordCancelTip.setText(R.string.moment_drag_cancel_tip);
                recordCancelTip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onCancel() {
            if (!canRecordVideo(false)) {
                return;
            }
            cancelRecord();
            recordCancelTip.setVisibility(View.INVISIBLE);
        }

        private void checkDelayAndStartShooting() {
            final long leftDuration = checkLeftDuration();
            if (leftDuration <= 0)
                return;
            delayTime = chooseDelayTime;
            if (chooseDelayTime > DELAY_OFF) {
                if (delayStartShootingTask != null) {
                    cancelDelayTask();
                    return;
                }
                cancelDelayBtn.setVisibility(View.VISIBLE);
                videoAdvancedBtnDelete.setVisibility(View.INVISIBLE);
                showVideoToolsLayout(false);
                recordPagerIndicator.setVisibility(View.GONE);
                btnClose.setEnabled(false);
                videoAdvancedBtnDelay.setEnabled(false);
                delayTextTask = new ShowDelayTimeTask();
                delayTextTask.run();
                delayStartShootingTask = new DelayStartShooting();
                MomoMainThreadExecutor.postDelayed(getTaskTag(), delayStartShootingTask, chooseDelayTime * 1000);
                AnimUtils.Default.hideToTop(btnClose, true, 300);
                if (videoAdvancedBtnGotoEdit.getVisibility() == View.VISIBLE)
                    AnimUtils.Default.hideToTop(videoAdvancedBtnGotoEdit, false, 300);
                videoAdvancedRecordBtn.setTouchBack(true);
            } else {
                videoDefaultRecordBtn.setEnabled(false);
                mPresenter.takePhoto();
            }
        }

        @Override
        public void onClick() {
            changeFragmentViewpager.setEnabled(false);
            recordPagerIndicator.setEnabled(false);
            if (mPresenter != null) {
                if (isDefault()) {
                    /*if (transBean != null && transBean.mediaType == AlbumConstant.MEDIA_TYPE_VIDEO) {
                        changeFragmentViewpager.setEnabled(true);
                        recordPagerIndicator.setEnabled(true);
                        if (!TextUtils.isEmpty(transBean.alertToast))
                            Toaster.show(transBean.alertToast);
                        return;
                    }*/
                    recordCancelTip.setVisibility(View.INVISIBLE);
                    checkDelayAndStartShooting();
//                        mPresenter.takePhoto();
                } else {
                    if (mPresenter.isRecording()) {
                        stopRecording(true);
                    } else {
                        draggingToCancel = false;
                        checkDelayAndStartRecord();
                    }
                }
            }
        }
    }

    private class DelayStartRecord implements Runnable {
        DelayStartRecord() {
        }

        @Override
        public void run() {
            delayStartRecordTask = null;
            startAnimToStartRecord(true);
        }
    }

    private void startAnimToStartRecord(boolean showToolBar) {
        if (state == Constants.RecordTab.PHOTO) {
            videoDefaultRecordBtn.startAnimToRecord();
        } else {
            videoAdvancedRecordBtn.startAnimToRecord();
        }
        if (showToolBar) {
            btnClose.setVisibility(View.VISIBLE);
        }
    }

    private void cancelRecord() {
        if (videoAdvancedProgressView != null)
            videoAdvancedProgressView.stopRecord();
        if (mPresenter != null) {
            mPresenter.cancelRecording();
            removeLast();
            refreshView(false);
            resetRecordButton(true);
        }
        if (videoRecordControllerLayout != null) {
            videoRecordControllerLayout.showRecordingFace(false);
        }
    }

    private class ShowDelayTimeTask implements Runnable {
        static final long ANIM_TIME = 300;
        boolean cancel = false;

        @Override
        public void run() {
            if (cancel)
                return;
            if (delayTime <= 0) {
                delayTextTask = null;
                delayText.setVisibility(View.INVISIBLE);
                return;
            }
            delayText.setText("" + delayTime);
            delayTime--;
            playDelayTextAnim(ANIM_TIME);
            if (cancel)
                return;
            MomoMainThreadExecutor.postDelayed(getTaskTag(), this, 1000);
        }
    }

    private void playDelayTextAnim(long duration) {
        delayText.setVisibility(View.VISIBLE);
        delayText.startAnimation(
                AnimUtils.Animations.setListener(
                        AnimUtils.Animations.playTogether(
                                AnimUtils.Animations.newFadeInThenFadeOutAnimation(duration, duration),
                                AnimUtils.Animations.newZoomInAnimation(duration)
                        ),
                        AnimUtils.Animations.newGoneListener(delayText)
                ));
    }

    @Override
    public void onFilterTabSelect(int selectPosition) {
        if (mPresenter == null)
            return;
        mPresenter.changeToFilter(selectPosition, false, 0);
        setCurrentFilterIndex(selectPosition);
    }

    @Override
    public void onBeautyTabSelect(int selectPosition, int type) {
        float[] value = new float[2];
        switch (type) {
            case MomentFilterPanelLayout.TYPE_BEAUTY:   // 美肤 美白
                value[0] = Configs.DOKI_BEAUTY[selectPosition];
                value[1] = Configs.DOKI_BEAUTY[selectPosition];
                mPresenter.setItemSelectSkinLevel(value);

                mCurFilterBeautyPos = selectPosition;
                break;
            case MomentFilterPanelLayout.TYPE_EYE_AND_THIN: //  大眼 瘦脸
                value[0] = Configs.DOKI_BIG_EYE[selectPosition];
                value[1] = Configs.DOKI_THIN_FACE[selectPosition];
                mPresenter.setFaceEyeScale(value[0]);
                mPresenter.setFaceThinScale(value[1]);
                mCurFilterEyeThinPos = selectPosition;
                break;
            case MomentFilterPanelLayout.TYPE_SLIMMING:    // 瘦身
                mPresenter.setSlimmingScale(VideoPanelFaceAndSkinManager.getInstance().getSlimmingAndLongLegsLevel(selectPosition, type));
                mCurFilterSlimmingPos = selectPosition;
                break;
            case MomentSkinAndFacePanelLayout.TYPE_LONG_LEGS:  // 长腿
                mPresenter.setLongLegScale(VideoPanelFaceAndSkinManager.getInstance().getSlimmingAndLongLegsLevel(selectPosition, type));
                mCurFilterLongLegPos = selectPosition;
                break;
            default:
                break;
        }
    }

    @Override
    public void onBeautyMoreChanged(float[] value, int type) {
        switch (type) {
            case MomentFilterPanelLayout.TYPE_BEAUTY:   // 美肤 美白
                mPresenter.setItemSelectSkinLevel(value);
                break;
            case MomentFilterPanelLayout.TYPE_EYE_AND_THIN: //  大眼 瘦脸
                //                value[0] = Configs.DOKI_BIG_EYE[selectPosition];
                //                value[1] = Configs.DOKI_THIN_FACE[selectPosition];
                mPresenter.setFaceEyeScale(value[0]);
                mPresenter.setFaceThinScale(value[1]);
                break;
            default:
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void release() {
        if (mPresenter != null) {
            mPresenter.clearTempFiles();
        }
    }


    private MRecorderActions.OnRecordFinishedListener onRecordFinishedListener = new MRecorderActions.OnRecordFinishedListener() {
        @Override
        public void onFinishingProgress(int progress) {
            onRecordProcess(progress);
        }

        @Override
        public void onRecordFinished() {
            if (getActivity() != null && !getActivity().isFinishing()) {
                File videoFile = new File(restoreVideoPath);
                final boolean invalid = !videoFile.exists() || videoFile.length() <= 0;
                onRecordFinish(restoreVideoPath, !invalid);

                if (invalid) {
                    Toaster.showInvalidate("视频录制错误，请重试");
                    return;
                }
            }
        }

        @Override
        public void onFinishError(final String errMsg) {
            MomoMainThreadExecutor.post(new Runnable() {
                @Override
                public void run() {
                    mPresenter.cancelRecording();
                    removeLast();
                    refreshView(false);
                    resetRecordButton(true);
                    onProcessError(errMsg);
                }
            });
        }
    };

    private class DelayStartShooting implements Runnable {
        DelayStartShooting() {
        }

        @Override
        public void run() {
            videoDefaultRecordBtn.setEnabled(false);
            cancelDelayBtn.setVisibility(View.GONE);
            delayStartShootingTask = null;
            mPresenter.takePhoto();
        }

    }
}
