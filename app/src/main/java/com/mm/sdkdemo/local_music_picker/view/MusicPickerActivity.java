package com.mm.sdkdemo.local_music_picker.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.mm.sdkdemo.R;
import com.mm.sdkdemo.local_music_picker.bean.MusicDirectory;
import com.mm.sdkdemo.local_music_picker.model.MusicStoreHelper;
import com.mm.sdkdemo.base.BaseActivity;
import com.mm.sdkdemo.recorder.model.MusicContent;


/**
 * Created by XiongFangyu on 17/2/16.
 * <p>
 * 选择音乐
 */
public class MusicPickerActivity extends BaseActivity {
    public static final String KEY_MAX_LENGTH = "KEY_MAX_LENGTH";

    public static final String KEY_MUSIC_EXTRA = "KEY_MUSIC_EXTRA";

    private MusicPickerDirectoryFragment directoryFragment;
    private MusicPickerFragment musicPickerFragment;

    private int maxLength = Integer.MAX_VALUE;

    public static final void startPickMusic(Activity context, int requestCode) {
        startPickMusic(context, requestCode, Integer.MAX_VALUE);
    }

    public static final void startPickMusic(Activity context, int requestCode, int maxLength) {
        Intent intent = new Intent(context, MusicPickerActivity.class);
        intent.putExtra(KEY_MAX_LENGTH, maxLength);
        context.startActivityForResult(intent, requestCode);
    }

    public static final void startPickMusic(Fragment context, int requestCode) {
        startPickMusic(context, requestCode, Integer.MAX_VALUE);
    }

    public static final void startPickMusic(Fragment context, int requestCode, int maxLength) {
        Intent intent = new Intent(context.getContext(), MusicPickerActivity.class);
        intent.putExtra(KEY_MAX_LENGTH, maxLength);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null) {
            maxLength = intent.getIntExtra(KEY_MAX_LENGTH, maxLength);
        }

        setContentView(R.layout.activity_music_picker);
        setTitle("选择音乐");

        initFragment();
        initEvent();
    }

    private void initEvent() {
        findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            outState.putInt(KEY_MAX_LENGTH, maxLength);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            maxLength = savedInstanceState.getInt(KEY_MAX_LENGTH, Integer.MAX_VALUE);
        }
    }

    private void initFragment() {
        directoryFragment = new MusicPickerDirectoryFragment();
        directoryFragment.setFilterMaxLength(maxLength);
        directoryFragment.setOnChooseListener(new MusicPickerDirectoryFragment.OnChooseListener() {
            @Override
            public void onChoose(MusicDirectory directory) {
                gotoMusic(directory);
            }
        });

        musicPickerFragment = new MusicPickerFragment();
        musicPickerFragment.setOnChooseMusicListener(new MusicPickerFragment.OnChooseMusicListener() {
            @Override
            public void onChoose(MusicContent music) {
                Intent intent = new Intent();
                intent.putExtra(KEY_MUSIC_EXTRA, (Parcelable) music);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        gotoDir();
    }

    private void gotoDir() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (musicPickerFragment.isAdded()) ft.hide(musicPickerFragment);
        if (!directoryFragment.isAdded()) {
            ft.add(R.id.music_picker_fragment_container, directoryFragment);
        } else {
            ft.show(directoryFragment);
        }
        ft.commit();
    }

    private void gotoMusic(MusicDirectory directory) {
        musicPickerFragment.setDirectory(directory);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.hide(directoryFragment);
        if (!musicPickerFragment.isAdded())
            ft.add(R.id.music_picker_fragment_container, musicPickerFragment);
        else ft.show(musicPickerFragment);
        ft.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MusicStoreHelper.cancelLoadMusic(this);
    }

    @Override
    public void onBackPressed() {
        if (musicPickerFragment != null && musicPickerFragment.isVisible()) {
            gotoDir();
            return;
        }
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
