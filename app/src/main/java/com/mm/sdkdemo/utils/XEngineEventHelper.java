package com.mm.sdkdemo.utils;

import android.view.MotionEvent;

import com.core.glcore.config.MediaModuleGlobalConfig;
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
        XEWindow window = XE3DEngine.getInstance().getWindow();
        if (window == null) {
            return false;
        }
        final int width = window.getWidth();
        final int height = window.getHeight();
        float x_ = x * width / UIUtils.getScreenWidth();
        float y_ = y * height / UIUtils.getScreenHeight();
        return window.handleTouchHitTest(x_, y_);
    }

    public static void handEvent(MotionEvent event) {
        if (!MediaModuleGlobalConfig.hasXE()) {
            return;
        }
        XEWindow window = XE3DEngine.getInstance().getWindow();
        if (window == null || event == null) {
            return;
        }
        final int width = window.getWidth();
        final int height = window.getHeight();
        final int pointerNumber = event.getPointerCount();
        final int[] ids = new int[pointerNumber];
        final float[] xs = new float[pointerNumber];
        final float[] ys = new float[pointerNumber];
        for (int i = 0; i < pointerNumber; i++) {
            ids[i] = event.getPointerId(i);
            xs[i] = event.getX(i) * width / UIUtils.getScreenWidth();
            ys[i] = event.getY(i) * height / UIUtils.getScreenHeight();
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_DOWN:
                window.handleTouchesBegin(pointerNumber, ids, xs, ys);
                break;
            case MotionEvent.ACTION_DOWN:
                window.handleTouchesBegin(pointerNumber, ids, xs, ys);
                break;
            case MotionEvent.ACTION_MOVE:
                window.handleTouchesMove(pointerNumber, ids, xs, ys);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                final int indexPointUp = event.getAction() >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                if (indexPointUp != 0) {
                    break;
                }
                window.handleTouchesEnd(pointerNumber, ids, xs, ys);
                break;
            case MotionEvent.ACTION_UP:
                window.handleTouchesEnd(pointerNumber, ids, xs, ys);
                break;
            case MotionEvent.ACTION_CANCEL:
                window.handleTouchesCancel(pointerNumber, ids, xs, ys);
                break;
            default:
        }
    }
}
