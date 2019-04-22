package com.mm.sdkdemo;

import android.content.Context;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.mm.mediasdk.MoMediaManager;
import com.mm.mmutil.FileUtil;
import com.mm.player.PlayerManager;
import com.mm.sdkdemo.config.Configs;
import com.mm.sdkdemo.utils.filter.FilterFileUtil;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;

public class DemoApplication extends MultiDexApplication {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MoMediaManager.init(this, "53c2b08cd6aa13e678c37240c9e6d1f9");
        PlayerManager.init(this, "53c2b08cd6aa13e678c37240c9e6d1f9");
        if (Configs.DEBUG) {
            MoMediaManager.openLog(new File(Environment.getExternalStorageDirectory(), "mmvideo_sdk_log").getAbsolutePath());
            PlayerManager.openDebugLog(true, null);
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
