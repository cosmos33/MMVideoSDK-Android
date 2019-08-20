package com.mm.recorduisdk.recorder.editor.image_composition_video.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.mm.mediasdk.utils.UIUtils;
import com.mm.recorduisdk.R;
import com.mm.recorduisdk.base.cement.CementModel;
import com.mm.recorduisdk.base.cement.SimpleCementAdapter;
import com.mm.recorduisdk.base.cement.eventhook.OnClickEventHook;
import com.mm.recorduisdk.recorder.editor.image_composition_video.model.OrderItemModel;
import com.mm.recorduisdk.recorder.editor.image_composition_video.wiget.LivePhotoItemTouchHelpCallback;
import com.mm.recorduisdk.recorder.model.Photo;
import com.mm.recorduisdk.widget.decoration.LinearPaddingItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderFragment extends BaseLivePhotoFragment {

    private static int MIN_COUNT = 2;
    private static int MAX_COUNT = 9;

    private List<CementModel<?>> orderModels;
    private SimpleCementAdapter adapter;
    private RecyclerView recyclerView;
    private List<Photo> photos;
    private LivePhotoItemTouchHelpCallback touchHelpCallback;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        photos = mPresenter.getLiveImageList();
        orderModels = getOrderModels(photos);
    }

    @Override
    protected int getLayout() {
        return R.layout.live_photo_order_page_layout;
    }

    @Override
    protected void initViews(View contentView) {
        recyclerView = contentView.findViewById(R.id.order_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), OrientationHelper.HORIZONTAL, false));
        recyclerView.addItemDecoration(new LinearPaddingItemDecoration(UIUtils.getPixels(28), 0, UIUtils.getPixels(8)));
        recyclerView.setItemAnimator(null);

        touchHelpCallback = new LivePhotoItemTouchHelpCallback(new LivePhotoItemTouchHelpCallback.OnItemTouchCallbackListener() {

            @Override
            public boolean onMove(int srcPosition, int targetPosition) {
                Collections.swap(orderModels, srcPosition, targetPosition);
                mPresenter.swapPhotoList(srcPosition, targetPosition);
                updateAdapter();
                return true;
            }
        });
        ItemTouchHelper touchHelper = new ItemTouchHelper(touchHelpCallback);
        touchHelper.attachToRecyclerView(recyclerView);
        adapter = new SimpleCementAdapter();

        adapter.addEventHook(new OnClickEventHook<OrderItemModel.ViewHolder>(OrderItemModel.ViewHolder.class) {

            @Override
            public void onClick(@NonNull View view, @NonNull final OrderItemModel.ViewHolder viewHolder, int position, @NonNull CementModel rawModel) {

                OrderItemModel orderItemModel = (OrderItemModel) rawModel;
                Photo photo = orderItemModel.getPhoto();
                orderModels.remove(orderItemModel);
                mPresenter.deletePhoto(photo);
                updateAdapter();
            }

            @Nullable
            @Override
            public View onBind(@NonNull OrderItemModel.ViewHolder viewHolder) {
                return viewHolder.delete;
            }
        });

    }

    private void updateAdapter() {
        boolean showDelete;
        if (orderModels.size() <= 1) {
            showDelete = false;
        } else {
            showDelete = true;
        }
        for (CementModel<?> cementModel : orderModels) {
            if (cementModel instanceof OrderItemModel) {
                ((OrderItemModel) cementModel).showDeleteIcon(showDelete);
            }
        }
        if(adapter!=null){
            adapter.updateDataList(orderModels);
        }
    }


    @Override
    protected void onLoad() {
        recyclerView.setAdapter(adapter);
        updateAdapter();
    }

    private List<CementModel<?>> getOrderModels(@NonNull List<Photo> photos) {
        List<CementModel<?>> models = new ArrayList<>();

        if (photos.isEmpty()) {
            return models;
        }
        for (Photo photo : photos) {
            models.add(new OrderItemModel(photo));
        }
        return models;
    }

    @Override
    public void onChange() {
        photos = mPresenter.getLiveImageList();
        orderModels = getOrderModels(photos);
        updateAdapter();
    }

}
