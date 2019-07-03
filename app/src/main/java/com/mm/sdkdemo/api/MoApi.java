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

        MusicContent music2 = new MusicContent();
        music2.id = "5c46f95319399";
        music2.name = "再见，昨天";
        music2.length = (4 * 60 + 55) * 1000;
        music2.cover = "http://img.momocdn.com/feedimage/BC/7D/BC7D1E98-AE1B-6B9A-CD39-D0C38778B8F520190122_250x250.webp";

        File file2 = Configs.getMusicFile(music2.id);
        if (!file.exists() || file2.length() <= 0) {
            FileUtil.copyAssets(AppContext.getContext(), "yesterday.mp3", file2);
        }
        music2.path = file2.getAbsolutePath();

        List<MusicContent> musics = new ArrayList<>();
        musics.add(music1);
        musics.add(music2);
        return musics;
    }

    public static Map<String, List<MomentFace>> getFaceData() {
        Map<String, List<MomentFace>> sourceMap = new LinkedHashMap<>();

        List<MomentFace> stickers = new ArrayList<>(5);
        MomentFace emptyFace = new MomentFace(true);
        emptyFace.setTitle("无");

        //手控樱花雨
        MomentFace momentFace1 = new MomentFace(false);
        momentFace1.setId("10");
        momentFace1.setImage_url("http://img.momocdn.com/app/42/CA/42CAA76F-C724-7BFF-FC92-78D680DC713420180308.png");
        momentFace1.setZip_url("http://img.momocdn.com/momentlib/2B/68/2B68CFD4-256A-1673-1543-CAE5D6CF364320190417.zip");
        momentFace1.setVersion(17);
        momentFace1.setTitle("比手枪");

        //满屏爱心
        MomentFace momentFace2 = new MomentFace(false);
        momentFace2.setId("20");
        momentFace2.setImage_url("http://img.momocdn.com/app/E5/F4/E5F4FF90-59B0-AB3D-30BD-9E919BA4643920181205.png");
        momentFace2.setZip_url("http://img.momocdn.com/momentlib/D6/86/D686CA2C-BE88-EA71-7AA5-A34DCADE35CD20190417.zip");
        momentFace2.setVersion(20);
        momentFace2.setTitle("多手势");

        //蝴蝶头饰
        MomentFace momentFace3 = new MomentFace(false);
        momentFace3.setId("30");
        momentFace3.setImage_url("http://img.momocdn.com/app/F4/0F/F40F1BD0-B3EE-DB53-920A-8B2AE92973FA20170628.png");
        momentFace3.setZip_url("http://img.momocdn.com/momentlib/87/9C/879C7889-9132-9144-B32E-A2C866CC498C20190412.zip");
        momentFace3.setVersion(12);
        momentFace3.setTitle("丑变美");

        //冰激凌抠图
        MomentFace momentFace4 = new MomentFace(false);
        momentFace4.setId("40");
        momentFace4.setImage_url("http://img.momocdn.com/app/3F/99/3F9943A2-2C8B-B418-E053-F9CE7111296620170707.png");
        momentFace4.setZip_url("http://img.momocdn.com/momentlib/B2/96/B2960775-91CF-5337-748B-08FF3EE720F620190417.zip");
        momentFace4.setVersion(20);
        momentFace4.setTitle("丘比特");

        //编辑器1
        MomentFace momentFace5 = new MomentFace(false);
        momentFace5.setId("50");
        momentFace5.setImage_url("http://img.momocdn.com/app/99/BC/99BCAEAE-E4ED-B303-2B53-9EF4872F5E8520190507.png");
        momentFace5.setZip_url("http://img.momocdn.com/momentlib/09/60/0960FD3E-8020-2331-6C7A-3BC9C8DE8B8B20190513.zip");
        momentFace5.setVersion(20);
        momentFace5.setTitle("编辑器1");

        //编辑器2
        MomentFace momentFace6 = new MomentFace(false);
        momentFace6.setId("60");
        momentFace6.setImage_url("http://img.momocdn.com/app/99/BC/99BCAEAE-E4ED-B303-2B53-9EF4872F5E8520190507.png");
        momentFace6.setZip_url("http://img.momocdn.com/momentlib/E1/FF/E1FFA1BD-4841-9040-B54E-D698DF5D1F5520190513.zip");
        momentFace6.setVersion(20);
        momentFace6.setTitle("编辑器2");

        //编辑器3
        MomentFace momentFace7 = new MomentFace(false);
        momentFace7.setId("70");
        momentFace7.setImage_url("http://img.momocdn.com/app/99/BC/99BCAEAE-E4ED-B303-2B53-9EF4872F5E8520190507.png");
        momentFace7.setZip_url("http://img.momocdn.com/momentlib/08/33/0833AACE-B8CC-0E5E-8885-F240AC53CEFD20190513.zip");
        momentFace7.setVersion(20);
        momentFace7.setTitle("编辑器3");

        //编辑器4
        MomentFace momentFace8 = new MomentFace(false);
        momentFace8.setId("80");
        momentFace8.setImage_url("http://img.momocdn.com/app/99/BC/99BCAEAE-E4ED-B303-2B53-9EF4872F5E8520190507.png");
        momentFace8.setZip_url("http://img.momocdn.com/momentlib/32/A6/32A60705-912F-7F23-0C16-AFC6637AF4AC20190513.zip");
        momentFace8.setVersion(20);
        momentFace8.setTitle("编辑器4");

        //编辑器5
        MomentFace momentFace9 = new MomentFace(false);
        momentFace9.setId("90");
        momentFace9.setImage_url("http://img.momocdn.com/app/99/BC/99BCAEAE-E4ED-B303-2B53-9EF4872F5E8520190507.png");
        momentFace9.setZip_url("http://img.momocdn.com/momentlib/8C/03/8C03CE17-6FB8-54B8-C913-DEACE80C06E320190513.zip");
        momentFace9.setVersion(20);
        momentFace9.setTitle("编辑器5");

        MomentFace momentFace10 = new MomentFace(false);
        momentFace10.setId("100");
        momentFace10.setImage_url("http://img.momocdn.com/app/99/BC/99BCAEAE-E4ED-B303-2B53-9EF4872F5E8520190507.png");
        momentFace10.setZip_url("http://img.momocdn.com/momentlib/53/1B/531BEBF3-2892-93D3-D428-B90EB89B3B3720190603.zip");
        momentFace10.setVersion(20);
        momentFace10.setTitle("左眨眼");

        MomentFace momentFace11 = new MomentFace(false);
        momentFace11.setId("110");
        momentFace11.setImage_url("http://img.momocdn.com/app/99/BC/99BCAEAE-E4ED-B303-2B53-9EF4872F5E8520190507.png");
        momentFace11.setZip_url("http://img.momocdn.com/momentlib/8D/53/8D53DED4-1AD5-1316-016E-D63DBCD36FD420190603.zip");
        momentFace11.setVersion(20);
        momentFace11.setTitle("右眨眼");

        MomentFace momentFace12 = new MomentFace(false);
        momentFace12.setId("120");
        momentFace12.setImage_url("http://img.momocdn.com/app/99/BC/99BCAEAE-E4ED-B303-2B53-9EF4872F5E8520190507.png");
        momentFace12.setZip_url("http://img.momocdn.com/momentlib/6B/9B/6B9B0C18-0C9A-E93E-BDFE-E8FC4996A17F20190603.zip");
        momentFace12.setVersion(20);
        momentFace12.setTitle("双眼眨");

        //小黄鸭
        MomentFace momentFace13 = new MomentFace(false);
        momentFace13.setId("5");
        momentFace13.setImage_url("http://img.momocdn.com/app/3F/54/3F5477B4-0E42-958F-7415-196D901D1A0320180227.png");
        momentFace13.setZip_url("http://img.momocdn.com/momentlib/A1/80/A1802264-5A87-E625-5391-3A4C053927AA20180227.zip");
        momentFace13.setVersion(20);
        momentFace13.setTitle("小黄鸭");

        MomentFace momentFace14 = new MomentFace(false);
        momentFace14.setId("6");
        momentFace14.setImage_url("http://img.momocdn.com/app/C6/35/C63576E7-6094-E223-773D-2D3B9471431420171023.png");
        momentFace14.setZip_url("http://img.momocdn.com/momentlib/4C/CF/4CCFD1B8-C548-3118-3EF0-C17C705455BF20171023.zip");
        momentFace14.setVersion(30);
        momentFace14.setTitle("3D_Test1");

        MomentFace momentFace15 = new MomentFace(false);
        momentFace15.setId("7");
        momentFace15.setImage_url("http://img.momocdn.com/app/C6/35/C63576E7-6094-E223-773D-2D3B9471431420171023.png");
        momentFace15.setZip_url("http://img.momocdn.com/momentlib/39/98/3998A7ED-6845-C278-79F4-F5A58972FEA820170904.zip");
        momentFace15.setVersion(30);
        momentFace15.setTitle("绵羊音");

        MomentFace momentFace18 = new MomentFace(false);
        momentFace18.setId("21");
        momentFace18.setImage_url("http://img.momocdn.com/app/E4/69/E4698EF3-F91C-00E1-DB7D-E19758BFFC3920190613.png");
        momentFace18.setZip_url("http://img.momocdn.com/momentlib/C4/AA/C4AAFBCA-6608-1654-3FA3-C82D1330EBFE20190621.zip");
        momentFace18.setVersion(30);
        momentFace18.setTitle("3D触屏点击");


        MomentFace momentFace23 = new MomentFace(false);
        momentFace23.setId("170");
        momentFace23.setImage_url("http://img.momocdn.com/app/E4/69/E4698EF3-F91C-00E1-DB7D-E19758BFFC3920190613.png");
        momentFace23.setZip_url("http://img.momocdn.com/momentlib/C2/4C/C24CD16D-457C-4420-980A-113AB785134320190627.zip");
        momentFace23.setVersion(30);
        momentFace23.setTitle("鹿角表情触发");

        stickers.add(emptyFace);
        stickers.add(momentFace1);
        stickers.add(momentFace2);
        stickers.add(momentFace3);
        stickers.add(momentFace4);
        stickers.add(momentFace5);
        stickers.add(momentFace6);
        stickers.add(momentFace7);
        stickers.add(momentFace8);
        stickers.add(momentFace9);
        stickers.add(momentFace10);
        stickers.add(momentFace11);
        stickers.add(momentFace12);
        stickers.add(momentFace13);
        stickers.add(momentFace14);
        stickers.add(momentFace15);
        stickers.add(momentFace18);
        /*stickers.add(momentFace19);
        stickers.add(momentFace20);
        stickers.add(momentFace21);
        stickers.add(momentFace22);*/
        stickers.add(momentFace23);

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
