package com.mm.sdkdemo.widget;

import android.annotation.TargetApi;
import android.content.Context;
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

import com.immomo.mmutil.log.Log4Android;
import com.immomo.mmutil.task.MomoTaskExecutor;
import com.mm.mediasdk.utils.UIUtils;
import com.mm.sdkdemo.R;
import com.mm.sdkdemo.api.MoApi;
import com.mm.sdkdemo.recorder.adapter.DynamicStickerListAdapter;
import com.mm.sdkdemo.recorder.sticker.DynamicSticker;
import com.mm.sdkdemo.recorder.sticker.StickerManager;
import com.mm.sdkdemo.widget.decoration.SpaceItemDecoration;

import java.util.ArrayList;

/**
 * Created by zhutao on 2017/6/14.
 */
public class DynamicStickerPanel extends FrameLayout {
    private static final String PREF_KEY_UPDATE_MOMENT_STICKER_TIME = "update_moment_sticker_time";

    private ArrayList<DynamicSticker> stickerList = new ArrayList<>();
    private IndeterminateDrawable progressDrawable;
    private View progressView;
    private TextView errorTipView;
    private View topCoverView;

    DynamicStickerListAdapter adapter;

    public DynamicStickerPanel(Context context) {
        super(context);
        init(context);
    }

    public DynamicStickerPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DynamicStickerPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DynamicStickerPanel(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private int dp2px(float dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()));
    }

    private void init(Context context) {
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
        recyclerView.addItemDecoration(new SpaceItemDecoration(dp2px(10), dp2px(20)));
        recyclerView.setVerticalFadingEdgeEnabled(false);
        recyclerView.setHorizontalFadingEdgeEnabled(false);

        adapter = new DynamicStickerListAdapter(stickerList, recyclerView);
        adapter.setOnClickListener(new DynamicStickerListAdapter.OnClickListener() {
            @Override
            public void onClick(View view, DynamicStickerListAdapter.ViewHolder vh, int position) {
                DynamicSticker sticker = adapter.getItem(position);
                if (sticker == null) {
                    return;
                }
                if (StickerManager.isStickerDownloading(sticker)) {
                    return;
                }
                //为显示进度条添加
                adapter.checkDownload = true;
                //传递点击事件
                if (mOnStickerPanelListener != null) {
                    mOnStickerPanelListener.onChooseSticker(view, vh, position, sticker);
                }
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

        topCoverView = new View(context);
        topCoverView.setBackgroundResource(R.drawable.bg_gradient_top);
        topCoverView.setVisibility(INVISIBLE);
        LayoutParams vp = new LayoutParams(LayoutParams.MATCH_PARENT, UIUtils.getPixels(120));
        lp.gravity = Gravity.TOP;
        addView(topCoverView, vp);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    topCoverView.setVisibility(VISIBLE);
                } else {
                    topCoverView.setVisibility(INVISIBLE);
                }
            }
        });

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

    private void onLoadSuccess(ArrayList<DynamicSticker> list) {
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

    private class LoadStickerTask extends MomoTaskExecutor.Task<Object, Object, ArrayList<DynamicSticker>> {
        @Override
        protected ArrayList<DynamicSticker> executeTask(Object... params) throws Exception {
            ArrayList<DynamicSticker> list = new ArrayList<>();
            //贴纸过期或者缓存无效时，需要重新拉取一次数据
            Log4Android.getInstance().i("tang-----过期或者没有缓存 " + list.size());
            MoApi.getDynamicStickerList(list);
            return list;
        }

        @Override
        protected void onTaskSuccess(ArrayList<DynamicSticker> list) {
            super.onTaskSuccess(list);
            onLoadSuccess(list);
        }

        @Override
        protected void onTaskError(Exception e) {
            super.onTaskError(e);
            onLoadFailed();
        }
    }

    public void notifyItem(int position) {
        adapter.notifyItemChanged(position);
    }

    private OnStickerPanelListener mOnStickerPanelListener;

    public void setOnStickerPanelListener(OnStickerPanelListener pOnStickerPanelListener) {
        mOnStickerPanelListener = pOnStickerPanelListener;
    }

    public interface OnStickerPanelListener {
        void onCloseClicked();

        void onChooseSticker(View view, DynamicStickerListAdapter.ViewHolder vh, int position, DynamicSticker sticker);
    }
}
