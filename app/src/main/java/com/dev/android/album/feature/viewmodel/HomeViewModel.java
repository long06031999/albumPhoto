package com.dev.android.album.feature.viewmodel;

import android.database.ContentObserver;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dev.android.album.data.models.MediaStoreImage;
import com.dev.android.album.data.models.Photo;

import java.util.List;

public class HomeViewModel extends ViewModel {
    public MutableLiveData<MediaStoreImage> photo =new MutableLiveData<>();
    public MutableLiveData<List<Photo>> listPhoto =new MutableLiveData<>();
    public MutableLiveData<Boolean> isHideCheckBox =new MutableLiveData<>();
    public void setPhoto(MediaStoreImage photo){
        this.photo.setValue(photo);
    }
    public void setListPhoto(List<Photo> photo){
        this.listPhoto.setValue(photo);
    }
    public void setIsHideCheckBox(boolean isHideCheckBox){
        this.isHideCheckBox.setValue(isHideCheckBox);
    }
}
