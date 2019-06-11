package com.mm.sdkdemo.api;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mm.base.BaseApi;
import com.mm.mmutil.FileUtil;
import com.mm.mmutil.IOUtils;
import com.mm.mmutil.app.AppContext;
import com.mm.sdkdemo.bean.MomentFace;
import com.mm.sdkdemo.bean.MomentSticker;
import com.mm.sdkdemo.config.Configs;
import com.mm.sdkdemo.player.PlayVideo;
import com.mm.sdkdemo.recorder.model.MusicContent;
import com.mm.sdkdemo.recorder.sticker.DynamicSticker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MoApi extends BaseApi {
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

    public static Map<String, List<MomentFace>> getFaceData() {
        Map<String, List<MomentFace>> sourceMap = new LinkedHashMap<>();

        List<MomentFace> stickers = new ArrayList<>(5);
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

        //小黄鸭
        MomentFace momentFace5 = new MomentFace(false);
        momentFace5.setId("5");
        momentFace5.setImage_url("http://img.momocdn.com/app/3F/54/3F5477B4-0E42-958F-7415-196D901D1A0320180227.png");
        momentFace5.setZip_url("http://img.momocdn.com/momentlib/A1/80/A1802264-5A87-E625-5391-3A4C053927AA20180227.zip");
        momentFace5.setVersion(20);
        momentFace5.setTitle("小黄鸭");

        MomentFace momentFace6 = new MomentFace(false);
        momentFace6.setId("6");
        momentFace6.setImage_url("http://img.momocdn.com/app/C6/35/C63576E7-6094-E223-773D-2D3B9471431420171023.png");
        momentFace6.setZip_url("http://img.momocdn.com/momentlib/4C/CF/4CCFD1B8-C548-3118-3EF0-C17C705455BF20171023.zip");
        momentFace6.setVersion(30);
        momentFace6.setTitle("3D_Test1");

        stickers.add(emptyFace);
        stickers.add(momentFace1);
        stickers.add(momentFace2);
        stickers.add(momentFace3);
        stickers.add(momentFace4);
        stickers.add(momentFace5);
        stickers.add(momentFace6);

        sourceMap.put("sticker", stickers);

        List<MomentFace> props = new ArrayList<>();
//        props.add(momentFace6);

        sourceMap.put("prop", props);

        return sourceMap;
    }

    private static String[] coverList = {
            "http://img.momocdn.com/feedvideo/66/50/6650822D-1E33-3CF1-7ED4-0477CE5D39BB20190407_L.jpg",
            "http://img.momocdn.com/feedvideo/97/F0/97F03508-9155-69BB-DA60-D4D0C29D6E3820190408_L.jpg",
            "http://img.momocdn.com/feedvideo/5F/56/5F56D618-2759-973D-5FE7-390B7D23891220190410_L.jpg",
            "http://img.momocdn.com/feedvideo/14/8C/148CCE0A-2ED7-5AA8-9D80-B1EE0940959D20190407_L.jpg",
            "http://img.momocdn.com/feedvideo/BE/FB/BEFBC913-BB1D-A3C1-20A2-681F34E59DB920190407_L.jpg",
            "http://img.momocdn.com/feedvideo/5B/07/5B079A58-DAE9-FF1B-461D-E5723CDD926920190407_L.jpg",
            "http://img.momocdn.com/feedvideo/56/A5/56A55EDD-68C3-0F54-2730-7C5EDD580F1520190407_L.jpg",
            "http://img.momocdn.com/feedvideo/30/C7/30C7C415-C3C5-D9DC-F0ED-688604C0AFC220190409_L.jpg",
            "http://img.momocdn.com/feedvideo/52/49/52495102-D2B5-BD47-DBC4-B54AA8C1AD1520190408_L.jpg",
            "http://img.momocdn.com/feedvideo/08/6A/086A5DC8-0FFF-CE5C-EC66-3BFFC142184620190407_L.jpg",
            "http://img.momocdn.com/feedvideo/7E/72/7E728D7B-F343-34CB-6121-837F7F035FBC20190409_L.jpg",
            "http://img.momocdn.com/feedvideo/BF/E3/BFE3D3B0-B1E8-F0F0-BCD7-6361FD6ADEAB20190331_L.jpg",
    };

    private static String[] videoList = {
            "http://video.momocdn.com/feedvideo/30/B6/30B6A927-FD47-39CD-A4A2-A73E4F371E8F20190407.mp4",
            "http://video.momocdn.com/feedvideo/D6/35/D635972C-16BC-79F0-430B-652C3FF5AE1720190408.mp4",
            "http://video.momocdn.com/feedvideo/DA/81/DA816D3C-DB2D-8884-9907-411D14A0973B20190410_h265.mp4",
            "http://video.momocdn.com/feedvideo/FC/9A/FC9AE355-9BD4-D3A9-D9B8-F22F42443EB220190407.mp4",
            "http://video.momocdn.com/feedvideo/D9/4F/D94F8AAA-1992-245C-4486-1FD098DFFC2F20190407_h265.mp4",
            "http://video.momocdn.com/feedvideo/59/7B/597BC349-CBD7-FA43-6AD9-31DCEB77780520190407_h265.mp4",
            "http://video.momocdn.com/feedvideo/14/C0/14C090C1-2597-7471-CC8C-FA7B7A261B6E20190407_h265.mp4",
            "http://video.momocdn.com/feedvideo/FB/B1/FBB1DF39-2B3B-E65E-3C02-1CE84954504D20190409_h265.mp4",
            "http://video.momocdn.com/feedvideo/24/6F/246F4D5D-2F22-E1DC-268E-211492AEFBF420190408_h265.mp4",
            "http://video.momocdn.com/feedvideo/49/AC/49AC0722-D4EF-DC15-EAA3-A21EBEDA3AEF20190407_h265.mp4",
            "http://video.momocdn.com/feedvideo/F7/E7/F7E746DF-004D-4E6A-0BE2-FA6EA33E726A20190409_h265.mp4",
            "http://video.momocdn.com/feedvideo/7B/FC/7BFCB03F-7561-6CF6-518A-E41F59BCDCD020190331_h265.mp4",
    };

    public List<PlayVideo> getRandomPlayVideoList() throws Exception {
        String jsonString = new JSONObject(request("play", null)).optString(DATA);
        return new Gson().fromJson(jsonString, new TypeToken<List<PlayVideo>>() {
        }.getType());
    }
}
