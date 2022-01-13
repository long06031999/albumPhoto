package com.paulbaker.album.feature.album.viewmodel;

import android.util.Pair;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.paulbaker.album.data.models.Album;
import com.paulbaker.album.data.models.MediaStoreImage;
import com.paulbaker.album.data.models.Photo;

import java.util.ArrayList;
import java.util.List;

public class AlbumViewModel extends ViewModel {

    public MutableLiveData<String> title = new MutableLiveData<>();

    public MutableLiveData<List<MediaStoreImage>> listPhoto = new MutableLiveData<>();

    public MutableLiveData<ArrayList<Photo>> listAlbum = new MutableLiveData<>();

    public void setTitle(String data) {
        this.title.setValue(data);
    }

    public void setListPhoto(List<MediaStoreImage> data) {
        this.listPhoto.setValue(data);
    }

    public void setListAlbumPhoto(ArrayList<Photo> data) {
        this.listAlbum.setValue(data);
    }
}