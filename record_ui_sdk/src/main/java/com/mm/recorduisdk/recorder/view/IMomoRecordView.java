package com.mm.recorduisdk.recorder.view;

import com.immomo.moment.model.VideoFragment;

import java.util.List;

/**
 * Created by wangduanqing on 2019/2/10.
 */

public interface IMomoRecordView extends IRecordView {

    void initFacePanel(boolean hasInitFace);

    void initFlashAndSwitchButton();

    long getRecordDuration();

    void restoreByFragments(List<VideoFragment> fragments);
}
