package com.mm.sdkdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.function.FunctionListActivity;
import com.mm.base_business.base.BaseFullScreenActivity;
import com.mm.mediasdk.log.RecorderMMFile;
import com.mm.mmutil.toast.Toaster;
import com.mm.player.log.PlayerMMFile;
import com.mm.player_business.PrePlayActivity;

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
        tvVersionName.setText("V " + BuildConfig.VERSION_NAME);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_record: {
                Intent intent = new Intent(this, RecordParamSettingActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.layout_edit: {
                Intent intent = new Intent(this, ChooseMediaParamSettingActivity.class);
                startActivity(intent);
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
        PlayerMMFile.forceUploadMMFile();
        RecorderMMFile.forceUploadMMFile();
    }
}
