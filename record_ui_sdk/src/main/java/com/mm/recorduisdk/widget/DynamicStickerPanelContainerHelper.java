package com.mm.recorduisdk.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mm.mediasdk.utils.UIUtils;
import com.mm.mmutil.task.MomoMainThreadExecutor;
import com.mm.recorduisdk.R;
import com.mm.recorduisdk.base.cement.CementModel;
import com.mm.recorduisdk.base.cement.SimpleCementAdapter;
import com.mm.recorduisdk.recorder.editor.player.IProcessPresenter;
import com.mm.recorduisdk.recorder.specialfilter.SpecialFilterAnimationUtils;
import com.mm.recorduisdk.recorder.specialfilter.model.KeysModel;
import com.mm.recorduisdk.recorder.sticker.StickerEntity;
import com.mm.recorduisdk.widget.decoration.LinearPaddingItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2019/6/17.
 *
 * @author jianxi[mabeijianxi@gmail.com]
 */
public class DynamicStickerPanelContainerHelper {
    @NonNull
    private RecyclerView mKeyframes;
    private SimpleCementAdapter mKeysAdapter;
    private StickerSeekView mStickerSeekView;
    private IProcessPresenter mProcessPlayer;
    private float mCurrentEndProgress = 1;
    private float mCurrentStartProgress = 0;
    private View mStickerPanelContainer;
    private float mCurrentProgress;
    private StickerEntity mCurrentEditSticker;
    private View mFlSeekRoot;
    private View mStickerPanelTopLayout;
    private ObjectAnimator show;
    private static final String TAG = "DynamicStickerPanelContainerHelper";

    private ObjectAnimator hide;
    private View mStickerPanelAffirmBtn;
    private View mStickerPanelCancelBtn;
    private List<Integer> mOnceShowAddStickers = new ArrayList<>();
    private OnCancelListener mOnCancelListener;

    public void init(View stickerPanelContainer, long videoLength, IProcessPresenter processPlayer) {
        mStickerPanelContainer = stickerPanelContainer;
        mStickerSeekView = stickerPanelContainer.findViewById(R.id.sticker_seekview);


        mStickerPanelAffirmBtn = stickerPanelContainer.findViewById(R.id.sticker_panel__affirm_btn);
        mStickerPanelCancelBtn = stickerPanelContainer.findViewById(R.id.sticker_panel__cancel_btn);
        mKeyframes = stickerPanelContainer.findViewById(R.id.keyframes);
        mFlSeekRoot = stickerPanelContainer.findViewById(R.id.fl_seek_root);
        mStickerPanelTopLayout = stickerPanelContainer.findViewById(R.id.sticker_panel_top_layout);
        mKeyframes.setLayoutManager(new LinearLayoutManager(stickerPanelContainer.getContext(), OrientationHelper.HORIZONTAL, false));
        mKeyframes.addItemDecoration(new LinearPaddingItemDecoration(0, 0, 0));
        mKeysAdapter = new SimpleCementAdapter();
        mProcessPlayer = processPlayer;
        mKeyframes.setAdapter(mKeysAdapter);

        initEvents();
    }

