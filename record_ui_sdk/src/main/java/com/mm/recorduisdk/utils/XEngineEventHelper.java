package com.mm.recorduisdk.utils;

import android.view.MotionEvent;
import android.view.View;

import com.core.glcore.config.MediaModuleGlobalConfig;
import com.core.glcore.util.XEEngineHelper;
import com.mm.mediasdk.utils.UIUtils;
import com.momo.xeengine.XE3DEngine;
import com.momo.xeengine.xnative.XEWindow;

/**
 * on 2018/3/16.
 *
 * @author chen.weiwei
 */

public class XEngineEventHelper {

    public static boolean touchHitTest(float x, float y) {
        if (!MediaModuleGlobalConfig.hasXE()) {
            return false;
        }
        XE3DEngine xe3DEngine = XEEngineHelper.get();
        if (xe3DEngine == null) {
            return false;
        }

        XEWindow window = xe3DEngine.getWindow();
        if (window == null) {
            return false;
        }
        final int width = window.getWidth();
        final int height = window.getHeight();
        float x_ = x * width / UIUtils.getScreenWidth();
        float y_ = y * height / UIUtils.getScreenHeight();
        return window.handleTouchHitTest(x_, y_);
    }

    public static void handEvent(MotionEvent event, View view) {
        if (!MediaModuleGlobalConfig.hasXE()) {
            return;
        }
        XE3DEngine xe3DEngine = XEEngineHelper.get();
        if (xe3DEngine == null) {
            return;
        }

        XEWindow window = xe3DEngine.getWindow();
        if (window == null || event == null) {
            return;
        }

        window.handleTouchEvent(event, view);
    }
}
