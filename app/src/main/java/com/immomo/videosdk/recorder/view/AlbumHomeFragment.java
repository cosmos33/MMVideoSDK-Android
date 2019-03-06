package com.immomo.videosdk.recorder.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.immomo.videosdk.R;
import com.immomo.videosdk.base.BaseFragment;
import com.immomo.videosdk.base.BaseScrollTabGroupFragment;
import com.immomo.videosdk.base.BaseTabOptionFragment;
import com.immomo.videosdk.base.tabinfo.FragmentTabInfo;
import com.immomo.videosdk.base.tabinfo.TextTabInfo;
import com.immomo.videosdk.bean.VideoInfoTransBean;
import com.immomo.videosdk.recorder.listener.FragmentChangeListener;
import com.immomo.videosdk.recorder.MediaConstants;
import com.immomo.videosdk.recorder.presenter.AlbumPresenterImpl;
import com.immomo.videosdk.recorder.presenter.IAlbumPresenter;
import com.immomo.videosdk.widget.DropDownTabInfo;
import com.immomo.videosdk.utils.StatusBarUtil;
import com.immomo.videosdk.utils.album.ScanResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;

import static com.immomo.videosdk.recorder.activity.VideoRecordAndEditActivity.BACK_TO_OLD;
import static com.immomo.videosdk.recorder.activity.VideoRecordAndEditActivity.GOTO_WHERE;

/**
 * Created by chenxin on 2018/8/28.
 */

public class AlbumHomeFragment extends BaseScrollTabGroupFragment implements IAlbumView {

    public static final String KEY_GOTO_PREVIEW_FROM_ALBUM = "KEY_GOTO_PREVIEW_FROM_ALBUM";

    public static final int REQ_PERMISSION = 100;

    public static final int STATE_PICTURE_ALBUM = 0x0001;//影集
    public static final int STATE_ALBUM = 0x0002;//相册
    public static final int STATE_VIDEO = 0x0004;//视频
    public static final int STATE_FACE = 0x0008; //人物
    public static final int STATE_ALL = STATE_PICTURE_ALBUM|STATE_ALBUM|STATE_VIDEO;

    private AppBarLayout appBarLayout;
    private View pagerTabContent;

    private VideoInfoTransBean mTransBean;

    private FragmentChangeListener mFragmentChangeListener;
    private IAlbumPresenter mPresenter;
    private TextView mSendBtn;

    private List<Integer> mInitTabs;//顶部tabs

    private TextTabInfo videoTab;
    private DropDownTabInfo albumTab;

