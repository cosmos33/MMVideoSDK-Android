package com.mm.sdkdemo.bean;

/**
 * Created by tanjie on 2015-11-04.
 *
 * @author tanjie
 */
public interface VideoRecordDefs {
    int VALUE_INTENT_FROM_CHAT = 0;
    int VALUE_INTENT_FROM_TOPIC_FEED = 1;
    int VALUE_INTENT_FROM_FEED = 2;
    int VALUE_INTENT_FROM_CIRCLE = 4;

    int REQ_GO_TO_VIDEO_DRAFT = 4;

    int VIDEO_TYPE_FROM_CHAT = 5;
    int VIDEO_TYPE_FROM_DRAFT = 6;
    int VIDEO_TYPE_FROM_LOCAL = 7;

    int REQ_LOCAL_VIDEO = 8;

    int REQ_CUT_VIDEO = 9;

    int VIDEO_TYPE_FROM_LIVING = 10;

    String KEY_VIDEO = "key_video_";

    //视频提示时间点
    String VIDEO_LENGTH_TIME = "VIDEO_LENGTH_TIME";
    String VIDEO_MIN_CUT_TIME = "VIDEO_MIN_CUT_TIME";

    String KEY_FILE_PATH = "key_filepath";
    String KEY_INTENT_FROM = "key_intent_from";
    String KEY_VIDEO_GOTO_DATA = "key_video_goto_data";

    String KEY_VIDEO_GOTO_EVENTID = "key_video_goto_eventID";


    //视频录制完了，需要回复frame，和source，麻烦确认

    String KEY_VIDEO_FINAL_FRAME_ID = "key_video_final_frame_id";//录制视频时最终合成的水印ID
    String KEY_VIDEO_FIRST_FRAME_ID = "key_video_first_frame_id";//录制视频时界面选择的水印

    /**
     * 录制视频是是否采用的是前置摄像头 true是前置，false是后置摄像头
     */
    String KEY_VIDEO_CAMERA_FRONT = "key_video_camera_front";

    /**
     * 视频是否使用了美颜效果
     */
    String KEY_VIDEO_APPLY_MEIYAN="key_video_apply_meiyan";

    String KEY_VIDEO_CANSHARE_STRING = "key_video_canshare";
    String KEY_VIDEO_LOCATION_STRING = "key_video_location";

    String KEY_VIDEO_TIME = "key_video_time";

    String KEY_VIDEO_TYPE = "key_video_type";

    //临时文件的路径
    String IN_TEMP_VIDEO_PATH = "key_in_tmp_video_path";
    String IN_SELECT_COVER_SRC = "key_in_select_cover_src";

    String KEY_IS_IMPORT_VIDEO = "key_is_import_video";

    int VR_START_PREVIEW = 1234;
}
