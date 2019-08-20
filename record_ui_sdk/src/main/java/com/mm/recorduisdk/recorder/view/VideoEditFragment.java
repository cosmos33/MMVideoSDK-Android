package com.mm.recorduisdk.recorder.view;

import android.animation.Animator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cosmos.mdlog.MDLog;
import com.immomo.moment.mediautils.VideoDataRetrieverBySoft;
import com.immomo.moment.mediautils.cmds.EffectModel;
import com.immomo.moment.mediautils.cmds.TimeRangeScale;
import com.mm.base_business.base.BaseFragment;
import com.mm.base_business.glide.ImageLoaderX;
import com.mm.base_business.utils.DeviceUtils;
import com.mm.mediasdk.utils.ImageUtil;
import com.mm.mediasdk.utils.UIUtils;
import com.mm.mmutil.NetUtils;
import com.mm.mmutil.task.MomoMainThreadExecutor;
import com.mm.mmutil.task.MomoTaskExecutor;
import com.mm.mmutil.task.ThreadUtils;
import com.mm.mmutil.toast.Toaster;
import com.mm.recorduisdk.R;
import com.mm.recorduisdk.bean.FinishGotoInfo;
import com.mm.recorduisdk.bean.MMChooseMediaParams;
import com.mm.recorduisdk.bean.MMVideoEditParams;
import com.mm.recorduisdk.bean.VideoRecordDefs;
import com.mm.recorduisdk.config.Configs;
import com.mm.recorduisdk.log.LogTag;
import com.mm.recorduisdk.recorder.MediaConstants;
import com.mm.recorduisdk.recorder.activity.SelectMomentCoverActivity;
import com.mm.recorduisdk.recorder.activity.VideoCutActivity;
import com.mm.recorduisdk.recorder.activity.VideoSpeedAdjustActivity;
import com.mm.recorduisdk.recorder.adapter.DynamicStickerListAdapter;
import com.mm.recorduisdk.recorder.editor.EditRecorder;
import com.mm.recorduisdk.recorder.editor.player.IProcessPlayerView;
import com.mm.recorduisdk.recorder.editor.player.IProcessPresenter;
import com.mm.recorduisdk.recorder.editor.player.OnVolumeChangeListener;
import com.mm.recorduisdk.recorder.editor.player.VideoEditPresenter;
import com.mm.recorduisdk.recorder.listener.FilterSelectListener;
import com.mm.recorduisdk.recorder.listener.FragmentChangeListener;
import com.mm.recorduisdk.recorder.listener.OnFilterDensityChangeListener;
import com.mm.recorduisdk.recorder.listener.StickerEditListener;
import com.mm.recorduisdk.recorder.model.MusicContent;
import com.mm.recorduisdk.recorder.model.Video;
import com.mm.recorduisdk.recorder.musicpanel.edit.MusicPanelHelper;
import com.mm.recorduisdk.recorder.specialfilter.ISpecialDataControl;
import com.mm.recorduisdk.recorder.specialfilter.SpecialDataControl;
import com.mm.recorduisdk.recorder.specialfilter.view.SpecialPanelViewHelper;
import com.mm.recorduisdk.recorder.sticker.DynamicSticker;
import com.mm.recorduisdk.recorder.sticker.StickerEntity;
import com.mm.recorduisdk.recorder.sticker.StickerManager;
import com.mm.recorduisdk.utils.AlbumNotifyHelper;
import com.mm.recorduisdk.utils.AnimUtils;
import com.mm.recorduisdk.utils.KeyBoardUtil;
import com.mm.recorduisdk.utils.OnAnimationEndListener;
import com.mm.recorduisdk.utils.VideoCompressUtil;
import com.mm.recorduisdk.utils.VideoUtils;
import com.mm.recorduisdk.widget.DynamicStickerPanel;
import com.mm.recorduisdk.widget.DynamicStickerPanelContainerHelper;
import com.mm.recorduisdk.widget.IndeterminateDrawable;
import com.mm.recorduisdk.widget.MomentEdittextPannel;
import com.mm.recorduisdk.widget.MomentFilterPanelLayout;
import com.mm.recorduisdk.widget.MomentFilterPanelTabLayout;
import com.mm.recorduisdk.widget.paint.PaintPanelView;
import com.mm.recorduisdk.widget.progress.CircleProgressView;
import com.mm.recorduisdk.widget.sticker.StickerContainerView;
import com.mm.recorduisdk.widget.sticker.StickerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import project.android.imageprocessing.filter.BasicFilter;

import static com.mm.recorduisdk.recorder.activity.VideoRecordAndEditActivity.BACK_TO_OLD;
import static com.mm.recorduisdk.recorder.activity.VideoRecordAndEditActivity.GOTO_WHERE;

/**
 * Created by XiongFangyu on 2017/8/9.
 */
