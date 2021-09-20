package com.dev.android.album.data.models;

import android.net.Uri;

import java.util.Date;

public class MediaStoreImage {
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

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public Uri getContentUri() {
        return contentUri;
    }

    public void setContentUri(Uri contentUri) {
        this.contentUri = contentUri;
    }

}
