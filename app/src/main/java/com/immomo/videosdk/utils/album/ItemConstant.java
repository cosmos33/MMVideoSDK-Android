package com.immomo.videosdk.utils.album;

/**
 * @author shidefeng
 * @since 2017/6/2.
 */

public class ItemConstant {

    // type
    public static final int TYPE_IMAGE      = 0x01;
    public static final int TYPE_VIDEO      = 0x01 << 1;
    public static final int TYPE_MASK       = TYPE_IMAGE | TYPE_VIDEO;
    // gif
    public static final int GIF_SHOW        = 0x01 << 2;
    public static final int GIF_HIDE        = 0x01 << 3;
    public static final int GIF_MASK        = GIF_SHOW | GIF_HIDE;

    // select state
    public static final int SELECT_HIDE     = 0x01 << 4;
    public static final int SELECT_SELECT   = 0x01 << 5;
    public static final int SELECT_UNSELECT = 0x01 << 6;
    public static final int SELECT_MASK     = SELECT_HIDE | SELECT_SELECT | SELECT_UNSELECT;
    // shadow
    public static final int STATUS_DISABLE  = 0x01 << 7;
    public static final int STATUS_ENABLE   = 0x01 << 8;
    public static final int STATUS_MASK     = STATUS_DISABLE | STATUS_ENABLE;
    // edit
    public static final int EDIT_SHOW       = 0x01 << 9;
    public static final int EDIT_HIDE       = 0x01 << 10;
    public static final int EDIT_MASK       = EDIT_SHOW | EDIT_HIDE;

}
