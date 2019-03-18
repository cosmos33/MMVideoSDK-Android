package com.mm.sdkdemo.recorder.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by XiongFangyu on 2017/6/7.
 */
public class PlaceHolderFragment extends Fragment {
    private String name;
    private long time;

    public static PlaceHolderFragment newInstance(String name) {
        PlaceHolderFragment f = new PlaceHolderFragment();
        f.name = name;
        f.time = System.currentTimeMillis();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return new View(container.getContext());
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "PlaceHolderFragment " + time + " " + name;
    }
}
