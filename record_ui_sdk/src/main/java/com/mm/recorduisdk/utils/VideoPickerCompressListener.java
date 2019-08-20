package com.mm.recorduisdk.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.mm.mediasdk.utils.UIUtils;
import com.mm.mmutil.toast.Toaster;
import com.mm.recorduisdk.bean.MMVideoEditParams;
import com.mm.recorduisdk.recorder.model.Video;

import java.lang.ref.WeakReference;

/**
 * Created by XiongFangyu on 2017/6/10.
 */
public class VideoPickerCompressListener implements VideoCompressUtil.CompressVideoListener {
    private WeakReference<Context> contextRef;
    private ProgressDialog progressDialog;
    private boolean isCompressing = false;

    private OnCompressListener onCompressListener;
    private MMVideoEditParams videoEditParams;
    private int requestCode;

    public VideoPickerCompressListener(Context context, MMVideoEditParams videoEditParams, OnCompressListener listener, int requestCode) {
        contextRef = new WeakReference<>(context);
        onCompressListener = listener;
        this.videoEditParams = videoEditParams;
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
            if (videoEditParams != null){
                videoEditParams = new MMVideoEditParams.Builder(videoEditParams,video).build();
            }
            onCompressListener.onFinish(getContext(), success, videoEditParams, requestCode);
        }
        onCompressListener = null;
    }

    public interface OnCompressListener {
        void onFinish(Context context, boolean success, MMVideoEditParams info, int requestCode);
    }
}
