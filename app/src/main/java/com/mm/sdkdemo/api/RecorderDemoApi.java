package com.mm.sdkdemo.api;


import android.text.TextUtils;

import com.mm.mediasdk.api.RecordBaseApi;
import com.mm.mmutil.FileUtil;
import com.mm.mmutil.IOUtils;
import com.mm.mmutil.app.AppContext;
import com.mm.recorduisdk.bean.CommonMomentFaceBean;
import com.mm.recorduisdk.bean.FaceClass;
import com.mm.recorduisdk.bean.MomentFace;
import com.mm.recorduisdk.bean.MomentSticker;
import com.mm.recorduisdk.config.Configs;
import com.mm.recorduisdk.recorder.model.MusicContent;
import com.mm.recorduisdk.recorder.sticker.DynamicSticker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecorderDemoApi extends RecordBaseApi {
    public static String getDynamicStickerList(ArrayList<DynamicSticker> list) throws Exception {
        String content = IOUtils.toString(AppContext.getContext().getAssets().open("tiezhi.txt"));
        JSONObject dataJson = new JSONObject(content).getJSONObject("data");
        JSONArray arrayJson = dataJson.getJSONArray("tags");

        DynamicSticker sticker;
        list.clear();
        for (int i = 0, length = arrayJson.length(); i < length; i++) {
            JSONObject json = arrayJson.getJSONObject(i);
            sticker = DynamicSticker.fromJson(json);
            if (sticker != null) {
                list.add(sticker);
            }
        }
        return arrayJson.toString();
    }

    public static String getStaticStickerList(ArrayList<MomentSticker> list) throws Exception {
        String content = IOUtils.toString(AppContext.getContext().getAssets().open("tiezhi.txt"));
        JSONObject dataJson = new JSONObject(content).getJSONObject("data");
        JSONArray arrayJson = dataJson.getJSONArray("tags");

        MomentSticker sticker;
        for (int i = 0; i < arrayJson.length(); i++) {
            JSONObject json = arrayJson.getJSONObject(i);
            sticker = MomentSticker.fromJson(json);
            if (sticker != null) {
                list.add(sticker);
            }
        }
        return arrayJson.toString();
    }

    public static List<MusicContent> getRecommendMusic() {

        MusicContent music1 = new MusicContent();
        music1.id = "5c46f95319390";
        music1.name = "多幸运-韩安旭";
        music1.length = 34000;
        music1.cover = "http://img.momocdn.com/feedimage/BC/7D/BC7D1E98-AE1B-6B9A-CD39-D0C38778B8F520190122_250x250.webp";

        File file = Configs.getMusicFile(music1.id);
        if (!file.exists() || file.length() <= 0) {
            FileUtil.copyAssets(AppContext.getContext(), "lucky_han.mp3", file);
        }
        music1.path = file.getAbsolutePath();


        List<MusicContent> musics = new ArrayList<>();
        musics.add(music1);
        return musics;
    }

    public CommonMomentFaceBean fetchCommonMomentFaceData() throws Exception {

        String resultData = request("face", null);
        if (TextUtils.isEmpty(resultData)) {
            return null;
        }
        JSONObject result = new JSONObject(resultData).optJSONObject("data");

        if (result == null) {
            return null;
        }
        int version = result.getInt("version");

        CommonMomentFaceBean.Builder builder = new CommonMomentFaceBean.Builder();

        builder.setLocalVersion(version);
        builder.setJsonString(result.toString());

        //先解析分类
        JSONArray clsItems = result.getJSONArray("class");
        JSONObject faceItems = result.getJSONObject("items");
        java.util.ArrayList<FaceClass> classList = new ArrayList<>();

        for (int i = 0, len = clsItems.length(); i < len; i++) {
            FaceClass bean = FaceClass.fromJson(clsItems.getJSONObject(i));
            //通过分类解析，各类别中的item值
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
    }
}
