package com.mm.recorduisdk.local_music_picker.filter;


import com.mm.recorduisdk.recorder.model.MusicContent;

/**
 * Created by XiongFangyu on 17/2/22.
 */
public class MusicSizeFilter implements MusicFilter {
    long minSize = 0;
    long maxSize = Long.MAX_VALUE;

    public MusicSizeFilter(long minSize, long maxSize) {
        this.minSize = minSize;
        this.maxSize = maxSize;
    }

    @Override
    public boolean filter(MusicContent musicContent) {
/*        if (musicContent.size <= minSize) return false;
        if (musicContent.size >= maxSize) return false;*/
        return true;
    }
}
