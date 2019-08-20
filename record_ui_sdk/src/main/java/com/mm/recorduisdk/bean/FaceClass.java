package com.mm.recorduisdk.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 *变脸的分类
 * Created by momo on 2017/5/10.
 */

public class FaceClass {
    /**
     * id : 123a
     * image_url : https://web.immomo.com/img/logo-momo.png?v=2
     */

    private String id;
    private String image_url;
    private String selected_image_url;
    private String tabName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getSelected_image_url() {
        return selected_image_url;
    }

    public void setSelected_image_url(String selected_image_url) {
        this.selected_image_url = selected_image_url;
    }

    private List<MomentFace> faces;

    public List<MomentFace> getFaces() {
        return faces;
    }

    public void setFaces(List<MomentFace> faces) {
        this.faces = faces;
    }

    public static FaceClass fromJson(JSONObject json) {
        if (json == null || !json.has("id")) {
            return null;
        }
        try {
            FaceClass bean = new FaceClass();
            bean.setId(json.getString("id"));
            bean.setImage_url(json.getString("image_url"));
            bean.setSelected_image_url(json.getString("selected_image_url"));
            bean.setTabName(json.optString("name"));
            return bean;
        } catch (JSONException e) {
            return null;
        }
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }
}