    public static AlbumHomeFragment newInstance(Bundle args) {
        final AlbumHomeFragment fragment = new AlbumHomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void setFragmentChangeListener(FragmentChangeListener fragmentChangeListener) {
        this.mFragmentChangeListener = fragmentChangeListener;
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_album;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appBarLayout = findViewById(R.id.appbar_id);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && !getActivity().isFinishing()) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) appBarLayout.getLayoutParams();
            params.topMargin = StatusBarUtil.getStatusBarHeight(getActivity());
            appBarLayout.setLayoutParams(params);
        }
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        loadMedia();
        int initTabIndex = (mTransBean == null || mTransBean.initAlbumIndex == 0) ? STATE_ALBUM : mTransBean.initAlbumIndex;
        if (mInitTabs != null && mInitTabs.contains(initTabIndex)) {
            setCurrentTab(mInitTabs.indexOf(initTabIndex));
        }
    }

    private void showTip() {
        IAlbumFragment fragment = (IAlbumFragment) getCurrentFragment();
        if (fragment != null) {
            fragment.showTip(toolbarHelper.getToolbar());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.destroy();
        }
    }

    private void loadMedia() {
        Activity activity = (Activity) getContext();
        if (activity == null) {
            return;
        }
        if (mPresenter != null) {
            mPresenter.loadMedia();
        }
    }

    private void initView() {
        mSendBtn = findViewById(R.id.finish);
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IAlbumFragment f = (IAlbumFragment) getCurrentFragment();
                if (f != null) {
                    f.onSendClick();
                }
            }
        });
        pagerTabContent = findViewById(R.id.pagertabcontent);
        toolbarHelper.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mTransBean = getArguments().getParcelable(MediaConstants.EXTRA_KEY_VIDEO_TRANS_INFO);
        mPresenter = new AlbumPresenterImpl(mTransBean);
        mPresenter.bindView(this);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    protected void initViews(View contentView) {
        initView();
    }

    @Override
    protected void onActivityResultReceived(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_PERMISSION) {
            if (resultCode == RESULT_OK && mPresenter != null) {
                mPresenter.loadMedia();
            }
            return;
        }
        BaseTabOptionFragment f = getCurrentFragment();
        if (f != null) {
            f.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResultReceived(requestCode, resultCode, data);
    }

    @Override
    protected List<? extends FragmentTabInfo> onLoadTabs() {
        int flag = (mTransBean == null || mTransBean.showAlbumTabs == 0) ? STATE_ALBUM : mTransBean.showAlbumTabs;
        List<TextTabInfo> textTabList = new ArrayList<>(4);
        mInitTabs = new ArrayList<>(4);

        if ((flag&STATE_ALBUM) != 0) {
            albumTab = new DropDownTabInfo("相册", AlbumFragment.class, getBundleByType(1));
            textTabList.add(albumTab);
            mInitTabs.add(STATE_ALBUM);
        }

        if ((flag&STATE_VIDEO) != 0) {
            videoTab = new TextTabInfo("视频", VideoFragment.class, getBundleByType(1));
            textTabList.add(videoTab);
            mInitTabs.add(STATE_VIDEO);
        }

        initTab();
        return textTabList;
    }

    private int getTabIndex(FragmentTabInfo fragmentTabInfo) {
        if (getTabs() == null) {
            return 0;
        }
        return getTabs().indexOf(fragmentTabInfo);
    }

    private void initTab() {
        if (albumTab != null) {
            albumTab.getCustomView(getTabLayout()).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getCurrentFragment() instanceof AlbumFragment) {
                        IAlbumFragment fragment = (IAlbumFragment) getCurrentFragment();
                        fragment.showDirectoriesView();
                    } else {
                        setCurrentTab(getTabIndex(albumTab));
                    }
                }
            });
        }
    }

    @Override
    protected void onTabChanged(int index, BaseTabOptionFragment fragment) {
        super.onTabChanged(index, fragment);
        for (Map.Entry<Integer, BaseTabOptionFragment> entry : fragments.entrySet()) {
            ((IAlbumFragment) entry.getValue()).clearSelect();
        }
        if (fragment instanceof AlbumFragment) {
            if (albumTab != null) {
                albumTab.showIcon(true);
            }
        } else {
//            if (livePhotoTab != null) {
//                livePhotoTab.showIcon(false);
//            }
            if (albumTab != null) {
                albumTab.showIcon(false);
            }
        }
        showTip();
    }

    private Bundle getBundleByType(int type) {
        return getArguments();
    }

    @Override
    public Context getContext() {
        return getActivity();
    }

    @Override
    public void changeFragment(BaseFragment old, Bundle extra) {
        if (mFragmentChangeListener != null) {
            mFragmentChangeListener.change(this, extra);
        }
    }

    @Override
    public void onSelectClick(int count, String sendText) {
        if (mSendBtn != null) {
            if (count <= 0) {
                mSendBtn.setVisibility(View.GONE);
            } else {
                mSendBtn.setVisibility(View.VISIBLE);
                mSendBtn.setEnabled(true);
                mSendBtn.setTextColor(0xff3bb3fa);
                mSendBtn.setText("完成" + "(" + count + ")");
            }
        }
    }

    @Override
    public void onMultiMediaLoad(ScanResult scanResult) {
        if (scanResult == null) {
            return;
        }
        for (Integer index : fragments.keySet()) {
            IAlbumFragment fragment = (IAlbumFragment) fragments.get(index);
            if (fragment instanceof AlbumFragment) {
                fragment.onActivityMediasLoaded(scanResult);
            } else if (fragment instanceof VideoFragment) {
                fragment.onActivityMediasLoaded(scanResult);
            }
        }
        if (!scanResult.showImage) {
            showTip();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public boolean onBackPressed() {
        if (mFragmentChangeListener != null) {
            Bundle bundle = new Bundle();
            bundle.putInt(MediaConstants.EXTRA_KEY_VIDEO_STATE, mTransBean.fromState);
            bundle.putParcelable(MediaConstants.EXTRA_KEY_VIDEO_TRANS_INFO, mTransBean);
            bundle.putString(GOTO_WHERE, BACK_TO_OLD);
            mFragmentChangeListener.change(this, bundle);
        } else {
            if (getActivity() != null && !getActivity().isFinishing()) {
                getActivity().finish();
            }
        }
        return true;
    }
}
