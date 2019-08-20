package com.mm.recorduisdk.recorder;

/**
 * Project momodev
 * Package com.mm.momo.moment
 * Created by tangyuchun on 7/25/16.
 */
public class MediaConstants {
    public final static long MIN_CUT_VIDEO_DURATION = 30000;    //裁切限制
    public final static int BIT_RATE_FOR_CUT_VIDEO = 6<<20;
    public final static int DEFAULT_FRAME_RATE = 20;

    public final static int MOMENT_DURATION_EXPAND = 999;       //放宽999ms限制

    public final static long MIN_VIDEO_DURATION = 2000;//最短2s

    public final static long MAX_PUBLISH_VIDEO_DURATION = 60000 + MOMENT_DURATION_EXPAND;    //发布时刻最大时长60S 视频库处理不精准，放宽限制
    public final static int MAX_VIDEO_FRAME_RATE = 40;      //本地文件帧率限制-1 不限制

    /**
     * 高级拍摄最长时长，可通过服务器配置
     */
    public static long MAX_VIDEO_DURATION = 60000;//最长60s
    /**
     * 普通拍摄最长时长，可通过服务器配置
     */
    public static long DEFUALT_RECORD_DURATION = 20000;
    //最大裁切时长
    public static long MAX_LONG_VIDEO_DURATION = 60000;

    //新模式
    public final static long UPPER_VIDEO_COMPRESS_DURATION = 60000;       // 视频码率 <= 5M && 视频时长 <= 60s 不需要压缩
    public final static long UPPER_VIDEO_COMPRESS_BITRATE = 5<<20;     // 视频码率 <= 5M && 视频时长 <= 60s 不需要压缩

    public static final int MAX_STICKER_COUNT = 20;//最多添加20个

    public static final String KEY_VIDEO_PATH = "video_path";

    /**
     * 进入时刻录制页面的来源
     */
    public static final String KEY_RECORD_FROM = "moment_record_from";
    /**
     * 选择时刻封面时，指定的封面保存路径
     */
    public static final String KEY_OUTPUT_COVER_PATH = "output_cover_path";
    public static final String KEY_SELECTED_COVER_POS = "moment_selected_cover_pos";

    public static final String MEDIA_TYPE_VIDEO = "VIDEO";
    public static final String MEDIA_TYPE_IMAGES = "IMAGE";
    public static final String EXTRA_KEY_VIDEO_DATA = "EXTRA_KEY_VIDEO_DATA";
    public static final String EXTRA_KEY_IMAGE_DATA = "EXTRA_KEY_IMAGE_DATA";
    public static final String EXTRA_KEY_VIDEO_TRANS_INFO = "EXTRA_KEY_VIDEO_TRANS_INFO";
    public static final String KEY_RESULT_CODE = "KEY_RESULT_CODE";
    public static final String KEY_PICKER_VIDEO = "key_cut_video";
    public static final String KEY_CUT_VIDEO_RESULT = "key_cut_video_result";
    public static final String KEY_SKIP_SWITCH_FACE = "key_skip_switch_face";
    public static final String KEY_IS_CHANGE_SPEED = "key_is_change_speed";

    public static final String KEY_RESTORE_VIDEO_PATH = "key_restore_video_path";

    public static final String KEY_JUST_EDIT = "KEY_JUST_EDIT";

    public static final String KEY_VIDEO_SPEED_PARAMS = "KEY_VIDEO_SPEED_PARAMS";


    public static final String KEY_RECORD_PARAMS = "key_record_params";

    public static final String KEY_VIDEO_EDIT_PARAMS = "key_video_edit_params";

    public static final String KEY_IMAGE_EDIT_PARAMS = "key_image_edit_params";

    public static final String KEY_CHOOSE_MEDIA_PARAMS = "key_choose_media_params";

    public static final String KEY_CACHE_EXTRA_PARAMS = "key_cache_extra_params";
}
