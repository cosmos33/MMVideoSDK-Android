package com.mm.recorduisdk;

import android.app.Application;

import com.mm.mediasdk.MoMediaManager;
import com.mm.mediasdk.bean.RecorderInitConfig;

/**
 * Created on 2019/7/19.
 *
 * @author jianxi[mabeijianxi@gmail.com]
 */
public class RecordUISDK {
    private static IRecordResourceGetter sRecordResourceGetter;
    private static boolean DEBUG = false;

    @Deprecated
    public static void init(Application context, String appId, IRecordResourceGetter recordResourceGetter) {
        MoMediaManager.init(context, appId);
        sRecordResourceGetter = recordResourceGetter;
    }
    public static void init(Application context, RecorderInitConfig config, IRecordResourceGetter recordResourceGetter) {
        MoMediaManager.init(context, config);
        sRecordResourceGetter = recordResourceGetter;
    }

    public static IRecordResourceGetter getResourceGetter() {
        if (sRecordResourceGetter == null) {
            throw new IllegalStateException("need implements IRecordResourceGetter");
        }
        return sRecordResourceGetter;
    }

    public static void openDebug(boolean toggle) {
        DEBUG = toggle;
    }

    public static boolean isDebug() {
        return DEBUG;
    }
}
