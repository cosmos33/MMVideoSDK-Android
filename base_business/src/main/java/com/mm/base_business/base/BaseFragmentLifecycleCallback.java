package com.mm.base_business.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;

/**
 * Created by ruanlei on 12/4/16.
 */
public interface BaseFragmentLifecycleCallback {

    void onFragmentAttach(Fragment fragment, Activity activity);
    void onFragmentCreate(Fragment fragment, Bundle bundle);
    void onFragmentCreateView(Fragment fragment, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
    void onFragmentActivityCreated(Fragment fragment, Bundle savedInstanceState);
    void onFragmentStart(Fragment fragment);
    void onFragmentResume(Fragment fragment);
    void onFragmentPause(Fragment fragment);
    void onFragmentStop(Fragment fragment);
    void onFragmentDestroyView(Fragment fragment);
    void onFragmentDestroy(Fragment fragment);
    void onFragmentDetach(Fragment fragment);
    void onFragmentSaveInstanceState(Fragment fragment, Bundle outState);
    void onFragmentLoad(Fragment fragment);
    void onFragmentVisibleResume(Fragment fragment);
    void onFragmentVisiblePause(Fragment fragment);

}
