package com.mm.sdkdemo;

import android.app.Application;
import android.os.Environment;

import com.immomo.mmutil.FileUtil;
import com.mm.mediasdk.MoMediaManager;
import com.mm.sdkdemo.config.Configs;
import com.mm.sdkdemo.utils.filter.FilterFileUtil;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;

public class DemoApplication extends Application {
    public static volatile String mToken;

    @Override
    public void onCreate() {
        super.onCreate();
        MoMediaManager.register(this, "53c2b08cd6aa13e678c37240c9e6d1f9", new MoMediaManager.RegisterCallback() {
            @Override
            public void onRegisterSuccess(String token) {
                mToken = token;
            }

            @Override
            public void onRegisterFailed(int reason) {

            }
        });
        if (Configs.DEBUG) {
            MoMediaManager.openLog(new File(Environment.getExternalStorageDirectory(), "mmvideo_sdk_log").getAbsolutePath());
        }
        File filterDir = FilterFileUtil.getMomentFilterHomeDir();
        if (!filterDir.exists() || filterDir.list().length <= 0) {
            FileUtil.copyAssets(this, "filterData.zip", new File(FilterFileUtil.getCacheDirectory(), "filterData.zip"));
            FileUtil.unzip(new File(FilterFileUtil.getCacheDirectory(), "filterData.zip").getAbsolutePath(), FilterFileUtil.getCacheDirectory().getAbsolutePath(), false);
        }

        //bugly
        CrashReport.initCrashReport(getApplicationContext(), "1ce7b8c2d3", false);
    }
}
