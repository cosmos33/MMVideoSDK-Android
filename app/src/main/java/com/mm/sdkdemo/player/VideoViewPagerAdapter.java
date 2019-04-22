package com.mm.sdkdemo.player;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class VideoViewPagerAdapter<T extends Fragment> extends FragmentStatePagerAdapter {
    private boolean needRefresh = false;
    private List<PlayVideo> datas;

    public VideoViewPagerAdapter(FragmentManager fm, List<PlayVideo> datas) {
        super(fm);
        this.datas = datas;
    }

    @Override
    public Fragment getItem(int position) {
        return VideoPlayItemFragment.newInstance(position, datas.get(position));
    }

    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size();
    }

    public String getNextUrl(int pos) {
        if (pos+1 >= datas.size()) {
            return null;
        }
        return datas.get(pos+1).videoUrl;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    public void addLastItems(List<PlayVideo> feeds) {
        needRefresh = false;
        datas.addAll(feeds);
        notifyDataSetChanged();
    }

    public void refreshList(List<PlayVideo> feeds) {
        needRefresh = true;
        datas.clear();
        datas.addAll(feeds);
        notifyDataSetChanged();
    }

    public T getIndexFragment(int index) {
        try {
            Field privateArrayList = FragmentStatePagerAdapter.class.getDeclaredField("mFragments");
            privateArrayList.setAccessible(true);
            ArrayList<T> mFragments = (ArrayList<T>) privateArrayList.get(this);
            return mFragments.size() > 0 ? mFragments.get(index) : null;

        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
        }
        return null;
    }

    public List<T> getAllFragment() {
        try {
            Field privateArrayList = FragmentStatePagerAdapter.class.getDeclaredField("mFragments");
            privateArrayList.setAccessible(true);
            ArrayList<T> mFragments = (ArrayList<T>) privateArrayList.get(this);
            return mFragments;

        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
        }
        return null;
    }

    @Override
    public int getItemPosition(Object object) {
        return needRefresh ? POSITION_NONE : super.getItemPosition(object);
    }
}
