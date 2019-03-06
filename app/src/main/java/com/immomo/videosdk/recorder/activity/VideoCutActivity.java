package com.immomo.videosdk.recorder.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.immomo.videosdk.R;
import com.immomo.videosdk.base.BaseFragment;
import com.immomo.videosdk.recorder.listener.FragmentChangeListener;
import com.immomo.videosdk.recorder.MediaConstants;
import com.immomo.videosdk.recorder.view.VideoCutFragment;

/**
 * Created by yichao on 17/2/9.
 * 视频截取预览界面，当时选取的本地视频大于1min的时候将会进入该界面
 */
public class VideoCutActivity extends BaseFullScreenActivity {

    private PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout view = new FrameLayout(this);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        view.setId(R.id.container);
        setContentView(view);
        final VideoCutFragment fragment = new VideoCutFragment();
        fragment.setArguments(getIntent().getExtras());
        fragment.setFragmentChangeListener(new FragmentChangeListener() {
            @Override
            public void change(BaseFragment old, Bundle extra) {
                Intent intent = new Intent();
                int resultCode = extra.getInt(MediaConstants.KEY_RESULT_CODE);
                extra.remove(MediaConstants.KEY_RESULT_CODE);
                intent.putExtras(extra);
                setResult(resultCode, intent);
                finish();
            }
        });
        getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();

        acquireWakeLock();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void acquireWakeLock() {
        if (wakeLock ==null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getCanonicalName());
            wakeLock.acquire();
        }
    }

    private void releaseWakeLock() {
        if (wakeLock !=null&& wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock =null;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseWakeLock();
    }

}