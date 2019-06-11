package com.mm.sdkdemo.local_music_picker.view.adapter;

import android.view.View;

/**
 * item长按监听
 */
public interface OnItemLongClickListener {
    /**
     * Called when a view has been clicked and held.
     *
     * @param v        The view that was clicked and held.
     * @param position the position that the view in.
     * @return true if the callback consumed the long click, false otherwise.
     */
    boolean onLongClick(View v, int position);
}