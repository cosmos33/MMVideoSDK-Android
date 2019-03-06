package com.immomo.videosdk.utils.album;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;

import com.immomo.videosdk.base.BaseActivity;
import com.immomo.videosdk.bean.VideoInfoTransBean;
import com.immomo.videosdk.recorder.MediaConstants;

import java.util.ArrayList;

import androidx.annotation.NonNull;

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
        final String gotoClass = wrapper.getGotoActivityName();
        final Bundle extras = wrapper.getExtras();

        intent.putParcelableArrayListExtra(wrapper.getDataKey(), wrapper.getResultMedias());

        if (extras != null) {
            intent.putExtras(extras);
        }
        if (!TextUtils.isEmpty(wrapper.getPresentContent())) {
            intent.putExtra("preset_text_content", wrapper.getPresentContent());
        }
        if (!TextUtils.isEmpty(gotoClass)) {
            intent.setComponent(new ComponentName(activity, gotoClass));
            activity.startActivity(intent);
            activity.finish();
            return;
        }
        activity.setResult(Activity.RESULT_OK, intent);
        activity.finish();
    }

    public interface IResultWrapper<T extends Parcelable> {

        ArrayList<T> getResultMedias();

        String getMediaType();

        @NonNull
        String getDataKey();

        String getGotoActivityName();

        Bundle getExtras();

        boolean backDirectly();

        String getPresentContent();

        VideoInfoTransBean getVideoInfoTransBean();


    }
}
