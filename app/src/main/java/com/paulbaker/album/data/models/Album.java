package com.paulbaker.album.data.models;

import java.util.List;

public class Album {
    private String title;
    private List<MediaStoreImage> items;



    public Album(String title, List<MediaStoreImage> items) {
        this.title = title;
        this.items = items;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<MediaStoreImage> getItems() {
        return items;
    }

    public void setItems(List<MediaStoreImage> items) {
        this.items = items;
    }
}
