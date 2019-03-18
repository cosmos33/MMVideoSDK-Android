package com.mm.sdkdemo.bean;

import com.immomo.mmutil.log.Log4Android;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Project momodev
 * Package com.mm.momo.moment.model
 * Created by tangyuchun on 7/27/16.
 */
public class MomentSticker {
    private String id;
    private String pic;

    public MomentSticker(String id, String pic) {
        this.id = id;
        this.pic = pic;
    }

    public String getId() {
        return id;
    }

    public String getPic() {
        return pic;
    }

    public static MomentSticker fromJson(JSONObject json) {
        if (!json.has("id") || !json.has("pic")) {
            return null;
        }
        try {
            return new MomentSticker(json.optString("id"), json.optString("pic"));
        } catch (Exception ex) {
            Log4Android.getInstance().e(ex);
        }
        return null;
    }

    public static String toJson(List<MomentSticker> list) {
        try {
            JSONArray array = new JSONArray();
            for (MomentSticker sticker : list) {
                JSONObject object = new JSONObject();
                object.put("id", sticker.id);
                object.put("pic", sticker.pic);

                array.put(object);
            }
            return array.toString();
        } catch (Exception ex) {
            Log4Android.getInstance().e(ex);
        }
        return "";
    }
}
