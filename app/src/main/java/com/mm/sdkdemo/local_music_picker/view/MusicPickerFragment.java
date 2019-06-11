package com.mm.sdkdemo.local_music_picker.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mm.sdkdemo.R;
import com.mm.sdkdemo.local_music_picker.bean.MusicDirectory;
import com.mm.sdkdemo.local_music_picker.view.adapter.MusicPickerAdapter;
import com.mm.sdkdemo.local_music_picker.view.adapter.OnItemClickListener;
import com.mm.sdkdemo.base.BaseFragment;
import com.mm.sdkdemo.recorder.model.MusicContent;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by XiongFangyu on 17/2/16.
 * <p>
 * 选择音乐
 */
public class MusicPickerFragment extends BaseFragment {
    private MusicDirectory directory;
    private boolean directoryChanged = false;

    private RecyclerView recyclerView;
    private MusicPickerAdapter adapter;

    private OnChooseMusicListener onChooseMusicListener;

    @Override
    protected int getLayout() {
        return R.layout.fragment_music_picker;
    }

    @Override
    protected void initViews(View contentView) {
        recyclerView = (RecyclerView) contentView;
    }

    @Override
    protected void onLoad() {
        refreshView();
    }

    public void setDirectory(MusicDirectory d) {
        this.directory = d;
        directoryChanged = true;
        refreshView();
    }

    public void setOnChooseMusicListener(OnChooseMusicListener onChooseMusicListener) {
        this.onChooseMusicListener = onChooseMusicListener;
    }

    private void refreshView() {
        if (recyclerView == null || !directoryChanged) return;
        directoryChanged = false;
        if (adapter == null) {
            List<MusicContent> data = new ArrayList<>();
            adapter = new MusicPickerAdapter(getActivity(), data);
            adapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onClick(View v, int position) {
                    if (onChooseMusicListener != null) onChooseMusicListener.onChoose(adapter.getItemInPosition(position));
                }
            });
            recyclerView.setAdapter(adapter);
        }
        adapter.replace(directory.getMusics());
    }

    public interface OnChooseMusicListener {
        void onChoose(MusicContent music);
    }
}
