package com.paulbaker.album.feature.viewphoto.delete;

import android.app.Application;
import android.app.RecoverableSecurityException;
import android.os.AsyncTask;
import android.os.Build;

import com.paulbaker.album.data.models.MediaStoreImage;
import com.paulbaker.album.feature.viewmodel.EditViewModel;


public class DeletePhotoAsyncTask extends AsyncTask<MediaStoreImage, Void, Void> {
    private Application application;
    private EditViewModel viewModel;
    private RecoverableSecurityException recoverableSecurityException;
    private CallBackForDeletePhoto callBackForDeletePhoto;
    public static MediaStoreImage pendingDeleteImage = null;

    public DeletePhotoAsyncTask(Application application, EditViewModel viewModel) {
        this.application = application;
        this.viewModel = viewModel;
    }

    public void setCallBackForDeletePhoto(CallBackForDeletePhoto callBackForDeletePhoto) {
        this.callBackForDeletePhoto = callBackForDeletePhoto;
    }

    @Override
    protected Void doInBackground(MediaStoreImage... mediaStoreImages) {
        try {
            application.getContentResolver()
                    .delete(mediaStoreImages[0].getContentUri(),
                            null,null);
            callBackForDeletePhoto.onSuccess();
        } catch (SecurityException securityException) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (securityException instanceof RecoverableSecurityException) {
                    recoverableSecurityException = (RecoverableSecurityException) securityException;
                    viewModel.permissionNeededForDelete.postValue(
                            recoverableSecurityException.getUserAction().getActionIntent().getIntentSender());
                    pendingDeleteImage=mediaStoreImages[0];
                    callBackForDeletePhoto.onSuccess();
                } else {
                    callBackForDeletePhoto.onError(securityException.getMessage());
                    throw securityException;
                }
            } else {
                callBackForDeletePhoto.onError(securityException.getMessage());
                throw securityException;
            }
        }
        return null;
    }

    public interface CallBackForDeletePhoto {
        void onSuccess();

        void onError(String message);
    }
}