    private void initEvents() {
        mStickerSeekView.setStartSeekListener(new StickerSeekView.OnSeekListener() {

            @Override
            public void onSeekFinish(float progress) {
                mCurrentStartProgress = progress;
                long currentRealVideoTime = mProcessPlayer.getCurrentRealVideoTime();
                mProcessPlayer.seekStatus(true);
                mProcessPlayer.seekVideo((long) (progress * currentRealVideoTime), false);
                mProcessPlayer.seekStatus(false);

                if (mCurrentEditSticker != null) {
                    mCurrentEditSticker.setStartShowTime((long) (mCurrentStartProgress * currentRealVideoTime));
                    mCurrentEditSticker.setEndShowTime((long) (mCurrentEndProgress * currentRealVideoTime));
                }
                mProcessPlayer.setStickerTimeRange((int) mCurrentEditSticker.getId(), mCurrentEditSticker.getStartShowTime(), mCurrentEditSticker.getEndShowTime());
            }
        });
        mStickerSeekView.setEndFilterSeekListener(new StickerSeekView.OnSeekListener() {
            @Override
            public void onSeekFinish(float progress) {
                mCurrentEndProgress = progress;
                if (mCurrentEditSticker != null) {
                    long currentRealVideoTime = mProcessPlayer.getCurrentRealVideoTime();

                    mCurrentEditSticker.setStartShowTime((long) (mCurrentStartProgress * currentRealVideoTime));
                    mCurrentEditSticker.setEndShowTime((long) (mCurrentEndProgress * currentRealVideoTime));
                }

                mProcessPlayer.setStickerTimeRange((int) mCurrentEditSticker.getId(), mCurrentEditSticker.getStartShowTime(), mCurrentEditSticker.getEndShowTime());
            }
        });
        mStickerSeekView.setProgressSeekListener(new StickerSeekView.OnSeekListener() {
            @Override
            public void onSeekFinish(float progress) {
                long currentRealVideoTime = mProcessPlayer.getCurrentRealVideoTime();

                mProcessPlayer.pause();
                mProcessPlayer.seekStatus(true);
                mProcessPlayer.seekVideo((long) (progress * currentRealVideoTime), false);
                mProcessPlayer.seekStatus(false);
            }
        });

        mStickerPanelAffirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mStickerPanelCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnCancelListener != null) {
                    mOnCancelListener.onCancel(mOnceShowAddStickers);
                }
                dismiss();
            }
        });
    }

    public void updateCurrentEditSticker(StickerEntity currentEditSticker, boolean isNeedAdd) {
        mCurrentEditSticker = currentEditSticker;
        if (mStickerSeekView.getVisibility() != View.VISIBLE) {
            mStickerSeekView.setVisibility(View.VISIBLE);
        }
        long currentRealVideoTime = mProcessPlayer.getCurrentRealVideoTime();

        mCurrentStartProgress = mCurrentEditSticker.getStartShowTime() == null ? 0 : (mCurrentEditSticker.getStartShowTime() * 1.0f) / currentRealVideoTime;
        mCurrentEndProgress = mCurrentEditSticker.getEndShowTime() == null ? 1 : (mCurrentEditSticker.getEndShowTime() * 1.0f) / currentRealVideoTime;

        mStickerSeekView.updateStartLocation(mCurrentStartProgress);
        mStickerSeekView.updateEndTimeLocation(mCurrentEndProgress);

        if (mCurrentProgress < mCurrentStartProgress || mCurrentProgress > mCurrentEndProgress) {

            mProcessPlayer.seekVideo((long) (mCurrentStartProgress * currentRealVideoTime), false);
        }

        if (isNeedAdd) {
            mOnceShowAddStickers.add((int) currentEditSticker.getId());
        }
    }

    public void onProcessProgress(float progress) {
        if (progress > 1) {
            progress = 1;
        }
        if (mCurrentProgress == progress) {
            return;
        }
        long currentRealVideoTime = mProcessPlayer.getCurrentRealVideoTime();

        if (mCurrentEndProgress <= progress) {
            progress = mCurrentEndProgress;
            updateProgress(progress);
            mProcessPlayer.seekVideo((long) (mCurrentStartProgress * currentRealVideoTime), false);
            return;
        }
        if (progress >= 1) {
            mProcessPlayer.seekVideo((long) (mCurrentStartProgress * currentRealVideoTime), false);
        }
        updateProgress(progress);
    }

    private void updateProgress(float progress) {
        mCurrentProgress = progress;
        if (mStickerSeekView.getVisibility() == View.VISIBLE) {
            mStickerSeekView.updateProgressLocation(progress);
        }
    }

    public boolean isShowing() {
        return mStickerPanelContainer.getVisibility() == View.VISIBLE;
    }

    public void show(final View videoView, boolean isVertical, Animator.AnimatorListener dismissListener) {
        SpecialFilterAnimationUtils.showAnimation(mStickerPanelContainer);
        mStickerPanelTopLayout.setVisibility(View.VISIBLE);
        mStickerPanelContainer.setVisibility(View.VISIBLE);
        mProcessPlayer.setLoopBack(false);
        long currentRealVideoTime = mProcessPlayer.getCurrentRealVideoTime();
        mStickerSeekView.initLength(currentRealVideoTime);
        showVideo(videoView, isVertical, dismissListener);
    }

    private void showVideo(final View videoView, boolean isVertical, final Animator.AnimatorListener dismissListener) {
        if (isVertical) {
            MomoMainThreadExecutor.post(TAG, new Runnable() {
                @Override
                public void run() {
                    if (show == null) {
                        final float seekRootY = mFlSeekRoot.getY();
                        final float topH = (float) mStickerPanelTopLayout.getHeight() + mStickerPanelTopLayout.getY();
                        float scaledHeight = seekRootY - topH - UIUtils.getPixels(12);
                        float rate = scaledHeight / (float) videoView.getHeight();
                        videoView.setPivotY((topH - videoView.getY()) / (1f - rate));//topH / (1f - rate)
                        videoView.setPivotX(videoView.getWidth() / 2);
                        show = SpecialFilterAnimationUtils.showScaleAnimation(videoView, 1f, rate);
                        show.start();
                        hide = SpecialFilterAnimationUtils.showScaleAnimation(videoView, rate, 1f);
                        hide.addListener(dismissListener);
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
                        final float seekRootY = mFlSeekRoot.getY();
                        final float topH = mStickerPanelTopLayout.getHeight();
                        float scaledHeight = seekRootY - topH;
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
                        hide.addListener(dismissListener);
                    } else {
                        show.start();
                    }

                }
            }, 50);
        }
    }

    public void dismiss() {
        mOnceShowAddStickers.clear();
        mStickerPanelContainer.setVisibility(View.GONE);
        mStickerSeekView.setVisibility(View.GONE);
        mProcessPlayer.setLoopBack(true);
        SpecialFilterAnimationUtils.hideAnimation(mStickerPanelContainer, mStickerPanelTopLayout);
        if (hide != null) {
            hide.start();
        }
    }


    public void bindVideoFrames(List<Bitmap> frames) {
        mKeysAdapter.updateDataList(tranForKeys(frames));
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

    public void release() {
        MomoMainThreadExecutor.cancelAllRunnables(TAG);

    }

    public OnCancelListener getOnCancelListener() {
        return mOnCancelListener;
    }

    public void setOnCancelListener(OnCancelListener onCancelListener) {
        this.mOnCancelListener = onCancelListener;
    }

    public void onStickerRemoved(long stickerId) {
        if (mCurrentEditSticker != null && mCurrentEditSticker.getId() == stickerId) {
            mCurrentEditSticker = null;
            if (mStickerSeekView.getVisibility() == View.VISIBLE) {
                mStickerSeekView.setVisibility(View.GONE);
            }
            mCurrentStartProgress = 0;
            mCurrentEndProgress = 1;

            mStickerSeekView.updateStartLocation(mCurrentStartProgress);
            mStickerSeekView.updateEndTimeLocation(mCurrentEndProgress);

            mOnceShowAddStickers.remove(new Integer((int) stickerId));
        }
    }

    public interface OnCancelListener {
        void onCancel(List<Integer> cancelList);
    }
}
