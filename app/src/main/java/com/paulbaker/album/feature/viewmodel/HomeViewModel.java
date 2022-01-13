package com.paulbaker.album.feature.viewmodel;

import android.util.Pair;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.paulbaker.album.data.models.MediaStoreImage;
import com.paulbaker.album.data.models.Photo;

import java.util.List;

public class HomeViewModel extends ViewModel {
    public MutableLiveData<MediaStoreImage> photo = new MutableLiveData<>();
    public MutableLiveData<List<Photo>> listPhoto = new MutableLiveData<>();
    public MutableLiveData<Boolean> isHideCheckBox = new MutableLiveData<>();

    public void setPhoto(MediaStoreImage photo) {
        this.photo.setValue(photo);
    }

    public void setListPhoto(List<Photo> photo) {
        this.listPhoto.setValue(photo);
    }

    public void setIsHideCheckBox(boolean isHideCheckBox) {
        this.isHideCheckBox.setValue(isHideCheckBox);
    }

    public MutableLiveData<Pair<String, String>> detail = new MutableLiveData<>();

    public void setDetailPhoto(Pair<String, String> detailPhoto) {
        this.detail.setValue(detailPhoto);
    }

}
