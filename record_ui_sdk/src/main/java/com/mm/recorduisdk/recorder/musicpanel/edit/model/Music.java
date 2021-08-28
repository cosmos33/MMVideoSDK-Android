package com.mm.recorduisdk.recorder.musicpanel.edit.model;

import androidx.annotation.NonNull;

/**
 * Created on 2019/5/24.
 *
 * @author jianxi[mabeijianxi@gmail.com]
 */
public class Music implements Comparable<Music> {
    /**歌曲名*/
    private String name;
    /**路径*/
    private String path;
    /**所属专辑*/
    private String album;
    /**艺术家(作者)*/
    private String artist;
    /**文件大小*/
    private long size;
    /**时长*/
    private int duration;
    /**歌曲名的拼音，用于字母排序*/
    private String pinyin;

    public Music(String name, String path, String album, String artist, long size, int duration) {
        this.name = name;
        this.path = path;
        this.album = album;
        this.artist = artist;
        this.size = size;
        this.duration = duration;
    }

    @Override
    public int compareTo(@NonNull Music o) {
        return 0;
    }
}

