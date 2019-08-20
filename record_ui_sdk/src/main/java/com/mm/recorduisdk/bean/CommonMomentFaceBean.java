package com.mm.recorduisdk.bean;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenwangwang on 2018/3/26.
 * 时刻变脸面板的数据javabean
 */
public class CommonMomentFaceBean {

    /**
     * 数据是否是从服务器拉取的
     */
    private boolean isFromServer;

    /**
     * 本地版本号
     */
    private int localVersion;

    private List<FaceClass> faceClasses;

    /**
     * 服务器下发的数据字符串
     */
    private String jsonString;

    protected CommonMomentFaceBean(int localVersion, ArrayList<FaceClass> faceClasses, String jsonString, boolean isFromServer) {
        this.localVersion = localVersion;
        this.faceClasses = faceClasses;
        this.jsonString = jsonString;
        this.isFromServer = isFromServer;
    }

    public int getLocalVersion() {
        return localVersion;
    }

    public List<FaceClass> getFaceClasses() {
        return faceClasses;
    }

    public String getJsonString() {
        return jsonString;
    }

    public boolean isFromServer() {
        return isFromServer;
    }

    public void setFromServer(boolean fromServer) {
        isFromServer = fromServer;
    }

    public static class Builder {
        /**
         * 本地版本号
         */
        private int localVersion = -1;

        private ArrayList<FaceClass> faceClasses = new ArrayList<>();

        /**
         * 服务器下发的数据字符串
         */
        private String jsonString;

        /**
         * 数据是否是从服务器拉取的
         */
        private boolean isFromServer;

        public Builder setLocalVersion(int localVersion) {
            this.localVersion = localVersion;
            return this;
        }

        public Builder setFaceClasses(ArrayList<FaceClass> faceClasses) {
            this.faceClasses = faceClasses;
            return this;
        }

        public Builder setJsonString(String jsonString) {
            this.jsonString = jsonString;
            return this;
        }

        public Builder setFromServer(boolean fromServer) {
            isFromServer = fromServer;
            return this;
        }

        public CommonMomentFaceBean build() {
            return new CommonMomentFaceBean(localVersion, faceClasses, jsonString, isFromServer);
        }
    }
}
