package com.mm.sdkdemo.utils;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Window;

import com.immomo.mdlog.MDLog;
import com.immomo.mmutil.BaseDeviceUtils;
import com.mm.sdkdemo.log.LogTag;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Properties;

/**
 * Created by ruanlei on 20/7/16.
 */
public class DeviceUtils extends BaseDeviceUtils {
    private static int isMiUiNotchScreen = -1;

    public static String model = null;

    /**
     * 获得手机型号
     */
    public static String getModle() {
        if (!TextUtils.isEmpty(model)) {
            return model;
        }
        if (TextUtils.isEmpty(Build.MODEL)) {
            return "unknown";
        }
        return needEncode(Build.MODEL) ? getUTF8String(Build.MODEL) : Build.MODEL;
    }

    private static String getUTF8String(String content) {
        try {
            return URLEncoder.encode(content, "UTF-8");
        } catch (Exception e) {
            return "momo";
        }
    }

    /**
     * 是否需要编码,包含特殊字符时,需要编码
     *
     * @return
     */
    private static boolean needEncode(String content) {
        boolean needEncode = false;
        if (!TextUtils.isEmpty(content)) {
            char contents[] = content.toCharArray();
            for (char c : contents) {
                if (c <= '\u001f' || c >= '\u007f') {
                    needEncode = true;
                    break;
                }
            }
        }
        return needEncode;
    }

    public static boolean isHuaWei() {
        return Build.MANUFACTURER.equalsIgnoreCase("huawei");
    }

    /**
     * 检测发现，使用 properties 去读取MIUI信息的方法已经失效了，直接读取 manufacgturer对比
     *
     * @return
     */
    public static boolean isMIUI() {
        return Build.MANUFACTURER.equalsIgnoreCase("xiaomi");
    }

    public static boolean isVivo() {
        return Build.MANUFACTURER.equalsIgnoreCase("vivo");
    }

    public static boolean isOppo() {
        return Build.MANUFACTURER.equalsIgnoreCase("oppo");
    }

    /**
     * 是否小米的刘海屏，判断方法参见 https://dev.mi.com/console/doc/detail?pId=1293
     * 此方法应该结合 {@link #isMIUI()} 使用
     *
     * @return
     */
    public static boolean isNotchScreenForMiUi() {
        if (isMiUiNotchScreen >= 0) {
            return isMiUiNotchScreen == 1;
        }
        String value = readSystemProperty("ro.miui.notch", null);
        if (!TextUtils.isEmpty(value) && "1".equals(value)) {
            isMiUiNotchScreen = 1;
        } else {
            isMiUiNotchScreen = 0;
        }
        return isMiUiNotchScreen == 1;
    }

    public static String readSystemProperty(String key, String defval) {
        Properties properties = new Properties();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            try {
                properties.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
                return properties.getProperty(key, defval);
            } catch (IOException e) {
                MDLog.printErrStackTrace(LogTag.COMMON, e);
            }
        } else {
            //8.0以上系统，不能直接读取 build.prop，会报 Permission Denied 错误，只能反射来获取
            try {
                Class clzSystemProperties = Class.forName("android.os.SystemProperties");
                Method getMethod = clzSystemProperties.getDeclaredMethod("get", String.class);
                String value = inVoKeGetLowerCaseName(properties, getMethod, key);
                return !TextUtils.isEmpty(value) ? value : defval;
            } catch (Exception ex) {
                MDLog.printErrStackTrace(LogTag.COMMON, ex);
            }
        }
        return defval;
    }

    @Nullable
    private static String inVoKeGetLowerCaseName(Properties p, Method get, String key) {
        String name = p.getProperty(key);
        if (name == null) {
            try {
                name = (String) get.invoke(null, key);
            } catch (Exception ignored) {
            }
        }
        if (name != null) name = name.toLowerCase();
        return name;
    }

    /**
     * 根据MIUI官方提供的方法设置状态栏的颜色，支持 light和dark两种模式
     *
     * @param activity
     * @param darkmode
     * @return
     */
    public static boolean setMiuiStatusBarDarkMode(Activity activity, boolean darkmode) {
        if (!isMIUI() || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return false;
        }
        Class<? extends Window> clazz = activity.getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String uri2Path(final Context context, final Uri uri) {
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                                                        null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
