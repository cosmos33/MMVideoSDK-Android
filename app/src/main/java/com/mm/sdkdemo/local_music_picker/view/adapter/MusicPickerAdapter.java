package com.mm.sdkdemo.local_music_picker.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mm.sdkdemo.R;
import com.mm.sdkdemo.recorder.model.MusicContent;

import java.util.List;

/**
 * Created by XiongFangyu on 17/2/16.
 * <p>
 * 选择音乐adapter
 */
public class MusicPickerAdapter extends BaseRecyclerAdapter<MusicContent, MusicPickerAdapter.VH> {

    public MusicPickerAdapter(Context context, List<MusicContent> data) {
        super(context, data);
    }

    @Override
    protected void setViews(VH holder, MusicContent data, int position) {
        holder.refreshView(data, position);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_music_picker, parent, false));
    }

    static final class VH extends BaseRecyclerAdapter.BaseViewHolder {
        private TextView name;
        private TextView artist;

        public VH(View itemView) {
            super(itemView);
        }

        @Override
        protected void setView() {
            name = findViewById(R.id.music_name);
            artist = findViewById(R.id.music_artist_album);
        }

        public void refreshView(MusicContent data, int position) {
            if (data != null) {
                name.setText(data.name);
                final String art = data.artist;
                final String album = data.album;
                artist.setText(art + " - " + album);
            }
        }
    }
}
