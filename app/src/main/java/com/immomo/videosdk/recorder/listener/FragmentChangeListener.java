package com.immomo.videosdk.recorder.listener;

import android.os.Bundle;

import com.immomo.videosdk.base.BaseFragment;

/**
 * Created by XiongFangyu on 17/2/21.
 */
public interface FragmentChangeListener {
    void change(BaseFragment old, Bundle extra);
}
