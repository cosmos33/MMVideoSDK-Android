package com.mm.sdkdemo.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.immomo.mmutil.toast.Toaster;
import com.mm.mediasdk.utils.UIUtils;
import com.mm.sdkdemo.bean.VideoInfoTransBean;
import com.mm.sdkdemo.recorder.model.Video;

import java.lang.ref.WeakReference;

/**
 * Created by XiongFangyu on 2017/6/10.
 */
public class VideoPickerCompressListener implements VideoCompressUtil.CompressVideoListener {
    private WeakReference<Context> contextRef;
    private ProgressDialog progressDialog;
    private boolean isCompressing = false;

    private OnCompressListener onCompressListener;
    private VideoInfoTransBean info;
    private int requestCode;

    public VideoPickerCompressListener(Context context, VideoInfoTransBean info, OnCompressListener listener, int requestCode) {
        contextRef = new WeakReference<>(context);
        onCompressListener = listener;
        this.info = info;
        this.requestCode = requestCode;
    }

    @Override
    public void onStartCompress() {
        showProgress();
        isCompressing = true;
    }

    @Override
    public void onUpdateCompress(float progress) {
        if (progress > 1.0f) {
            progress = 1.0f;
        }
        String str = "正在压缩 " + (int) (progress * 100) + "%";
        if (isCompressing) {
            showD();
            progressDialog.setMessage(str);
        }
    }

    @Override
    public void onFinishCompress(Video result, boolean hasTranscoding) {
        isCompressing = false;
        hideProgress();
        if (VideoUtils.getVideoMetaInfo(result)) {
            notifyListener(true, result);
        } else {
            Toaster.show("压缩异常，请稍后再试");
            VideoUtils.deleteTempFile(result.path);
            notifyListener(false, null);
        }
    }

    @Override
    public void onErrorCompress(Video result) {
        isCompressing = false;
        Toaster.show("压缩异常，请稍后再试");
        hideProgress();
        VideoUtils.deleteTempFile(result.path);
        notifyListener(false, null);
    }

    private void showProgress() {
        Context c = getContext();
        if (progressDialog == null && c != null) {
            progressDialog = new ProgressDialog(c);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    isCompressing = false;
                    VideoCompressUtil.stopCompress();
                    Toaster.showInvalidate("已停止压缩", Toaster.LENGTH_SHORT);
                    hideProgress();
                    notifyListener(false, null);
                }
            });
        }
        progressDialog.setMessage("视频压缩中......");
        progressDialog.getWindow().setLayout(UIUtils.getPixels(170), UIUtils.getPixels(50));
        showD();
    }

    private void showD() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private Context getContext() {
        return contextRef != null ? contextRef.get() : null;
    }

    private void notifyListener(boolean success, Video video) {
        if (onCompressListener != null) {
            if (info != null)
                info.video = video;
            onCompressListener.onFinish(getContext(), success, info, requestCode);
        }
        onCompressListener = null;
    }

    public interface OnCompressListener {
        void onFinish(Context context, boolean success, VideoInfoTransBean info, int requestCode);
    }
}
