package com.mm.sdkdemo.recorder.sticker;

/**
 * 用于动态贴纸
 * Created by zhu.tao on 2017/6/14.
 */

public class StickerEntity {

    public static final int TYPE_TEXT_STICKER = 1;
    public static final int TYPE_IMG_STICKER = 2;

    private String cover;
    private String zipurl;
    private long id;
    private String name;
    private String rank;
    private int status;
    private int type;
    private String default_text;
    private TagEngity tagEntity;
    private StickerLocationEntity location;//location 上传到server radio
    private StickerLocationEntity locationScreen;//location转化后的位置

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getZipurl() {
        return zipurl;
    }

    public void setZipurl(String zipurl) {
        this.zipurl = zipurl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDefault_text() {
        return default_text;
    }

    public void setDefault_text(String default_text) {
        this.default_text = default_text;
    }

    public TagEngity getTagEntity() {
        return tagEntity;
    }

    public void setTagEntity(TagEngity tagEntity) {
        this.tagEntity = tagEntity;
    }

    public StickerLocationEntity getLocation() {
        return location;
    }

    public void setLocation(StickerLocationEntity location) {
        this.location = location;
    }

    public StickerLocationEntity getLocationScreen() {
        return locationScreen;
    }

    public void setLocationScreen(StickerLocationEntity locationScreen) {
        this.locationScreen = locationScreen;
    }

    public static class StickerLocationEntity {
        private float originx;
        private float originy;
        private float width;
        private float height;
        private float angle;
        private String default_text;
        private long stickerId;

        public float getOriginx() {
            return originx;
        }

        public void setOriginx(float originx) {
            this.originx = originx;
        }

        public float getOriginy() {
            return originy;
        }

        public void setOriginy(float originy) {
            this.originy = originy;
        }

        public float getWidth() {
            return width;
        }

        public void setWidth(float width) {
            this.width = width;
        }

        public float getHeight() {
            return height;
        }

        public void setHeight(float height) {
            this.height = height;
        }

        public float getAngle() {
            return angle;
        }

        public void setAngle(float angle) {
            this.angle = angle;
        }

        public String getDefault_text() {
            return default_text;
        }

        public void setDefault_text(String default_text) {
            this.default_text = default_text;
        }

        public long getStickerId() {
            return stickerId;
        }

        public void setStickerId(long stickerId) {
            this.stickerId = stickerId;
        }
    }

    public static class TagEngity {
        private String bg_color;
        private String fg_color;
        private String text;

        public String getBg_color() {
            return bg_color;
        }

        public void setBg_color(String bg_color) {
            this.bg_color = bg_color;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getFg_color() {
            return fg_color;
        }

        public void setFg_color(String fg_color) {
            this.fg_color = fg_color;
        }
    }
}