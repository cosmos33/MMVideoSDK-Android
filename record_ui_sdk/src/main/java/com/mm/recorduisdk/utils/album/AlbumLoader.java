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
import android.net.Uri;
import android.provider.MediaStore;

import androidx.loader.content.CursorLoader;

/**
 * Load images and videos into a single cursor.
 */
public class AlbumLoader extends CursorLoader {

    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");

    private static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DATE_ADDED,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.MIME_TYPE,
            "bucket_id",
            "bucket_display_name",
            "duration",
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT};

    private static final String ORDER_BY = MediaStore.Files.FileColumns.DATE_ADDED + " DESC";

    public AlbumLoader(Context context, int type) {
        super(context);
        setUri(QUERY_URI);
        setProjection(PROJECTION);
        setSortOrder(ORDER_BY);
        setSelectionAndArgs(type);
    }

    private void setSelectionAndArgs(int type) {
        String selection;
        String[] selectionArgs;
        switch (type) {

            case AlbumConstant.MEDIA_TYPE_IMAGE:
                selection = "("
                        + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?) AND "
                        + MediaStore.MediaColumns.SIZE + ">0";
                selectionArgs = new String[]{
                        "" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE};
                break;

            case AlbumConstant.MEDIA_TYPE_VIDEO:
                selection = "("
                        + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?) AND "
                        + MediaStore.MediaColumns.SIZE + ">0";
                selectionArgs = new String[]{
                        "" + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO};
                break;

            case AlbumConstant.MEDIA_TYPE_MIXED:
            default:
                selection = "("
                        + MediaStore.Files.FileColumns.MEDIA_TYPE + "=? OR "
                        + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?) AND "
                        + MediaStore.MediaColumns.SIZE + ">0";
                selectionArgs = new String[]{
                        "" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE,
                        "" + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO};
                break;
        }

        setSelection(selection);
        setSelectionArgs(selectionArgs);
    }
}
