package com.mm.recorduisdk.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.cosmos.mdlog.MDLog;
import com.mm.mmutil.FileUtil;
import com.mm.mmutil.app.AppContext;
import com.mm.mmutil.task.MomoTaskExecutor;
import com.mm.recorduisdk.log.LogTag;
import com.mm.recorduisdk.recorder.model.Photo;

import java.io.File;
import java.io.IOException;

/**
 * 保存 图片 或者 视频 到系统相册中
 */
public class AlbumNotifyHelper {

    public static final String TAG = AlbumNotifyHelper.class.getSimpleName();
    public static final int FLAG_IMAGE = 1;
    public static final int FLAG_VIDEO = 2;
    private static volatile AlbumNotifyHelper sAlbumNotifyHelper = null;
    private MediaScannerConnection sMediaScannerConnection;
    private final String DCIM = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
    private String rootDir = DCIM + File.separator + "Camera";
    private OnSaveVideoListener mOnSaveVideoListener;
    private OnSaveImageListener mOnSaveImageListener;

    private AlbumNotifyHelper() {

    }

    public static AlbumNotifyHelper getAblumNotifyHelper() {
        if (sAlbumNotifyHelper == null) {
            synchronized (AlbumNotifyHelper.class) {
                if (sAlbumNotifyHelper == null) {
                    sAlbumNotifyHelper = new AlbumNotifyHelper();
                }
            }
        }
        return sAlbumNotifyHelper;
    }


    public interface OnSaveVideoListener {

        void saveVideoSuccess(boolean isToast);

        void saveVideoError();
    }

    public interface OnSaveImageListener {

        void saveImageSuccess();

        void saveImageError();
    }

    public void setOnSaveVideoListener(OnSaveVideoListener onSaveVideoListener) {
        mOnSaveVideoListener = onSaveVideoListener;
    }

    public int getTaskTag() {
        return hashCode();
    }

    public void copyVideoFile(boolean isDeleteSourceFile, final File sourceFile) {
        if (sourceFile == null || !sourceFile.exists()) {
            return;
        }
        MomoTaskExecutor.executeUserTask(getTaskTag(), new SaveAlbumOrVideoTask(isDeleteSourceFile, FLAG_VIDEO, sourceFile));
    }

    public void copyVideoFile(final File sourceFile) {
        if (sourceFile == null || !sourceFile.exists()) {
            return;
        }
        MomoTaskExecutor.executeUserTask(getTaskTag(), new SaveAlbumOrVideoTask(false, FLAG_VIDEO, sourceFile));
    }

    private void deleteSourceFile(File sourceFile) {
        boolean delete = sourceFile.delete();
        if (!delete) {
            MDLog.e(LogTag.COMMON, "file delete failed");
        }
    }

