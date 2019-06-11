package com.mm.sdkdemo.recorder.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.cosmos.mdlog.MDLog;
import com.mm.mmutil.task.MomoMainThreadExecutor;
import com.mm.sdkdemo.log.LogTag;
import com.mm.sdkdemo.widget.FaceTipView;
import com.momo.mcamera.mask.AdditionalInfo;
import com.momo.mcamera.mask.TriggerTip;

/**
 * Created by XiongFangyu on 2017/9/5.
 */

public class RecordTipManager {
    private AdditionalInfo additionalInfo;
    private RecorderProxy recorder;
    private FaceTipView tipView;
//    private int faceTipRes = R.drawable.moment_record_face_tip_bg;

    private boolean raiseVolumeTip = false;
    private boolean volumeTipShowing = false, triggerTipShowed = false;
    private boolean triggerShowing = false;
    private boolean showFaceTip = false;
    private boolean faceTipShowed = false;
    private boolean isTransienceTipShowing = false;
    private boolean isTransienceTipShowed = false;
    private boolean faceDetected;

    private VolumeReceiver volumeReceiver;

    public RecordTipManager() {
    }

    public RecordTipManager(Context context) {
        volumeReceiver = new VolumeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.media.VOLUME_CHANGED_ACTION");
        context.registerReceiver(volumeReceiver, filter);
    }

    public void setTipView(FaceTipView tipView) {
        this.tipView = tipView;
    }

