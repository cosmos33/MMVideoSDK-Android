package com.immomo.videosdk.recorder.musicpanel.edit;

import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.immomo.mdlog.MDLog;
import com.immomo.mmutil.task.MomoTaskExecutor;
import com.immomo.mmutil.task.ThreadUtils;
import com.immomo.videosdk.R;
import com.immomo.videosdk.api.MoApi;
import com.immomo.videosdk.base.cement.CementModel;
import com.immomo.videosdk.base.cement.SimpleCementAdapter;
import com.immomo.videosdk.base.cement.eventhook.OnClickEventHook;
import com.immomo.videosdk.log.LogTag;
import com.immomo.videosdk.recorder.model.MusicContent;
import com.immomo.videosdk.recorder.musicpanel.edit.model.ClearMusicModel;
import com.immomo.videosdk.recorder.musicpanel.edit.model.EditMusicModel;
import com.immomo.videosdk.recorder.musicpanel.widget.VolumeSeekBar;
import com.immomo.videosdk.utils.UIUtils;
import com.immomo.videosdk.widget.decoration.LinearPaddingItemDecoration;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * create time 2018/11/20
 * by wangrenguang
 */
public class VolFragment extends BaseEditMusicFragment {

    private RecyclerView recyclerView;
    private View musicVolBar;
    private SimpleCementAdapter cementAdapter;
    private VolumeSeekBar volumeBar;
    private TextView videoVolumeView;
    private TextView musicVolumeView;
    private TextView nameTv;
    private EditMusicModel selectMusicModel;
    private int valume;
    private ClearMusicModel clearMusicModel;
    private boolean canSeekVol;

    @Override
    protected int getLayout() {
        return R.layout.fragment_video_edit_music_vol;
    }

