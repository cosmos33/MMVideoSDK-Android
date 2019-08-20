package com.mm.recorduisdk.recorder.helper;

/**
 * Created by chenwangwang on 2018/3/27.
 */
public class MomentFaceConstants {

    /**
     * Json中用来存放最后修改时间的key值
     */
    public static final String ROOT_NAME = "dir";
    /**
     * 用来记录文件夹最后修改时间的文件名称
     */
    public static final String LAST_MODIFY_NAME = "LM";

    /**
     * 普通变脸素材的缓存文件名称
     */
    public static final String COMMON_CACHE_FILE_NAME = "moment_face_configs_v2";

    /**
     * @see #SINGLE_FACE
     */
    public static final String RANDOM_FACE_CACHE_FILE_NAME = "random_face_configs_v2";
    public static final String FRIEND_FACE_CACHE_FILE_NAME = "friend_face_configs_v2";
    public static final String SQUARE_FACE_CACHE_FILE_NAME = "square_face_configs_v2";

    /**
     * 录制页面的
     */
    public static final String RECOMMEND_FACE_CACHE_FILE_NAME = "moment_recommend_face_configs";

    /**
     * 变脸面板类型（普通变脸面板）
     */
    public static final int MOMENT_FACE = 15;

    public static final String FACE_FROM_RANDOM = "kliao_single_random";
    public static final String FACE_FROM_FRIEND = "kliao_single_friend";
    public static final String FACE_FROM_SQUARE = "kliao_single_square";
}
