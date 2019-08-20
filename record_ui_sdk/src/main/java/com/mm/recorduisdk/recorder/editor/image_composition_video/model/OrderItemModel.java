package com.mm.recorduisdk.recorder.editor.image_composition_video.model;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.request.RequestOptions;
import com.mm.recorduisdk.R;
import com.mm.recorduisdk.base.cement.CementAdapter;
import com.mm.recorduisdk.base.cement.CementModel;
import com.mm.recorduisdk.base.cement.CementViewHolder;
import com.mm.base_business.glide.ImageLoaderX;
import com.mm.recorduisdk.recorder.model.Photo;


public class OrderItemModel extends CementModel<OrderItemModel.ViewHolder> {


    @NonNull
    private Photo photo;
    public boolean showDeleteIcon;

    public OrderItemModel(@NonNull Photo photo) {
        this.photo = photo;
    }


    @NonNull
    public Photo getPhoto() {
        return photo;
    }

    public void showDeleteIcon(boolean showDeleteIcon){
        this.showDeleteIcon = showDeleteIcon;
    }


    @Override
    public boolean isContentTheSame(@NonNull CementModel<?> item) {
        return false;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.live_photo_order_fuction_item_layout;
    }

    @NonNull
    @Override
    public CementAdapter.IViewHolderCreator<ViewHolder> getViewHolderCreator() {
        return new CementAdapter.IViewHolderCreator<ViewHolder>() {
            @NonNull
            @Override
            public ViewHolder create(@NonNull View view) {
                return new ViewHolder(view);
            }
        };
    }


    @Override
    public void bindData(@NonNull ViewHolder holder) {
        super.bindData(holder);
        if(showDeleteIcon){
            holder.delete.setVisibility(View.VISIBLE);
        } else {
            holder.delete.setVisibility(View.INVISIBLE);
        }
        ImageLoaderX.load(photo.tempPath)
//                .type(ImageType.IMAGE_TYPE_LOCAL_PATH)
                .requestOptions(new RequestOptions().centerCrop())
                .into(holder.image);
    }

    public class ViewHolder extends CementViewHolder {


        public View delete;
        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            delete = itemView.findViewById(R.id.delete_iv);
            image = itemView.findViewById(R.id.order_image);
        }
    }
}
