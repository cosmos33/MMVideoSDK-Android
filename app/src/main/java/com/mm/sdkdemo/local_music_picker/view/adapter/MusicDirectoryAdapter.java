package com.mm.sdkdemo.local_music_picker.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mm.sdkdemo.R;

import com.mm.sdkdemo.local_music_picker.bean.MusicDirectory;

import java.util.List;

/**
 * Created by XiongFangyu on 17/2/16.
 * <p>
 * 音乐文件夹adapter
 */
public class MusicDirectoryAdapter extends BaseRecyclerAdapter<MusicDirectory, MusicDirectoryAdapter.VH> {

    public MusicDirectoryAdapter(Context context, List<MusicDirectory> data) {
        super(context, data);
    }

    @Override
    protected void setViews(VH holder, MusicDirectory data, int position) {
        holder.refreshView(data, position);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_music_picker_directory, parent, false));
    }

    static final class VH extends BaseRecyclerAdapter.BaseViewHolder {
        private TextView name;

        public VH(View itemView) {
            super(itemView);
        }

        @Override
        protected void setView() {
            name = findViewById(R.id.music_directory_name);
        }

        public void refreshView(MusicDirectory data, int position) {
            if (data != null) {
                final String n = data.getName();
                final int count = data.getMusicCount();
                name.setText(n + " (" + count + ")");
            }
        }
    }
}
