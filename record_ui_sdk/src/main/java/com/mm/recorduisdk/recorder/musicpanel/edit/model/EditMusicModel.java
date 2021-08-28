package com.mm.recorduisdk.recorder.musicpanel.edit.model;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.mm.base_business.glide.ImageLoaderX;
import com.mm.recorduisdk.R;
import com.mm.recorduisdk.base.cement.CementAdapter;
import com.mm.recorduisdk.base.cement.CementModel;
import com.mm.recorduisdk.base.cement.CementViewHolder;
import com.mm.recorduisdk.recorder.model.MusicContent;

public class EditMusicModel extends CementModel<EditMusicModel.ViewHolder> {

    private MusicContent musicContent;

    public EditMusicModel(@NonNull MusicContent musicWrapper) {
        this.musicContent = musicWrapper;
    }

    @NonNull
    public MusicContent getMusicWrapper() {
        return musicContent;
    }

    private boolean isSelect;

    public void setSelected(boolean select) {
        this.isSelect = select;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.edit_video_music_item;
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

        if (TextUtils.isEmpty(musicContent.cover)) {
            holder.imageView.setImageResource(R.drawable.ic_video_music_default);
        } else {
            ImageLoaderX.load(musicContent.cover).showDefault(R.drawable.bg_moment_local_music_item).into(holder.imageView);
        }

        holder.name.setText(musicContent.name);
        if (isSelect) {
            holder.select.setVisibility(View.VISIBLE);
        } else {
            holder.select.setVisibility(View.GONE);
        }
    }

    public class ViewHolder extends CementViewHolder {

        ImageView imageView;
        TextView name;
        View select;

        public ViewHolder(View itemView) {
            super(itemView);

            select = itemView.findViewById(R.id.music_select_view);

            imageView = itemView.findViewById(R.id.music_bg);
            name = itemView.findViewById(R.id.music_name);
        }
    }
}
