package com.mm.sdkdemo.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import com.mm.sdkdemo.R;
import com.mm.sdkdemo.bean.MomentFacePanelElement;
import com.mm.sdkdemo.utils.filter.FilterFileUtil;
import com.momo.mcamera.dokibeauty.MakeupHelper;

import java.io.File;

/**
 * 美妆面板         <br/>
 * <br/>
 * 该类只处理和UI相关的逻辑，其他逻辑不放在这，需要使用，请用{@link MomentFacePanelElement}
 * 配合{@link android.view.ViewStub}进行懒加载使用，因为该类比较重       <br/>
 * <br/>
 * Created by momo on 2017/5/10.
 */

public class MomentBeautyPanelLayout extends FrameLayout implements View.OnClickListener {

    private File[] dokiRes;
    private SeekBar seekbar;
    private String currentKey;
    private int lastRichangProgress = 80, lastShaonianProgress = 80, lastQuebanProgress = 80, lastYuanqiProgress = 80, lastTantanProgress = 80;

    public MomentBeautyPanelLayout(Context context) {
        this(context, null);
    }

    public MomentBeautyPanelLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MomentBeautyPanelLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MomentBeautyPanelLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_beauty_panel_layout, this);
        findViewById(R.id.richang).setOnClickListener(this);
        findViewById(R.id.shaoniangan).setOnClickListener(this);
        findViewById(R.id.xiaoqueban).setOnClickListener(this);
        findViewById(R.id.yuanqi).setOnClickListener(this);
        findViewById(R.id.tantan).setOnClickListener(this);
        seekbar = findViewById(R.id.seekbar);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && !TextUtils.isEmpty(currentKey)) {
                    switch (currentKey) {
                        case "日常":
                            lastRichangProgress = progress;
                            break;
                        case "少年":
                            lastShaonianProgress = progress;
                            break;
                        case "雀斑":
                            lastQuebanProgress = progress;
                            break;
                        case "元气":
                            lastYuanqiProgress = progress;
                            break;
                        case "探探":
                            lastTantanProgress = progress;
                            break;
                    }
                    MakeupHelper.setMakeUpStrength(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        dokiRes = FilterFileUtil.getDokiFilterHomeDir().listFiles();
    }

    public void clickCurrentTab() {
        if (TextUtils.isEmpty(currentKey)) {
            return;
        }
        int currentProgress = 0;

        switch (currentKey) {
            case "日常": {
                currentProgress = lastRichangProgress;

                break;
            }
            case "少年": {
                currentProgress = lastShaonianProgress;

                break;
            }
            case "雀斑": {
                currentProgress = lastQuebanProgress;

                break;
            }
            case "元气": {
                currentProgress = lastYuanqiProgress;

                break;
            }
            case "探探": {
                currentProgress = lastTantanProgress;

                break;
            }

        }
        if (mListener != null) {
            mListener.onSelect(getFileByName(currentKey));
        }
        seekbar.setProgress(currentProgress);
        MakeupHelper.setMakeUpStrength(currentProgress);
    }

    @Override
    public void onClick(View v) {
        String key = null;
        int currentProgress = 0;
        switch (v.getId()) {
            case R.id.richang:
                key = "日常";
                currentProgress = lastRichangProgress;
                break;
            case R.id.shaoniangan:
                key = "少年";
                currentProgress = lastShaonianProgress;
                break;
            case R.id.xiaoqueban:
                key = "雀斑";
                currentProgress = lastQuebanProgress;
                break;
            case R.id.yuanqi:
                key = "元气";
                currentProgress = lastYuanqiProgress;
                break;
            case R.id.tantan:
                key = "探探";
                currentProgress = lastTantanProgress;
                break;
        }
        currentKey = key;
        if (mListener != null) {
            mListener.onSelect(getFileByName(key));
        }
        seekbar.setProgress(currentProgress);
        MakeupHelper.setMakeUpStrength(currentProgress);


    }

    private File getFileByName(String name) {
        if (null != dokiRes) {
            for (File file : dokiRes) {
                if (file.getName().contains(name)) {
                    return file;
                }
            }
        }
        return null;
    }

    private BeautySelectListener mListener;

    public void setBeautySelectListener(BeautySelectListener listener) {
        this.mListener = listener;
    }

    public interface BeautySelectListener {
        void onSelect(File file);
    }
}
