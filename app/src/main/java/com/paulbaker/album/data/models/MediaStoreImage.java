package com.paulbaker.album.data.models;

import android.net.Uri;

import java.io.Serializable;
import java.util.Date;

public class MediaStoreImage implements Comparable<MediaStoreImage>, Serializable {
    private Long id;
    private String displayName;
    private Date dateAdded;
    private Uri contentUri;

    public MediaStoreImage(Long id, String displayName, Date dateAdded, Uri contentUri) {
        this.id = id;
        this.displayName = displayName;
        this.dateAdded = dateAdded;
        this.contentUri = contentUri;
    }

    public Long getId() {
        return id;
    }

    public MediaStoreImage setId(Long id) {
        this.id = id;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public MediaStoreImage setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public Long getTime(){
        return dateAdded.getTime();
    }

    public MediaStoreImage setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
        return this;
    }

    public Uri getContentUri() {
        return contentUri;
    }

    public MediaStoreImage setContentUri(Uri contentUri) {
        this.contentUri = contentUri;
        return this;
    }

    @Override
    public int compareTo(MediaStoreImage mediaStoreImage) {
        return getTime().compareTo(mediaStoreImage.getTime());
    }
}
