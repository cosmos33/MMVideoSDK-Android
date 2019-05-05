package com.mm.sdkdemo.player;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mm.mdlog.MDLog;
import com.mm.mediasdk.utils.UIUtils;
import com.mm.mmutil.task.MomoMainThreadExecutor;
import com.mm.player.ICosPlayer;
import com.mm.player.VideoView;
import com.mm.player.log.LogTag;
import com.mm.player.scale.ScalableType;
import com.mm.sdkdemo.R;
import com.mm.sdkdemo.glide.ImageLoaderX;
import com.mm.sdkdemo.recorder.activity.BaseFullScreenActivity;
import com.mm.sdkdemo.utils.WakeManager;

public class PlayerActivity extends BaseFullScreenActivity {
    public static final String KEY_URL = "KEY_URL";
    public static final String KEY_COVER = "KEY_COVER";

    private VideoView videoView;
    private ProgressBar videoLoadingView;
    private SeekBar seekBar;
    private ImageView playPauseBtn;
    private View playControlLayout;
    private TextView playTime, totalTime;

    private String videoUrl;
    private boolean alreadyPlay;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MDLog.e(LogTag.Player, "handleMessage");
            if (msg.what == 123) {
                if (videoView.isPlaying()) {
                    playPauseBtn.setImageResource(android.R.drawable.ic_media_pause);
                } else {
                    playPauseBtn.setImageResource(android.R.drawable.ic_media_play);
                }
                int duration = (int) videoView.getDuration();
                if (duration != 0) {
                    totalTime.setText(UIUtils.formatTime(duration));
                    int currentPos = (int) videoView.getCurrentPosition();
                    playTime.setText(UIUtils.formatTime(currentPos));
                    seekBar.setProgress(currentPos * 100 / duration);
                }
                handler.sendEmptyMessageDelayed(123, 500);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            WakeManager.getInstance().keepScreenOn(this);
        } catch (Exception ex) {
            MDLog.printErrStackTrace("VideoRecordAndEditActivity", ex);
        }
        if (savedInstanceState != null) {
            lastPos = savedInstanceState.getLong("lastPos");
        }
        setContentView(R.layout.activity_player);
        String cover = getIntent().getStringExtra(KEY_COVER);
        videoUrl = getIntent().getStringExtra(KEY_URL);

        videoView = findViewById(R.id.player_videoview);

        if (!TextUtils.isEmpty(cover)) {
            ImageLoaderX.load(cover).showDefault(R.drawable.ic_moment_theme_bg).into(videoView.getCoverView());
        }
        videoLoadingView = findViewById(R.id.player_loading);
        videoView.setOnStateChangedListener(new ICosPlayer.OnStateChangedListener() {
            @Override
            public void onStateChanged(final int state) {
                if (state == ICosPlayer.STATE_BUFFERING) {
                    videoLoadingView.setVisibility(View.VISIBLE);
                } else if (state == ICosPlayer.STATE_READY) {
                    videoLoadingView.setVisibility(View.GONE);
                    if (lastPos > 0 && isForeground) {
                        videoView.seekTo(lastPos);
                        lastPos = -1;
                    }
                }
            }
        });
        seekBar = findViewById(R.id.player_seeekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    long ms = progress * videoView.getDuration() / 100;
                    videoView.seekTo(ms);
                    delayHidePlayControlLayout();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        playPauseBtn = findViewById(R.id.player_playorpause);
        playControlLayout = findViewById(R.id.player_controller);
        playTime = findViewById(R.id.player_playTime);
        totalTime = findViewById(R.id.player_totalTime);
    }

    private boolean isForeground;

    @Override
    public void onResume() {
        super.onResume();
        isForeground = true;
        if (!alreadyPlay) {
            playVideo(videoUrl);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isForeground = false;
        pauseVideo();
        handler.removeMessages(123);
    }

    private void playVideo(String videoUrl) {
        if (TextUtils.isEmpty(videoUrl) || alreadyPlay) {
            return;
        }
        videoView.setScaleType(ScalableType.FIT_CENTER);
        videoView.playVideo(videoUrl, PrePlayActivity.mediaCodec);
        alreadyPlay = true;
    }

    private long lastPos = -1;

    private void pauseVideo() {
        lastPos = videoView.getCurrentPosition();
        videoView.releaseVideo();
        alreadyPlay = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("lastPos", lastPos);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.player_close:
                finish();
                break;
            case R.id.player_playorpause:
                if (videoView.isPlaying()) {
                    videoView.pause();
                    playPauseBtn.setImageResource(android.R.drawable.ic_media_play);
                } else {
                    videoView.resume();
                    playPauseBtn.setImageResource(android.R.drawable.ic_media_pause);
                }
                delayHidePlayControlLayout();
                break;
            case R.id.player_layout:
                if (playControlLayout.getVisibility() == View.VISIBLE) {
                    hidePlayControlLayout();
                } else {
                    showPlayControlLayout();
                    delayHidePlayControlLayout();
                }
                break;
        }
    }

    private void delayHidePlayControlLayout() {
        MomoMainThreadExecutor.cancelAllRunnables(hashCode());
        MomoMainThreadExecutor.postDelayed(hashCode(), new Runnable() {
            @Override
            public void run() {
                hidePlayControlLayout();
            }
        }, 3000);
    }

    private void showPlayControlLayout() {
        playControlLayout.setVisibility(View.VISIBLE);
        handler.removeMessages(123);
        handler.sendEmptyMessage(123);
    }

    private void hidePlayControlLayout() {
        playControlLayout.setVisibility(View.GONE);
        MomoMainThreadExecutor.cancelAllRunnables(hashCode());
        handler.removeMessages(123);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        MomoMainThreadExecutor.cancelAllRunnables(hashCode());
    }
}
