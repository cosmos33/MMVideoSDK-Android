package com.mm.sdkdemo.local_music_picker.filter;


import com.mm.sdkdemo.recorder.model.MusicContent;

/**
 * 音乐筛选器
 */
public interface MusicFilter {
    /**
     * 根据具体信息筛选
     *
     * @param musicContent
     * @return true 保存 false 丢弃
     */
    boolean filter(MusicContent musicContent);
}
