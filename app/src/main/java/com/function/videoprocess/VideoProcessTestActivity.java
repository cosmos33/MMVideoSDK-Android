package com.function.videoprocess;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.immomo.mdlog.MDLog;
import com.mm.mediasdk.IVideoProcessor;
import com.mm.mediasdk.MoMediaManager;
import com.mm.mediasdk.filters.FilterUtils;
import com.mm.mediasdk.videoprocess.MoVideo;
import com.immomo.mmutil.app.AppContext;
import com.immomo.mmutil.toast.Toaster;
import com.immomo.moment.config.MRecorderActions;
import com.mm.sdkdemo.R;
import com.mm.sdkdemo.recorder.activity.BaseFullScreenActivity;
import com.mm.sdkdemo.utils.DeviceUtils;
import com.mm.sdkdemo.utils.filter.FiltersManager;
import com.momo.mcamera.filtermanager.MMPresetFilter;
import com.momo.mcamera.mask.MaskModel;
import com.momo.mcamera.mask.MaskStore;

import java.io.File;
import java.util.List;

import project.android.imageprocessing.filter.BasicFilter;

public class VideoProcessTestActivity extends BaseFullScreenActivity {
    private final String TAG = "VideoProcessTestActivity";
    private IVideoProcessor videoProcessor;
    private BasicFilter lastFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_process_test);
        videoProcessor = MoMediaManager.createVideoProcessor(null);
        videoProcessor.setLoopBack(true);
        videoProcessor.setOutVideoInfo(720, 1280, 30, 4<<20);
        videoProcessor.setOnProcessErrorListener(new MRecorderActions.OnProcessErrorListener() {
            @Override
            public void onErrorCallback(int what, int errorCode, String msg) {
                MDLog.e(TAG, "onErrorCallback %d", what);
            }
        });

        videoProcessor.setOnStatusListener(new MRecorderActions.OnProcessProgressListener() {
            @Override
            public void onProcessProgress(float progress) {
                MDLog.e(TAG, "onProcessProgress %f", progress);
            }

            @Override
            public void onProcessFinished() {
                MDLog.e(TAG, "onProcessFinished");
                Toaster.show("导出完成");
            }
        });

        SurfaceView surfaceView = findViewById(R.id.surface_view);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                videoProcessor.addScreenSurface(holder);
                videoProcessor.startPreview();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.choose_video:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Video"), 101);
                break;
            case R.id.addFilter:
                List<MMPresetFilter> filters = FiltersManager.getAllFilters();
                lastFilter = FilterUtils.getFilterGroupByIndex(2, filters);
                videoProcessor.addFilter(lastFilter);
                break;
            case R.id.removeFilter:
                if (null != lastFilter) {
                    videoProcessor.deleteFilter(lastFilter);
                    lastFilter = null;
                }
                break;
            case R.id.export_video:
                File file = new File(Environment.getExternalStorageDirectory(), "videoprocess_test.mp4");
                videoProcessor.makeVideo(file.getAbsolutePath());
                break;
            case R.id.sticker:
                //进行此操作前提是，已经下载了狗头动态贴纸
                MaskModel model = MaskStore.getInstance().getMask(AppContext.getContext(), "/storage/emulated/0/MomoVideoSDK/moment/dynamic_sticker/59ccd466f38ef/3");
                videoProcessor.addMaskModel(model, 1, 0.5f, 0.5f);
                break;
            case R.id.remove_sticker:
                videoProcessor.removeMaskModel(1);
                break;
            case R.id.pause:
                videoProcessor.pause();
                break;
            case R.id.resume:
                videoProcessor.resume();
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 101) {
                String selectedImagePath = DeviceUtils.uri2Path(getApplicationContext(), data.getData());
                MDLog.e(TAG, "selectedImagePath: %s", selectedImagePath);
                prepare(selectedImagePath);
            }
        }
    }

    private void prepare(String videoPath) {
        MoVideo moVideo = new MoVideo();
        moVideo.path = videoPath;
        moVideo.osPercent = 100;
        moVideo.psPercent = 0;
        videoProcessor.prepareVideo(moVideo);
    }
}
