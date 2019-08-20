/*
 * Copyright (C) 2014 nohana, Inc.
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mm.recorduisdk.utils.album;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.util.LongSparseArray;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.mm.mmutil.StringUtils;
import com.mm.mmutil.task.MomoTaskExecutor;
import com.mm.recorduisdk.bean.MMChooseMediaParams;
import com.mm.recorduisdk.recorder.model.AlbumDirectory;
import com.mm.recorduisdk.recorder.model.Photo;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class AlbumCollection implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 2;
    private final WeakReference<Context> mContext;
    private final WeakReference<OnMediaListener> mListener;
    private final LoaderManager mLoaderManager;

    private final MMChooseMediaParams mConditions;
    private boolean mIsParseCursor = false;
    private boolean mShowImage = true;
    private int newPosLength = 0;
    private int mediaType = AlbumConstant.MEDIA_TYPE_MIXED;

    public AlbumCollection(@NonNull MMChooseMediaParams conditions, int mediaType, @NonNull FragmentActivity context, @NonNull OnMediaListener listener) {
        mConditions = conditions;
        this.mediaType = mediaType == 0 ? this.mediaType : mediaType;
        mContext = new WeakReference<Context>(context);
        mListener = new WeakReference<>(listener);
        mLoaderManager = context.getSupportLoaderManager();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Context context = mContext.get();
        if (context == null) {
            return null;
        }
        return new AlbumLoader(context, mediaType);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
        final long temp = System.currentTimeMillis();
        final OnMediaListener listener = mListener.get();
        if (mIsParseCursor || listener == null) {
            return;
        }
        mIsParseCursor = true;

        if (data == null) {
            ScanResult scanResult = new ScanResult();
            scanResult.showImage = false;
            scanResult.newPosPhotoLength = newPosLength;
            listener.onMultiMediaLoad(scanResult);
            return;
        }

        MomoTaskExecutor.executeUserTask(hashCode(), new MomoTaskExecutor.Task<Object, Void, ScanResult>() {
            @Override
            protected ScanResult executeTask(Object[] objects) throws Exception {
                final LongSparseArray<String> videoThumbPaths = getVideoThumbPaths();
                final LongSparseArray<String> imageThumbPaths = getImageThumbPaths();
                return getAlbumDirectories(data, videoThumbPaths, imageThumbPaths);
            }

            @Override
            protected void onTaskSuccess(ScanResult result) {
                super.onTaskSuccess(result);
                if (mShowImage && mLoaderManager != null) {
                    mLoaderManager.destroyLoader(LOADER_ID);
                }
                mIsParseCursor = false;
                final OnMediaListener listener = mListener.get();
                result.showImage = mShowImage;
                result.newPosPhotoLength = newPosLength;
                if (listener != null) {
                    listener.onMultiMediaLoad(result);
                    mShowImage = true;
                }
                data.close();
            }

            @Override
            protected void onTaskError(Exception e) {
                super.onTaskError(e);
                data.close();
            }
        });

    }

    @NonNull
    private ScanResult getAlbumDirectories(Cursor data,
                                           LongSparseArray<String> videoThumbPaths,
                                           LongSparseArray<String> imageThumbPaths) {
        ScanResult scanResult = new ScanResult();

        List<Photo> newPosPhoto = null;
        long recentTime = 0;
        long lastTime = 0;
        int index = 0;
        boolean needCheck = false;

        final ArrayList<AlbumDirectory> directories = new ArrayList<>();
        // 纯图片文件分类list
        final ArrayList<AlbumDirectory> pictureDirectories = new ArrayList<>();

        final ArrayList<AlbumDirectory> videoDirectories = new ArrayList<>();
        if (data.isClosed() || !data.moveToFirst()) {
            scanResult.albumDirectories = directories;
            scanResult.pictureDirectories = directories;
            scanResult.videoDirectories = videoDirectories;
            return scanResult;
        }

        // 创建所有分类tris
        final AlbumDirectory directoryAll = new AlbumDirectory();
        directoryAll.setId(AlbumConstant.DIRECTORY_ID_ALL);
        directoryAll.setName(AlbumConstant.DIRECTORY_NAME_ALL);

        // 创建视频分类
        final AlbumDirectory directoryVideo = new AlbumDirectory();
        directoryVideo.setId(AlbumConstant.DIRECTORY_ID_VIDEO);
        directoryVideo.setName(AlbumConstant.DIRECTORY_NAME_VIDEO);

        //创建照片分类
        final AlbumDirectory directoryPicture = new AlbumDirectory();
        directoryPicture.setId(AlbumConstant.DIRECTORY_ID_PICTURE);
        directoryPicture.setName(AlbumConstant.DIRECTORY_NAME__PICTURE_ALL);

        directories.add(AlbumConstant.INDEX_ALL_CATEGORY, directoryAll);
        pictureDirectories.add(AlbumConstant.INDEX_ALL_CATEGORY, directoryPicture);

        do {
            final Photo media = parseFromCursor(data, videoThumbPaths, imageThumbPaths);
            if (media == null) {
                continue;
            }

            final AlbumDirectory directory = new AlbumDirectory();
            directory.setId(media.bucketId);
            directory.setName(media.bucketName);

            final AlbumDirectory onlyPicture = new AlbumDirectory();
            onlyPicture.setId(media.bucketId);
            onlyPicture.setName(media.bucketName);

            if (directories.contains(directory)) {
                directories.get(directories.indexOf(directory)).getMedias().add(media);
            } else {
                directory.setCoverPath(TextUtils.isEmpty(media.thumbPath) ? media.path : media.thumbPath);
                directory.getMedias().add(media);
                directory.setDateAdded(media.dateAdded);
                directories.add(directory);
            }

            if (media.type == ItemConstant.TYPE_VIDEO) {
                directoryVideo.getMedias().add(media);
            } else {
                directoryPicture.getMedias().add(media);

                if (pictureDirectories.contains(onlyPicture)) {
                    pictureDirectories.get(pictureDirectories.indexOf(onlyPicture)).getMedias().add(media);
                } else {
                    onlyPicture.setCoverPath(TextUtils.isEmpty(media.thumbPath) ? media.path : media.thumbPath);
                    onlyPicture.getMedias().add(media);
                    onlyPicture.setDateAdded(media.dateAdded);
                    pictureDirectories.add(onlyPicture);
                }
            }
            directoryAll.getMedias().add(media);

        } while (data.moveToNext());

        data.close();

        directoryVideo.bulidCoverPath();
        directoryPicture.bulidCoverPath();
        directoryAll.bulidCoverPath();
        videoDirectories.add(directoryVideo);

        scanResult.albumDirectories = directories;
        scanResult.pictureDirectories = pictureDirectories;
        scanResult.videoDirectories = videoDirectories;
        return scanResult;
    }

    private Photo parseFromCursorFast(Cursor cursor) {
        if (cursor == null) {
            return null;
        }

        final String path = getString(cursor, MediaStore.MediaColumns.DATA);
        if (StringUtils.isEmpty(path)) {
            return null;
        }

        String mimeType;
        if ("mp4".equals(MimeTypeMap.getFileExtensionFromUrl(path))) {
            // 修复MIME_TYPE是图片 但是文件视频的bug
            mimeType = "video/mp4";
        } else {
            mimeType = getString(cursor, MediaStore.MediaColumns.MIME_TYPE);
        }
        final boolean isImage = Photo.isImage(mimeType);
        final boolean isVideo = Photo.isVideo(mimeType);
        final boolean isGif = Photo.isGif(mimeType);

        if ((!isImage || (isGif)) && !isVideo) {
            return null;
        }
        final Photo media = new Photo();
        media.id = getLong(cursor, MediaStore.Files.FileColumns._ID);
        media.path = path;
        media.tempPath = path;
        media.mimeType = mimeType;

        if (isImage) {
            media.type = ItemConstant.TYPE_IMAGE;
        } else {
            media.type = ItemConstant.TYPE_VIDEO;
        }

        return media;
    }

    private Photo parseFromCursor(Cursor cursor,
                                  LongSparseArray<String> videoThumbPaths,
                                  LongSparseArray<String> imageThumbPaths) {

        final Photo media = parseFromCursorFast(cursor);
        if (media == null) {
            return null;
        }

        // 3、过滤0kb的数据
        final File file = new File(media.path);
        if (!file.exists() || file.length() <= 0) {
            return null;
        }

        if (media.type == ItemConstant.TYPE_IMAGE) {
            // 判断是否是长图
            int width = getInt(cursor, MediaStore.Images.Media.WIDTH);
            int height = getInt(cursor, MediaStore.Images.Media.HEIGHT);
            if (width <= 0 || height <= 0) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(media.path, options);

                width = options.outWidth;
                height = options.outHeight;
            }
            media.width = width;
            media.height = height;
            media.isLong = checkIsLong(width, height);
            if (imageThumbPaths != null) {
                media.thumbPath = imageThumbPaths.get((int) media.id);
            }
        } else {
            media.duration = getLong(cursor, "duration");
            if (videoThumbPaths != null) {
                media.thumbPath = videoThumbPaths.get((int) media.id);
            }
        }

        media.size = getLong(cursor, MediaStore.Images.Media.SIZE);
        media.dateAdded = getLong(cursor, MediaStore.MediaColumns.DATE_ADDED);
        media.bucketId = getString(cursor, "bucket_id");
        media.bucketName = getString(cursor, "bucket_display_name");
        return media;
    }

    /**
     * 获取视频缩略图集合，部分可能获取不到
     *
     * @return LongSparseArray
     */
    private LongSparseArray<String> getVideoThumbPaths() {
        final LongSparseArray<String> paths = new LongSparseArray<>();
        final Context context = mContext.get();

        if (context == null) {
            return paths;
        }

        final Uri uri = MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI;
        final String[] projection = {MediaStore.Video.Thumbnails.VIDEO_ID, MediaStore.Video.Thumbnails.DATA};
        final Cursor thumbCursor = context.getContentResolver().query(uri, projection, null, null, null);

        if (thumbCursor == null) {
            return paths;
        }

        while (thumbCursor.moveToNext()) {
            final long videoId = getLong(thumbCursor, MediaStore.Video.Thumbnails.VIDEO_ID);
            final String thumbPath = getString(thumbCursor, MediaStore.Video.Thumbnails.DATA);
            if (TextUtils.isEmpty(thumbPath)) {
                continue;
            }
            final File thumb = new File(thumbPath);
            if (!thumb.exists() || thumb.length() <= 0) {
                continue;
            }
            paths.put(videoId, thumbPath);
        }
        thumbCursor.close();

        return paths;
    }

    /**
     * 获取图片缩略图集合，部分可能获取不到
     *
     * @return LongSparseArray
     */
    private LongSparseArray<String> getImageThumbPaths() {
        final LongSparseArray<String> paths = new LongSparseArray<>();
        final Context context = mContext.get();

        if (context == null) {
            return paths;
        }

        final Uri uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
        final String[] projection = {MediaStore.Images.Thumbnails._ID, MediaStore.Images.Thumbnails.IMAGE_ID, MediaStore.Images.Thumbnails.DATA};
        final Cursor thumbCursor = context.getContentResolver().query(uri, projection, null, null, null);

        if (thumbCursor == null) {
            return paths;
        }

        while (thumbCursor.moveToNext()) {
            final long imageId = getLong(thumbCursor, MediaStore.Images.Thumbnails.IMAGE_ID);
            final String thumbPath = getString(thumbCursor, MediaStore.Images.Thumbnails.DATA);
            if (TextUtils.isEmpty(thumbPath)) {
                continue;
            }
            final File thumb = new File(thumbPath);
            if (!thumb.exists() || thumb.length() <= 0) {
                continue;
            }
            paths.put(imageId, thumbPath);
        }
        thumbCursor.close();

        return paths;
    }

    private static boolean checkIsLong(int width, int height) {
        boolean isLong = false;
        //计算高宽比,判断是否为长图
        if (height != 0 && width != 0) {
            float ratio = (float) height / (float) width;
            if (ratio > 3.1f && ratio < 60f) {//先计算高宽比
                isLong = true;
            } else {
                ratio = (float) width / (float) height;//在判断是不是宽图
                if (ratio > 3.1f && ratio < 60f) {
                    isLong = true;
                }
            }
        }
        return isLong;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        final Context context = mContext.get();
        final OnMediaListener listener = mListener.get();
        if (context == null || listener == null) {
            return;
        }

        listener.onMultiMediaReset();
    }

    public void destroy() {
        if (mLoaderManager != null) {
            mLoaderManager.destroyLoader(LOADER_ID);
        }
        MomoTaskExecutor.cancleAllTasksByTag(hashCode());
        mListener.clear();
    }

    public void load() {
        if (mLoaderManager != null) {
            mLoaderManager.initLoader(LOADER_ID, null, this);
        }
    }

    public interface OnMediaListener {
        void onMultiMediaLoad(ScanResult scanResult);

        void onMultiMediaReset();
    }

    public static int getInt(Cursor cursor, String field) {
        int c = cursor.getColumnIndex(field);
        if (c >= 0) {
            return cursor.getInt(c);
        }
        return -1;
    }

    public static long getLong(Cursor cursor, String field) {
        int c = cursor.getColumnIndex(field);
        if (c >= 0) {
            return cursor.getLong(c);
        }
        return -1;
    }

    public static String getString(Cursor cursor, String field) {
        int c = cursor.getColumnIndex(field);
        if (c >= 0) {
            return cursor.getString(c);
        }
        return null;
    }
}
