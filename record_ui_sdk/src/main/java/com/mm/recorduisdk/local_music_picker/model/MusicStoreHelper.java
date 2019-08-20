package com.mm.recorduisdk.local_music_picker.model;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;

import com.mm.recorduisdk.local_music_picker.bean.MusicDirectory;
import com.mm.recorduisdk.local_music_picker.filter.MusicFilter;
import com.mm.recorduisdk.recorder.model.MusicContent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XiongFangyu on 17/2/16.
 * <p>
 * 获取本地音乐帮助类
 */
public class MusicStoreHelper {
    private static final int MusicLoaderIdentifier = 111;

    public static void cancelLoadMusic(Activity context) {
        try {
            context.getLoaderManager().destroyLoader(MusicLoaderIdentifier);
        } catch (Exception ex) {

        }
    }

    public static void getMusicDirectory(Activity context, Bundle args, MusicResultCallback callback, MusicFilter filter) {
        context.getLoaderManager().initLoader(MusicLoaderIdentifier, args, new MusicDirLoaderCallbacks(context, callback, filter));
    }

    static class MusicDirLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {
        Context context;
        MusicResultCallback callback;
        MusicFilter filter;
        static final String FILE_PRE = "file://";

        MusicDirLoaderCallbacks(Context context, MusicResultCallback callback, MusicFilter filter) {
            this.callback = callback;
            this.context = context;
            this.filter = filter;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new MusicDirectoryLoader(context);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data == null) return;
            List<MusicDirectory> directories = new ArrayList<>();
            while (data.moveToNext()) {
                int id = data.getInt(data.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                int duration = data.getInt(data.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                String title = data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String path = data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                long size = data.getLong(data.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                String album = data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                String artist = data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                File file = new File(path);
                File parent = file.getParentFile();

                MusicDirectory d = new MusicDirectory();
                d.setPathName(parent.getAbsolutePath());
                d.setName(parent.getName());
                MusicContent music = new MusicContent();
                music.name = title;
                music.length = duration;
                music.path = /*FILE_PRE + */path;
                music.id = id + "";
                music.album = album;
                music.artist = artist;
                boolean result = true;
                if (filter != null) result = filter.filter(music);
                if (result) {
                    if (!directories.contains(d)) {
                        directories.add(d);
                        d.addMusic(music);
                    } else {
                        directories.get(directories.indexOf(d)).addMusic(music);
                    }
                }
            }
            if (callback != null) callback.onResult(directories);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }

    }

    public interface MusicResultCallback {
        void onResult(List<MusicDirectory> directories);
    }
}
