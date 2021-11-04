package com.paulbaker.album.feature.viewmodel;

import android.content.IntentSender;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EditViewModel extends ViewModel {
    public MutableLiveData<Boolean> isShow = new MutableLiveData<>();
    public MutableLiveData<IntentSender> permissionNeededForDelete = new MutableLiveData<>();
    public void setIsShow(Boolean isShow) {
        this.isShow.setValue(isShow);
    }

}
