package com.mm.recorduisdk.utils;

import android.view.ViewStub;
import android.widget.TextView;

import com.immomo.moment.recorder.MultiRecorder;

/**
 * Created by XiongFangyu on 2017/8/14.
 */

public class TestTextHelper {

    private TextView testTV;

    public TestTextHelper(ViewStub viewStub) {
        testTV = (TextView) viewStub.inflate();
    }

    public void setCameraPreviewInfo(MultiRecorder.PreviewInfo previewInfo) {
        if (previewInfo == null)
            return;
        StringBuilder sb = new StringBuilder("show in debug\n").append(previewInfo.toString());
        testTV.setText(sb.toString());
    }

    public void print(String msg) {
        testTV.setText("show in debug\n" + msg);
    }
}
