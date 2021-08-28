package com.mm.base_business.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

/**
 * Created by ruanlei on 12/4/16.
 */
public class BaseFragmentLifecycleEventDispatcher {

    private static final ArrayList<BaseFragmentLifecycleCallback> mFragmentLifecycleCallbacks = new ArrayList<>();

    public static void registerBaseFragmentLifecycleCallback(BaseFragmentLifecycleCallback callback) {
        synchronized (mFragmentLifecycleCallbacks) {
            mFragmentLifecycleCallbacks.add(callback);
        }
    }

    public static void unregisterBaseFragmentLifecycleCallback(BaseFragmentLifecycleCallback callback) {
        mFragmentLifecycleCallbacks.remove(callback);
    }

    public static void dispatchFragmentAttach(Fragment fragment, Activity activity) {
        Object[] callbacks = collectFrgmentLifecycleCallbacks();
        if (callbacks != null) {
            for (int i=0; i<callbacks.length; i++) {
                ((BaseFragmentLifecycleCallback)callbacks[i]).onFragmentAttach(fragment, activity);
            }
        }

    }

    public static void dispatchFragmentCreate(Fragment fragment, Bundle bundle) {
        Object[] callbacks = collectFrgmentLifecycleCallbacks();
        if (callbacks != null) {
            for (int i=0; i<callbacks.length; i++) {
                ((BaseFragmentLifecycleCallback)callbacks[i]).onFragmentCreate(fragment, bundle);
            }
        }
    }
    public static void dispatchFragmentCreateView(Fragment fragment, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Object[] callbacks = collectFrgmentLifecycleCallbacks();
        if (callbacks != null) {
            for (int i=0; i<callbacks.length; i++) {
                ((BaseFragmentLifecycleCallback)callbacks[i]).onFragmentCreateView(fragment, inflater, container, savedInstanceState);
            }
        }
    }

    public static void dispatchFragmentActivityCreated(Fragment baseFragment, Bundle savedInstanceState) {
        Object[] callbacks = collectFrgmentLifecycleCallbacks();
        if (callbacks != null) {
            for (int i=0; i<callbacks.length; i++) {
                ((BaseFragmentLifecycleCallback)callbacks[i]).onFragmentActivityCreated(baseFragment, savedInstanceState);
            }
        }
    }

    public static void dispatchFragmentStart(Fragment baseFragment) {
        Object[] callbacks = collectFrgmentLifecycleCallbacks();
        if (callbacks != null) {
            for (int i=0; i<callbacks.length; i++) {
                ((BaseFragmentLifecycleCallback)callbacks[i]).onFragmentStart(baseFragment);
            }
        }
    }

    public static void dispatchFragmentResume(Fragment baseFragment) {
        Object[] callbacks = collectFrgmentLifecycleCallbacks();
        if (callbacks != null) {
            for (int i=0; i<callbacks.length; i++) {
                ((BaseFragmentLifecycleCallback)callbacks[i]).onFragmentResume(baseFragment);
            }
        }
    }

    public static void dispatchFragmentPause(Fragment baseFragment) {
        Object[] callbacks = collectFrgmentLifecycleCallbacks();
        if (callbacks != null) {
            for (int i=0; i<callbacks.length; i++) {
                ((BaseFragmentLifecycleCallback)callbacks[i]).onFragmentPause(baseFragment);
            }
        }
    }

    public static void dispatchFragmentStop(Fragment baseFragment) {
        Object[] callbacks = collectFrgmentLifecycleCallbacks();
        if (callbacks != null) {
            for (int i=0; i<callbacks.length; i++) {
                ((BaseFragmentLifecycleCallback)callbacks[i]).onFragmentStop(baseFragment);
            }
        }
    }

    public static void dispatchFragmentSaveInstanceState(Fragment baseFragment, Bundle outState) {
        Object[] callbacks = collectFrgmentLifecycleCallbacks();
        if (callbacks != null) {
            for (int i=0; i<callbacks.length; i++) {
                ((BaseFragmentLifecycleCallback)callbacks[i]).onFragmentSaveInstanceState(baseFragment, outState);
            }
        }
    }

    public static void dispatchFragmentDestroyView(Fragment baseFragment) {
        Object[] callbacks = collectFrgmentLifecycleCallbacks();
        if (callbacks != null) {
            for (int i=0; i<callbacks.length; i++) {
                ((BaseFragmentLifecycleCallback)callbacks[i]).onFragmentDestroyView(baseFragment);
            }
        }
    }

    public static void dispatchFragmentDestroy(Fragment baseFragment) {
        Object[] callbacks = collectFrgmentLifecycleCallbacks();
        if (callbacks != null) {
            for (int i=0; i<callbacks.length; i++) {
                ((BaseFragmentLifecycleCallback)callbacks[i]).onFragmentDestroy(baseFragment);
            }
        }
    }

    public static void dispatchFragmentDetach(Fragment baseFragment) {
        Object[] callbacks = collectFrgmentLifecycleCallbacks();
        if (callbacks != null) {
            for (int i=0; i<callbacks.length; i++) {
                ((BaseFragmentLifecycleCallback)callbacks[i]).onFragmentDetach(baseFragment);
            }
        }
    }

    public static void dispatchFragmentLoad(Fragment baseFragment) {
        Object[] callbacks = collectFrgmentLifecycleCallbacks();
        if (callbacks != null) {
            for (int i=0; i<callbacks.length; i++) {
                ((BaseFragmentLifecycleCallback)callbacks[i]).onFragmentLoad(baseFragment);
            }
        }
    }

    public static void dispatchFragmentVisibleResume(Fragment baseFragment) {
        Object[] callbacks = collectFrgmentLifecycleCallbacks();
        if (callbacks != null) {
            for (int i=0; i<callbacks.length; i++) {
                ((BaseFragmentLifecycleCallback)callbacks[i]).onFragmentVisibleResume(baseFragment);
            }
        }
    }

    public static void dispatchFragmentVisiblePause(Fragment baseFragment) {
        Object[] callbacks = collectFrgmentLifecycleCallbacks();
        if (callbacks != null) {
            for (int i=0; i<callbacks.length; i++) {
                ((BaseFragmentLifecycleCallback)callbacks[i]).onFragmentVisiblePause(baseFragment);
            }
        }
    }

    private static Object[] collectFrgmentLifecycleCallbacks() {
        Object[] callbacks = null;
        synchronized (mFragmentLifecycleCallbacks) {
            if (mFragmentLifecycleCallbacks.size() > 0) {
                callbacks = mFragmentLifecycleCallbacks.toArray();
            }
        }
        return callbacks;
    }
}
