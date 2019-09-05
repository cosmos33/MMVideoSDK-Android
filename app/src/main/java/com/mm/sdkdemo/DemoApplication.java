package com.mm.sdkdemo;

import android.content.Context;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.core.glcore.config.MediaModuleGlobalConfig;
import com.immomo.performance.core.BlockConfiguration;
import com.immomo.performance.core.CaptureConfiguration;
import com.immomo.performance.core.Configuration;
import com.immomo.performance.core.PerformanceMonitor;
import com.immomo.performance.utils.PerformanceUtil;
import com.mm.mediasdk.MoMediaManager;
import com.mm.mmutil.FileUtil;
import com.mm.player.PlayerManager;
import com.mm.recorduisdk.IRecordResourceGetter;
import com.mm.recorduisdk.RecordUISDK;
import com.mm.recorduisdk.bean.CommonMomentFaceBean;
import com.mm.recorduisdk.bean.MomentFace;
import com.mm.recorduisdk.bean.MomentSticker;
import com.mm.recorduisdk.config.Configs;
import com.mm.recorduisdk.recorder.model.MusicContent;
import com.mm.recorduisdk.recorder.sticker.DynamicSticker;
import com.mm.rifle.Rifle;
import com.mm.sdkdemo.api.DemoApi;
import com.mm.sdkdemo.utils.FilterFileUtil;
import com.mm.sdkdemo.utils.LivePhotoUtil;
import com.momo.xeengine.XELogger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DemoApplication extends MultiDexApplication {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Rifle.init(this, "9dac61837c9bc9eba14f8a32584bde1f", true);
        RecordUISDK.init(this, "100cb616072fdc76c983460b8c2b470a", new DemoRecordResourceGetterImpl());
        PlayerManager.init(this, "100cb616072fdc76c983460b8c2b470a");
        if (Configs.DEBUG) {
            MoMediaManager.openLog(new File(Environment.getExternalStorageDirectory(), "mmvideo_sdk_log").getAbsolutePath());
            PlayerManager.openDebugLog(true, null);
            if (MediaModuleGlobalConfig.hasXE()) {
                XELogger.getInstance().setLogEnable(Configs.DEBUG);
            }
        }
        MoMediaManager.openLogAnalyze(true);
        PlayerManager.openLogAnalyze(true);
        File filterDir = FilterFileUtil.getMomentFilterHomeDir();
        if (FilterFileUtil.needUpdateFilter(getApplicationContext()) || !filterDir.exists() || filterDir.list().length <= 0) {
            if (filterDir.exists()) {
                FileUtil.deleteDir(filterDir);
            }
            FileUtil.copyAssets(this, "filterData.zip", new File(FilterFileUtil.getCacheDirectory(), "filterData.zip"));
            FileUtil.unzip(new File(FilterFileUtil.getCacheDirectory(), "filterData.zip").getAbsolutePath(), FilterFileUtil.getCacheDirectory().getAbsolutePath(), false);
            FilterFileUtil.saveCurrentFilterVersion(getApplicationContext());
        }

        File livePhotoHomeDir = LivePhotoUtil.getLivePhotoHomeDir();
        if (LivePhotoUtil.needUpdate(getApplicationContext()) || !livePhotoHomeDir.exists() || livePhotoHomeDir.list().length <= 0) {
            if (livePhotoHomeDir.exists()) {
                FileUtil.deleteDir(livePhotoHomeDir);
            }
            FileUtil.copyAssets(this, "photoLiveSrc.zip", new File(LivePhotoUtil.getCacheDirectory(), "photoLiveSrc.zip"));
            FileUtil.unzip(new File(LivePhotoUtil.getCacheDirectory(), "photoLiveSrc.zip").getAbsolutePath(), livePhotoHomeDir.getAbsolutePath(), false);
            LivePhotoUtil.saveCurrentVersion(getApplicationContext());
        }


        File dokiDir = FilterFileUtil.getDokiFilterHomeDir();
        if (!dokiDir.exists() || dokiDir.list().length <= 0) {
            FileUtil.copyAssets(this, "doki_res.zip", new File(FilterFileUtil.getCacheDirectory(), "doki_res.zip"));
            FileUtil.unzip(new File(FilterFileUtil.getCacheDirectory(), "doki_res.zip").getAbsolutePath(), FilterFileUtil.getCacheDirectory().getAbsolutePath(), false);
        }

        //bugly
//        CrashReport.initCrashReport(getApplicationContext(), "1ce7b8c2d3", false);

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


    static class DemoRecordResourceGetterImpl implements IRecordResourceGetter {
        private DemoApi demoApi = new DemoApi();

        @Override
        public File getFiltersImgHomeDir() {
            return FilterFileUtil.getMomentFilterImgHomeDir();
        }

        @Override
        public File getLivePhotoHomeDir() {
            return LivePhotoUtil.getLivePhotoHomeDir();
        }

        @Override
        public File getMakeUpHomeDir() {
            return FilterFileUtil.getDokiFilterHomeDir();
        }

        @Override
        public List<DynamicSticker> getDynamicStickerList() {
            ArrayList<DynamicSticker> dynamicStickers = new ArrayList<DynamicSticker>();
            try {
                demoApi.getDynamicStickerList(dynamicStickers);
                return dynamicStickers;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public List<MomentSticker> getStaticStickerList() {
            ArrayList<MomentSticker> momentStickers = new ArrayList<MomentSticker>();
            try {
                demoApi.getStaticStickerList(momentStickers);
                return momentStickers;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public List<MusicContent> getRecommendMusic() {
            return demoApi.getRecommendMusic();
        }

        @Override
        public Map<String, List<MomentFace>> getFaceMomentData() {
            return demoApi.getFaceData();
        }

        @Override
        public CommonMomentFaceBean fetchMomentFaceData() throws Exception {
            return demoApi.fetchCommonMomentFaceData();
        }
    }
}
