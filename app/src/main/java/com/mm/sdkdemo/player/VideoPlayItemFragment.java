package com.mm.sdkdemo.player;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.mm.mediasdk.utils.UIUtils;
import com.mm.player.ICosPlayer;
import com.mm.player.PlayerManager;
import com.mm.player.VideoView;
import com.mm.player.log.LogTag;
import com.mm.sdkdemo.R;
import com.mm.sdkdemo.glide.ImageLoaderX;

public class VideoPlayItemFragment extends Fragment {
    private static final String ARG_POSITION = "position";
    private static final String ARG_FEED_COVER = "common_cover";
    private static final String ARG_FEED_VIDEO = "common_video_url";

    private int position;
    private String cover;
    private String videoUrl;
    private boolean alreadyPlay;
    private boolean currentVisibleToUser;
    private VideoView videoView;
    private ImageView pauseImageView;
    private ProgressBar progressBar;
    private long currentPos = -1;

    public static VideoPlayItemFragment newInstance(int position, PlayVideo video) {
        VideoPlayItemFragment fragment = new VideoPlayItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        args.putString(ARG_FEED_COVER, video.cover);
        args.putString(ARG_FEED_VIDEO, video.videoUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
            cover = getArguments().getString(ARG_FEED_COVER);
            videoUrl = getArguments().getString(ARG_FEED_VIDEO);
            //开始预加载
            PlayerManager.getMediaPreLoader().addTask(videoUrl, Uri.parse(videoUrl).getPath());
        }
    }

    private boolean isForeground;

    @Override
    public void onResume() {
        super.onResume();
        isForeground = true;
        if (!alreadyPlay && currentVisibleToUser) {
            playVideo(videoUrl);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isForeground = false;
        pauseVideo();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser == currentVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);
            return;
        }
        Log.e(LogTag.Player, "VideoPlayItemFragment setUserVisibleHint " + isVisibleToUser + " post:" + position);
        currentVisibleToUser = isVisibleToUser;
        if (!currentVisibleToUser) {
            pauseVideo();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    public void playVideo() {
        playVideo(videoUrl);
    }

    private void playVideo(String videoUrl) {
        if (TextUtils.isEmpty(videoUrl) || alreadyPlay) {
            return;
        }
        pauseImageView.setVisibility(View.GONE);
        videoView.setScaleType(PrePlayActivity.scaleType);
        //停止当前的预加载
        PlayerManager.getMediaPreLoader().clearTaskByUrl(videoUrl);
        Log.e(LogTag.Player, "VideoPlayItemFragment playVideo " + position);
        videoView.playVideo(videoUrl, PrePlayActivity.mediaCodec);
        alreadyPlay = true;
    }

    private void pauseVideo() {
        currentPos = videoView.getCurrentPosition();
        videoView.releaseVideo();
        alreadyPlay = false;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FrameLayout frameLayout = new FrameLayout(getContext());
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        videoView = new VideoView(getContext());
        videoView.setScaleType(PrePlayActivity.scaleType);
        videoView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        ImageLoaderX.load(cover).showDefault(R.drawable.ic_moment_theme_bg).into(videoView.getCoverView());
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                    pauseImageView.setVisibility(View.VISIBLE);
                } else {
                    videoView.resume();
                    pauseImageView.setVisibility(View.GONE);
                }
            }
        });
        videoView.setOnStateChangedListener(new ICosPlayer.OnStateChangedListener() {
            @Override
            public void onStateChanged(final int state) {
                if (state == ICosPlayer.STATE_BUFFERING) {
                    progressBar.setVisibility(View.VISIBLE);
                } else if (state == ICosPlayer.STATE_READY) {
                    progressBar.setVisibility(View.GONE);
                    if (currentPos > 0 && isForeground) {
                        videoView.seekTo(currentPos);
                        currentPos = -1;
                    }
                }
            }
        });
        frameLayout.addView(videoView);

        pauseImageView = new ImageView(getContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(UIUtils.getPixels(100), UIUtils.getPixels(100));
        layoutParams.gravity = Gravity.CENTER;
        pauseImageView.setLayoutParams(layoutParams);
        pauseImageView.setImageResource(android.R.drawable.ic_media_play);
        frameLayout.addView(pauseImageView);

        progressBar = new ProgressBar(getContext());
        FrameLayout.LayoutParams progressParams = new FrameLayout.LayoutParams(UIUtils.getPixels(100), UIUtils.getPixels(100));
        progressParams.gravity = Gravity.CENTER;
        progressBar.setLayoutParams(layoutParams);
        progressBar.setVisibility(View.GONE);
        frameLayout.addView(progressBar);

        return frameLayout;
    }
}
