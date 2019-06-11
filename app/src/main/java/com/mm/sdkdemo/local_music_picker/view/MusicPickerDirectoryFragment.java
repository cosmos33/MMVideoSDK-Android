package com.mm.sdkdemo.local_music_picker.view;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mm.sdkdemo.R;
import com.mm.sdkdemo.base.BaseFragment;
import com.mm.sdkdemo.local_music_picker.bean.MusicDirectory;
import com.mm.sdkdemo.local_music_picker.filter.MusicFilter;
import com.mm.sdkdemo.local_music_picker.filter.MusicFilterContainer;
import com.mm.sdkdemo.local_music_picker.filter.MusicLengthFilter;
import com.mm.sdkdemo.local_music_picker.model.MusicStoreHelper;
import com.mm.sdkdemo.local_music_picker.view.adapter.MusicDirectoryAdapter;
import com.mm.sdkdemo.local_music_picker.view.adapter.OnItemClickListener;

import java.util.List;


/**
 * Created by XiongFangyu on 17/2/16.
 * <p>
 * 音乐文件夹
 */
public class MusicPickerDirectoryFragment extends BaseFragment {
    private long minSize = 0;
    private long maxSize = Long.MAX_VALUE;
    private int maxLength = Integer.MAX_VALUE;

    private MusicFilter musicFilter;

    private RecyclerView recyclerView;
    private MusicDirectoryAdapter adapter;

    private OnChooseListener onChooseListener;

    public void setFilterSize(long minSize, long maxSize) {
        this.maxSize = maxSize;
        this.minSize = minSize;
    }

    public void setFilterMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_music_picker_directory;
    }

    @Override
    protected void initViews(View contentView) {
        recyclerView = (RecyclerView) contentView;
    }

    @Override
    protected void onLoad() {
        getMusicDirectorys();
    }

    public void setOnChooseListener(OnChooseListener onChooseListener) {
        this.onChooseListener = onChooseListener;
    }

    private void onGetData(List<MusicDirectory> directories) {
        if (adapter == null) {
            adapter = new MusicDirectoryAdapter(getActivity(), directories);
            adapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onClick(View v, int position) {
                    MusicDirectory directory = adapter.getItemInPosition(position);
                    if (onChooseListener != null) onChooseListener.onChoose(directory);
                }
            });
            recyclerView.setAdapter(adapter);
        } else {
            adapter.getData().clear();
            adapter.addAll(directories);
            adapter.notifyDataSetChanged();
        }
    }

    private void getMusicDirectorys() {
        if (musicFilter == null)
            musicFilter = new MusicFilterContainer(new MusicLengthFilter(maxLength));
        MusicStoreHelper.getMusicDirectory(getActivity(), new Bundle(), callback, musicFilter);
    }

    private final MusicStoreHelper.MusicResultCallback callback = new MusicStoreHelper.MusicResultCallback() {
        @Override
        public void onResult(List<MusicDirectory> directories) {
            onGetData(directories);
        }
    };

    public interface OnChooseListener {
        void onChoose(MusicDirectory directory);
    }
}
