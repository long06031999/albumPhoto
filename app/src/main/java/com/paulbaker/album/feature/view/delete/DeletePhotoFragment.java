package com.paulbaker.album.feature.view.delete;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.paulbaker.album.core.constants.Constants;
import com.paulbaker.album.core.platform.BottomSheetDialogCorner;
import com.paulbaker.album.data.models.MediaStoreImage;
import com.paulbaker.album.feature.viewmodel.EditViewModel;

import album.R;
import album.databinding.BottomSheetDeletePhotoBinding;

public class DeletePhotoFragment extends BottomSheetDialogCorner implements View.OnClickListener, DeletePhotoAsyncTask.CallBackForDeletePhoto {
    private BottomSheetDeletePhotoBinding binding;
    private OnActionDialog onActionDialog;
    private MediaStoreImage photo;
    private EditViewModel editViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetDeletePhotoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editViewModel = new ViewModelProvider(requireActivity()).get(EditViewModel.class);
        setupListener();
        getParam();
        observerPermissionForDelete();
    }

    private void observerPermissionForDelete() {
        editViewModel.permissionNeededForDelete.observe(this, intentSender -> {
            if (intentSender != null) {
                try {
                    startIntentSenderForResult(intentSender,
                            Constants.DELETE_PERMISSION_REQUEST,
                            null,
                            0,
                            0,
                            0,
                            null);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getParam() {
        if (getArguments() != null)
            photo = (MediaStoreImage) getArguments().getSerializable(Constants.KEY_DELETE_PHOTO);
    }

    public void setupCallBack(OnActionDialog onActionDialog) {
        this.onActionDialog = onActionDialog;
    }

    private void setupListener() {
        binding.btnDelete.setOnClickListener(this);
        binding.btnCancel.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnDelete:
                deletePhoto();
                break;
            case R.id.btnCancel:
                dismiss();
                break;
        }
    }

    private void deletePhoto() {
        if (photo != null) {
            DeletePhotoAsyncTask deletePhoto = new DeletePhotoAsyncTask(requireActivity().getApplication(), editViewModel);
            deletePhoto.execute(photo);
            deletePhoto.setCallBackForDeletePhoto(this);
        }
    }

    @Override
    public void onSuccess() {
        onActionDialog.onDeleteSuccess();
    }

    @Override
    public void onError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        onActionDialog.onCancel();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constants.DELETE_PERMISSION_REQUEST && resultCode == RESULT_OK) {
            if (DeletePhotoAsyncTask.pendingDeleteImage != null) {
                DeletePhotoAsyncTask.pendingDeleteImage = null;
                deletePhoto();
            }
        }
    }

    public interface OnActionDialog {
        void onDeleteSuccess();

        void onCancel();
    }
}
