package com.paulbaker.album.feature.album.adapter;

import android.view.View;

import com.paulbaker.album.data.models.MediaStoreImage;

public interface OnItemPhotoClick {
    void onItemPhotoClick(View view, MediaStoreImage photo);
}
