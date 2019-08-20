package com.mm.recorduisdk.recorder.editor.image_composition_video.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mm.mediasdk.utils.UIUtils;
import com.mm.recorduisdk.R;
import com.mm.recorduisdk.base.cement.CementModel;
import com.mm.recorduisdk.base.cement.SimpleCementAdapter;
import com.mm.recorduisdk.base.cement.eventhook.OnClickEventHook;
import com.mm.recorduisdk.recorder.editor.image_composition_video.bean.LiveAnimate;
import com.mm.recorduisdk.recorder.editor.image_composition_video.model.AnimateItemModel;
import com.mm.recorduisdk.widget.decoration.LinearPaddingItemDecoration;

import java.util.ArrayList;
import java.util.List;


public class AnimateFragment extends BaseLivePhotoFragment {

    private RecyclerView recyclerView;
    private SimpleCementAdapter adapter;
    private List<CementModel<?>> animateModels;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    private List<CementModel<?>> getAnimateModels() {
        List<LiveAnimate> animates = mPresenter.getLiveAnimates();
        List<CementModel<?>> models = new ArrayList<>(animates.size());
        for (LiveAnimate liveAnimate : animates) {
            models.add(new AnimateItemModel(liveAnimate));
        }
        return models;
    }

    @Override
    protected void onChange() {
        if (animateModels == null) {
            return;
        }
        LiveAnimate currentAnimate = mPresenter.getCurrentAnimate();
        for (CementModel<?> model : animateModels) {
            LiveAnimate liveAnimate = ((AnimateItemModel) model).getLiveAnimate();
            if (liveAnimate.getAnimateType() == currentAnimate.getAnimateType()) {
                liveAnimate.setSelect(true);
            } else {
                liveAnimate.setSelect(false);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected int getLayout() {
        return R.layout.live_photo_animate_page_layout;
    }

    @Override
    protected void initViews(View contentView) {
        animateModels = getAnimateModels();
        initSelectModel();
        recyclerView = contentView.findViewById(R.id.animate_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), OrientationHelper.HORIZONTAL, false));
        recyclerView.addItemDecoration(new LinearPaddingItemDecoration(UIUtils.getPixels(28), 0, UIUtils.getPixels(8)));
        recyclerView.setItemAnimator(null);
        adapter = new SimpleCementAdapter();

        adapter.addEventHook(new OnClickEventHook<AnimateItemModel.ViewHolder>(AnimateItemModel.ViewHolder.class) {
            @Override
            public void onClick(@NonNull View view, @NonNull final AnimateItemModel.ViewHolder viewHolder, int position, @NonNull CementModel rawModel) {
                AnimateItemModel animateItemModel = ((AnimateItemModel) rawModel);
                LiveAnimate liveAnimate = animateItemModel.getLiveAnimate();
                if (liveAnimate.isSelect()) {
                    return;
                }
                updateAnimate(liveAnimate);
            }

            @Nullable
            @Override
            public View onBind(@NonNull AnimateItemModel.ViewHolder viewHolder) {
                return viewHolder.itemView;
            }
        });


        recyclerView.setAdapter(adapter);
        adapter.updateDataList(animateModels);
    }

    private void initSelectModel() {
        int defaultAnimateType = LiveAnimate.AnimateType.ANIMATE_HORIZONTAL;
        for (CementModel<?> model : animateModels) {
            LiveAnimate liveAnimate = ((AnimateItemModel) model).getLiveAnimate();
            if (liveAnimate.getAnimateType() == defaultAnimateType) {
                liveAnimate.setSelect(true);
            } else {
                liveAnimate.setSelect(false);
            }
        }
    }

    private void updateAnimate(LiveAnimate liveAnimate) {
        for (CementModel model : animateModels) {
            ((AnimateItemModel) model).getLiveAnimate().setSelect(false);
        }
        liveAnimate.setSelect(true);
        mPresenter.setLiveAnimate(liveAnimate);
        adapter.notifyDataSetChanged();

    }


    @Override
    protected void onLoad() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
