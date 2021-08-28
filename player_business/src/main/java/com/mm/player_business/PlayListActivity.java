package com.mm.player_business;

import android.os.Bundle;

import androidx.viewpager.widget.ViewPager;

import com.cosmos.mdlog.MDLog;
import com.mm.base_business.base.BaseFullScreenActivity;
import com.mm.base_business.utils.WakeManager;
import com.mm.mmutil.task.MomoTaskExecutor;
import com.mm.player.log.LogTag;
import com.mm.player_business.api.PlayerDemoApi;

import java.util.ArrayList;
import java.util.List;

public class PlayListActivity extends BaseFullScreenActivity {
    private VideoViewPagerAdapter<VideoPlayItemFragment> mAdapter;
    private List<PlayVideo> datas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            WakeManager.getInstance().keepScreenOn(this);
        } catch (Exception ex) {
            MDLog.printErrStackTrace("VideoRecordAndEditActivity", ex);
        }
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
                return new PlayerDemoApi().getRandomPlayVideoList();
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
                return new PlayerDemoApi().getRandomPlayVideoList();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MomoTaskExecutor.cancleAllTasksByTag(hashCode());
    }
}