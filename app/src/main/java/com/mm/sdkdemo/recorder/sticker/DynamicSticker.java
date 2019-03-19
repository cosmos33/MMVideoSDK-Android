package com.mm.sdkdemo.recorder.sticker;

import com.mm.mmutil.log.Log4Android;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by zhu.tao on 2017/6/16.
 * 动态贴纸的类
 */

public class DynamicSticker {
    private String id;
    private String pic;
    private String zip;
    private int version;

    public DynamicSticker(String id, String pic, String zip, int version) {
        this.id = id;
        this.pic = pic;
        this.zip = zip;
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public String getPic() {
        return pic;
    }

    public String getZip() {
        return zip;
    }

    public int getVersion(){return version;}

    public static DynamicSticker fromJson(JSONObject json) {
        if (!json.has("id") || !json.has("pic") || !json.has("zip") || !json.has("version")) {
            return null;
        }
        try {
            return new DynamicSticker(json.optString("id"), json.optString("pic"), json.optString("zip"), json.optInt("version"));
        } catch (Exception ex) {
            Log4Android.getInstance().e(ex);
        }
        return null;
    }

    public static String toJson(List<DynamicSticker> list) {
        try {
            JSONArray array = new JSONArray();
            for (DynamicSticker sticker : list) {
                JSONObject object = new JSONObject();
                object.put("id", sticker.id);
                object.put("pic", sticker.pic);
                object.put("zip", sticker.zip);
                object.put("version", sticker.version);

                array.put(object);
            }
            return array.toString();
        } catch (Exception ex) {
            Log4Android.getInstance().e(ex);
        }
        return "";
    }
}
