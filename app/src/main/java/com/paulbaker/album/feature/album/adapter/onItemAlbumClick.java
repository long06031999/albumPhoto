package com.paulbaker.album.feature.album.adapter;

import android.view.View;

import com.paulbaker.album.data.models.Album;
import com.paulbaker.album.data.models.MediaStoreImage;
import com.paulbaker.album.data.models.Photo;

public interface onItemAlbumClick{
    public void onClick(View view, Photo album);
}


