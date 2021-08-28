package com.mm.recorduisdk.recorder.helper;

import android.text.TextUtils;

import com.mm.base.PreferenceUtils;
import com.mm.mmutil.FileUtil;
import com.mm.mmutil.StringUtils;
import com.mm.recorduisdk.Constants;
import com.mm.recorduisdk.IRecordResourceConfig;
import com.mm.recorduisdk.RecordUISDK;
import com.mm.recorduisdk.bean.CommonMomentFaceBean;
import com.mm.recorduisdk.bean.FaceClass;
import com.mm.recorduisdk.bean.MomentFace;
import com.mm.recorduisdk.bean.MomentFaceDataProvider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by chenwangwang on 2018/3/28   <br/>
 * 快聊面板的数据获取实现类
 */
public class CommonMomentFaceDataProvider extends MomentFaceDataProvider<CommonMomentFaceBean> {

    @Override
    protected CommonMomentFaceBean getFromCache() {
        File file = getCacheFile();
        if (!FileUtil.isValidFile(file)) {
            return null;
        }
        try {
            CommonMomentFaceBean.Builder builder = new CommonMomentFaceBean.Builder();

            String data = FileUtil.readStr(file);
            JSONObject result = new JSONObject(data);
            builder.setLocalVersion(result.optInt("version", -1));
            JSONArray clsItems = result.getJSONArray("class");
            JSONObject faceItems = result.getJSONObject("items");
            java.util.ArrayList<FaceClass> classList = new ArrayList<>();

            for (int i = 0, len = clsItems.length(); i < len; i++) {
                FaceClass bean = FaceClass.fromJson(clsItems.getJSONObject(i));
                if (bean != null && !TextUtils.isEmpty(bean.getId()) && faceItems.has(bean.getId())) {
                    JSONArray items = faceItems.getJSONArray(bean.getId());

                    java.util.ArrayList<MomentFace> list = new ArrayList<>();
                    for (int j = 0, len2 = items.length(); j < len2; j++) {
                        MomentFace face = MomentFace.fromJson(items.getJSONObject(j));
                        if (face != null) {
                            list.add(face);
                            face.setClassId(bean.getId());
                        }
                    }
                    bean.setFaces(list);
                    classList.add(bean);
                }
            }
            builder.setFaceClasses(classList);

            return builder.build();
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    protected boolean isDataOutOfDate(CommonMomentFaceBean data) {
        if (data == null) {
            return true;
        }
        int serverVersion = PreferenceUtils.getValue(Constants.SPKey.Moment.KEY_MOMENT_FACE_VERSION, -1);
        return data.getLocalVersion() != serverVersion;
    }

    @Override
    protected CommonMomentFaceBean getFromServer() {
        try {
            IRecordResourceConfig<CommonMomentFaceBean> commonMomentFaceBeanIRecordResourceConfig = RecordUISDK.getResourceGetter().getMomentFaceDataConfig();
            if (commonMomentFaceBeanIRecordResourceConfig != null && commonMomentFaceBeanIRecordResourceConfig.isOpen()) {
                return commonMomentFaceBeanIRecordResourceConfig.getResource();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void saveToCache(CommonMomentFaceBean data) {
        if (data == null || TextUtils.isEmpty(data.getJsonString())) {
            return;
        }
        try {
            FileUtil.writeStr(getCacheFile(), data.getJsonString());
        } catch (IOException e) {

        }
    }

    @Override
    protected File getCacheFile() {
        return new File(MomentFaceFileUtil.getMomentFaceHomeDir(), StringUtils.md5(MomentFaceConstants.COMMON_CACHE_FILE_NAME));
    }
}
