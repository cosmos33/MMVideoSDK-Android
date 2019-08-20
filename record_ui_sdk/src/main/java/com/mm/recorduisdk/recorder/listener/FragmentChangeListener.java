package com.mm.recorduisdk.recorder.listener;

import android.os.Bundle;

import com.mm.base_business.base.BaseFragment;

/**
 * Created by XiongFangyu on 17/2/21.
 */
public interface FragmentChangeListener {
    void change(BaseFragment old, Bundle extra);
}
