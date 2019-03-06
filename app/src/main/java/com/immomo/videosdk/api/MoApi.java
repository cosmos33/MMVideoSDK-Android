package com.immomo.videosdk.api;

import com.immomo.mmutil.FileUtil;
import com.immomo.mmutil.IOUtils;
import com.immomo.mmutil.app.AppContext;
import com.immomo.videosdk.bean.MomentFace;
import com.immomo.videosdk.bean.MomentSticker;
import com.immomo.videosdk.config.Configs;
import com.immomo.videosdk.recorder.model.MusicContent;
import com.immomo.videosdk.recorder.sticker.DynamicSticker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MoApi {
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

        MusicContent music = new MusicContent();
        music.id = "5c46f95319374";
        music.name = "多幸运-韩安旭";
        music.length = 34000;
        music.cover = "http://img.momocdn.com/feedimage/BC/7D/BC7D1E98-AE1B-6B9A-CD39-D0C38778B8F520190122_250x250.webp";

        File file = Configs.getMusicFile(music.id);
        if (!file.exists() || file.length() <= 0) {
            FileUtil.copyAssets(AppContext.getContext(), "lucky_han.mp3", file);
        }
        music.path = file.getAbsolutePath();

        List<MusicContent> musics = new ArrayList<>();
        musics.add(music);
        return musics;
    }

    public static List<MomentFace> getFaceData() {
        List<MomentFace> faces = new ArrayList<>(5);
        MomentFace emptyFace = new MomentFace(true);
        emptyFace.setTitle("无");

        //手控樱花雨
        MomentFace momentFace1 = new MomentFace(false);
        momentFace1.setId("1");
        momentFace1.setImage_url("http://img.momocdn.com/app/42/CA/42CAA76F-C724-7BFF-FC92-78D680DC713420180308.png");
        momentFace1.setZip_url("http://img.momocdn.com/momentlib/EC/A9/ECA93927-D8FD-902F-41AE-D1284D7DB07920180308.zip");
        momentFace1.setVersion(17);
        momentFace1.setTitle("手势");

        //满屏爱心
        MomentFace momentFace2 = new MomentFace(false);
        momentFace2.setId("2");
        momentFace2.setImage_url("http://img.momocdn.com/app/E5/F4/E5F4FF90-59B0-AB3D-30BD-9E919BA4643920181205.png");
        momentFace2.setZip_url("http://img.momocdn.com/momentlib/D6/70/D67028AE-98AE-5E10-389A-25AA934AC73E20181204.zip");
        momentFace2.setVersion(20);
        momentFace2.setTitle("表情");

        //蝴蝶头饰
        MomentFace momentFace3 = new MomentFace(false);
        momentFace3.setId("3");
        momentFace3.setImage_url("http://img.momocdn.com/app/F4/0F/F40F1BD0-B3EE-DB53-920A-8B2AE92973FA20170628.png");
        momentFace3.setZip_url("http://img.momocdn.com/momentlib/FF/92/FF92E9B5-A905-5D02-00DA-BBD6CF3D9E4D20170929.zip");
        momentFace3.setVersion(12);
        momentFace3.setTitle("3D");

        //冰激凌抠图
        MomentFace momentFace4 = new MomentFace(false);
        momentFace4.setId("4");
        momentFace4.setImage_url("http://img.momocdn.com/app/3F/99/3F9943A2-2C8B-B418-E053-F9CE7111296620170707.png");
        momentFace4.setZip_url("http://img.momocdn.com/momentlib/51/0B/510BA543-4633-7E2D-7BF6-AAB93FFE9CEC20170705.zip");
        momentFace4.setVersion(20);
        momentFace4.setTitle("抠图");

        faces.add(emptyFace);
        faces.add(momentFace1);
        faces.add(momentFace2);
        faces.add(momentFace3);
        faces.add(momentFace4);

        return faces;
    }
}
