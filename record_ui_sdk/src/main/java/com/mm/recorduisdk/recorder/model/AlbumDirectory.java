package com.mm.recorduisdk.recorder.model;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author shidefeng
 * @since 2017/6/6.
 */

public class AlbumDirectory implements Serializable{

    private String id;
    private String name;
    private String coverPath;
    private long dateAdded;
    private ArrayList<Photo> medias = new ArrayList<>();
    private ArrayList<Photo> facePhotos = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AlbumDirectory)) {
            return false;
        }

        AlbumDirectory directory = (AlbumDirectory) o;
        return TextUtils.equals(id, directory.id) && TextUtils.equals(name, directory.name);
    }

    @Override
    public int hashCode() {
        if (TextUtils.isEmpty(id)) {
            id = "";
        }
        if (TextUtils.isEmpty(name)) {
            name = "";
        }
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public ArrayList<Photo> getMedias() {
        return medias;
    }

    public ArrayList<Photo> getFacePhotos() {
        return facePhotos;
    }

    public void setMedias(ArrayList<Photo> medias) {
        this.medias = medias;
    }

    public void bulidCoverPath() {
        if (medias != null && medias.size() > 0) {
            final Photo photo = medias.get(0);
            coverPath = TextUtils.isEmpty(photo.thumbPath) ? photo.path : photo.thumbPath;
        }
    }
}