    @Override
    protected void initViews(View contentView) {
        recyclerView = contentView.findViewById(R.id.music_list);
        volumeBar = contentView.findViewById(R.id.music_os_seek_bar);
        videoVolumeView = contentView.findViewById(R.id.music_video_volume);
        nameTv = contentView.findViewById(R.id.music_panel_name);
        musicVolBar = contentView.findViewById(R.id.music_panel_toolbar);
        if (canSeekVol) {
            musicVolBar.setVisibility(View.VISIBLE);
        } else {
            musicVolBar.setVisibility(View.GONE);
            nameTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            nameTv.setTextColor(UIUtils.getColor(R.color.white));
        }
        musicVolumeView = contentView.findViewById(R.id.music_music_volume);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), OrientationHelper.HORIZONTAL, false));
        recyclerView.setItemAnimator(null);
        recyclerView.addItemDecoration(new LinearPaddingItemDecoration(UIUtils.getPixels(20), UIUtils.getPixels(20), UIUtils.getPixels(12)));
        cementAdapter = new SimpleCementAdapter();

        volumeBar.setOnVolumeSeekListener(new VolumeSeekBar.OnVolumeSeekListener() {
            int result;

            @Override
            public void onSeekChanged(float percent) {
                result = (int) percent;
                musicVolumeView.setText(result + "%");
                videoVolumeView.setText((100 - result) + "%");
                musicVolumeView.setEnabled(result != 0);
                videoVolumeView.setEnabled(result != 100);

                if (iEditMusic != null) {
                    iEditMusic.setVolume(result);
                }
                valume = result;
            }
        });

        cementAdapter.addEventHook(new OnClickEventHook<ClearMusicModel.ViewHolder>(ClearMusicModel.ViewHolder.class) {
            @Override
            public void onClick(@NonNull View view, @NonNull ClearMusicModel.ViewHolder viewHolder, int position, @NonNull CementModel rawModel) {
                // clear music
                clearSelectMusic();
                if (iEditMusic != null) {
                    iEditMusic.clearSelectMusic();
                }
            }

            @Nullable
            @Override
            public View onBind(@NonNull ClearMusicModel.ViewHolder viewHolder) {
                return viewHolder.itemView;
            }
        });

        cementAdapter.addEventHook(new OnClickEventHook<EditMusicModel.ViewHolder>(EditMusicModel.ViewHolder.class) {
            @Override
            public void onClick(@NonNull View view, @NonNull EditMusicModel.ViewHolder viewHolder, int position, @NonNull CementModel rawModel) {
                handleSelectMusic((EditMusicModel) rawModel);
            }

            @Nullable
            @Override
            public View onBind(@NonNull EditMusicModel.ViewHolder viewHolder) {
                return viewHolder.itemView;
            }
        });

        recyclerView.setAdapter(cementAdapter);
        MomoTaskExecutor.executeTask(ThreadUtils.TYPE_RIGHT_NOW, hashCode(), new GetMusicList());
        valume = iEditMusic != null ? iEditMusic.getVolume() : 0;
        volumeBar.setCurrentProgress(valume);
    }

    @Override
    public void onSelectMusic(@NonNull MusicContent musicContent, boolean canSeekVol) {
        this.canSeekVol = canSeekVol;
        if (cementAdapter == null) {
            return;
        }
        if (musicContent == null) {
            selectMusicModel = null;
            return;
        }

        EditMusicModel insetModel = null;
        MusicContent temp;
        if (selectMusicModel != null) {
            // reset preselect music
            if (MusicContent.isSame(selectMusicModel.getMusicWrapper(), musicContent)) {
                return;
            }
            selectMusicModel.setSelected(false);
            cementAdapter.notifyDataChanged(selectMusicModel);
        }
        if (valume <= 0) {
            volumeBar.setCurrentProgress(50);
        }
        for (CementModel cementModel : cementAdapter.getModels()) {
            if (!(cementModel instanceof EditMusicModel)) {
                continue;
            }
            temp = ((EditMusicModel) cementModel).getMusicWrapper();
            if (MusicContent.isSame(temp, musicContent)) {
                ((EditMusicModel) cementModel).setSelected(true);
                insetModel = (EditMusicModel) cementModel;
                break;
            }
        }
        if (insetModel != null) {
            cementAdapter.removeModel(insetModel);
        } else {
            insetModel = new EditMusicModel(musicContent);
            insetModel.setSelected(true);
        }
        cementAdapter.insertModelAfter(insetModel, clearMusicModel);
        selectMusicModel = insetModel;
        refreshMusicTitle();
    }

    public void clearSelectMusic() {
        if (selectMusicModel != null) {
            selectMusicModel.setSelected(false);
            cementAdapter.notifyDataChanged(selectMusicModel);
            selectMusicModel = null;
        }
        refreshMusicTitle();
    }

    private void refreshMusicTitle() {
        if (selectMusicModel != null) {
            MusicContent selectMusic = selectMusicModel.getMusicWrapper();
            StringBuilder sb = new StringBuilder();
            sb.append("音乐：");
            sb.append(selectMusic.name);
            nameTv.setText(sb.toString());
        } else {
            nameTv.setText(R.string.music_panel_tip_no_music);
        }
    }

    private void handleSelectMusic(final EditMusicModel musicModel) {
        MDLog.i(LogTag.RECORDER.MUSIC, "handleSelectMusic:" + musicModel.getMusicWrapper());
        final MusicContent musicWrapper = musicModel.getMusicWrapper();
        if (selectMusicModel != null && TextUtils.equals(musicWrapper.id, selectMusicModel.getMusicWrapper().id)) {
            if (iEditMusic != null) {
                iEditMusic.gotoCutPage();
            }
            return;
        }
        if (selectMusicModel != null) {
            selectMusicModel.setSelected(false);
        }

        musicModel.setSelected(true);
        selectMusicModel = musicModel;
        realSelectMusic(musicWrapper);
        refreshMusicTitle();
        cementAdapter.notifyDataSetChanged();
    }

    private void realSelectMusic(MusicContent music) {
        if (iEditMusic != null) {
            if (valume <= 0) {
                volumeBar.setCurrentProgress(50);
            }
            music.reset();
            iEditMusic.selectMusic(music);
        }
    }

    @Override
    protected void onLoad() {
    }

    @Override
    public void release() {
    }

    private class GetMusicList extends MomoTaskExecutor.Task<String, String, List<MusicContent>> {

        @Override
        protected List<MusicContent> executeTask(String... strings) {
            return MoApi.getRecommendMusic();
        }

        @Override
        protected void onTaskSuccess(List<MusicContent> musicWrappers) {
            if (null == musicWrappers) {
                musicWrappers = new ArrayList<>();
            }
            List<CementModel<?>> models = new ArrayList<>();
            clearMusicModel = new ClearMusicModel();
            models.add(clearMusicModel);
            if (iEditMusic != null && iEditMusic.getSelectMusic() != null) {
                selectMusicModel = new EditMusicModel(iEditMusic.getSelectMusic());
                selectMusicModel.setSelected(true);
                models.add(selectMusicModel);
            }

            for (MusicContent musicWrapper : musicWrappers) {
                if ((selectMusicModel != null && MusicContent.isSame(selectMusicModel.getMusicWrapper(), musicWrapper))) {
                    continue;
                }
                EditMusicModel editMusicModel = new EditMusicModel(musicWrapper);
                editMusicModel.setSelected(false);
                models.add(editMusicModel);
            }
            cementAdapter.addModels(models);
            refreshMusicTitle();
        }

        @Override
        protected void onTaskError(Exception e) {
            super.onTaskError(e);
        }
    }
}
