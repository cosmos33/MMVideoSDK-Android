package com.mm.recorduisdk.local_music_picker.view.adapter;

import android.view.View;

/**
 * Created by XK on 16/5/30.
 * <p>
 * recycler view item内部button点击监听
 */
public interface OnItemButtonClickListener {
    /**
     * Called when a view has been clicked.
     *
     * @param v        the view that was clicked.
     * @param position the position that the view in.
     */
    void onClick(View v, int position);
}
