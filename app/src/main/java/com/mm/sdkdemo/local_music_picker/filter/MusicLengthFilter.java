package com.mm.sdkdemo.local_music_picker.filter;


import com.mm.sdkdemo.recorder.model.MusicContent;

/**
 * Created by XiongFangyu on 17/2/22.
 */

public class MusicLengthFilter implements MusicFilter {

    private int maxLength = Integer.MAX_VALUE;

    public MusicLengthFilter(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public boolean filter(MusicContent musicContent) {
        return musicContent.length <= maxLength;
    }
}
