package com.mm.player_business.api;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mm.base.BaseApi;
import com.mm.player_business.PlayVideo;

import org.json.JSONObject;

import java.util.List;

public class PlayerDemoApi  extends BaseApi {

    private static String[] coverList = {
            "http://img.momocdn.com/feedvideo/66/50/6650822D-1E33-3CF1-7ED4-0477CE5D39BB20190407_L.jpg",
            "http://img.momocdn.com/feedvideo/97/F0/97F03508-9155-69BB-DA60-D4D0C29D6E3820190408_L.jpg",
            "http://img.momocdn.com/feedvideo/5F/56/5F56D618-2759-973D-5FE7-390B7D23891220190410_L.jpg",
            "http://img.momocdn.com/feedvideo/14/8C/148CCE0A-2ED7-5AA8-9D80-B1EE0940959D20190407_L.jpg",
            "http://img.momocdn.com/feedvideo/BE/FB/BEFBC913-BB1D-A3C1-20A2-681F34E59DB920190407_L.jpg",
            "http://img.momocdn.com/feedvideo/5B/07/5B079A58-DAE9-FF1B-461D-E5723CDD926920190407_L.jpg",
            "http://img.momocdn.com/feedvideo/56/A5/56A55EDD-68C3-0F54-2730-7C5EDD580F1520190407_L.jpg",
            "http://img.momocdn.com/feedvideo/32/C7/30C7C415-C3C5-D9DC-F0ED-688604C0AFC220190409_L.jpg",
            "http://img.momocdn.com/feedvideo/52/49/52495102-D2B5-BD47-DBC4-B54AA8C1AD1520190408_L.jpg",
            "http://img.momocdn.com/feedvideo/08/6A/086A5DC8-0FFF-CE5C-EC66-3BFFC142184620190407_L.jpg",
            "http://img.momocdn.com/feedvideo/7E/72/7E728D7B-F343-34CB-6121-837F7F035FBC20190409_L.jpg",
            "http://img.momocdn.com/feedvideo/BF/E3/BFE3D3B0-B1E8-F0F0-BCD7-6361FD6ADEAB20190331_L.jpg",
    };

    private static String[] videoList = {
            "http://video.momocdn.com/feedvideo/32/B6/30B6A927-FD47-39CD-A4A2-A73E4F371E8F20190407.mp4",
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
