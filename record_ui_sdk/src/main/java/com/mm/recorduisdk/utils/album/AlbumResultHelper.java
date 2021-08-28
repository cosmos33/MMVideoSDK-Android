package com.mm.recorduisdk.utils.album;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.mm.base_business.base.BaseActivity;
import com.mm.recorduisdk.bean.FinishGotoInfo;
import com.mm.recorduisdk.recorder.MediaConstants;

import java.util.ArrayList;

/**
 * @author shidefeng
 * @since 2017/6/9.
 */

public class AlbumResultHelper {

    public static void handleResult(BaseActivity activity, IResultWrapper<? extends Parcelable> wrapper) {
        if (null == activity || activity.isDestroyed() || wrapper == null) {
            return;
        }
        final ArrayList<? extends Parcelable> resultMedias = wrapper.getResultMedias();
        if (resultMedias == null || resultMedias.isEmpty() || wrapper.backDirectly() ||
                !TextUtils.equals(wrapper.getMediaType(), MediaConstants.MEDIA_TYPE_IMAGES)) {
            gotoOtherActivity(activity, wrapper);
            return;
        }
        gotoOtherActivity(activity, wrapper);
    }

    private static void gotoOtherActivity(BaseActivity activity, IResultWrapper<? extends Parcelable> wrapper) {

        final Intent intent = new Intent();
        final FinishGotoInfo gotoInfo = wrapper.getGotoInfo();

        intent.putParcelableArrayListExtra(wrapper.getDataKey(), wrapper.getResultMedias());
        if (gotoInfo != null) {
            if (gotoInfo.getExtraBundle() != null) {
                intent.putExtras(gotoInfo.getExtraBundle());
            }
            if (gotoInfo.isNeedFinishResultMode()) {
                activity.setResult(Activity.RESULT_OK, intent);
                activity.finish();
            } else {
                if (!TextUtils.isEmpty(gotoInfo.getGotoActivityName())) {
                    intent.setComponent(new ComponentName(activity, gotoInfo.getGotoActivityName()));
                    activity.startActivity(intent);
                    if (gotoInfo.isFinishCurrentActivity()) {
                        activity.finish();
                    }
                }
            }
        }
    }

    public interface IResultWrapper<T extends Parcelable> {

        ArrayList<T> getResultMedias();

        String getMediaType();

        @NonNull
        String getDataKey();

        FinishGotoInfo getGotoInfo();

        boolean backDirectly();


    }
}