    /**
     * @param targetFile 要保存的照片文件
     */
    public void insertImageToMedia(long createTime, File targetFile) {
        Context context = AppContext.getContext();
        ContentResolver resolver = context.getContentResolver();
        ContentValues newValues = new ContentValues();
        newValues.put(MediaStore.Images.Media.TITLE, targetFile.getName());
        newValues.put(MediaStore.Images.Media.DISPLAY_NAME, targetFile.getName());
        newValues.put(MediaStore.Images.Media.MIME_TYPE, getPhotoMimeType(targetFile.getAbsolutePath()));
        newValues.put(MediaStore.Images.Media.DATE_TAKEN, createTime);
        newValues.put(MediaStore.Images.Media.DATE_ADDED, createTime);
        newValues.put(MediaStore.Images.Media.DATE_MODIFIED, createTime);
        newValues.put(MediaStore.Images.Media.ORIENTATION, 0);
        newValues.put(MediaStore.Images.Media.DATA, targetFile.getAbsolutePath());
        newValues.put(MediaStore.Images.Media.SIZE, targetFile.length());
        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, newValues);
        scanFile(FLAG_IMAGE, targetFile.getAbsolutePath());
    }

    /**
     * @param saveFile 要保存的视频文件
     */
    public void insertVideoToMedia(long createTime, File saveFile) {
        Context context = AppContext.getContext();
        ContentResolver mContentResolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Video.Media.TITLE, saveFile.getName());
        values.put(MediaStore.Video.Media.DISPLAY_NAME, saveFile.getName());
        values.put(MediaStore.Video.Media.MIME_TYPE, getVideoMimeType(saveFile.getAbsolutePath()));
        values.put(MediaStore.Video.Media.DATE_TAKEN, createTime);
        values.put(MediaStore.Video.Media.DATE_MODIFIED, createTime);
        values.put(MediaStore.Video.Media.DATE_ADDED, createTime);
        values.put(MediaStore.Video.Media.DURATION, getDurationOfVideo(saveFile.getAbsolutePath()));
        values.put(MediaStore.MediaColumns.DATA, saveFile.getAbsolutePath());
        values.put(MediaStore.MediaColumns.SIZE, saveFile.length());
        mContentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
        scanFile(FLAG_VIDEO, saveFile.getAbsolutePath());
    }

    private void scanFile(final int flag, final String filePath) {
        sMediaScannerConnection = new MediaScannerConnection(AppContext.getContext(), new MediaScannerConnection.MediaScannerConnectionClient() {
            @Override
            public void onMediaScannerConnected() {
                String mimeType = "";
                if (FLAG_IMAGE == flag) {
                    mimeType = getPhotoMimeType(filePath);
                } else if (FLAG_VIDEO == flag) {
                    mimeType = getVideoMimeType(filePath);
                }
                try {
                    sMediaScannerConnection.scanFile(filePath, mimeType);
                } catch (Exception ignored) {

                }
            }

            @Override
            public void onScanCompleted(String path, Uri uri) {
                sMediaScannerConnection.disconnect();
            }
        });
        sMediaScannerConnection.connect();
    }


    // 获取音视频播放时长
    private long getDurationOfVideo(String filePath) {
        MediaMetadataRetriever mmr = null;
        try {
            mmr = new MediaMetadataRetriever();
            mmr.setDataSource(filePath);
            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            return Long.valueOf(duration);
        } catch (Exception e) {
            return 0;
        } finally {
            if (mmr != null) {
                mmr.release();
            }
        }
    }

    // 获取照片的mine_type
    private String getPhotoMimeType(String path) {
        String lowerPath = path.toLowerCase();
        if (lowerPath.endsWith("jpg") || lowerPath.endsWith("jpeg")) {
            return "image/jpeg";
        } else if (lowerPath.endsWith("png")) {
            return "image/png";
        } else if (lowerPath.endsWith("gif")) {
            return "image/gif";
        }
        return "image/jpeg";
    }

    // 获取video的mine_type,暂时只支持mp4,3gp
    private String getVideoMimeType(String path) {
        String lowerPath = path.toLowerCase();
        if (lowerPath.endsWith("mp4") || lowerPath.endsWith("mpeg4") || lowerPath.endsWith("mp4_")) {
            return "video/mp4";
        } else if (lowerPath.endsWith("3gp")) {
            return "video/3gp";
        }
        return "video/mp4";
    }

    private void executeCopyImageFile(final Photo photo, final File sourceFile) {
        final long createTime = System.currentTimeMillis();
        File destFile = new File(rootDir, createTime + ".jpg");
        try {
            if (!destFile.exists()) {
                destFile.createNewFile();
            }
            FileUtil.copyFile(sourceFile, destFile);
            photo.tempPath = destFile.getAbsolutePath();
            insertImageToMedia(createTime, destFile);
        } catch (IOException e) {
        }
    }

    public void executeCopyVideoFile(File sourceFile) {
        final long createTime = System.currentTimeMillis();
        File destFile = new File(rootDir, createTime + ".mp4");
        try {
            if (!destFile.exists()) {
                destFile.createNewFile();
            }
            FileUtil.copyFile(sourceFile, destFile);
            insertVideoToMedia(createTime, destFile);
        } catch (IOException e) {
        }
    }

    private class SaveAlbumOrVideoTask extends MomoTaskExecutor.Task<Object, Void, Void> {

        private int flag;
        private Photo photo;
        private File sourceFile;
        private boolean isDeleteSourceFile;

        public SaveAlbumOrVideoTask(int flag, Photo photo, File sourceFile) {
            this.flag = flag;
            this.photo = photo;
            this.sourceFile = sourceFile;
        }

        public SaveAlbumOrVideoTask(boolean isDeleteSourceFile, int flag, Photo photo, File sourceFile) {
            this.flag = flag;
            this.photo = photo;
            this.sourceFile = sourceFile;
            this.isDeleteSourceFile = isDeleteSourceFile;
        }

        public SaveAlbumOrVideoTask(boolean isDeleteSourceFile, int flag, File sourceFile) {
            this.flag = flag;
            this.sourceFile = sourceFile;
            this.isDeleteSourceFile = isDeleteSourceFile;
        }

        @Override
        protected Void executeTask(Object... objects) throws Exception {
            if (FLAG_IMAGE == flag) {
                executeCopyImageFile(photo, sourceFile);
            } else if (FLAG_VIDEO == flag) {
                executeCopyVideoFile(sourceFile);
            }
            return null;
        }

        @Override
        protected void onTaskSuccess(Void aVoid) {
            super.onTaskSuccess(aVoid);
            if (FLAG_IMAGE == flag) {
                if (isDeleteSourceFile) {
                    if (sourceFile.exists()) {
                        deleteSourceFile(sourceFile);
                    }
                }
                if (mOnSaveImageListener != null) {
                    mOnSaveImageListener.saveImageSuccess();
                }
            } else if (FLAG_VIDEO == flag) {
                if (isDeleteSourceFile) {
                    if (sourceFile.exists()) {
                        deleteSourceFile(sourceFile);
                    }
                }
                if (mOnSaveVideoListener != null) {
                    mOnSaveVideoListener.saveVideoSuccess(isDeleteSourceFile);
                }
            }
        }

        @Override
        protected void onTaskError(Exception e) {
            super.onTaskError(e);
            if (FLAG_IMAGE == flag) {
                if (mOnSaveImageListener != null) {
                    mOnSaveImageListener.saveImageError();
                }
            } else if (FLAG_VIDEO == flag) {
                if (mOnSaveVideoListener != null) {
                    mOnSaveVideoListener.saveVideoError();
                }
            }
        }
    }

    public void onDestroy() {
        if (mOnSaveVideoListener != null) {
            mOnSaveVideoListener = null;
        }
        if (mOnSaveImageListener != null) {
            mOnSaveImageListener = null;
        }
        MomoTaskExecutor.cancleAllTasksByTag(getTaskTag());
        if (mOnSaveImageListener != null) {
            mOnSaveImageListener = null;
        }
        if (mOnSaveVideoListener != null) {
            mOnSaveVideoListener = null;
        }
    }
}


