package com.mm.base_business.glide;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.executor.GlideExecutor;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.cosmos.mdlog.MDLog;
import com.mm.base_business.log.LogTag;

/**
 * @author wangduanqing
 */
@GlideModule
public class MMGlideModule extends AppGlideModule {
    public LruResourceCache memoryCache;
    private DiskCache diskCache;

    /**
     * 针对V3升级到v4的用户，可以提升初始化速度，避免一些潜在错误
     *
     * @return
     */
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {

        /*图片内存缓存上限*/
        int maxMemorySize = 20;
        try {
            int memorySizeForSingleApp = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
            maxMemorySize = memorySizeForSingleApp / 10;
            if (maxMemorySize > 20) {
                maxMemorySize = 20;
            }
        } catch (Exception ex) {
            MDLog.printErrStackTrace(LogTag.COMMON, ex);
        }
        memoryCache = new LruResourceCache(maxMemorySize * 1024 * 1024);
        builder.setMemoryCache(memoryCache)
               .setBitmapPool(new LruBitmapPool(5 * 1024 * 1024))
               .setDiskCacheExecutor(GlideExecutor.newDiskCacheExecutor(2, "disk-cache", GlideExecutor.UncaughtThrowableStrategy.DEFAULT))
               .setSourceExecutor(GlideExecutor.newSourceExecutor(3, "source", GlideExecutor.UncaughtThrowableStrategy.DEFAULT))
               .setDefaultRequestOptions(new RequestOptions()
                                                 .disallowHardwareConfig()
                                                 .fitCenter()
                                                 .format(DecodeFormat.PREFER_ARGB_8888)
                                                 .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
               .setLogLevel(Log.ERROR);
    }
}
