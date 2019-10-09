package com.mm.recorduisdk.widget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mm.base_business.glide.ImageLoaderX;
import com.mm.mmutil.log.Log4Android;
import com.mm.mmutil.task.MomoTaskExecutor;
import com.mm.mmutil.task.ThreadUtils;
import com.mm.recorduisdk.IRecordResourceConfig;
import com.mm.recorduisdk.R;
import com.mm.recorduisdk.RecordUISDK;
import com.mm.recorduisdk.bean.MomentSticker;
import com.mm.recorduisdk.recorder.adapter.MomentStickerListAdapter;
import com.mm.recorduisdk.widget.decoration.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Project momodev
 * Package com.mm.momo.moment.widget
 * Created by tangyuchun on 7/27/16.
 */
public class MomentStickerPanel extends FrameLayout {
    private static final String PREF_KEY_UPDATE_MOMENT_STICKER_TIME = "update_moment_sticker_time";
    private static final long EXPIRE_UPDATE_TIME = 24 * 3600 * 1000;//过期时间 24小时 毫秒

    private List<MomentSticker> stickerList = new ArrayList<>();
    private IndeterminateDrawable progressDrawable;
    private View progressView;
    private TextView errorTipView;

    MomentStickerListAdapter adapter;

    public MomentStickerPanel(Context context) {
        super(context);
        init(context);
    }

    public MomentStickerPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MomentStickerPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MomentStickerPanel(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private int dp2px(float dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()));
    }

    private void init(final Context context) {
        setBackgroundColor(getResources().getColor(R.color.black_overlay));

        ImageView iv = new ImageView(context);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.LEFT;
        iv.setPadding(dp2px(13f), dp2px(16f), dp2px(6f), dp2px(6f));
        iv.setImageResource(R.drawable.ic_moment_close);
        addView(iv, lp);

        RecyclerView recyclerView = new RecyclerView(context);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 4);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new SpaceItemDecoration(dp2px(10)));
        recyclerView.setVerticalFadingEdgeEnabled(false);
        recyclerView.setHorizontalFadingEdgeEnabled(false);

        adapter = new MomentStickerListAdapter(stickerList, recyclerView);
        adapter.setOnClickListener(new MomentStickerListAdapter.OnClickListener() {
            @Override
            public void onClick(final View view, MomentStickerListAdapter.ViewHolder vh, final int position) {
                final MomentSticker sticker = adapter.getItem(position);
                if (sticker == null) {
                    return;
                }
                final int[] location = new int[2];
                view.getLocationOnScreen(location);
                //拿到View的位置，找到图片的位置
                ThreadUtils.execute(ThreadUtils.TYPE_RIGHT_NOW, new Runnable() {
                    @Override
                    public void run() {
                        final Bitmap bitmap = ImageLoaderX.load(sticker.getPic()).loadAsync();
                        if (bitmap == null || bitmap.isRecycled()) {
                            return;
                        }
                        Activity activity = (Activity) context;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int posX = location[0] + ((view.getWidth() - bitmap.getWidth()) / 2);
                                int posY = location[1] + ((view.getHeight() - bitmap.getHeight()) / 2);
                                Log4Android.getInstance().i("tang-----放置位置 " + posX + ":" + posY);
                                if (mOnStickerPanelListener != null) {
                                    mOnStickerPanelListener.onChooseSticker(posX, posY, adapter.getItem(position));
                                }
                            }
                        });
                    }
                });
            }
        });
        recyclerView.setAdapter(adapter);
        LayoutParams recyclerViewLP = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        recyclerViewLP.topMargin = dp2px(44f);
        int margin = dp2px(12f);
        recyclerViewLP.leftMargin = margin;
        recyclerViewLP.rightMargin = margin;
        addView(recyclerView, recyclerViewLP);

        progressDrawable = new IndeterminateDrawable(Color.WHITE, dp2px(3));
        progressView = new View(context);
        progressView.setBackgroundDrawable(progressDrawable);

        int size = dp2px(64f);
        LayoutParams progressLP = new LayoutParams(size, size);
        progressLP.gravity = Gravity.CENTER;
        addView(progressView, progressLP);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnStickerPanelListener) {
                    mOnStickerPanelListener.onCloseClicked();
                }
            }
        });
    }

    public void loadStickerData() {
        if (adapter.getItemCount() > 0) {
            //避免每次都读取
            return;
        }
        showProgress(true);
        if (errorTipView != null) {
            errorTipView.setVisibility(View.GONE);
        }
        MomoTaskExecutor.executeTask(MomoTaskExecutor.EXECUTOR_TYPE_USER, this.hashCode(), new LoadStickerTask());
    }

    private void onLoadSuccess(List<MomentSticker> list) {
        if (errorTipView != null) {
            errorTipView.setVisibility(View.GONE);
        }
        showProgress(false);
        stickerList.clear();
        stickerList.addAll(list);
        adapter.notifyItemRangeInserted(0, list.size());
    }

    private void showProgress(boolean show) {
        if (show) {
            progressView.setVisibility(View.VISIBLE);
            progressDrawable.startProgress();
        } else {
            progressDrawable.stopProgress();
            progressView.setVisibility(View.GONE);
        }
    }

    private void onLoadFailed() {
        if (errorTipView == null) {
            errorTipView = new TextView(getContext());
            errorTipView.setTextColor(Color.WHITE);
            errorTipView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
            errorTipView.setText("加载失败，点击重试");
            errorTipView.setGravity(Gravity.CENTER);

            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            lp.topMargin = dp2px(64f);
            lp.bottomMargin = lp.topMargin;
            addView(errorTipView, lp);
            errorTipView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadStickerData();
                }
            });
        }
        errorTipView.setVisibility(View.VISIBLE);

        showProgress(false);
    }

    private class LoadStickerTask extends MomoTaskExecutor.Task<Object, Object, List<MomentSticker>> {
        @Override
        protected List<MomentSticker> executeTask(Object... params) throws Exception {
            IRecordResourceConfig<List<MomentSticker>> staticStickerListConfig = RecordUISDK.getResourceGetter().getStaticStickerListConfig();
            List<MomentSticker> staticStickerList = null;
            if (staticStickerListConfig != null && staticStickerListConfig.isOpen()) {
                staticStickerList = staticStickerListConfig.getResource();
            }
            if (staticStickerList == null) {
                throw new IllegalArgumentException();
            }
            return staticStickerList;
        }

        @Override
        protected void onTaskSuccess(List<MomentSticker> list) {
            super.onTaskSuccess(list);
            onLoadSuccess(list);
        }

        @Override
        protected void onTaskError(Exception e) {
            super.onTaskError(e);
            onLoadFailed();
        }
    }

    private OnStickerPanelListener mOnStickerPanelListener;

    public void setOnStickerPanelListener(OnStickerPanelListener pOnStickerPanelListener) {
        mOnStickerPanelListener = pOnStickerPanelListener;
    }

    public interface OnStickerPanelListener {
        void onCloseClicked();

        void onChooseSticker(int x, int y, MomentSticker sticker);
    }
}
