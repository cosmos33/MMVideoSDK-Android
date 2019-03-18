package com.mm.sdkdemo.recorder.view;

/**
 * Created by wangduanqing on 2019/2/10.
 */

public interface IMomoRecordView extends IRecordView {

    void initFacePanel(boolean hasInitFace);

    void initFlashAndSwitchButton();

    long getRecordDuration();
}
