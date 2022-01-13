package com.paulbaker.album.feature.album.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.paulbaker.album.MainActivity;
import com.paulbaker.album.core.utils.Utils;
import com.paulbaker.album.data.models.Album;
import com.paulbaker.album.data.models.MediaStoreImage;
import com.paulbaker.album.data.models.Photo;
import com.paulbaker.album.feature.album.adapter.AlbumAdapter;
import com.paulbaker.album.feature.album.adapter.OnItemPhotoClick;
import com.paulbaker.album.feature.album.adapter.ViewAlbumAdapter;
import com.paulbaker.album.feature.album.adapter.onItemAlbumClick;
import com.paulbaker.album.feature.album.viewmodel.AlbumViewModel;
import com.paulbaker.album.feature.viewmodel.HomeViewModel;
import com.paulbaker.album.feature.viewphoto.ViewPhotoFragment;

import java.util.ArrayList;

import album.R;
import album.databinding.ViewAlbumFragmentBinding;

public class ViewAlbumFragment extends Fragment implements onItemAlbumClick, OnItemPhotoClick, View.OnClickListener {

    private ViewAlbumFragmentBinding binding;
    private AlbumAdapter albumAdapter;
    private ArrayList<Photo> dataGroup = new ArrayList<>();

    private ViewAlbumAdapter viewAlbumAdapter;
    private ArrayList<MediaStoreImage> data = new ArrayList<>();

    private HomeViewModel homeViewModel;
    private AlbumViewModel albumViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ViewAlbumFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        albumViewModel = new ViewModelProvider(requireActivity()).get(AlbumViewModel.class);
        setupAdapter();
        setupObserver();
        setupListener();
        ((MainActivity) getActivity()).showTabBarLayout(View.GONE);
        handleOnBackPress();
    }

    private void handleOnBackPress() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(binding.getRoot()).popBackStack();
            }
        });
    }

    private void setupListener() {
        binding.btnBack.setOnClickListener(this);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setupObserver() {
        albumViewModel.title.observe(getViewLifecycleOwner(), title -> {
            binding.titleHome.setText(title);
        });

        albumViewModel.listPhoto.observe(getViewLifecycleOwner(), item -> {
            data.clear();
            data.addAll(item);
            viewAlbumAdapter.notifyDataSetChanged();
        });

        albumViewModel.listAlbum.observe(getViewLifecycleOwner(), item -> {
            dataGroup.addAll(item);
            albumAdapter.notifyDataSetChanged();
        });

    }

    private void setupAdapter() {
        albumAdapter = new AlbumAdapter(requireContext(), dataGroup, Utils.getDeviceWidth(requireContext()), this, true);
        binding.rcvAlbumGroup.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.rcvAlbumGroup.setAdapter(albumAdapter);

        viewAlbumAdapter = new ViewAlbumAdapter(requireContext(), data, Utils.getDeviceWidth(requireContext()), this);
        binding.rcvAlbumDetail.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        binding.rcvAlbumDetail.setAdapter(viewAlbumAdapter);
    }


    @Override
    public void onClick(View view, Photo album) {
        albumViewModel.setListPhoto(album.getItems());
        binding.titleHome.setText(album.getTitle());
    }

    @Override
    public void onItemPhotoClick(View view, MediaStoreImage photo) {
        homeViewModel.setPhoto(photo);
        homeViewModel.setListPhoto(dataGroup);
        Navigation.findNavController(view).navigate(R.id.navigateToViewPhoto);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                Navigation.findNavController(v).popBackStack();
                break;
            default:
                break;
        }
    }
}
