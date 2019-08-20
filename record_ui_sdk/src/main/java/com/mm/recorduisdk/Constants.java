package com.mm.recorduisdk;

import android.hardware.Camera;

import com.core.glcore.config.MRConfig;

/**
 * Created on 2019/5/21.
 *
 * @author jianxi[mabeijianxi@gmail.com]
 */
public class Constants {
    public interface Resolution {
        int RESOLUTION_720 = MRConfig.VideoResolution.RESOLUTION_1280;
        int RESOLUTION_540 = MRConfig.VideoResolution.RESOLUTION_960;
        int RESOLUTION_480 = MRConfig.VideoResolution.RESOLUTION_640;
        int RESOLUTION_1080 = MRConfig.VideoResolution.RESOLUTION_1920;
    }

    public interface CameraType {
        int FRONT = Camera.CameraInfo.CAMERA_FACING_FRONT;
        int BACK = Camera.CameraInfo.CAMERA_FACING_BACK;
    }

    public interface VideoRatio {
        int RATIO_1X1 = 0;
        int RATIO_3X4 = 1;
        int RATIO_9X16 = 2;
    }

    public interface BeautyFaceVersion {
        int V1 = 0;
        int V2 = 1;
        int V3 = 2;
    }

    public interface RecordTab {
        int PHOTO = 0;
        int VIDEO = 1;
    }

    public interface EditChooseMode {
        int MODE_STYLE_ONE = 1;// 单选
        int MODE_MULTIPLE = 2;//多选
    }

    public interface EditChooseMediaType {
        int MEDIA_TYPE_IMAGE = 0x01;
        int MEDIA_TYPE_VIDEO = 0x01 << 1;
        int MEDIA_TYPE_MIXED = MEDIA_TYPE_IMAGE | MEDIA_TYPE_VIDEO;
    }

    public interface ShowMediaTabType {
        int STATE_ALBUM = 0x0002;
        int STATE_VIDEO = 0x0004;
        int STATE_ALL = STATE_ALBUM | STATE_VIDEO;
    }

    public interface SPKey{
         interface Moment{
            String KEY_MOMENT_FACE_VERSION= "key_moment_face_version";
        }
    }
}
