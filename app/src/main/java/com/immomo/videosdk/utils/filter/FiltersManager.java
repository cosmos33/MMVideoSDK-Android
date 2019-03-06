package com.immomo.videosdk.utils.filter;

import android.text.TextUtils;

import com.immomo.mmutil.app.AppContext;
import com.momo.mcamera.filtermanager.MMPresetFilter;
import com.momo.mcamera.filtermanager.MMPresetFilterStore;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * NOTE
 * 1. 使用滤镜的页面oncreate时addRefrence
 * 2. 页面销毁时调用subRefrence
 *
 * 注意使用后调用resetFilter，
 * Created by zhoukai on 7/7/16.
 */
public class FiltersManager {
    public static List<MMPresetFilter> getAllFilters() {
        List<MMPresetFilter> list = new ArrayList<>();
        File[] folders = FilterFileUtil.getMomentFilterImgHomeDir().listFiles();
        if (folders == null) {
            return list;
        }

        List<MMPresetFilter> netFilterList = getNetFilters(folders);
        if (netFilterList != null && netFilterList.size() > 0) {
            list.addAll(list.size(), netFilterList);
        }
        return list;
    }

    /**
     * 获取从网络下载到本地的Filters资源
     */
    private static List<MMPresetFilter> getNetFilters(File[] folders) {
        List<MMPresetFilter> list = new ArrayList<>();
        Arrays.sort(folders);
        for (int i = 0, length = folders.length; i < length; i++) {
            MMPresetFilter filter = new MMPresetFilter(AppContext.getContext());
            File folder = folders[i];

            String[] names = folder.getName().split("_");
            String id = "";
            String firstName = "";
            if (names.length > 2) {
                id = names[1];
                firstName = names[2];
            }

            boolean isMacosx = folder.getPath().toLowerCase().endsWith("__macosx");
            if (!isMacosx) {
                filter.mFilterName = firstName;
                filter.mFilterId = id;
                //需要确保文件存在

                filter.lookupUrl = folder.getPath() + "/" + "lookup.png";
                filter.manifestUrl = folder.getPath() + "/" + "manifest.json";
                if (TextUtils.isEmpty(filter.mFilterName)) {
                    filter.isFilterFileExist = false;
                    continue;
                }
                filter.isFilterFileExist = true;
                MMPresetFilterStore.generateFilter(AppContext.getContext(), folder, filter);
                list.add(filter);
            }
        }
        return list;
    }
}
