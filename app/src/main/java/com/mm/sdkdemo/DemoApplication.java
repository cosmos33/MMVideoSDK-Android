package com.mm.sdkdemo;

import android.content.Context;
import android.os.Environment;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.mm.mediasdk.MoMediaManager;
import com.mm.mediasdk.bean.RecorderInitConfig;
import com.mm.mmutil.FileUtil;
import com.mm.player.PlayerManager;
import com.mm.player.config.PlayerInitConfig;
import com.mm.recorduisdk.IRecordResourceConfig;
import com.mm.recorduisdk.IRecordResourceGetter;
import com.mm.recorduisdk.RecordUISDK;
import com.mm.recorduisdk.bean.CommonMomentFaceBean;
import com.mm.recorduisdk.bean.MomentSticker;
import com.mm.recorduisdk.recorder.model.MusicContent;
import com.mm.recorduisdk.recorder.sticker.DynamicSticker;
import com.mm.sdkdemo.api.RecorderDemoApi;
import com.mm.sdkdemo.utils.FilterFileUtil;
import com.mm.sdkdemo.utils.LivePhotoUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DemoApplication extends MultiDexApplication {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        RecordUISDK.openDebug(true);
        if (RecordUISDK.isDebug()) {
            MoMediaManager.openLog(new File(Environment.getExternalStorageDirectory(), "mmvideo_sdk_log").getAbsolutePath());
            PlayerManager.openDebugLog(true, null);
        }


        RecorderInitConfig recorderInitConfig = new RecorderInitConfig.Builder("9dac61837c9bc9eba14f8a32584bde1f")
                .setUserVersionCode(BuildConfig.VERSION_CODE)
                .setUserVersionName("demo:" + BuildConfig.VERSION_NAME)
                .build();

        PlayerInitConfig playerConfig = new PlayerInitConfig.Builder("9dac61837c9bc9eba14f8a32584bde1f")
                .setUserVersionCode(BuildConfig.VERSION_CODE)
                .setUserVersionName("demo:" + BuildConfig.VERSION_NAME)
                .build();

        MoMediaManager.openLogAnalyze(true);
        PlayerManager.openLogAnalyze(true);
        PlayerManager.init(this, playerConfig);
        RecordUISDK.init(this, recorderInitConfig, new DemoRecordResourceGetterImpl());

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

        File makeupHomeDir = FilterFileUtil.getMakeupHomeDir();
        if (FilterFileUtil.needUpdateMakeup(getApplicationContext()) || !makeupHomeDir.exists() || makeupHomeDir.list().length <= 0) {
            if (makeupHomeDir.exists()) {
                FileUtil.deleteDir(makeupHomeDir);
            }
            FileUtil.copyAssets(this, FilterFileUtil.MAKEUP_ASSETS_FILE, new File(FilterFileUtil.getCacheDirectory(), FilterFileUtil.MAKEUP_ASSETS_FILE));
            FileUtil.unzip(new File(FilterFileUtil.getCacheDirectory(), FilterFileUtil.MAKEUP_ASSETS_FILE).getAbsolutePath(), FilterFileUtil.getCacheDirectory().getAbsolutePath(), false);
            FilterFileUtil.saveCurrentMakeupVersion(getApplicationContext());
        }
    }


    static class DemoRecordResourceGetterImpl implements IRecordResourceGetter {
        private RecorderDemoApi demoApi = new RecorderDemoApi();

        @Override
        public IRecordResourceConfig<File> getFiltersImgHomeDirConfig() {
            return new IRecordResourceConfig<File>() {
                @Override
                public boolean isOpen() {
                    return true;
                }

                @Override
                public File getResource() {
                    return FilterFileUtil.getMomentFilterImgHomeDir();
                }
            };
        }

        @Override
        public IRecordResourceConfig<File> getLivePhotoHomeDirConfig() {
            return new IRecordResourceConfig<File>() {
                @Override
                public boolean isOpen() {
                    return true;
                }

                @Override
                public File getResource() {
                    return LivePhotoUtil.getLivePhotoHomeDir();
                }
            };
        }

        @Override
        public IRecordResourceConfig<File> getMakeUpHomeDirConfig() {
            return new IRecordResourceConfig<File>() {
                @Override
                public boolean isOpen() {
                    return true;
                }

                @Override
                public File getResource() {
                    return FilterFileUtil.getMakeupHomeDir();
                }
            };
        }

        @Override
        public IRecordResourceConfig<List<DynamicSticker>> getDynamicStickerListConfig() {
            return new IRecordResourceConfig<List<DynamicSticker>>() {
                @Override
                public boolean isOpen() {
                    return true;
                }

                @Override
                public List<DynamicSticker> getResource() {
                    ArrayList<DynamicSticker> dynamicStickers = new ArrayList<DynamicSticker>();
                    try {
                        demoApi.getDynamicStickerList(dynamicStickers);
                        return dynamicStickers;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
        }

        @Override
        public IRecordResourceConfig<List<MomentSticker>> getStaticStickerListConfig() {
            return new IRecordResourceConfig<List<MomentSticker>>() {
                @Override
                public boolean isOpen() {
                    return true;
                }

                @Override
                public List<MomentSticker> getResource() {
                    ArrayList<MomentSticker> momentStickers = new ArrayList<MomentSticker>();
                    try {
                        demoApi.getStaticStickerList(momentStickers);
                        return momentStickers;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
        }

        @Override
        public IRecordResourceConfig<List<MusicContent>> getRecommendMusicConfig() {
            return new IRecordResourceConfig<List<MusicContent>>() {
                @Override
                public boolean isOpen() {
                    return true;
                }

                @Override
                public List<MusicContent> getResource() {
                    return demoApi.getRecommendMusic();
                }
            };
        }

        @Override
        public IRecordResourceConfig<CommonMomentFaceBean> getMomentFaceDataConfig() {
            return new IRecordResourceConfig<CommonMomentFaceBean>() {
                @Override
                public boolean isOpen() {
                    return true;
                }

                @Override
                public CommonMomentFaceBean getResource() {
                    try {
                        return demoApi.fetchCommonMomentFaceData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
        }
    }
}
