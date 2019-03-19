package com.mm.sdkdemo.recorder.specialfilter.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;

import com.mm.mmutil.task.MomoMainThreadExecutor;
import com.mm.mmutil.toast.Toaster;
import com.mm.mediasdk.utils.UIUtils;
import com.mm.sdkdemo.R;
import com.mm.sdkdemo.base.BaseFragment;
import com.mm.sdkdemo.base.cement.CementAdapter;
import com.mm.sdkdemo.base.cement.CementModel;
import com.mm.sdkdemo.base.cement.SimpleCementAdapter;
import com.mm.sdkdemo.base.cement.eventhook.EventHook;
import com.mm.sdkdemo.recorder.editor.player.IProcessPresenter;
import com.mm.sdkdemo.recorder.model.Video;
import com.mm.sdkdemo.recorder.specialfilter.ISpecialDataControl;
import com.mm.sdkdemo.recorder.specialfilter.SpecialFilterAnimationUtils;
import com.mm.sdkdemo.recorder.specialfilter.bean.FrameFilter;
import com.mm.sdkdemo.recorder.specialfilter.model.FrameFilterModel;
import com.mm.sdkdemo.recorder.specialfilter.model.KeysModel;
import com.mm.sdkdemo.recorder.specialfilter.widget.FilterImageView;
import com.mm.sdkdemo.recorder.specialfilter.widget.FilterSeekView;
import com.mm.sdkdemo.widget.decoration.LinearPaddingItemDecoration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SpecialPanelViewHelper {

    private static final String TAG = "SpecialPanelViewHelper";

    public static final int KEY_COUNT = 15;

    @NonNull
    private BaseFragment baseFragment;

    @NonNull
    private View hintTextView;
    @NonNull
    private View specialTopView;
    @NonNull
    private View specialPanelView;
    @NonNull
    private View specialBottomView;
    @NonNull
    private View specialCancelBtn;
    @NonNull
    private View specialAffirmBtn;
    @NonNull
    private View videoPlayLayout;
    @NonNull
    private View videoPlayIcon;
    @NonNull
    private View rollbackBtn;
    @NonNull
    private RecyclerView keyframes;
    @NonNull
    private View framesSpecialLineView;
    @NonNull
    private FilterSeekView filterSeekView;
    @NonNull
    private RecyclerView specialFilter;
    @NonNull
    private SimpleCementAdapter simpleAdapter;
    @NonNull
    private final ISpecialDataControl specialDataControl;
    @NonNull
    private final List<CementModel<?>> framesModels;

    private boolean isInFilterMode = false;

    @Nullable
    private SpecialPanelListener specialPanelListener;
    @NonNull
    private IProcessPresenter processPlayer;

    // 当前选中的滤镜
    @Nullable
    private FrameFilter currentSelectFrameFilter;
    @NonNull
    private final Video video;

    private long currentPlayingTime = 0L;

    @NonNull
    private List<Bitmap> frames;

    ObjectAnimator show;

    ObjectAnimator hide;

    public SpecialPanelViewHelper(@NonNull BaseFragment baseFragment, @NonNull View rootView,
                                  @NonNull ISpecialDataControl specialDataControl,
                                  @Nullable IProcessPresenter processPlayer,
                                  @NonNull Video video,
                                  @NonNull List<Bitmap> frames) {
        this.baseFragment = baseFragment;
        this.frames = frames;
        this.specialDataControl = specialDataControl;
        this.processPlayer = processPlayer;
        this.video = video;
        initView(rootView);
        initEvent();

        framesModels = tranForFrames(specialDataControl.getFrameFilters());

        onFrameTabSelect();
        this.filterSeekView.initLength(video.length, this.specialDataControl.getUsedFrameFilters());

    }

    private void initView(View rootView) {
        ViewStub vs = rootView.findViewById(R.id.stub_special_filter);
        specialPanelView = vs.inflate();

        this.specialTopView = specialPanelView.findViewById(R.id.special_top_layout);
        this.hintTextView = specialPanelView.findViewById(R.id.hint_text);
        this.specialBottomView = specialPanelView.findViewById(R.id.special_bottom_layout);
        this.specialCancelBtn = specialPanelView.findViewById(R.id.special_cancel_btn);
        this.specialAffirmBtn = specialPanelView.findViewById(R.id.special_affirm_btn);

        this.keyframes = specialPanelView.findViewById(R.id.keyframes);
        this.framesSpecialLineView = specialPanelView.findViewById(R.id.frames_special_line);
        this.specialFilter = specialPanelView.findViewById(R.id.special_filter);
        this.filterSeekView = specialPanelView.findViewById(R.id.filter_seekview);
        this.videoPlayLayout = specialPanelView.findViewById(R.id.video_play_layout);
        this.rollbackBtn = specialPanelView.findViewById(R.id.special_filter_back);
        this.videoPlayIcon = specialPanelView.findViewById(R.id.video_edit_status);

        simpleAdapter = new SimpleCementAdapter();

        simpleAdapter.addEventHook(new EventHook<FrameFilterModel.ViewHolder>(FrameFilterModel.ViewHolder.class) {

            @Override
            public void onEvent(@NonNull View view, @NonNull final FrameFilterModel.ViewHolder viewHolder, @NonNull final CementAdapter adapter) {
                viewHolder.cover.setFilterEvent(new FilterImageView.FilterEvent() {
                    @Override
                    public void onLongPressStartEvent() {
                        int position = viewHolder.getAdapterPosition();
                        CementModel rawModel = adapter.getModel(position);
                        if (rawModel == null) {
                            return;
                        }
                        FrameFilterModel frameFilterModel = (FrameFilterModel) rawModel;
                        viewHolder.cover.setScaleX(1.2f);
                        viewHolder.cover.setScaleY(1.2f);
                        viewHolder.name.setSelected(true);
                        startUseFilter(frameFilterModel.getFrameFilter());
                    }

                    @Override
                    public void onLongPressEndEvent() {
                        int position = viewHolder.getAdapterPosition();
                        CementModel rawModel = adapter.getModel(position);
                        if (rawModel == null) {
                            return;
                        }
                        FrameFilterModel frameFilterModel = (FrameFilterModel) rawModel;
                        viewHolder.cover.setScaleX(1.0f);
                        viewHolder.cover.setScaleY(1.0f);
                        viewHolder.name.setSelected(false);
                        endUseFilter(frameFilterModel.getFrameFilter());
                    }

                    @Override
                    public void onClickEvent() {
                        Toaster.show("特效需长按选择生效");
                    }
                });
            }

            @Nullable
            @Override
            public View onBind(@NonNull FrameFilterModel.ViewHolder viewHolder) {
                return viewHolder.cover;
            }
        });
        keyframes.setLayoutManager(new LinearLayoutManager(rootView.getContext(), OrientationHelper.HORIZONTAL, false));
        keyframes.addItemDecoration(new LinearPaddingItemDecoration(0, 0, 0));
        SimpleCementAdapter keysAdapter = new SimpleCementAdapter();
        keysAdapter.updateDataList(tranForKeys(frames));
        keyframes.setAdapter(keysAdapter);

        specialFilter.setLayoutManager(new LinearLayoutManager(rootView.getContext(), OrientationHelper.HORIZONTAL, false));
        specialFilter.addItemDecoration(new LinearPaddingItemDecoration(UIUtils.getPixels(26), 0, UIUtils.getPixels(20)));
        specialFilter.setItemAnimator(null);
        specialFilter.setAdapter(simpleAdapter);
    }

    private void initEvent() {

        this.specialCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (useSpecialFilter()) {
                    showVerifyDialog();
                } else {
                    cancel();
                }
            }
        });

        this.specialAffirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (specialPanelListener != null) {
                    specialPanelListener.onHide();
                }
            }
        });

        filterSeekView.setSeekListener(new FilterSeekView.SeekListener() {
            @Override
            public void onSeek(float progress) {
                // 性能原因暂定停用 实时seek预览
                // 问张萌
                //processPlayer.seekStatus(true);
                // 在倒放模式  进度需要反转
                //progress = getRealprogress(progress);

            }

            @Override
            public void onFinish(float progress) {
                currentSelectFrameFilter = null;
                progress = getRealprogress(progress);
                processPlayer.seekStatus(true);
                processPlayer.seekVideo((long) (progress * video.length), true);
                processPlayer.seekStatus(false);
                // 时间特效模式  seek完 直接播放  不需要暂定
                //                if (!isTimeFilterSelected) {
                //                    videoPlayIcon.setVisibility(View.VISIBLE);
                //                } else {
                //                    videoPlayIcon.setVisibility(View.GONE);
                //                }
            }
        });

        videoPlayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onVideoViewOnClick();
            }
        });
        rollbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 撤销 删除滤镜链表中最后一个
                FrameFilter frameFilter = specialDataControl.doRollback();
                if (frameFilter != null) {
                    processPlayer.seekVideo(frameFilter.getStartTime(), true);
                }
                if (specialDataControl.getUsedFrameFilterSize() <= 0) {
                    rollbackBtn.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 滤镜特效tab 选中
     */
    private void onFrameTabSelect() {
        if (specialDataControl.getUsedFrameFilterSize() > 0) {
            rollbackBtn.setVisibility(View.VISIBLE);
        } else {
            rollbackBtn.setVisibility(View.GONE);
        }
        filterSeekView.setModel(FilterSeekView.MODE_NORMAL, 0L, false, false);
        simpleAdapter.clearData();
        framesSpecialLineView.setSelected(true);
        simpleAdapter.updateDataList(framesModels);
    }

    private List<CementModel<?>> tranForKeys(@NonNull List<Bitmap> bitmaps) {
        List<CementModel<?>> models = new ArrayList<>();

        if (bitmaps.isEmpty()) {
            return models;
        }
        int size = bitmaps.size();
        for (int i = 0; i < size; i++) {
            models.add(new KeysModel(bitmaps.get(i)));
        }
        return models;
    }

    private List<CementModel<?>> tranForFrames(@NonNull List<FrameFilter> frameFilters) {
        List<CementModel<?>> models = new ArrayList<>();

        if (frameFilters.isEmpty()) {
            return models;
        }
        for (FrameFilter frameFilter : frameFilters) {
            models.add(new FrameFilterModel(frameFilter));
        }
        return models;
    }

    /**
     * 开始特效滤镜
     *
     * @param frameFilter
     */
    private void startUseFilter(FrameFilter frameFilter) {
        if (currentPlayingTime >= video.length) {
            return;
        }
        // 添加过程中 不允许撤销操作  防止滤镜链表发生排序错误
        this.rollbackBtn.setVisibility(View.GONE);
        videoPlayIcon.setVisibility(View.GONE);
        // 创建需要新加的滤镜 更新到滤镜链表中
        currentSelectFrameFilter = FrameFilter.clone(frameFilter);
        currentSelectFrameFilter.setStartTime(currentPlayingTime);
        currentSelectFrameFilter.setEndTime(currentSelectFrameFilter.getStartTime() + 1);
        specialDataControl.insertFrameFilter(currentSelectFrameFilter);
        // 设置给播放器的滤镜 开始时间为当前起始时间  结束时间为视频时长  在结束时 重新设置时间片
        specialDataControl.syncSingleFilter(currentSelectFrameFilter.getBasicFilter().mFilterId, currentPlayingTime, video.length);
        this.processPlayer.focusPlayVideo();
    }

    /**
     * 结束特效滤镜
     *
     * @param frameFilter
     */
    private void endUseFilter(FrameFilter frameFilter) {
        if (specialDataControl.getUsedFrameFilterSize() > 0) {
            this.rollbackBtn.setVisibility(View.VISIBLE);
        }
        processPlayer.pause();
    }

    public void show(final View videoView, boolean isVertical, Animator.AnimatorListener listener) {
        isInFilterMode = true;
        SpecialFilterAnimationUtils.showAnimation(specialBottomView);
        specialTopView.setVisibility(View.VISIBLE);
        processPlayer.setNeedAutoPlay(false);
        processPlayer.setLoopBack(false);
        processPlayer.seekVideo(currentPlayingTime, true);
        videoPlayIcon.setVisibility(View.VISIBLE);
        specialPanelView.setVisibility(View.VISIBLE);
        filterSeekView.update(currentPlayingTime);
        showVideo(videoView, isVertical, listener);
    }

    private void showVideo(final View videoView, boolean isVertical, final Animator.AnimatorListener listener) {
        if (isVertical) {
            MomoMainThreadExecutor.post(TAG, new Runnable() {
                @Override
                public void run() {
                    if (show == null) {
                        final float hintTextY = hintTextView.getY();
                        final float topH = (float) specialTopView.getHeight() + specialTopView.getY();
                        float scaledHeight = hintTextY - topH - UIUtils.getPixels(12);
                        float rate = scaledHeight / (float) videoView.getHeight();
                        videoView.setPivotY((topH - videoView.getY()) / (1f - rate));//topH / (1f - rate)
                        videoView.setPivotX(videoView.getWidth() / 2);
                        show = SpecialFilterAnimationUtils.showScaleAnimation(videoView, 1f, rate);
                        show.start();
                        hide = SpecialFilterAnimationUtils.showScaleAnimation(videoView, rate, 1f);
                        hide.addListener(listener);
                        setVideoPlayPosition(scaledHeight);
                    } else {
                        show.start();
                    }
                }
            });
        } else {
            MomoMainThreadExecutor.postDelayed(TAG, new Runnable() {
                @Override
                public void run() {
                    if (show == null) {
                        final float hintTextY = hintTextView.getY();
                        final float topH = specialTopView.getHeight();
                        float scaledHeight = hintTextY - topH;
                        float videoY = videoView.getY();
                        float videoHeight = videoView.getHeight();
                        float rate = scaledHeight / (float) videoView.getHeight();
                        boolean shouldScale = videoHeight > scaledHeight;
                        float move = videoY - (scaledHeight / 2f - videoHeight / 2 + topH);
                        if (shouldScale) {
                            show = SpecialFilterAnimationUtils.showTranslateAndScaleAnimation(videoView, 0, -move, 1f, rate);
                            hide = SpecialFilterAnimationUtils.showTranslateAndScaleAnimation(videoView, -move, 0, rate, 1f);
                        } else {
                            show = SpecialFilterAnimationUtils.showTranslateAnimation(videoView, 0, -move);
                            hide = SpecialFilterAnimationUtils.showTranslateAnimation(videoView, -move, 0);
                        }
                        show.start();
                        hide.addListener(listener);
                        setVideoPlayPosition(scaledHeight);
                    } else {
                        show.start();
                    }

                }
            }, 50);
        }
    }

    private void setVideoPlayPosition(float finalHeight) {
        float y = specialTopView.getHeight() + finalHeight / 2 - videoPlayLayout.getHeight() / 2;
        videoPlayLayout.setY(y);
    }

    public void hide() {
        specialTopView.setVisibility(View.GONE);
        SpecialFilterAnimationUtils.hideAnimation(specialBottomView, specialPanelView);
        if (hide != null) {
            hide.start();
        }
        //specialPanelView.setVisibility(View.GONE);
        videoPlayIcon.setVisibility(View.GONE);
        isInFilterMode = false;
    }

    public void onVideoViewOnClick() {
        currentSelectFrameFilter = null;
        if (processPlayer.isPlaying()) {
            processPlayer.pause();
            videoPlayIcon.setVisibility(View.VISIBLE);
        } else {
            videoPlayIcon.setVisibility(View.GONE);
            // 播放到末尾  再点击播放 重头开始播
            if (currentPlayingTime == video.length) {
                processPlayer.seekVideo(0L, false);
            } else {
                processPlayer.focusPlayVideo();
            }
        }
    }

    public boolean isInFilterMode() {
        return isInFilterMode;
    }

    public void setOnSpecialPanelListener(SpecialPanelListener specialPanelListener) {
        this.specialPanelListener = specialPanelListener;
    }

    public void onProcessFinish() {
        videoPlayIcon.setVisibility(View.VISIBLE);
    }

    /**
     * 播放过程的进度更新
     *
     * @param progress
     */
    public void onProcessProgress(float progress) {
        if (progress == 1.0F) {
            videoPlayIcon.setVisibility(View.VISIBLE);
        }
        updateAll(progress);
    }

    private void updateAll(float progress) {
        currentPlayingTime = (long) (progress * video.length);
        if (currentSelectFrameFilter != null) {
            // 需要实时更新当前使用的滤镜 结束时间
            currentSelectFrameFilter.setEndTime(currentPlayingTime);
            // 实时排序滤镜链表  保证没有重叠的滤镜时间片
            specialDataControl.updateFilterList(currentSelectFrameFilter);
        }
        this.filterSeekView.update(getRealprogress(progress));
    }

    /**
     * 播放暂停
     */
    public void onPlayingPaused() {
        videoPlayIcon.setVisibility(View.VISIBLE);
        // 结束后 需要同步底层滤镜列表中的时间片
        specialDataControl.syncGroupFilter();
        // 需要清空当前记录的fitler
        currentSelectFrameFilter = null;
    }

    /**
     * 如果是倒放模式  需要转换
     *
     * @param progress
     * @return
     */
    private float getRealprogress(float progress) {
        return progress;
    }

    /**
     * 是否使用过特效滤镜
     *
     * @return
     */
    public boolean useSpecialFilter() {
        return specialDataControl.getUsedFrameFilterSize() > 0;
    }

    public boolean onBackPressed() {
        if (useSpecialFilter()) {
            showVerifyDialog();
            return true;
        }
        return false;
    }

    public String getUsedFilterInfo() {
        LinkedList<FrameFilter> frameFilters = specialDataControl.getUsedFrameFilters();
        List<String> tempFilters = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        for (Iterator<FrameFilter> iter = frameFilters.iterator(); iter.hasNext(); ) {
            FrameFilter frameFilter = iter.next();
            if (tempFilters.contains(frameFilter.getTag())) {
                continue;
            }
            tempFilters.add(frameFilter.getTag());
            sb.append(frameFilter.getTag()).append(",");
        }
        sb.append("100");
        tempFilters.clear();
        return sb.toString();
    }

    private void showVerifyDialog() {
        if (baseFragment.getActivity() == null) {
            return;
        }
        final AlertDialog mCloseDialog = new AlertDialog
                .Builder(baseFragment.getActivity())
                .setTitle("提示")
                .setMessage("是否放弃特效滤镜效果？")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        cancel();
                    }
                }).setNegativeButton("取消", null)
                .create();
        baseFragment.showDialog(mCloseDialog);
    }

    private void cancel() {
        currentPlayingTime = 0L;
        // 取消操作
        // 清空滤镜时间片
        specialDataControl.clearUsedFrameFilter();
        specialTopView.setVisibility(View.GONE);
        if (specialPanelListener != null) {
            specialPanelListener.onHide();
        }
    }

    public void destory() {
        MomoMainThreadExecutor.cancelAllRunnables(TAG);
    }

    public interface SpecialPanelListener {
        void onHide();
    }

}
