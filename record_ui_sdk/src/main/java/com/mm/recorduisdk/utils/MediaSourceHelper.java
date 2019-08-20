package com.mm.recorduisdk.utils;

import com.mm.recorduisdk.recorder.model.LatLonPhotoList;
import com.mm.recorduisdk.recorder.model.Photo;

import java.util.List;

/**
 * Created by wangrenguang on 2017/6/10.
 */

public class MediaSourceHelper {

    public static List<Photo> sAllMedias;

    public static LatLonPhotoList sLatLonMedias;

    public static void reset() {
        if (sAllMedias != null) {
            sAllMedias = null;
        }
    }

    public static void resetLatLon() {
        if (sLatLonMedias != null) {
            sLatLonMedias = null;
        }
    }

}
