package com.mm.recorduisdk.recorder.editor.image_composition_video.model;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mm.recorduisdk.R;
import com.mm.recorduisdk.base.cement.CementAdapter;
import com.mm.recorduisdk.base.cement.CementModel;
import com.mm.recorduisdk.base.cement.CementViewHolder;
import com.mm.recorduisdk.recorder.editor.image_composition_video.bean.LiveAnimate;


public class AnimateItemModel extends CementModel<AnimateItemModel.ViewHolder> {


    @NonNull
    private LiveAnimate liveAnimate;


    public AnimateItemModel(@NonNull LiveAnimate liveAnimate) {
        this.liveAnimate = liveAnimate;
    }

    @NonNull
    public LiveAnimate getLiveAnimate() {
        return liveAnimate;
    }


    @NonNull
    public LiveAnimate getPath() {
        return liveAnimate;
    }


    @Override
    public int getLayoutRes() {
        return R.layout.live_photo_animate_fuction_item_layout;
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
        holder.name.setText(liveAnimate.getName());
        holder.image.setImageResource(liveAnimate.getImg());
        holder.name.setSelected(liveAnimate.isSelect());
        holder.selectView.setVisibility(liveAnimate.isSelect() ? View.VISIBLE : View.GONE);

    }

    public class ViewHolder extends CementViewHolder {


        public ImageView image;
        public TextView name;
        public View selectView;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.animate_name);
            image = itemView.findViewById(R.id.animate_image);
            selectView = itemView.findViewById(R.id.select_view);
        }
    }
}