public class VideoEditFragment extends BaseFragment
        implements View.OnClickListener, IProcessPlayerView, VideoCompressUtil.CompressVideoListener, AlbumNotifyHelper.OnSaveVideoListener {
    private static final String TAG = "VideoEditFragment";
    private static final int SPEED_ADJUST_CODE = 0X1245;
    private static final int REQUEST_CODE_SELECT_COVER = 0x1246;
    private static final int REQUEST_CODE_CUT_VIDEO = 0X1247;

    private static final String CHOSEN_MUSIC_IN_RECORD = "CHOSEN_MUSIC_IN_RECORD";
    private static final String KEY_OLD_PATH = "KEY_OLD_PATH";
    private static final String KEY_OLD_LENGTH = "KEY_OLD_LENGTH";

    private View allStickerContainer;
    StickerContainerView stickerContainerView;
    ImageView addDrawBgImage;

    private StickerView editingStickerView;

    View progressContent;
    private TextView sendText, titleView;
    private View musicProgress;
    CircleProgressView progressView;

    ViewGroup.MarginLayoutParams imageParams;

    private View closeBtn;
    private View addStickerLayout, musicLayout;
    private ImageView deleteStickerView;
    //涂鸦view
    private PaintPanelView paintPanel;
    private TextView sendBtn;
    //所有贴纸
    private DynamicStickerPanel stickerPanel;
    //编辑文字贴纸
    private MomentEdittextPannel edittextPannel;

    private View filterLayout, filterBg;

    private View toolsLayout;

    private View textLayout;
    private View coverLayout;
    private View paintLayout;
    private View speedLayout;
    /**
     * 文字贴纸view
     */
    private ArrayList<StickerView> textStickerView;

    //选择的封面路径
    private String selectedCoverPath;
    //选择的封面位置
    private int selectedCoverPos = 0;

    private IndeterminateDrawable progressDrawable;
    private MusicContent chooseMusicInRecord;
    private TextureView videoView;
    private View videoRootView;

    private Video video;
    private int textPosY = 0;
    private boolean musicFromAuto;
    private int stickerMarginLeft = 0;
    private int stickerMarginTop = 0;
    private int stickerWidth = 0;
    private int stickerHeight = 0;
    private boolean isProcessing;
    private long maxPublishDuration;
    private String oldPath;
    private long oldLength;

    private boolean mIsDialogBtnClicked = false;

    private IProcessPresenter processPresenter;
    private List<Bitmap> frames;
    private EditRecorder editRecorder;

    private FragmentChangeListener fragmentChangeListener;

    private View specialViewBtn;
    private SpecialPanelViewHelper specialPanelViewHelper;
    private ISpecialDataControl specialDataControl;

    private Animator.AnimatorListener animatorListener;
    private ProgressDialog progressDialog;
    private MusicPanelHelper musicPanelHelper;
    private int mCurrentFilterSelectPosition;
    private View mStickerPanelContentView;
    private DynamicStickerPanelContainerHelper mDynamicStickerPanelContainerHelper;
    private MMVideoEditParams mVideoEditParams;

    public void setFragmentChangeListener(FragmentChangeListener fragmentChangeListener) {
        this.fragmentChangeListener = fragmentChangeListener;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_moment_edit;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getActivity() != null && getActivity().getWindow() != null) {
            getActivity().getWindow().setFormat(PixelFormat.TRANSLUCENT);
            getActivity().getWindow().setBackgroundDrawableResource(R.drawable.ic_moment_theme_bg);
        }
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        AlbumNotifyHelper.getAblumNotifyHelper().setOnSaveVideoListener(this);
        if (bundle != null) {
            mVideoEditParams = bundle.getParcelable(MediaConstants.KEY_VIDEO_EDIT_PARAMS);
            Video v = mVideoEditParams.getVideo();
            video = v;
            if (v != null) {
                chooseMusicInRecord = v.playingMusic;//记录下录制界面的音乐
                initVideoParam();
                initEditUploadParam(video);
                getCoverByVideo();
                if (chooseMusicInRecord != null) {
                    v.osPercent = 0;
                    v.psPercent = 100;
                } else {
                    v.osPercent = 100;
                    v.psPercent = 0;
                }
                musicFromAuto = v.playingMusic != null;
            }
            File file = null;
            if (v != null)
                file = new File(v.path);
            if (file == null || !file.exists() || file.length() <= 0) {
                Toaster.showInvalidate("视频录制错误，请重试");
                finishActivity();
                return;
            }
            v.size = (int) file.length();
            initEditUploadParam(v);
        }
        editRecorder = new EditRecorder();
        processPresenter = new VideoEditPresenter(this, mVideoEditParams);

        videoView = findViewById(R.id.video_process_content);
        if (processPresenter != null) {
            videoView.setSurfaceTextureListener(processPresenter);
        }
    }

    private void initEditUploadParam(@NonNull Video video) {
    }

    private void getCoverByVideo() {
        com.mm.mediasdk.utils.VideoUtils.generateThumbnail(video.path, video.rotate, new File(Configs.getDir("record"), System.currentTimeMillis() + ".jpg").getAbsolutePath(), new com.mm.mediasdk.utils.VideoUtils.ThumbnailCallback() {
            @Override
            public void onSaveThumb(File file) {
                if (file != null && file.exists() && file.length() > 0) {
                    selectedCoverPath = file.getAbsolutePath();
                }
            }
        });
    }

    @Override
    protected void initViews(View contentView) {
        initNormal();
        videoRootView = findViewById(R.id.video_cover_and_process);

        closeBtn = findViewById(R.id.moment_edit_btn_close);
        addDrawBgImage = findViewById(R.id.moment_edit_draw_bg);
        allStickerContainer = findViewById(R.id.moment_edit_all_sticker_container);
        stickerContainerView = findViewById(R.id.moment_edit_sticker_container);
        //绑定删除按钮
        deleteStickerView = findViewById(R.id.moment_edit_delete_sticker);
        stickerContainerView.deleteBtn = deleteStickerView;

        progressContent = findViewById(R.id.moment_edit_progress_layout);
        progressView = findViewById(R.id.moment_edit_progressview);
        sendText = findViewById(R.id.moment_edit_send_text);
        titleView = findViewById(R.id.moment_edit_title);

        progressDrawable = new IndeterminateDrawable(Color.WHITE, UIUtils.getPixels(3));
        musicProgress = findViewById(R.id.moment_edit_music_progressview);
        musicProgress.setBackgroundDrawable(progressDrawable);
        toolsLayout = findViewById(R.id.moment_edit_tools_layout);
        paintLayout = findViewById(R.id.moment_edit_paint_layout);
        speedLayout = findViewById(R.id.moment_edit_change_speed_layout);
        specialViewBtn = findViewById(R.id.special_fiter_layout);

        sendBtn = findViewById(R.id.moment_edit_btn_send);
        if (!showSticker()) {
            addStickerLayout.setVisibility(View.GONE);
        } else {
            addStickerLayout.setVisibility(View.VISIBLE);
        }
        initListener();
        initParams();

        coverLayout.setActivated(false);

        initToolsLayout();

        musicLayout.setActivated(chooseMusicInRecord != null);
        titleView.setText(UIUtils.formatTime((int) video.length));
    }

    private void initNormal() {
        toolsLayout = findViewById(R.id.moment_edit_tools_layout);
        addStickerLayout = toolsLayout.findViewById(R.id.moment_edit_add_sticker_layout);
        textLayout = toolsLayout.findViewById(R.id.moment_edit_text_layout);
        musicLayout = toolsLayout.findViewById(R.id.moment_edit_music_layout);
        coverLayout = toolsLayout.findViewById(R.id.moment_edit_select_cover_layout);
        filterBg = findViewById(R.id.moment_edit_filter_cover);
        filterLayout = findViewById(R.id.moment_edit_filter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (video == null || TextUtils.isEmpty(video.path) || !new File(video.path).exists()) {
            Toaster.showInvalidate("视频文件错误，请重新录制");
            finishActivity();
            return;
        }
        if (processPresenter != null) {
            processPresenter.playVideo();
        }
        if (null != musicPanelHelper) {
            musicPanelHelper.onResume();
        }
    }

    @Override
    public void onPause() {
        MDLog.i(LogTag.PROCESSOR.PROCESS, "onPause");
        UIUtils.hideInputMethod(getActivity());
        if (processPresenter != null) {
            processPresenter.pause();
        }
        super.onPause();
        if (null != musicPanelHelper) {
            musicPanelHelper.onPause();
        }
    }

    @Override
    public void onDestroy() {
        MomoTaskExecutor.cancleAllTasksByTag(getTaskTag());
        AlbumNotifyHelper.getAblumNotifyHelper().onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (edittextPannel != null)
            edittextPannel.release();
        if (specialPanelViewHelper != null) {
            specialPanelViewHelper.destory();
        }
        if (musicPanelHelper != null) {
            musicPanelHelper.onDestory();
        }
        MomoMainThreadExecutor.cancelAllRunnables(getTaskTag());
        if (textStickerView != null) {
            textStickerView.clear();
            textStickerView = null;
        }
        fragmentChangeListener = null;

        super.onDestroy();
    }

    @Override
    public boolean onBackPressed() {
        if (null != filterPanel && filterPanel.getVisibility() == View.VISIBLE) {
            hideFilterPanel();
        }
        if (specialPanelViewHelper != null && specialPanelViewHelper.isInFilterMode()) {
            if (!specialPanelViewHelper.onBackPressed()) {
                hideSpecialFilterPanel();
            }
            return true;
        }
        if (musicPanelHelper != null && musicPanelHelper.onBackPress()) {
            return true;
        }
        if (paintPanel != null && paintPanel.getVisibility() == View.VISIBLE) {
            hidePaintPanel();
            return true;
        }
        if (edittextPannel != null && edittextPannel.getVisibility() == View.VISIBLE) {
            edittextPannel.onBackPressed();
            return true;
        }
        if (mDynamicStickerPanelContainerHelper != null && mDynamicStickerPanelContainerHelper.isShowing()) {
            hideStickerPanel();
            return true;
        }
        if (processPresenter != null && progressContent.getVisibility() == View.VISIBLE) {
            processPresenter.pause();
            showCancelProcessDialog();
            return true;
        }

        if (isFromAlbum()) {
            if (isEdited()) {
                showCloseDialog();
            } else {
                cancelVideo();
            }
        } else {
            showCloseDialog();
        }

        return true;
    }

    private void showCloseDialog() {
        if (this.getActivity() == null) {
            return;
        }
        String contentId = "要放弃该视频吗？";
        String rightStr = "确认";
        AlertDialog mCloseDialog = new AlertDialog.Builder(this.getActivity())
                .setTitle("提示")
                .setMessage(contentId).setPositiveButton(rightStr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelVideo();
                    }
                }).setNegativeButton("取消", null)
                .create();
        showDialog(mCloseDialog);
    }

    private boolean isEdited() {
        return selectedCoverPos != 0 || editRecorder.isChangeVideo() || isUsedSpecialFilter();
    }

    private boolean isVideoEdited() {
        return selectedCoverPos != 0 || editRecorder.isOnlyChangeVideo() || isUsedSpecialFilter() || isAudioPitch();
    }

    private boolean isAudioPitch() {
        return video != null && video.soundPitchMode != 0 && VideoUtils.isSupportSoundPitch();
    }

    private boolean isUsedSpecialFilter() {
        return specialPanelViewHelper != null && specialPanelViewHelper.useSpecialFilter();
    }

    private boolean isFromAlbum() {
        return getArguments() != null && getArguments().getParcelable(MediaConstants.KEY_CACHE_EXTRA_PARAMS) != null && getArguments().getParcelable(MediaConstants.KEY_CACHE_EXTRA_PARAMS) instanceof MMChooseMediaParams;
    }


    private int getVirtualBarHeight() {
        return 0;
    }

    private void initToolsLayout() {
        toolsLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                toolsLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                //                final int mw = moreButton.getWidth();
                //                final int mtw = UIUtils.getDimensionPixelSize(R.dimen.moment_edit_more_tools_layout_width);
                //                final int margin = (mw - mtw) >> 1;
                //                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) moreToolLayout.getLayoutParams();
                //                params.rightMargin = margin;
                //                moreToolLayout.setLayoutParams(params);
            }
        });
        final int vh = getVirtualBarHeight();
        if (vh > 0) {
            toolsLayout.setPadding(toolsLayout.getPaddingLeft(),
                    toolsLayout.getPaddingTop(),
                    toolsLayout.getPaddingRight(),
                    toolsLayout.getPaddingBottom() + vh);
            toolsLayout.getLayoutParams().height += vh;
            toolsLayout.requestLayout();
            deleteStickerView.setPadding(deleteStickerView.getPaddingLeft(),
                    deleteStickerView.getPaddingTop(),
                    deleteStickerView.getPaddingRight(),
                    deleteStickerView.getPaddingBottom() + vh);
            deleteStickerView.getLayoutParams().height += vh;
            deleteStickerView.requestLayout();
        }
    }

    private void initVideoParam() {
        VideoUtils.getVideoFixMetaInfo(video);
        int width = video.getWidth();
        int height = video.height;
        int bgWidth = UIUtils.getScreenWidth();
        int bgHeight = UIUtils.getScreenHeight();
        textPosY = bgHeight >> 1;
        if (width / (float) height >= bgWidth / (float) bgHeight) {
            stickerWidth = bgWidth;
            float scale = bgWidth / (float) width;
            stickerHeight = (int) (height * scale);
        } else {
            stickerHeight = bgHeight;
            float scale = bgHeight / (float) height;
            stickerWidth = (int) (width * scale);
        }
        stickerMarginTop = (bgHeight - stickerHeight) / 2;
        stickerMarginLeft = (bgWidth - stickerWidth) / 2;
    }

    private void initParams() {
        imageParams = new ViewGroup.MarginLayoutParams(stickerWidth, stickerHeight);
        imageParams.setMargins(stickerMarginLeft, stickerMarginTop, 0, 0);
        videoRootView.setLayoutParams(new RelativeLayout.LayoutParams(imageParams));
        stickerContainerView.setParams(stickerWidth, stickerHeight, 0, 0);
        stickerContainerView.showRect = new Rect(0, 0, stickerWidth, stickerHeight);
    }

    private boolean showSticker() {
        //等于0 表示不使用动态贴纸，其它情况都要使用
        return true;
    }

    private void initListener() {

        addStickerLayout.setOnClickListener(this);
        textLayout.setOnClickListener(this);
        musicLayout.setOnClickListener(this);

        videoRootView.setOnClickListener(this);
        sendBtn.setOnClickListener(this);
        showSendButton(true);

        closeBtn.setOnClickListener(this);
        coverLayout.setOnClickListener(this);
        filterBg.setOnClickListener(this);
        filterLayout.setOnClickListener(this);
        speedLayout.setOnClickListener(this);
        paintLayout.setOnClickListener(this);
        specialViewBtn.setOnClickListener(this);

        //动态贴纸，超出屏幕范围不显示toast
        stickerContainerView.isDynamicSticker = true;
        stickerContainerView.stickerEditListener = new StickerEditListener(toolsLayout) {
            @Override
            public void onTouch() {
            }

            @Override
            public void beginEdit() {
                super.beginEdit();
            }

            @Override
            public void onDeleteSticker(StickerView stickerView) {
                if (stickerView.isText()) {
                    unRecordTextSticker(stickerView);
                } else
                    unRecordSticker(stickerView);
                boolean has = (textStickerView != null && !textStickerView.isEmpty());
                if (has) {
                    return;
                }
                has = (processPresenter != null && processPresenter.getDynamicStickerCount() > 0);
                if (!has)
                    editRecorder.setChangeSticker(false);
            }

            @Override
            public void onStickerClick(StickerView view) {
                stickerContainerView.clearAllShowClickEditState();
                if (view.isText()) {
                    editingStickerView = view;
                    String text = view.getText();
                    int index = view.getChosenTextColorIndex();
                    showEdittextPannel(text, index);
                } else if (view.getType() == StickerView.TYPE_IMG) {
                    showStickerPanel();
                    mDynamicStickerPanelContainerHelper.updateCurrentEditSticker(view.getStickerEntity(), false);
                    view.setShowEditBorder(true);
                }
                stickerContainerView.endEditIndeed();
            }
        };
    }

    private void showSendButton(boolean isshow) {
        if (isshow) {
            sendBtn.setVisibility(View.VISIBLE);
        } else {
            sendBtn.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 删除贴纸后，取消纪录
     */
    private void unRecordSticker(StickerView view) {
        if (processPresenter != null) {
            processPresenter.removeMaskModel((int) view.getStickerId());
        }
        if (mDynamicStickerPanelContainerHelper != null && mDynamicStickerPanelContainerHelper.isShowing()) {
            mDynamicStickerPanelContainerHelper.onStickerRemoved(view.getStickerId());
        }
    }

    /**
     * 添加文字贴纸
     */
    private void recordTextSticker(StickerView view) {
        editRecorder.setChangeSticker(true);
        if (textStickerView == null)
            textStickerView = new ArrayList<>();
        if (!textStickerView.contains(view))
            textStickerView.add(view);
    }

    /**
     * 删除文字贴纸
     */
    private void unRecordTextSticker(StickerView view) {
        if (textStickerView != null)
            textStickerView.remove(view);
    }

    /**
     * 显示编辑文字pannel
     */
    private void showEdittextPannel(String text, int index) {
        if (edittextPannel == null) {
            ViewStub vs = findViewById(R.id.moment_edit_text_layout_stub);
            edittextPannel = (MomentEdittextPannel) vs.inflate();
            edittextPannel.setHint("描述这个视频");
            edittextPannel.setChangeTextListener(new MomentEdittextPannel.ChangeTextListener() {
                @Override
                public void onChange(Bitmap textBitmap, String text, int checkedIndex) {
                    if (textBitmap != null && !TextUtils.isEmpty(text)) {
                        if (editingStickerView != null) {
                            editingStickerView.changeBitmap(textBitmap);
                            editingStickerView.setTextAndColorIndex(text, checkedIndex);
                            editingStickerView = null;
                        } else if (getAllStickerCount() >= MediaConstants.MAX_STICKER_COUNT) {
                            Toaster.showInvalidate("最多只能添加 " + MediaConstants.MAX_STICKER_COUNT + " 个贴纸");
                        } else {
                            StickerView stickerView = stickerContainerView.addSticker(textBitmap, text, checkedIndex, getTextStickerPosX(textBitmap), getTextStickerPosY(textBitmap));
                            recordTextSticker(stickerView);
                        }
                    }
                    showToolsLayout();
                }
            });
        }
        edittextPannel.setText(text);
        edittextPannel.setCheckedIndex(index);
        edittextPannel.setVisibility(View.VISIBLE);
        edittextPannel.beginEdit(getActivity());
        hideToolsLayout(true);
        if (mDynamicStickerPanelContainerHelper != null && mDynamicStickerPanelContainerHelper.isShowing()) {
            mDynamicStickerPanelContainerHelper.dismiss();
        }
    }

    private int getAllStickerCount() {
        //        return stickerContainerView.getChildCount();
        final int tc = textStickerView != null ? textStickerView.size() : 0;
        final int tv = processPresenter != null ? processPresenter.getDynamicStickerCount() : 0;
        return tc + tv;
    }

    private int getTextStickerPosX(Bitmap bitmap) {
        final int w = bitmap.getWidth();
        final int sw = stickerContainerView.getWidth();
        return (sw - w) >> 1;
    }

    private int getTextStickerPosY(Bitmap bitmap) {
        final int h = bitmap.getHeight();
        final int sh = stickerContainerView.getHeight();
        return (sh - h) >> 1;
    }

    private void showToolsLayout() {
        toolsLayout.setVisibility(View.VISIBLE);
        if (paintPanel != null) {
            paintPanel.setVisibility(View.GONE);
        }
        sendBtn.setVisibility(View.VISIBLE);
        closeBtn.setVisibility(View.VISIBLE);
    }

    private void hideToolsLayout(boolean anim) {
        toolsLayout.setVisibility(View.INVISIBLE);
        closeBtn.setVisibility(View.INVISIBLE);
        sendBtn.setVisibility(View.INVISIBLE);
    }

    private boolean isClicking = false;

    @Override
    public void onClick(View v) {
        if (isClicking) {
            return;
        }
        isClicking = true;

        if (v == sendBtn) {
            sendVideo();
        } else if (v == speedLayout) {
            gotoVideoSpeedAdjustPage();
        } else if (v == paintLayout) {
            showPaintPanel();
        } else if (v == textLayout) {
            //正在处理视频时，不能编辑
            if (isProcessing) {
                return;
            }
            showEdittextPannel(null, 0);
        } else if (v == addStickerLayout) {
            showStickerPanel();
        } else if (v == closeBtn) {
            KeyBoardUtil.hideSoftKeyboardNotAlways(getActivity());
            onBackPressed();
        } else if (v == coverLayout) {
            File file = getCoverSaveFile();
            Intent intent = new Intent(getActivity(), SelectMomentCoverActivity.class);
            intent.putExtra(MediaConstants.KEY_VIDEO_PATH, video);
            intent.putExtra(MediaConstants.KEY_OUTPUT_COVER_PATH, file.getAbsolutePath());
            intent.putExtra(MediaConstants.KEY_SELECTED_COVER_POS, selectedCoverPos);
            startActivityForResult(intent, REQUEST_CODE_SELECT_COVER);
        } else if (v == musicLayout) {
            showMusicPanel();
        } else if (v == specialViewBtn) {
            showSpecialFilterPanel();
        } else if (v == videoRootView) {
            if (specialPanelViewHelper != null && specialPanelViewHelper.isInFilterMode()) {
                specialPanelViewHelper.onVideoViewOnClick();
            }
            if (null != filterPanel && filterPanel.getVisibility() == View.VISIBLE) {
                hideFilterPanel();
            }
        } else if (v == filterBg) {
            hideFilterPanel();
        } else if (v == filterLayout) {
            showFilterPanel(MomentFilterPanelTabLayout.ON_CLICK_FILTER);
        }

        MomoMainThreadExecutor.postDelayed(hashCode(), new Runnable() {
            @Override
            public void run() {
                isClicking = false;
            }
        }, 500);
    }

    private void gotoVideoSpeedAdjustPage() {
        if (specialPanelViewHelper != null && specialPanelViewHelper.useSpecialFilter()) {
            Toaster.show("特效滤镜与变速不能叠加使用");
            return;
        } else if (stickerContainerView.hasImageSticker()) {
            Toaster.show("先使用了贴纸不能使用变速");
            return;
        }
        if (processPresenter != null) {
            VideoSpeedAdjustActivity.start(getActivity(), video.path, processPresenter
                    .getEffectModelForSpeedAdjust(), SPEED_ADJUST_CODE);
        }
    }

    @Override
    public void onStartCompress() {
        MDLog.i(LogTag.PROCESSOR.PROCESS, "onStartCompress");
        if (getContext() == null) {
            return;
        }
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    VideoCompressUtil.stopCompress();
                    progressDialog.dismiss();
                    if (processPresenter != null) {
                        processPresenter.onResume();
                    }
                    progressDialog = null;
                }
            });
            progressDialog.setMessage("请稍候......");
            final Window window = progressDialog.getWindow();
            if (window != null) {
                window.setLayout(UIUtils.getPixels(170), UIUtils.getPixels(50));
            }
        }

        if (!progressDialog.isShowing()) {
            showDialog(progressDialog);
        }
    }

    @Override
    public void onUpdateCompress(float progress) {
        MDLog.i(LogTag.PROCESSOR.PROCESS, "onUpdateCompress");
        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }
        if (progress > 1.0f) {
            progress = 1.0f;
        }
        String str = "处理中 " + (int) (progress * 100) + "%";
        progressDialog.setMessage(str);
    }

    @Override
    public void onFinishCompress(Video result, boolean hasTranscoding) {
        MDLog.i(LogTag.PROCESSOR.PROCESS, "onFinishCompress");
        if (videoView == null) {
            return;
        }
        // 获取关键帧
        video.path = result.path;
        File file = new File(video.path);

        if (!file.exists() || file.length() <= 0) {
            video.path = oldPath;
            Toaster.showInvalidate("压缩异常，请稍后再试");
            return;
        }
        oldPath = result.path;
        video.hasTranscoding = true;
        video.size = (int) file.length();
        // 重新初始化video
        VideoUtils.getVideoFixMetaInfo(video);
        oldLength = video.length;
        oldPath = video.path;

        if (videoView != null && videoView.getSurfaceTexture() != null) {
            videoView.getSurfaceTexture().setDefaultBufferSize(video.getWidth(), video.height);
        }

        if (null != processPresenter) {
            processPresenter.updateVideo(video);
        }
        MomoTaskExecutor.executeUserTask(getTaskTag(), new GetFrameByVideo(video) {
            @Override
            protected void onTaskSuccess(List<Bitmap> bitmaps) {
                super.onTaskSuccess(bitmaps);
                List<TimeRangeScale> timeRangeScales = new ArrayList<>();
                timeRangeScales.add(new TimeRangeScale(0, video.length, 1f));
                if (processPresenter != null) {
                    processPresenter.updateEffectModelWithoutPlay(timeRangeScales, 0L);
                }
                showSpecialFilterPanel();
            }
        });
    }

    @Override
    public void onErrorCompress(Video result) {
        Toaster.show("压缩异常，请稍后再试");
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        processPresenter.focusPlayVideo();
    }

    private boolean compressVideoIfNeed() {
        if (!video.hasTranscoding || frames == null) {
            processPresenter.pause();
            //processPresenter.clearAllCodec();
            VideoCompressUtil.compressVideo(video
                    , VideoUtils.getMaxVideoSize(), !video.hasTranscoding
                    , this);
            return true;
        }
        return false;
    }

    private void showSpecialFilterPanel() {
        MDLog.i(LogTag.PROCESSOR.PROCESS, "showSpecialFilterPanel");
        if (processPresenter == null) {
            return;
        }
        if (processPresenter.hasChangeSpeed()) {
            Toaster.show("特效滤镜与变速不能叠加使用");
            return;
        }
        if (compressVideoIfNeed()) {
            return;
        }
        if (!isForeGround) {
            return;
        }
        hideToolsLayout(true);
        if (specialPanelViewHelper == null) {
            specialDataControl = new SpecialDataControl(getContext().getApplicationContext());
            processPresenter.addSpecialFilter(specialDataControl.getSpecialFilter());
            specialPanelViewHelper = new SpecialPanelViewHelper(this, getContentView(), specialDataControl, processPresenter, video, frames);
            specialPanelViewHelper.setOnSpecialPanelListener(new SpecialPanelViewHelper.SpecialPanelListener() {
                @Override
                public void onHide(boolean isTimeFilterSelected) {
                    hideSpecialFilterPanel();

                }
            });
            animatorListener = new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    processPresenter.pause();
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    allStickerContainer.setVisibility(View.VISIBLE);
                    processPresenter.setNeedAutoPlay(true);
                    processPresenter.setLoopBack(true);
                    processPresenter.seekVideo(0L, false);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            };
        }
        // 暂定播放
        allStickerContainer.setVisibility(View.GONE);

        specialPanelViewHelper.show(videoRootView, videoRootView.getWidth() < videoRootView.getHeight(), animatorListener);
    }

    @Override
    public List<BasicFilter> getSpeicalFilter() {
        if (specialDataControl != null) {
            return specialDataControl.getSpecialFilter();
        }
        return null;
    }

    private void hideSpecialFilterPanel() {
        if (processPresenter == null || specialPanelViewHelper == null) {
            return;
        }
        specialPanelViewHelper.hide();
        showToolsLayout();
    }

    private void showMusicPanel() {
        if (musicPanelHelper == null) {
            int initMusicVolume = chooseMusicInRecord != null ? 100 : 0;
            musicPanelHelper = new MusicPanelHelper(getChildFragmentManager(), getContentView(), initMusicVolume);
            musicPanelHelper.setOnMusicListener(new MusicPanelHelper.OnMusicListener() {

                @Override
                public void onSelect(@NonNull MusicContent music) {
                    video.playingMusic = music;
                    processPresenter.setPlayMusic(music);
                    if (processPresenter != null) {
                        editRecorder.setChangeMusic(true);
                        processPresenter.updateEffectModelAndPlay(0L);
                    }
                }

                @Override
                public void onCut(int startTime, int endTime) {
                    if (processPresenter != null && video.playingMusic != null) {
                        video.playingMusic.startMillTime = startTime;
                        video.playingMusic.endMillTime = endTime;
                        processPresenter.setPlayMusic(video.playingMusic);
                        editRecorder.setChangeMusic(true);
                        processPresenter.updateEffectModelAndPlay(0L);
                    }
                }

                @Override
                public void onVolumeChanged(int percent) {
                    if (processPresenter == null) {
                        return;
                    }
                    OnVolumeChangeListener onVolumeChangeListener = processPresenter.getVolumeChangeListener();
                    if (onVolumeChangeListener != null) {
                        onVolumeChangeListener.onMusicVolumeChanged(percent);
                        if (chooseMusicInRecord == null) {
                            onVolumeChangeListener.onVideoVolumeChanged(100 - percent);
                        }
                    }
                }

                @Override
                public void onHide() {
                    Animation anim = AnimUtils.Animations.newFromBottonAnimation(300);
                    toolsLayout.setVisibility(View.VISIBLE);
                    toolsLayout.startAnimation(anim);
                }

                @Override
                public void pauseVideo() {
                    if (processPresenter == null) {
                        return;
                    }
                    processPresenter.pause();
                }

                @Override
                public void clearMusic() {
                    if (video.playingMusic == null) {
                        return;
                    }
                    video.playingMusic = null;
                    musicLayout.setActivated(false);
                    if (processPresenter != null) {
                        processPresenter.setPlayMusic(null);
                        editRecorder.setChangeMusic(true);
                        processPresenter.updateEffectModelAndPlay(0L);
                    }
                }

            });
        }
        musicPanelHelper.show(video.playingMusic, true);
        Animation anim = AnimUtils.Animations.newToBottonAnimation(300);
        anim.setAnimationListener(new OnAnimationEndListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                toolsLayout.setVisibility(View.GONE);
            }
        });
        toolsLayout.startAnimation(anim);
    }

    /***********      Music Panel END        ************/

    private File getCoverSaveFile() {
        String dir = new File(video.path).getParent();
        return new File(dir, System.currentTimeMillis() + "_cover.jpg");
    }

    /**
     * 显示所有贴纸界面
     */
    private void showStickerPanel() {
        if (frames == null || frames.size() <= 0) {
            MomoTaskExecutor.executeUserTask(getTaskTag(), new GetFrameByVideo(video) {
                @Override
                protected void onTaskSuccess(List<Bitmap> bitmaps) {
                    super.onTaskSuccess(bitmaps);
                    showStickerPanel();
                }
            });
            return;
        }

        if (paintPanel != null) {
            paintPanel.setVisibility(View.GONE);
        }
        hideToolsLayout(true);
        if (stickerPanel == null) {
            ViewStub vs = findViewById(R.id.moment_edit_sticker_panel_stub);
            mStickerPanelContentView = vs.inflate();
            mDynamicStickerPanelContainerHelper = new DynamicStickerPanelContainerHelper();
            mDynamicStickerPanelContainerHelper.init(mStickerPanelContentView, video.length, processPresenter);
            mDynamicStickerPanelContainerHelper.bindVideoFrames(frames);
            stickerPanel = mStickerPanelContentView.findViewById(R.id.sticker);
            mDynamicStickerPanelContainerHelper.setOnCancelListener(new DynamicStickerPanelContainerHelper.OnCancelListener() {
                @Override
                public void onCancel(List<Integer> cancelList) {
                    for (int stickerId : cancelList) {
                        stickerContainerView.removeStickerViewById(stickerId);
                    }
                }
            });
            stickerPanel.setOnStickerPanelListener(new DynamicStickerPanel.OnStickerPanelListener() {
                @Override
                public void onCloseClicked() {
                    hideStickerPanel();
                }

                @Override
                public void onChooseSticker(final View view, DynamicStickerListAdapter.ViewHolder vh, int position, DynamicSticker sticker) {
                    Rect viewRect = new Rect();
                    view.getGlobalVisibleRect(viewRect);

                    if (StickerManager.isStickerDownloaded(sticker)) {
                        performClick(viewRect, sticker);
                    } else {
                        if (!NetUtils.isNetworkAvailable()) {
                            Toaster.show("没网络");
                            return;
                        }
                        //先去下载，然后传listener
                        StickerManager manager = new StickerManager();
                        manager.startDownloadFaceResource(viewRect, sticker, position, new StickerManager.OnStickerDownloadListener() {
                            @Override
                            public void onStart(Rect viewRect, DynamicSticker sticker, String stickerId, int position) {
                                if (!isAdded() || isDetached()) {
                                    return;
                                }
                                stickerPanel.notifyItem(position);
                            }

                            @Override
                            public void onSuccess(Rect viewRect, DynamicSticker sticker, String stickerId, int position) {
                                if (!isAdded() || isDetached()) {
                                    return;
                                }
                                stickerPanel.notifyItem(position);
                                performClick(viewRect, sticker);
                            }

                            @Override
                            public void onFailed(Rect viewRect, DynamicSticker sticker, String stickerId, int position) {
                                if (!isAdded() || isDetached()) {
                                    return;
                                }
                                stickerPanel.notifyItem(position);
                            }
                        });
                    }
                }
            });
        }

        if (mDynamicStickerPanelContainerHelper != null && !mDynamicStickerPanelContainerHelper.isShowing()) {
//            allStickerContainer.setVisibility(View.GONE);
            stickerContainerView.setClickDeleteMode(true);
            mDynamicStickerPanelContainerHelper.show(videoRootView, videoRootView.getWidth() < videoRootView.getHeight(), new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    processPresenter.pause();
                }

                @Override
                public void onAnimationEnd(Animator animator) {
//                    allStickerContainer.setVisibility(View.VISIBLE);
                    processPresenter.setNeedAutoPlay(true);
                    processPresenter.setLoopBack(true);
                    processPresenter.seekVideo(0L, false);
                    stickerContainerView.setClickDeleteMode(false);
                    stickerContainerView.clearAllShowClickEditState();
                    showToolsLayout();

                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }


        stickerPanel.loadStickerData();
    }

    /**
     * 点击动态贴纸，并下载后的逻辑
     */
    private void performClick(final Rect viewRect, final DynamicSticker sticker) {
        if (processPresenter == null)
            return;
        MDLog.e(LogTag.COMMON, "processPresenter.getDynamicStickerCount() %d  getMaxDynamicStickerCount()  %d", processPresenter.getDynamicStickerCount(), getMaxDynamicStickerCount());
        if (processPresenter.getDynamicStickerCount() >= getMaxDynamicStickerCount()) {
            Toaster.showInvalidate("最多只能添加 " + getMaxDynamicStickerCount() + " 个动态贴纸");
            return;
        }

        //拿到View的位置，找到图片的位置
        ThreadUtils.execute(ThreadUtils.TYPE_RIGHT_NOW, new Runnable() {
            @Override
            public void run() {

                Bitmap bitmap = ImageLoaderX.load(sticker.getPic()).loadAsync();
                if (bitmap == null || bitmap.isRecycled() || (!VideoEditFragment.this.isAdded())) {
                    return;
                }
                Activity activity = VideoEditFragment.this.getActivity();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int posX = viewRect.left;
                        int posY = viewRect.top;
                        String path = StickerManager.getStickerDir(sticker).getAbsolutePath();
                        processPresenter.addMaskModel(path, posX, posY, viewRect.width());
                    }
                });
            }
        });
    }

    private int maxDynamicSticker = -100;

    private int getMaxDynamicStickerCount() {
        if (-100 == maxDynamicSticker) {
            maxDynamicSticker = 3;
        }
        if (DeviceUtils.getSDKVersion() < 21) {
            maxDynamicSticker = 1;
        }
        return maxDynamicSticker;
    }

    /**
     * 显示涂鸦界面
     */
    private void showPaintPanel() {
        hideToolsLayout(false);
        if (paintPanel == null) {
            ViewStub viewStub = findViewById(R.id.moment_edit_paint_layout_stub);
            paintPanel = (PaintPanelView) viewStub.inflate();
            paintPanel.init();

            paintPanel.setPaintActionListener(new PaintPanelView.PaintActionListener() {
                @Override
                public void onUndo(Bitmap paintBmp, Bitmap easeBitmap) {
                    setBlendBitmap(paintBmp, easeBitmap);
                    editRecorder.setChangePaint(paintPanel.canUndo());
                }

                @Override
                public void onFinished(Bitmap paintBmp, Bitmap easeBitmap) {
                    setBlendBitmap(paintBmp, easeBitmap);
                    //隐藏绘制面板
                    showToolsLayout();
                    editRecorder.setChangePaint(paintBmp != null || easeBitmap != null);
                }

                @Override
                public void onDraw(Bitmap paintBmp, Bitmap easeBitmap) {
                    setBlendBitmap(paintBmp, easeBitmap);
                }

                private void setBlendBitmap(Bitmap bitmap, Bitmap masik) {
                    if (bitmap == null || !bitmap.isRecycled()) {
                        Bitmap temp = null;
                        if (bitmap != null) {
                            // 防止拍摄器主动recyle bitmap  导致crash
                            temp = Bitmap.createBitmap(bitmap);
                        }
                        addDrawBgImage.setImageBitmap(temp);
                    }
                    if (processPresenter != null)
                        processPresenter.setBlendBitmap(bitmap, masik);
                }
            });
            paintPanel.setImageParams(new RelativeLayout.LayoutParams(imageParams));
        }
        paintPanel.setVisibility(View.VISIBLE);
        paintPanel.bringToFront();
    }

    @Override
    public void hideOrShowCover(boolean show, boolean release) {
        //        if (show) {
        //            if (videoCover.getVisibility() == View.VISIBLE && videoCover.getDrawable() != null) {
        //                return;
        //            }
        //            MDLog.i(LogTag.PROCESSOR.PROCESS_play, "hideOrShowCover selectedCoverPath:" + selectedCoverPath);
        //            if (!TextUtils.isEmpty(selectedCoverPath)) {
        //                try {
        //                    Bitmap cover = BitmapFactory.decodeFile(selectedCoverPath);
        //                    if (cover != null && !cover.isRecycled()) {
        //                        videoCover.setImageBitmap(cover);
        //                        videoCover.setVisibility(View.VISIBLE);
        //                    }
        //                } catch (Exception e) {
        //                    MDLog.printErrStackTrace(LogTag.PROCESSOR.PROCESS, e);
        //                }
        //            }
        //        } else {
        //            final Drawable d = videoCover.getDrawable();
        //            videoCover.setImageDrawable(null);
        //            videoCover.setVisibility(View.GONE);
        //            if (release && d instanceof BitmapDrawable) {
        //                BitmapDrawable bd = (BitmapDrawable) d;
        //                Bitmap bm = bd.getBitmap();
        //                if (bm != null && !bm.isRecycled()) {
        //                    bm.recycle();
        //                }
        //            }
        //        }
    }

    @Override
    public void showProcessAudioProgress() {
        ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("音频处理中，请稍候...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        showDialog(dialog);
    }

    @Override
    public TextureView getVideoView() {
        return videoView;
    }

    @Override
    public void onPitchShiftProcessError(int msg, Exception e) {
        MomoMainThreadExecutor.post(new Runnable() {
            @Override
            public void run() {
                closeDialog();
            }
        });
    }

    @Override
    public void onPitchShiftProcessFinished() {
        MomoMainThreadExecutor.post(new Runnable() {
            @Override
            public void run() {
                closeDialog();
            }
        });
    }

    @Override
    public void onAddSticker(Rect viewRect, Bitmap bitmap, final StickerEntity entity) {
        if (mDynamicStickerPanelContainerHelper != null) {
            mDynamicStickerPanelContainerHelper.updateCurrentEditSticker(entity, true);
        }
        editRecorder.setChangeSticker(true);
        stickerContainerView.addSticker(bitmap, entity, new StickerView.onUpdateViewListener() {
            @Override
            public void onUpdateView(PointF centerPoint, long stickerId, float scale, float angle) {
                if (entity.getId() == stickerId) {
                    //由于视频播放的view不是全屏，需要根据坐标重置centerX、centerY。
                    Rect viewRect = new Rect();
                    videoView.getGlobalVisibleRect(viewRect);
                    int width = videoView.getWidth();
                    int height = videoView.getHeight();
                    float centerX = centerPoint.x / (float) width;
                    float centerY = centerPoint.y / (float) height;
                    float shaderScale = scale;//shader 坐标转换
                    if (processPresenter != null)
                        processPresenter.updateMaskModel(new PointF(centerX, centerY), shaderScale, angle, (int) stickerId);
                }
            }
        });
        stickerContainerView.clearAllShowClickEditState();
        stickerContainerView.getCurrentEditView().setShowEditBorder(true);
    }

    @Override
    public void onPlayingPaused() {
        MomoMainThreadExecutor.post(new Runnable() {
            @Override
            public void run() {
                if (specialPanelViewHelper != null && specialPanelViewHelper.isInFilterMode()) {
                    specialPanelViewHelper.onPlayingPaused();
                }
            }
        });
    }

    @Override
    protected void onLoad() {

    }

    @Override
    public void onPlaying(final long ptsMs) {
        MomoMainThreadExecutor.post(new Runnable() {
            @Override
            public void run() {
                if (musicPanelHelper != null && musicPanelHelper.isShowing() && video.playingMusic != null) {
                    MusicContent music = video.playingMusic;
                    int pos = (int) (music.startMillTime + ptsMs);
                    int maxEnd = music.endMillTime > 0 ? music.endMillTime : music.length;
                    if (pos > maxEnd) {
                        int duration = maxEnd - music.startMillTime;
                        int diff = pos - music.startMillTime;
                        pos = music.startMillTime + (diff % duration);
                    }
                    musicPanelHelper.updatePlayingTime(pos);
                }
            }
        });

    }

    @Override
    public void onProcessProgress(final float progress) {
        MomoMainThreadExecutor.post(new Runnable() {
            @Override
            public void run() {
                if (specialPanelViewHelper != null && specialPanelViewHelper.isInFilterMode()) {
                    specialPanelViewHelper.onProcessProgress(progress);
                }
                if (mDynamicStickerPanelContainerHelper != null && mDynamicStickerPanelContainerHelper.isShowing()) {
                    mDynamicStickerPanelContainerHelper.onProcessProgress(progress);
                }
                if (stickerContainerView != null) {
                    stickerContainerView.checkStickerNeedShow((long) (progress * video.length));
                }
            }
        });
    }

    @Override
    public void onProcessFinish() {
        MomoMainThreadExecutor.post(new Runnable() {
            @Override
            public void run() {
                if (specialPanelViewHelper != null && specialPanelViewHelper.isInFilterMode()) {
                    specialPanelViewHelper.onProcessFinish();
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            if (!TextUtils.isEmpty(selectedCoverPath)) {
                outState.putString(MediaConstants.KEY_OUTPUT_COVER_PATH, selectedCoverPath);
            }
            outState.putParcelable(CHOSEN_MUSIC_IN_RECORD, chooseMusicInRecord);
            outState.putParcelable(MediaConstants.EXTRA_KEY_VIDEO_DATA, video);
            outState.putString(KEY_OLD_PATH, oldPath);
            outState.putLong(KEY_OLD_LENGTH, oldLength);
            outState.putParcelable(MediaConstants.KEY_VIDEO_EDIT_PARAMS, mVideoEditParams);
            editRecorder.onSaveInstanceState(outState);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            String coverPath = savedInstanceState.getString(MediaConstants.KEY_OUTPUT_COVER_PATH);
            if (!TextUtils.isEmpty(coverPath)) {
                selectedCoverPath = coverPath;
            }
            chooseMusicInRecord = savedInstanceState.getParcelable(CHOSEN_MUSIC_IN_RECORD);
            video = savedInstanceState.getParcelable(MediaConstants.EXTRA_KEY_VIDEO_DATA);
            oldPath = savedInstanceState.getString(KEY_OLD_PATH);
            oldLength = savedInstanceState.getLong(KEY_OLD_LENGTH, 0);
            editRecorder.onRestoredState(savedInstanceState);
        }
    }

    private void cancelVideo() {
        deleteOldVideo(video);
//        todo wujian
//        if (justEdit) {
//            finishActivity();
//        } else {
        //放弃编辑后，重新回到录制界面
        if (fragmentChangeListener != null) {
            Bundle bundle = new Bundle();
            if (getArguments() != null && getArguments().getParcelable(MediaConstants.KEY_CACHE_EXTRA_PARAMS) != null) {
                bundle.putParcelable(MediaConstants.KEY_CACHE_EXTRA_PARAMS, getArguments().getParcelable(MediaConstants.KEY_CACHE_EXTRA_PARAMS));
            }
            hideViews();
            bundle.putString(GOTO_WHERE, BACK_TO_OLD);
            if (video != null && !TextUtils.isEmpty(video.path)) {
                bundle.putString(MediaConstants.KEY_RESTORE_VIDEO_PATH, video.path);
            }
            fragmentChangeListener.change(this, bundle);
        }
//        }
        if (processPresenter != null)
            processPresenter.onDestroy();

    }

    private void deleteOldVideo(Video video) {
        if (!video.isChosenFromLocal) {
            File file = new File(video.path);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private Object getTaskTag() {
        return TAG;
    }

    private void hideViews() {
        final int state = View.INVISIBLE;
        if (allStickerContainer != null)
            allStickerContainer.setVisibility(state);
        if (closeBtn != null)
            closeBtn.setVisibility(state);
        if (sendBtn != null)
            sendBtn.setVisibility(state);
        if (toolsLayout != null)
            toolsLayout.setVisibility(state);
    }

    /**
     * 隐藏涂鸦界面
     */
    private void hidePaintPanel() {
        if (paintPanel != null && paintPanel.getVisibility() != View.GONE) {
            paintPanel.finishPaint();
        }
    }

    private void finishActivity() {
        Activity a = getActivity();
        if (a != null) {
            a.setResult(Activity.RESULT_CANCELED);
            a.finish();
        }
    }

    /**
     * 隐藏所有贴纸界面
     */
    private void hideStickerPanel() {
        if (mDynamicStickerPanelContainerHelper != null && mDynamicStickerPanelContainerHelper.isShowing()) {
            mDynamicStickerPanelContainerHelper.dismiss();
        }
        showToolsLayout();
    }

    private void showCancelProcessDialog() {
        if (this.getActivity() == null) {
            return;
        }
        AlertDialog mCloseDialog = new AlertDialog.Builder(this.getActivity())
                .setTitle("提示")
                .setMessage("放弃合成视频？")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mIsDialogBtnClicked = true;
                        if (processPresenter != null && progressContent.getVisibility() == View.VISIBLE) {
                            processPresenter.changeToPreviewMode();
                            showToolsLayout();
                            showSendButton(true);
                            hideProgressView();
                            setIsProcessing(false);
                            Toaster.show(R.string.moment_cancel_process);
                        }
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        mCloseDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mIsDialogBtnClicked) {
                    mIsDialogBtnClicked = false;
                } else if (processPresenter != null) {
                    processPresenter.onResume();
                }
            }
        });
        showDialog(mCloseDialog);
    }

    private void hideProgressView() {
        progressView.setProgressNoAnim(0f);
        progressContent.setVisibility(View.GONE);
        progressView.clearAnimation();
        sendText.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResultReceived(int requestCode, int resultCode, Intent data) {
        //获取封面
        if (requestCode == REQUEST_CODE_SELECT_COVER) {
            if (data != null && resultCode == RESULT_OK) {
                String filePath = data.getStringExtra(MediaConstants.KEY_OUTPUT_COVER_PATH);
                selectedCoverPos = data.getIntExtra(MediaConstants.KEY_SELECTED_COVER_POS, 0);
                if (!TextUtils.isEmpty(filePath) && new File(filePath).exists()) {
                    //删除上次选择的封面
                    if (!TextUtils.isEmpty(selectedCoverPath)) {
                        File oldFile = new File(selectedCoverPath);
                        if (oldFile.exists()) {
                            oldFile.delete();
                        }
                    }
                    selectedCoverPath = filePath;
                    coverLayout.setActivated(!TextUtils.isEmpty(selectedCoverPath));
                } else {
                    Toaster.showInvalidate("获取封面失败，请重试");
                }
            }
        }
        //变速
        else if (requestCode == SPEED_ADJUST_CODE) {
            EffectModel effectModel = null;
            if (data != null && resultCode == RESULT_OK) {
                effectModel = (EffectModel) data.getSerializableExtra(MediaConstants.KEY_VIDEO_SPEED_PARAMS);
            }
            if (processPresenter != null) {
                processPresenter.setEffectModelForSpeedAdjust(effectModel);
                editRecorder.setChangeSpeed(processPresenter.getTimeRangeScales(effectModel) != null);
            }
            showToolsLayout();
        }
        //超过1分钟裁剪
        else if (requestCode == REQUEST_CODE_CUT_VIDEO) {
            onCutVideoBack(resultCode, data);
        }
        if (musicPanelHelper != null && musicPanelHelper.onActivityResultReceived(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResultReceived(requestCode, resultCode, data);
    }

    private void onCutVideoBack(int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            //取消裁剪视频,需重新裁剪
            checkOldFileAndRestart();
        } else if (resultCode == RESULT_OK && data != null) {
            boolean success = data.getBooleanExtra(MediaConstants.KEY_CUT_VIDEO_RESULT, false);
            if (success) {
                Video cutVideo = data.getParcelableExtra(MediaConstants.KEY_PICKER_VIDEO);
                File file = new File(cutVideo.path);
                if (file.exists()) {
                    returnVideo(file, false);
                    return;
                } else {
                    //文件不存在
                    Toaster.show(R.string.moment_cut_file_not_exists);
                    checkOldFileAndRestart();
                }
            } else {
                Toaster.show(R.string.moment_cut_failed);
                checkOldFileAndRestart();
            }
        }
    }

    private void setIsProcessing(boolean isProcess) {
        isProcessing = isProcess;
        //合成视频时，不能拖动贴纸
        stickerContainerView.setCanEdit(!isProcess);
    }

    private void saveToGallery(File sourceFile, boolean hasProcessed) {
        AlbumNotifyHelper.getAblumNotifyHelper().copyVideoFile(true, sourceFile);
        showToolsLayout();
        showSendButton(true);
        hideProgressView();
        if (hasProcessed) {
            setIsProcessing(false);
        }
        if (processPresenter != null) {
            editRecorder.setChangeMusic(false);
            //processPresenter.restartVideo(shouldMuteVideo(), shouldPlayMusic());
            processPresenter.changeToPreviewMode();
        }
    }

    private void sendVideo() {
        if (video == null || video.path == null) {
            Toaster.showInvalidate("视频文件非法，请重新录制");
            return;
        }
        //判断原文件大小是否一致
        final File sourceFile = new File(video.path);
        if (!sourceFile.exists() || sourceFile.length() != video.size) {
            Toaster.showInvalidate("视频文件非法，请重新录制");
            finishActivity();
            return;
        }
        final boolean isEdited = isEdited();
        final boolean isRecord = !video.isChosenFromLocal;
        if (isEdited || isRecord) {
            makeVideo(true, true);
        } else { // 压缩、保存、跳转
            makeVideo(true, false);
        }
    }

    private void makeVideo(final boolean send, final boolean save) {
        MDLog.i(LogTag.PROCESSOR.PROCESS, "makeVideo send:" + send);
        MDLog.i(LogTag.PROCESSOR.PROCESS, "makeVideo save:" + save);
        prepareEncoding(send, true, false);
        setIsProcessing(true);

        progressContent.setVisibility(View.VISIBLE);
        sendText.setText("视频处理中...");
        sendText.setVisibility(View.VISIBLE);
        //一开始就显示一点进度 假象
        if (progressView != null) {
            progressView.setProgressNoAnim(1f);
        }

        processPresenter.setProcessListener(new VideoEditPresenter.OnProcessListener() {
            @Override
            public void onProcessProgress(float progress) {
                MDLog.i(LogTag.PROCESSOR.PROCESS, "onProcessProgress " + progress);
                if (null != getActivity() && !getActivity().isFinishing()) {
                    final float newProgress = progress * 100;
                    if (newProgress < 5f) {
                        return;
                    }
                    if (progressView != null)
                        progressView.setProgressNoAnim(newProgress);
                }
            }

            @Override
            public void onProcessFinish(final String path) {
                MDLog.i(LogTag.PROCESSOR.PROCESS, "onProcessFinish " + path);
                if (null == getActivity() || getActivity().isFinishing()) {
                    return;
                }
                File file = new File(path);
                if (send) {
                    if (!checkNeedCutVideo(file)) {
                        returnVideo(file, send);
                    }
                } else {
                    saveToGallery(file, true);
                }
            }

            @Override
            public void onProcessStart() {
                MDLog.i(LogTag.PROCESSOR.PROCESS, "onProcessStart ");
            }

            @Override
            public void onProcessFailed(String e) {
                MDLog.i(LogTag.PROCESSOR.PROCESS, e);
                if (null == getActivity() || getActivity().isFinishing()) {
                    return;
                }
                setIsProcessing(false);
                if (processPresenter != null) {
                    editRecorder.setChangeMusic(false);
                    processPresenter.restartVideo();
                }
                Toaster.showInvalidate("视频合成失败，请重试");
                hideProgressView();
                showToolsLayout();
                showSendButton(true);
            }
        });

        //视频处理时，隐藏所有View元素
        hideToolsLayout(true);
        showSendButton(false);

        processPresenter.makeVideo(false);

    }

    private boolean checkNeedCutVideo(File file) {
        boolean tooLong = isVideoLengthTooLong(file);
        if (tooLong) {
            String alert = "视频超过60秒，需要裁剪";
            AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setTitle("提示")
                    .setMessage(alert).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mIsDialogBtnClicked = true;
                            startCutVideo(maxPublishDuration - MediaConstants.MOMENT_DURATION_EXPAND);
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mIsDialogBtnClicked = true;
                            checkOldFileAndRestart();
                        }
                    }).create();
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (mIsDialogBtnClicked) {
                        mIsDialogBtnClicked = false;
                    } else {
                        checkOldFileAndRestart();
                    }
                }
            });
            showDialog(dialog);
        }
        return tooLong;
    }

    private boolean checkOldFileAndRestart() {
        if (!TextUtils.isEmpty(oldPath) && oldLength > 0) {
            File f = new File(oldPath);
            if (f.exists()) {
                video.path = oldPath;
                video.length = oldLength;
                showToolsLayout();
                showSendButton(true);
                hideProgressView();
                setIsProcessing(false);
                if (processPresenter != null) {
                    editRecorder.setChangeMusic(false);
                    processPresenter.restartVideo();
                }
                return true;
            }
        }
        Toaster.show(R.string.moment_file_not_exists);
        cancelVideo();
        return false;
    }

    private void startCutVideo(long max) {
        Intent intent = new Intent(getActivity(), VideoCutActivity.class);
        intent.putExtra(VideoRecordDefs.KEY_VIDEO, video);
        intent.putExtra(VideoRecordDefs.VIDEO_LENGTH_TIME, max);
        startActivityForResult(intent, REQUEST_CODE_CUT_VIDEO);
    }

    private boolean isVideoLengthTooLong(File file) {
        long length = VideoUtils.getVideoDuration(file.getAbsolutePath());
        final boolean result = length > getMaxDuration();
        if (result) {
            oldLength = video.length;
            oldPath = video.path;
            video.length = length;
            video.path = file.getAbsolutePath();
        }
        return result;
    }

    private long getMaxDuration() {
        // 8.7.9 修改为统一限制时长为最长时长1min
        //        long duration = transBean.maxDuration;
        //        if (duration <= 0) {
        //            duration = MomentConstants.MAX_PUBLISH_VIDEO_DURATION;
        //        } else {
        //            duration += MomentConstants.MOMENT_DURATION_EXPAND;
        //        }
        maxPublishDuration = MediaConstants.MAX_PUBLISH_VIDEO_DURATION;
        return MediaConstants.MAX_PUBLISH_VIDEO_DURATION;
    }

    //如果视频没有经过编辑,通过这个函数判断是否需要转码
    private boolean isNeedTranscode(MMVideoEditParams params) {
        if (params == null) {
            return false;
        }
        // 视频大小, 时长限制 时长 <= 60s 码率小于5Mb 不用压缩
        return params.getUpperVideoCompressBitRate() <= 0 || video.avgBitrate > params.getUpperVideoCompressBitRate()
                || params.getUpperVideoCompressDuration() <= 0 || video.length > params.getUpperVideoCompressDuration();
    }

    private void prepareEncoding(boolean isSend, boolean saveToGallery, boolean secondCompress) {
        //判断原文件大小是否一致
        File sourceFile = new File(video.path);
        if (!sourceFile.exists() || sourceFile.length() != video.size) {
            Toaster.showInvalidate("视频文件非法，请重新录制");
            finishActivity();
            return;
        }

        //todo cover and pitch
        //.withCoverPath(isDoLastSave ? null : selectedCoverPath)
        //withMasicKey(IProcessPlayer.KEY_FOR_MASICBITMAP);
        //helper.setOriginalCovers(transBean.originalCovers);
        //helper.withPitchFile(processPresenter.getPitchFile())
        //.withPitchMode(video.soundPitchMode);

        Bitmap blendBitmap = null;
        boolean blendChanged = false;
        if (stickerWidth == 0 || stickerHeight == 0) {
            initVideoParam();
        }

        if (stickerWidth > 0 && stickerHeight > 0) {
            if (allStickerContainer != null && stickerContainerView != null && editRecorder.isChangeFilter()) {
                blendBitmap = ImageUtil.createBitmapByView(allStickerContainer,
                        stickerWidth,
                        stickerHeight,
                        0,
                        0);
                blendChanged = true;
            }
            processPresenter.getMomentExtraInfo().setBlendBmp(blendBitmap);

        }
        //  blendChanged 肯定为 true 有何意义？

        boolean onlyAudioChanged = false;
        boolean hasVideoChanged = isVideoEdited() || blendChanged || video.frameRate > MediaConstants.MAX_VIDEO_FRAME_RATE  // 是否编辑过
                || !video.isChosenFromLocal                                                                             // 录制的视频是否要压缩
                || isNeedTranscode(mVideoEditParams);

        //如果配乐不为空，且视频未经过任何其它的编辑情况下，进行如下处理
        if (!hasVideoChanged && video.playingMusic != null) {
            hasVideoChanged = true;
            onlyAudioChanged = true;
        }

        processPresenter.getMomentExtraInfo().setVideoChanged(hasVideoChanged);
        processPresenter.getMomentExtraInfo().setUseBgChanger(onlyAudioChanged);
    }

    private void returnVideo(File file, boolean isSend) {
        if (getActivity() != null && !getActivity().isFinishing()) {
            processPresenter.onDestroy();
            video.path = file.getAbsolutePath();
            video.size = (int) file.length();
            VideoUtils.getVideoFixMetaInfo(video);
            if (video.length != 0) {
                video.avgBitrate = (int) ((long) video.size * 8000 / (video.length));
            }

            Intent intent = new Intent();
            intent.putExtra(MediaConstants.EXTRA_KEY_VIDEO_DATA, video);

            if (mVideoEditParams != null && mVideoEditParams.getFinishGotoInfo() != null) {
                FinishGotoInfo finishGotoInfo = mVideoEditParams.getFinishGotoInfo();
                if (finishGotoInfo.getExtraBundle() != null) {
                    intent.putExtras(finishGotoInfo.getExtraBundle());
                }
                if (finishGotoInfo.isNeedFinishResultMode()) {
                    final FragmentActivity activity = getActivity();
                    if (activity != null && !activity.isFinishing()) {

                        activity.setResult(Activity.RESULT_OK, intent);
                        activity.finish();
                    }
                } else {
                    if (!TextUtils.isEmpty(finishGotoInfo.getGotoActivityName()) && getActivity() != null) {
                        intent.setComponent(new ComponentName(getActivity(), finishGotoInfo.getGotoActivityName()));
                        getActivity().startActivity(intent);
                        if (finishGotoInfo.isFinishCurrentActivity()) {
                            getActivity().finish();
                        }
                    }
                }
            }
        }

    }

    @Override
    public void saveVideoSuccess(boolean isToast) {
        if (isToast) {
            Toaster.show("视频已保存到相册中");
        }
    }

    @Override
    public void saveVideoError() {
        Toaster.show("视频保存失败");
    }

    private class GetFrameByVideo extends MomoTaskExecutor.Task<Object, Object, List<Bitmap>> {
        private final VideoDataRetrieverBySoft videoDataRetrieve;
        private final List<VideoDataRetrieverBySoft.Node> videoNodes;
        private final Video video;
        private int frameW = UIUtils.getPixels(22);
        private int frameH = UIUtils.getPixels(40);
        private Matrix matrix = new Matrix();
        private int count = SpecialPanelViewHelper.KEY_COUNT;

        public GetFrameByVideo(@NonNull Video video) {
            this.video = video;
            matrix.setRotate(video.rotate);
            videoDataRetrieve = new VideoDataRetrieverBySoft();
            videoNodes = new ArrayList<>();
            float space = video.length / (count * 1F);

            long frameTime = 0;
            for (int i = 0; i < count; i++) {
                if (frameTime > video.length) {
                    // 防止超出
                    frameTime = video.length;
                }
                VideoDataRetrieverBySoft.Node node = new VideoDataRetrieverBySoft.Node(frameTime * 1000, 0, frameW, frameH);
                videoNodes.add(node);
                frameTime = (long) (frameTime + space);
            }
        }

        @Override
        protected void onPreTask() {
            if (progressDialog == null) {
                onStartCompress();
            }
        }

        @Override
        protected List<Bitmap> executeTask(Object... objects) {
            videoDataRetrieve.init(video.path);
            videoDataRetrieve.getImageByList(videoNodes);
            List<Bitmap> result = new ArrayList<>();
            Bitmap temp = null;
            for (VideoDataRetrieverBySoft.Node node : videoNodes) {
                if (node.bmp == null) {
                    node.bmp = temp;
                }
                if (node.bmp == null) {
                    continue;
                }
                temp = node.bmp;
                result.add(Bitmap.createBitmap(node.bmp, 0, 0, node.bmp.getWidth(), node.bmp.getHeight(), matrix, true));
            }
            return result;
        }

        @Override
        protected void onTaskSuccess(List<Bitmap> bitmaps) {
            if (frames != null) {
                frames.clear();
            }
            videoNodes.clear();
            frames = bitmaps;

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (videoDataRetrieve != null) {
                videoDataRetrieve.release();
            }
        }

        @Override
        protected void onTaskFinish() {
            super.onTaskFinish();
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (videoDataRetrieve != null) {
                videoDataRetrieve.release();
            }
        }

        @Override
        protected void onTaskError(Exception e) {
            if (videoDataRetrieve != null) {
                videoDataRetrieve.release();
            }
            onErrorCompress(video);
        }
    }

    private MomentFilterPanelLayout filterPanel;

    private void initFilterPanel() {
        if (filterPanel == null) {
            ViewStub filterStub = findViewById(R.id.media_filter_stub);
            if (filterStub == null) {
                return;
            }
            filterPanel = (MomentFilterPanelLayout) filterStub.inflate();
            filterPanel.setFilterDensityChangeListener(new OnFilterDensityChangeListener() {
                @Override
                public void onFilterDensityChange(int density) {
                    processPresenter.setFilterIntensity(density / 100.0f);
                }
            });
            filterPanel.setFilterSelectListener(new FilterSelectListener() {
                @Override
                public void onFilterTabSelect(int selectPosition) {
                    mCurrentFilterSelectPosition = selectPosition;
                    processPresenter.changeToFilter(selectPosition);
                }

                @Override
                public void onBeautyTabSelect(int selectPosition, int type) {

                }

                @Override
                public void onBeautyMoreChanged(float[] value, int type) {

                }
            }, true);
        }
    }

    //filter相关
    private void showFilterPanel(@MomentFilterPanelTabLayout.TabSelectedPosition final int tabPanelPosition) {
        filterBg.setVisibility(View.VISIBLE);
        initFilterPanel();
        filterPanel.switchTabPanel(tabPanelPosition, processPresenter.getAllFilters(), mCurrentFilterSelectPosition, 0, 0, 0, 0);
        AnimUtils.Default.showFromBottom(filterPanel, 400);
        AnimUtils.Default.hideToBottom(toolsLayout, false, 400);
    }

    private void hideFilterPanel() {
        filterBg.setVisibility(View.GONE);
        AnimUtils.Default.showFromBottom(toolsLayout, 400);

        if (filterPanel != null) {
            AnimUtils.Default.hideToBottom(filterPanel, true, 400);
        }
    }
}
