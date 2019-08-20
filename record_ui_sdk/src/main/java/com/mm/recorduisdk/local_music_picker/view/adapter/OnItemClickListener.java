package com.mm.recorduisdk.local_music_picker.view.adapter;

import android.view.View;

/**
 * item点击监听
 */
public interface OnItemClickListener {
    /**
     * Called when a view has been clicked.
     *
     * @param v        the view that was clicked.
     * @param position the position that the view in.
     */
    void onClick(View v, int position);
}