package com.mm.recorduisdk;

import android.app.Application;

import com.mm.mediasdk.MoMediaManager;

/**
 * Created on 2019/7/19.
 *
 * @author jianxi[mabeijianxi@gmail.com]
 */
public class RecordUISDK {
    private static IRecordResourceGetter sRecordResourceGetter;

    public static void init(Application context, String appId, IRecordResourceGetter recordResourceGetter) {
        MoMediaManager.init(context, appId);
        sRecordResourceGetter = recordResourceGetter;
    }

    public static IRecordResourceGetter getResourceGetter() {
        if (sRecordResourceGetter == null) {
            throw new IllegalStateException("need implements IRecordResourceGetter");
        }
        return sRecordResourceGetter;
    }
}