    public synchronized void setAdditionalInfo(@Nullable AdditionalInfo additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public synchronized AdditionalInfo getAdditionalInfo() {
        return this.additionalInfo;
    }

    public void setRecorder(RecorderProxy recorder) {
        this.recorder = recorder;
    }

    public void onFaceDetected(boolean hasFace) {
        if (volumeTipShowing) {
            return;
        }
        faceDetected = hasFace;
        if (needFaceDetectTip()) {
            refreshFaceTip();
        }
        if (hasFace && !triggerTipShowed && needFaceDetectTip()) {
            showTriggerTip();
        }
    }

    public void playStateChanged(boolean play) {
        if (play && !raiseVolumeTip) {
            if (needRaiseVolume()) {
                showVolumeTip();
            }
        } else {
            volumeTipShowing = false;
            hideVolumeTip();
        }
    }

    public void onClearMask() {
        refreshFaceTip();
    }

    public void onCameraSet() {
        if (!isFrontCamera()) {
            hideMouseTip();
        }

        if (needFaceDetectTip()) {
            refreshFaceTip();
        } else if (needTransienceTip()) {
            showTransienceTip();
        }
    }

    public synchronized void reset() {
        tipView.setVisibility(View.GONE);
        triggerTipShowed = false;
        triggerShowing = false;
        isTransienceTipShowed = false;
        isTransienceTipShowing = false;
        faceTipShowed = false;
        volumeTipShowing = false;
        MomoMainThreadExecutor.cancelAllRunnables(getTaskTag());
        additionalInfo = null;
    }

    public synchronized void release() {
        additionalInfo = null;
        recorder = null;
        tipView = null;
        MomoMainThreadExecutor.cancelAllRunnables(getTaskTag());
    }

    public void release(Context context) {
        release();
        try {
            if (volumeReceiver != null) {
                context.unregisterReceiver(volumeReceiver);
            }
        } catch (Exception ex) {
            MDLog.printErrStackTrace(LogTag.RECORDER.RECORD, ex);
        }
        volumeReceiver = null;
    }

    public synchronized boolean needFaceDetectTip() {
        if (additionalInfo != null) {
            AdditionalInfo.TipInfo tipInfo = isFrontCamera() ? additionalInfo.getFrontTip() : additionalInfo.getBackTip();
            if (tipInfo != null) {
                return tipInfo.isFaceTrack();
            }
        }

        return false;
    }

    private synchronized boolean needTransienceTip() {
        if (additionalInfo != null) {
            AdditionalInfo.TipInfo tipInfo = isFrontCamera() ? additionalInfo.getFrontTip() : additionalInfo.getBackTip();
            if (tipInfo != null) {
                return !tipInfo.isFaceTrack();
            }
        }

        return false;
    }

    private synchronized String getFirstLevelTip() {
        if (additionalInfo != null) {
            AdditionalInfo.TipInfo tipInfo = isFrontCamera() ? additionalInfo.getFrontTip() : additionalInfo.getBackTip();
            if (tipInfo != null) {
                return tipInfo.getContent();
            }
        }
        return null;
    }

    private synchronized String getTriggerTip() {
        if (additionalInfo != null) {
            AdditionalInfo.TipInfo tipInfo = isFrontCamera() ? additionalInfo.getFrontTip() : additionalInfo.getBackTip();
            if (tipInfo != null) {
                TriggerTip triggerTip = tipInfo.getTriggerTip();
                if (triggerTip != null && triggerTip.getContent() != null) {
                    return triggerTip.getContent();
                }
            }
        }
        return null;
    }

    private synchronized void refreshFaceTip() {
        MomoMainThreadExecutor.post(getTaskTag(), new Runnable() {
            @Override
            public void run() {
                if (additionalInfo == null) {
                    hideTipView();
                    volumeTipShowing = false;
                    return;
                }
                if (volumeTipShowing || triggerShowing || isTransienceTipShowing) {
                    return;
                }
                if (faceDetected || isDefaultFace()) {
                    hideTipView();
                    showFaceTip = false;
                } else if (!faceTipShowed) {
                    showTipView();
                    if (tipView != null)
                        tipView.setText(getFirstLevelTip());
                    setTipViewTopDrawable();
                    showFaceTip = true;
                    faceTipShowed = true;
                }
                volumeTipShowing = false;
            }
        });
    }

    private void showTriggerTip() {
        if (volumeTipShowing || triggerTipShowed || triggerShowing) {
            return;
        }
        MomoMainThreadExecutor.cancelAllRunnables(getTaskTag());
        MomoMainThreadExecutor.post(getTaskTag(), new Runnable() {
            @Override
            public void run() {
                if (triggerTipShowed || triggerShowing) {
                    return;
                }
                String triggerTipContent = getTriggerTip();
                if (!TextUtils.isEmpty(triggerTipContent)) {
                    showFaceTip = false;
                    showTipView();
                    if (tipView != null)
                        tipView.setText(triggerTipContent);
                    triggerTipShowed = true;
                    triggerShowing = true;
                    clearTipViewDrawable();
                } else {
                    triggerShowing = false;
                    if (!showFaceTip) {
                        hideTipView();
                    }
                }
            }
        });

        MomoMainThreadExecutor.postDelayed(getTaskTag(), new Runnable() {
            @Override
            public void run() {
                if (triggerShowing) {
                    triggerShowing = false;
                    hideTipView();
                }
            }
        }, 2000);
    }

    private void showTransienceTip() {
        if (volumeTipShowing || isTransienceTipShowing || isTransienceTipShowed) {
            return;
        }

        MomoMainThreadExecutor.cancelAllRunnables(getTaskTag());
        MomoMainThreadExecutor.post(getTaskTag(), new Runnable() {
            @Override
            public void run() {
                if (isTransienceTipShowing || isTransienceTipShowed) {
                    return;
                }
                isTransienceTipShowing = true;
                isTransienceTipShowed = true;

                String transienceTip = getFirstLevelTip();
                if (!TextUtils.isEmpty(transienceTip)) {
                    showTipView();
                    if (tipView != null)
                        tipView.setText(transienceTip);
                    clearTipViewDrawable();
                }
            }
        });

        MomoMainThreadExecutor.postDelayed(getTaskTag(), new Runnable() {
            @Override
            public void run() {
                if (isTransienceTipShowing) {
                    isTransienceTipShowing = false;
                    hideTipView();
                }
            }
        }, 2000);
    }

    private void showVolumeTip() {
        volumeTipShowing = true;
        MomoMainThreadExecutor.cancelAllRunnables(getTaskTag());
        MomoMainThreadExecutor.post(getTaskTag(), new Runnable() {
            @Override
            public void run() {
                showTipView();
                clearTipViewDrawable();
                if (tipView != null)
                    tipView.setText("请开大手机音量");
            }
        });
    }

    private void hideVolumeTip() {
        volumeTipShowing = false;
        MomoMainThreadExecutor.post(getTaskTag(), new Runnable() {
            @Override
            public void run() {
                if (tipView != null)
                    tipView.setVisibility(View.GONE);
            }
        });
    }

    private boolean needRaiseVolume() {
        if (tipView == null)
            return false;
        try {
            AudioManager am = (AudioManager) tipView.getContext().getSystemService(Context.AUDIO_SERVICE);
            //获取多媒体的声音大小
            if (am != null) {
                int maxValue = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                int curValue = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                return maxValue > 0 && curValue < (maxValue * 0.5);
            }
        } catch (Exception ex) {
            MDLog.printErrStackTrace(LogTag.RECORDER.RECORD, ex);
        }
        return false;
    }

    private void hideMouseTip() {
        hideTipView();
        volumeTipShowing = false;
    }

    private void showBeautying() {
        if (tipView != null)
            tipView.setText("正在为你美颜");
        setTipViewTopDrawable();
    }

    private void hideTipView() {
        if (tipView == null)
            return;
        if (tipView.getVisibility() == View.VISIBLE) {
            tipView.setVisibility(View.GONE);
            tipView.setText("");
            clearTipViewDrawable();
        }
    }

    private void showTipView() {
        if (tipView == null)
            return;
        if (tipView.getVisibility() != View.VISIBLE) {
            tipView.setVisibility(View.VISIBLE);
        }
    }

    private void clearTipViewDrawable() {
        if (tipView != null)
            tipView.showBg(false);
    }

    private void setTipViewTopDrawable() {
        if (tipView != null)
            tipView.showBg(true);
    }

    private boolean isFrontCamera() {
        return recorder != null && recorder.isFrontCamera();
    }

    private boolean isDefaultFace() {
        return recorder != null && recorder.isDefaultFace();
    }

    private Object getTaskTag() {
        return hashCode();
    }

    private class VolumeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), "android.media.VOLUME_CHANGED_ACTION")) {
                if (!needRaiseVolume() && volumeTipShowing) {
                    hideVolumeTip();
                }
            }
        }
    }

    public interface RecorderProxy {
        boolean isFrontCamera();

        boolean isDefaultFace();
    }
}
