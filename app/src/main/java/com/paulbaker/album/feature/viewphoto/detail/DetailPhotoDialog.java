package com.paulbaker.album.feature.viewphoto.detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.paulbaker.album.core.platform.BottomSheetDialogCorner;
import com.paulbaker.album.feature.viewmodel.HomeViewModel;

import album.databinding.BottomSheetDetailBinding;

public class DetailPhotoDialog extends BottomSheetDialogCorner {

    private BottomSheetDetailBinding binding;
    private HomeViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        viewModel.detail.observe(getViewLifecycleOwner(), detail -> {
            binding.tvDate.setText(detail.first);
            binding.tvDetail.setText(detail.second);
        });
    }
}
