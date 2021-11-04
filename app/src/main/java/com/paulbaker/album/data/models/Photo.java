package com.paulbaker.album.data.models;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.List;

public class Photo implements Comparable<Photo> {
    private String title;
    private List<MediaStoreImage> items;
    private Long maxTime;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Photo(String title, List<MediaStoreImage> items, Long maxTime) {
        this.title = title;
        this.items = items;
        this.maxTime = maxTime;
    }

    public String getTitle() {
        return title;
    }

    public Photo setTitle(String title) {
        this.title = title;
        return this;
    }

    public List<MediaStoreImage> getItems() {
        return items;
    }

    public Photo setItems(List<MediaStoreImage> items) {
        this.items = items;
        return this;
    }

    public Long getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(Long maxTime) {
        this.maxTime = maxTime;
    }

    @Override
    public int compareTo(Photo photo) {
        return getMaxTime().compareTo(photo.maxTime);
    }
}
