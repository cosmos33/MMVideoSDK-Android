package com.mm.sdkdemo.player;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.mm.mdlog.MDLog;
import com.mm.mmutil.task.MomoTaskExecutor;
import com.mm.mmutil.toast.Toaster;
import com.mm.player.log.LogTag;
import com.mm.player.scale.ScalableType;
import com.mm.sdkdemo.R;
import com.mm.sdkdemo.api.MoApi;
import com.mm.sdkdemo.recorder.activity.BaseFullScreenActivity;

import java.util.ArrayList;
import java.util.List;

public class PlayListActivity extends BaseFullScreenActivity {
    private VideoViewPagerAdapter<VideoPlayItemFragment> mAdapter;
    public static int scaleType = ScalableType.CENTER_CROP;
    public static boolean mediaCodec = true;
    private List<PlayVideo> datas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFullscreen();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_list);
        VerticalViewPager viewPager = findViewById(R.id.vp_video_play);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                VideoPlayItemFragment playItemFragment = mAdapter.getIndexFragment(i);
                playItemFragment.playVideo();
                MDLog.e(LogTag.Player, "VideoPlayItemFragment onPageSelected: %d", i);
                if (i <= datas.size() - 5) {
                    //还剩余5个，loadmore
                    loadMore();
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mAdapter = new VideoViewPagerAdapter<>(getSupportFragmentManager(), datas);
        viewPager.setAdapter(mAdapter);
        if (datas.size() == 0) {
            refresh();
        }
    }

    private void refresh() {
        MomoTaskExecutor.executeUserTask(hashCode(), new MomoTaskExecutor.Task<Object, Object, List<PlayVideo>>() {
            @Override
            protected List<PlayVideo> executeTask(Object... objects) throws Exception {
                return MoApi.getRandomPlayVideoList();
            }

            @Override
            protected void onTaskSuccess(List<PlayVideo> playVideos) {
                mAdapter.refreshList(playVideos);
            }

            @Override
            protected void onTaskError(Exception e) {
                MDLog.printErrStackTrace(LogTag.Player, e);
            }
        });
    }

    private boolean loadingMore;

    private void loadMore() {
        if (loadingMore) {
            return;
        }
        loadingMore = true;
        MomoTaskExecutor.executeUserTask(hashCode(), new MomoTaskExecutor.Task<Object, Object, List<PlayVideo>>() {
            @Override
            protected List<PlayVideo> executeTask(Object... objects) throws Exception {
                return MoApi.getRandomPlayVideoList();
            }

            @Override
            protected void onTaskSuccess(List<PlayVideo> playVideos) {
                mAdapter.addLastItems(playVideos);
            }

            @Override
            protected void onTaskError(Exception e) {
                MDLog.printErrStackTrace(LogTag.Player, e);
            }

            @Override
            protected void onTaskFinish() {
                super.onTaskFinish();
                loadingMore = false;
            }
        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.playlist_CENTER_CROP:
                scaleType = ScalableType.CENTER_CROP;
                Toaster.show("下个视频才生效哦");
                break;
            case R.id.playlist_FIT_CENTER:
                scaleType = ScalableType.FIT_CENTER;
                Toaster.show("下个视频才生效哦");
                break;
            case R.id.playlist_FIT_XY:
                scaleType = ScalableType.FIT_XY;
                Toaster.show("下个视频才生效哦");
                break;

            case R.id.playlist_codec:
                mediaCodec = !mediaCodec;
                if (mediaCodec) {
                    Toaster.show("已经切换到硬解码");
                } else {
                    Toaster.show("已经切换到软解码");
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MomoTaskExecutor.cancleAllTasksByTag(hashCode());
    }
}
