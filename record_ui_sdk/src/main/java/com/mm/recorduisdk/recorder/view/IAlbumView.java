package com.mm.recorduisdk.recorder.view;

import android.content.Context;
import android.os.Bundle;

import com.mm.base_business.base.BaseFragment;
import com.mm.recorduisdk.utils.album.ScanResult;

/**
 * Created by chenxin on 2018/8/31.
 */

public interface IAlbumView {

    Context getContext();

    void changeFragment(BaseFragment old, Bundle extra);

    void onSelectClick(int count, String sendText);

    void onMultiMediaLoad(ScanResult scanResult);
}
