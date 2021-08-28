package com.mm.recorduisdk.recorder.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.appbar.AppBarLayout;
import com.mm.base_business.base.BaseFragment;
import com.mm.base_business.base.BaseScrollTabGroupFragment;
import com.mm.base_business.base.BaseTabOptionFragment;
import com.mm.base_business.base.tabinfo.FragmentTabInfo;
import com.mm.base_business.base.tabinfo.TextTabInfo;
import com.mm.base_business.utils.StatusBarUtil;
import com.mm.recorduisdk.Constants;
import com.mm.recorduisdk.R;
import com.mm.recorduisdk.bean.MMChooseMediaParams;
import com.mm.recorduisdk.recorder.MediaConstants;
import com.mm.recorduisdk.recorder.listener.FragmentChangeListener;
import com.mm.recorduisdk.recorder.presenter.AlbumPresenterImpl;
import com.mm.recorduisdk.recorder.presenter.IAlbumPresenter;
import com.mm.recorduisdk.utils.album.ScanResult;
import com.mm.recorduisdk.widget.DropDownTabInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.mm.recorduisdk.recorder.activity.VideoRecordAndEditActivity.BACK_TO_OLD;
import static com.mm.recorduisdk.recorder.activity.VideoRecordAndEditActivity.GOTO_WHERE;

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
    public static final int STATE_ALL = STATE_PICTURE_ALBUM | STATE_ALBUM | STATE_VIDEO;

    private AppBarLayout appBarLayout;
    private View pagerTabContent;

    private MMChooseMediaParams mChooseMediaParams;

    private FragmentChangeListener mFragmentChangeListener;
    private IAlbumPresenter mPresenter;

    private List<Integer> mInitTabs;//顶部tabs

    private TextTabInfo videoTab;
    private DropDownTabInfo albumTab;
    private TextView mSendBtn;

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
        int initTabIndex = (mChooseMediaParams == null || mChooseMediaParams.getInitAlbumIndex() == 0) ? Constants.ShowMediaTabType.STATE_ALBUM : mChooseMediaParams.getInitAlbumIndex();
        if (mInitTabs != null && mInitTabs.contains(initTabIndex)) {
            setCurrentTab(mInitTabs.indexOf(initTabIndex));
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
        pagerTabContent = findViewById(R.id.pagertabcontent);

        mChooseMediaParams = getArguments().getParcelable(MediaConstants.KEY_CHOOSE_MEDIA_PARAMS);
        if (mChooseMediaParams == null) {
            mChooseMediaParams = getArguments().getParcelable(MediaConstants.KEY_CACHE_EXTRA_PARAMS);
        }
        mPresenter = new AlbumPresenterImpl(mChooseMediaParams);
        mPresenter.bindView(this);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    protected void initViews(View contentView) {
        initView();
        initEvents();
    }

    private void initEvents() {
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IAlbumFragment f = (IAlbumFragment) getCurrentFragment();
                if (f != null) {
                    f.onSendClick();
                }
            }
        });

        toolbarHelper.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
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
        int flag = (mChooseMediaParams == null || mChooseMediaParams.getShowAlbumTabs() == 0) ? Constants.ShowMediaTabType.STATE_ALL : mChooseMediaParams.getShowAlbumTabs();
        List<TextTabInfo> textTabList = new ArrayList<>(4);
        mInitTabs = new ArrayList<>(4);

        if ((flag & STATE_ALBUM) != 0) {
            albumTab = new DropDownTabInfo("相册", AlbumFragment.class, getBundleByType(1));
            textTabList.add(albumTab);
            mInitTabs.add(STATE_ALBUM);
        }

        if ((flag & STATE_VIDEO) != 0) {
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
                mSendBtn.setText("完成");
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public boolean onBackPressed() {
        if (mFragmentChangeListener != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(MediaConstants.EXTRA_KEY_VIDEO_TRANS_INFO, mChooseMediaParams);
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
