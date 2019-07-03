package com.mm.sdkdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.function.FunctionListActivity;
import com.mm.base.MediaLogger;
import com.mm.mmutil.toast.Toaster;
import com.mm.sdkdemo.bean.VideoInfoTransBean;
import com.mm.sdkdemo.player.PrePlayActivity;
import com.mm.sdkdemo.recorder.activity.BaseFullScreenActivity;
import com.mm.sdkdemo.recorder.activity.RecordParamSettingActivity;
import com.mm.sdkdemo.recorder.activity.VideoRecordAndEditActivity;
import com.mm.sdkdemo.recorder.view.AlbumHomeFragment;

/**
 * Created by wangduanqing on 2019/1/23.
 */

public class MainActivity extends BaseFullScreenActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA}, 1001);
        }
    }

    private void initView() {
        TextView tvVersionName = findViewById(R.id.tv_version_name);
        tvVersionName.setText("V "+BuildConfig.VERSION_NAME);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_record: {
                Intent intent = new Intent(this, RecordParamSettingActivity.class);
                startActivity(intent);
            }

            break;

            case R.id.layout_edit: {
                VideoInfoTransBean videoRecordInfo = new VideoInfoTransBean();
                videoRecordInfo.state = VideoInfoTransBean.STATE_CHOOSE_MEDIA;
                videoRecordInfo.showAlbumTabs = AlbumHomeFragment.STATE_ALL;
                videoRecordInfo.mode = VideoInfoTransBean.MODE_MULTIPLE;
                videoRecordInfo.initAlbumIndex = AlbumHomeFragment.STATE_ALBUM;
                videoRecordInfo.sendText = VideoInfoTransBean.BTN_TEXT_COMPLETE;
                videoRecordInfo.setShowCamera(true);
                videoRecordInfo.hasLatLonPhotos = true;
                VideoRecordAndEditActivity.startActivity(this, videoRecordInfo, -1);
            }
            break;
            case R.id.layout_player: {
                startActivity(new Intent(this, PrePlayActivity.class));
            }
            break;
            case R.id.layout_funcation_test: {
                startActivity(new Intent(this, FunctionListActivity.class));
            }
            break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                Toaster.show("不授权你玩个毛线");
                finish();
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaLogger.forceUploadMMFile();
    }
}
