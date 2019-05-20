package com.mm.sdkdemo;

import android.content.Context;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.immomo.performance.core.BlockConfiguration;
import com.immomo.performance.core.CaptureConfiguration;
import com.immomo.performance.core.Configuration;
import com.immomo.performance.core.PerformanceMonitor;
import com.immomo.performance.utils.PerformanceUtil;
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
        MoMediaManager.init(this, "100cb616072fdc76c983460b8c2b470a");
        PlayerManager.init(this, "100cb616072fdc76c983460b8c2b470a");
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

        initPerformance();
    }

    private void initPerformance() {
        PerformanceMonitor.install(new Configuration.Builder(this)
                                           // 设置卡顿监测配置
                                           .setBlockConfiguration(new BlockConfiguration.Builder(this)
                                                                          // 卡顿日志保存目录
                                                                          .setLogPath("/sdcard/MMVideoSDK/")
                                                                          .build())
                                           // 打开卡顿监测（默认就是打开的）
                                           .setMonitorBlock(true)
                                           // 打开性能数据抓取（默认就是打开的）
                                           .setCapturePerformance(true)
                                           // 设置抓取性能数据的配置
                                           .setCaptureConfiguration(new CaptureConfiguration.Builder()
                                                                            // 抓取时间间隔（ms）
                                                                            .setClockTimeMillis(2000L)
                                                                            // 后台抓取时间间隔（ms）
                                                                            .setClockTimeMillisBackGround(15000L)
                                                                            // 飘红警报
                                                                            .setAlarmThreshold(true)
                                                                            // CPU预警阈值百分比（1-100）
                                                                            .setCPUThreshold(40)
                                                                            // JAVA内存预警阈值百分比（1-100）
                                                                            // 内存最大可用值可以通过MemoryUtils.getRuntimeMaxHeapSize()查看
                                                                            .setJavaMemoryThreshold(40)
                                                                            // 预警内存值，超过这个值出发红色警告
                                                                            .setMaxJavaMemory(100)
                                                                            .setMaxThreadCount(40)
                                                                            .setMaxRunningThreadCount(8)
                                                                            .build())
                                           // 设置当前应用的信息（卡顿日志会保存这些信息）
                                           .setAppInfo(PerformanceUtil.getSimpleAppInfo(this))
                                           .build());

    }
}
