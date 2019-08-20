package com.mm.recorduisdk.local_music_picker.model;

import android.content.Context;
import android.content.CursorLoader;
import android.provider.MediaStore;

/**
 * Created by XiongFangyu on 17/2/16.
 */
public class MusicDirectoryLoader extends CursorLoader {

    private final String[] MEDIA_PROJECTION = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ALBUM,   //专辑
            MediaStore.Audio.Media.ARTIST,  //歌手名
            MediaStore.Audio.Media.DATA,    //文件路径
            MediaStore.Audio.Media.SIZE,    //文件大小
            MediaStore.Audio.Media.DURATION,};

    public MusicDirectoryLoader(Context context) {
        super(context);
        setProjection(MEDIA_PROJECTION);
        setUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        setSortOrder(MediaStore.Video.Media.DATE_ADDED + " DESC");
        setSelectionAndArgs();
    }

    private void setSelectionAndArgs() {
        setSelection(MediaStore.MediaColumns.MIME_TYPE + "=?");
        setSelectionArgs(new String[]{"audio/mpeg"});
    }
}
