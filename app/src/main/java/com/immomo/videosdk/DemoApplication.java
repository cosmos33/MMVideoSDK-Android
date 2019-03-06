package com.immomo.videosdk;

import android.app.Application;

import com.immomo.mediasdk.MoMediaManager;
import com.immomo.mmutil.FileUtil;
import com.immomo.videosdk.utils.filter.FilterFileUtil;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;

public class DemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MoMediaManager.register(this, "c078bff4c2754152b1adc8325a09aa6c", new MoMediaManager.RegisterCallback() {
            @Override
            public void onRegisterSuccess(String token) {
//                DynamicResourceManager.getInstance().onWifiConnect();
            }

            @Override
            public void onRegisterFailed(int reason) {

            }
        });
        if (BuildConfig.DEBUG) {
            MoMediaManager.openLog(null);
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
