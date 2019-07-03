package com.mm.sdkdemo.utils.album;

import android.support.annotation.IntDef;

/**
 * @author shidefeng
 * @since 2017/6/6.
 */

public class AlbumConstant {

    /* ================ start arguments ================ */
    /**
     * 封装传入的条件及数据
     */
    public static final String KEY_MEDIA_CONDITIONS = "MEDIA_CONDITIONS";

    /* ================ constant ================ */
    public static final int INDEX_ALL_CATEGORY = 0;
    public static final int INDEX_VIDEO_CATEGORY = 1;
    public static final int INDEX_PICTURE_CATEGORY = 2;
    //public static final int MAX_VIDEO_FRAME_RATE = MomentConstants.MAX_VIDEO_FRAME_RATE;
    public static final int MAX_VIDEO_FRAME_RATE = 61; // 支持fps = 60帧的视频导入
    public static final int MAX_SELECTED_COUNT = 10; // 最多能选择图片数

    /* ================ media type ================ */
    public static final int MEDIA_TYPE_IMAGE = 0x01;
    public static final int MEDIA_TYPE_VIDEO = 0x01 << 1;
    public static final int MEDIA_TYPE_MIXED = MEDIA_TYPE_IMAGE | MEDIA_TYPE_VIDEO;

    /* ================ extra data ================ */
    /**
     * 选择的图片集合
     */
    public static final String KEY_SELECTED_MEDIA_PATHS = "SELECTED_MEDIA_PATHS";
    /**
     * 选择的图片的路径集合
     */
    public static final String KEY_SELECTED_MEDIAS = "SELECTED_MEDIAS";
    /**
     * 是否直接发送
     */
    public static final String KEY_SEND_DIRECT = "SEND_DIRECT";

    public static final String KEY_VIEW_INDEX = "key_view_index";
    public static final String KEY_MAX_SELECT_COUNT = "key_max_select_count";
    /**
     * 选择原图模式
     * 0：单独勾选
     * 1：全局模式
     * 2：不能选择原图
     */
    public static final String KEY_SELECT_ORIGIN_MODE = "key_select_origin_mode";

    public static final String KEY_RESULT_MEDIA_LIST = "key_result_media_list";
    /**
     * 预览页面直接点击发送，带回KEY_RESULT_MEDIA_LIST
     */
    public static final String KEY_RESULT_IS_PUBLISH = "key_result_is_publish";
    /**
     * 图片编辑结果
     */
    public static final String KEY_RESULT_IMAGE_EDIT = "key_result_image_edit";
    public static final String KEY_EDIT_MEDIA = "key_edit_media";
    public static final String KEY_FINISH_TEXT = "key_finish_text";

    /**
     * 是否来着数码宝贝
     */
    public static final String KEY_IS_FROM_DIGIMON = "key_is_from_digimon";

    public static final String KEY_IS_FROM_ARPET = "key_is_from_arpet";

    /**
     * 默认模式，单个原图可选
     */
    public static final int ORIGIN_MODE_DEFAULT = 0;
    /**
     * 原图全局模式
     */
    public static final int ORIGIN_MODE_GLOBAL = 1;
    /**
     * 不能选择原图
     */
    public static final int ORIGIN_MODE_FORBIDDEN = 2;

    @IntDef({ORIGIN_MODE_DEFAULT, ORIGIN_MODE_GLOBAL, ORIGIN_MODE_FORBIDDEN})
    public @interface OriginMode {

    }

    /**
     * 完成/发送按钮的文案
     */
    public static final String KEY_BUTTON_TEXT = "key_button_text";

    public static final String DIRECTORY_ID_ALL = "ALL";
    public static final String DIRECTORY_ID_PICTURE = "照片";
    public static final String DIRECTORY_ID_VIDEO = "视频";
    public static final String DIRECTORY_NAME_ALL = "相册";
    public static final String DIRECTORY_NAME_PICTURE = "照片";
    public static final String DIRECTORY_NAME_VIDEO = "视频";

    public static final String DIRECTORY_NAME__PICTURE_ALL = "影集";
}

