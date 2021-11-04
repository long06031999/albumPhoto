package com.dev.android.album.feature.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.dev.android.album.R;
import com.dev.android.album.core.constants.Constants;
import com.dev.android.album.core.utils.Utils;
import com.dev.android.album.data.models.MediaStoreImage;
import com.dev.android.album.data.models.Photo;
import com.dev.android.album.databinding.FragmentViewPhotoBinding;
import com.dev.android.album.feature.view.adapter.ViewPagerAdapter;
import com.dev.android.album.feature.view.delete.DeletePhotoFragment;
import com.dev.android.album.feature.home.adapter.HorizonAdapter;
import com.dev.android.album.feature.viewmodel.EditViewModel;
import com.dev.android.album.feature.viewmodel.HomeViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ViewPhotoFragment extends Fragment implements HorizonAdapter.ListenerItemClick,
        ViewPager.OnPageChangeListener, View.OnClickListener, DeletePhotoFragment.OnActionDialog {
    private FragmentViewPhotoBinding binding;
    private HomeViewModel viewModel;
    private EditViewModel editViewModel;
    private final List<Photo> dataRaw = new ArrayList<>();
    private final List<MediaStoreImage> listData = new ArrayList<>();
    private MediaStoreImage photo = null;
    private ViewPagerAdapter viewPagerAdapter;
    private HorizonAdapter adapter;

    private DeletePhotoFragment deletePhotoFragment = new DeletePhotoFragment();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentViewPhotoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViewModel();
        setupClickListener();
        loadPhotoSelected();
        loadAllPhoto();
        handleSlideViewPager();
        handleClickViewPager();
    }

    private void setupClickListener() {
        deletePhotoFragment.setupCallBack(this);
        binding.btnBack.setOnClickListener(this);
        binding.btnDelete.setOnClickListener(this);
        binding.btnEdit.setOnClickListener(this);
        binding.btnLove.setOnClickListener(this);
        binding.btnShare.setOnClickListener(this);
        binding.btnOptions.setOnClickListener(this);
        binding.btnSearch.setOnClickListener(this);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        editViewModel = new ViewModelProvider(requireActivity()).get(EditViewModel.class);
    }

    private void handleClickViewPager() {
        editViewModel.isShow.observe(getViewLifecycleOwner(), isShow -> {
            if (isShow) {
                binding.toolBarEdit.setVisibility(View.VISIBLE);
                binding.bottomBar.setVisibility(View.VISIBLE);
            } else {
                binding.toolBarEdit.setVisibility(View.GONE);
                binding.bottomBar.setVisibility(View.GONE);
            }
        });
    }

    private void handleSlideViewPager() {
        binding.viewPager.addOnPageChangeListener(this);
    }

    private void loadAllPhoto() {
        viewModel.listPhoto.observe(getViewLifecycleOwner(), item -> {
            addData(item);
            setupViewPager();
            setUpAdapter();
        });
    }

    private void addData(List<Photo> data) {
        dataRaw.addAll(data);
        dataRaw.forEach(item ->
                listData.addAll(item.getItems())
        );
    }

    private void setUpAdapter() {
        adapter = new HorizonAdapter(listData, Utils.getDeviceWidth(requireContext()));
        adapter.setListener(this);
        binding.rcvListPhoto.setLayoutManager(
                new LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false));
        binding.rcvListPhoto.setAdapter(adapter);
        scrollToDefaultPhotoSelected();
    }

    private void setupViewPager() {
        viewPagerAdapter = new ViewPagerAdapter(
                requireActivity().getSupportFragmentManager(), listData);
        binding.viewPager.setAdapter(viewPagerAdapter);
    }

    private void scrollToDefaultPhotoSelected() {
        if (photo != null) {
            binding.rcvListPhoto.scrollToPosition(listData.indexOf(photo));
            adapter.setSelected(listData.indexOf(photo));
            binding.viewPager.setCurrentItem(listData.indexOf(photo));
        }
    }

    private void loadPhotoSelected() {
        viewModel.photo.observe(getViewLifecycleOwner(), mediaStoreImage -> {
            photo = mediaStoreImage;
            if (adapter != null) {
                binding.rcvListPhoto.scrollToPosition(listData.indexOf(mediaStoreImage));
                adapter.setSelected(listData.indexOf(mediaStoreImage));
                binding.viewPager.setCurrentItem(listData.indexOf(photo));
            }
        });
    }


    //adapter Horizon
    @Override
    public void itemClick(int position) {
        adapter.setSelected(position);
        viewModel.setPhoto(listData.get(position));
    }


    //viewPager
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        adapter.setSelected(position);
        binding.rcvListPhoto.scrollToPosition(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        editViewModel.setIsShow(true);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                Navigation.findNavController(binding.getRoot()).popBackStack();
                break;
            case R.id.btnDelete:
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.KEY_DELETE_PHOTO, photo);
                deletePhotoFragment.setArguments(bundle);
                deletePhotoFragment.show(getChildFragmentManager(), deletePhotoFragment.getTag());
                break;
            case R.id.btnEdit:
                Navigation.findNavController(binding.getRoot()).navigate(R.id.navigateToEditPhoto);
                break;
            case R.id.btnShare:
                shareImage();
                break;
            default:
                break;
        }
    }

    private void shareImage() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setDataAndType(buildFileProviderUri(requireContext(), photo.getContentUri()), "image/*");
        intent.putExtra(Intent.EXTRA_STREAM, buildFileProviderUri(requireContext(), photo.getContentUri()));
        startActivity(intent);
    }

    public Uri buildFileProviderUri(Context context, @NonNull Uri uri) {
        return FileProvider.getUriForFile(context,
                context.getPackageName() + ".provider",
                new File(uri.getPath()));
    }


    //dialog Delete
    @Override
    public void onDeleteSuccess() {
        Log.d("TAG", "onDeleteSuccess: ");
    }

    @Override
    public void onCancel() {
        Log.d("TAG", "onCancel: ");
    }
}
