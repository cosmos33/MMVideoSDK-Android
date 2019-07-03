package com.mm.sdkdemo.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.cosmos.mdlog.MDLog;
import com.mm.sdkdemo.log.LogTag;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 时刻变脸素材
 * Project momodev
 * Package com.mm.momo.moment.model
 * Created by tangyuchun on 8/23/16.
 */
public class MomentFace implements Cloneable, Parcelable, IZipResourceModel {
    private String id;


    private String title;
    private int version;
    private String zip_url;


    private String image_url;
    private boolean isFaceRig = false;
    private boolean isArkit = false;
    private String classId;

    //是否空的资源，用于第一位展示，清空资源用
    private boolean isEmptyFace = false;

    private boolean isCustomMaskModelType = false;


    public MomentFace(boolean pIsEmptyFace) {
        this.isEmptyFace = pIsEmptyFace;
    }


    protected MomentFace(Parcel in) {
        id = in.readString();
        title = in.readString();
        version = in.readInt();
        zip_url = in.readString();
        image_url = in.readString();
        isFaceRig = in.readByte() != 0;
        isArkit = in.readByte() != 0;
        classId = in.readString();
        isEmptyFace = in.readByte() != 0;
        isCustomMaskModelType = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeInt(version);
        dest.writeString(zip_url);
        dest.writeString(image_url);
        dest.writeByte((byte) (isFaceRig ? 1 : 0));
        dest.writeByte((byte) (isArkit ? 1 : 0));
        dest.writeString(classId);
        dest.writeByte((byte) (isEmptyFace ? 1 : 0));
        dest.writeByte((byte) (isCustomMaskModelType ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MomentFace> CREATOR = new Creator<MomentFace>() {
        @Override
        public MomentFace createFromParcel(Parcel in) {
            return new MomentFace(in);
        }

        @Override
        public MomentFace[] newArray(int size) {
            return new MomentFace[size];
        }
    };

    public boolean isEmptyFace() {
        return isEmptyFace;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static MomentFace fromJson(JSONObject json) {
        if (json == null || !json.has("id")) {
            return null;
        }
        try {
            MomentFace face = new MomentFace(false);
            face.id = json.getString("id");
            face.title = json.optString("title");
            face.version = json.getInt("version");
            face.zip_url = json.getString("zip_url");
            face.image_url = json.getString("image_url");
            face.isFaceRig = json.optInt("is_facerig") == 1;
            face.isArkit = json.optInt("is_arkit") == 1;
            return face;
        } catch (JSONException e) {
            MDLog.printErrStackTrace(LogTag.RECORDER.FACE, e);
        }
        return null;
    }

    public static JSONObject toJson(MomentFace face) {
        if (face == null) {
            return null;
        }
        try {
            JSONObject json = new JSONObject();
            json.put("id", face.id);
            json.put("title", face.title);
            json.put("version", face.version);
            json.put("zip_url", face.zip_url);
            json.put("image_url", face.image_url);
            json.put("is_facerig", face.isFaceRig ? 1 : 0);
            json.put("is_arkit", face.isArkit ? 1 : 0);
            return json;
        } catch (JSONException e) {
            MDLog.printErrStackTrace(LogTag.RECORDER.FACE, e);
        }
        return null;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getVersion() {
        return version;
    }

    @Override
    public String getResource() {
        return zip_url;
    }

    public String getZip_url() {
        return zip_url;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClassId() {
        return classId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setZip_url(String zip_url) {
        this.zip_url = zip_url;
    }

    public boolean isFaceRig() {
        return isFaceRig;
    }

    public boolean isArkit() {
        return isArkit;
    }

    public void setIsArkit(boolean is_arkit) {
        this.isArkit = is_arkit;
    }

    @Override
    public MomentFace clone() {
        MomentFace face = null;
        try {
            face = (MomentFace) super.clone();
        } catch (CloneNotSupportedException e) {
            MDLog.printErrStackTrace(LogTag.RECORDER.FACE, e);
        }
        if (face == null) {
            face = new MomentFace(isEmptyFace);
            face.id = this.id;
            face.title = this.title;
            face.version = this.version;
            face.zip_url = this.zip_url;
            face.image_url = this.image_url;
            face.isCustomMaskModelType = this.isCustomMaskModelType;
        }
        return face;
    }

    public boolean isCustomMaskModelType() {
        return isCustomMaskModelType;
    }

    public void setCustomMaskModelType(boolean customMaskModelType) {
        isCustomMaskModelType = customMaskModelType;
    }
}
