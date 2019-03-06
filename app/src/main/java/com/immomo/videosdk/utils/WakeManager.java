package com.immomo.videosdk.utils;

import android.app.Activity;
import android.content.Context;
import android.os.PowerManager;
import android.view.WindowManager;

import static android.content.Context.POWER_SERVICE;

/**
 * Created by qi.xing on 2018/1/12.
 * description :唤醒管理
 * 单例模式
 */

public class WakeManager {
    private static final String TAG = "MyWakeManager";

    private static WakeManager wakeManager;

    private PowerManager.WakeLock wakeLock;

    public static WakeManager getInstance() {
        if (wakeManager == null) {
            synchronized (WakeManager.class) {
                if (wakeManager == null) {
                    wakeManager = new WakeManager();
                }
            }
        }
        return wakeManager;
    }

    private WakeManager() {

    }

    /**
     * activity常亮
     * 不用去取消，系统会自动管理，除非手动想取消常亮
     *
     * @param activity
     */
    public void keepScreenOn(final Activity activity) {
        if (activity != null) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    /**
     * 取消屏幕常亮
     * 想手动取消，其他情况WindowManager会自动管理
     * 进过测试，activity进入stop状态，常亮会失去作用，pause状态不会
     *
     * @param activity
     */
    public void cancelKeepScreenOn(final Activity activity) {
        if (activity != null) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    /**
     * 保持唤醒CPU
     *
     * @param context 获取服务使用的上下文，建议使用application的context
     * @param timeout 获取唤醒CPU超时时间，必须传
     * @return 是否保持唤醒成功
     */
    public boolean wakeLockCpu(final Context context, final long timeout) {
        if (wakeLock == null) {
            try {
                PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
                // 创建唤醒锁
                if (powerManager == null) {
                    return false;
                }
                wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
            } catch (SecurityException ex) {
                return false;
            } finally {

            }
        }
        if (wakeLock == null) {
            return false;
        }
        wakeLock.acquire(timeout);
        return true;
    }

    /**
     * 释放CPU保持唤醒
     * 原则上与wakeLockCpu成对出现
     * wakeLockCpu会超时自动释放
     * 释放需要捕获异常
     */
    public void releaseWakeLockCpu() {
        if (wakeLock == null) {
            return;
        }
        try {
            wakeLock.release();
        } catch (RuntimeException ex) {

        } finally {

        }
    }
}
