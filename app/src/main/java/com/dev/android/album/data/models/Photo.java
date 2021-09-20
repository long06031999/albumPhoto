package com.dev.android.album.data.models;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.jiajunhui.xapp.medialoader.bean.PhotoItem;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Photo {
    private String title;
    private List<MediaStoreImage> items;
    @RequiresApi(api = Build.VERSION_CODES.N)
    public Photo(String title, List<MediaStoreImage> items) {
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
