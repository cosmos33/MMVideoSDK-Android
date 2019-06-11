package com.mm.sdkdemo.local_music_picker.filter;


import com.mm.sdkdemo.recorder.model.MusicContent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XiongFangyu on 17/2/22.
 */
public class MusicFilterContainer implements MusicFilter {

    private List<MusicFilter> filters;

    public MusicFilterContainer(MusicFilter... filters) {
        addMusicFilter(filters);
    }

    public void addMusicFilter(MusicFilter... filters) {
        if (filters == null)
            return;
        if (this.filters == null) {
            this.filters = new ArrayList<>();
        }
        final int len = filters.length;
        for (int i = 0; i < len; i++) {
            MusicFilter mf = filters[i];
            if (mf != null)
                this.filters.add(mf);
        }
    }

    public void removeMusicFilter(MusicFilter filter) {
        if (filters != null && filters.contains(filter))
            filters.remove(filter);
    }

    @Override
    public boolean filter(MusicContent musicContent) {
        if (filters == null || filters.size() <= 0)
            return true;
        for (MusicFilter mf : filters) {
            if (mf != null) {
                if (!mf.filter(musicContent))
                    return false;
            }
        }
        return true;
    }
}
