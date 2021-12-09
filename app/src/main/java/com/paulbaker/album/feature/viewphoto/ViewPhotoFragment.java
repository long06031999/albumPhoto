package com.paulbaker.album.feature.viewphoto;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;


import com.paulbaker.album.MainActivity;
import com.paulbaker.album.core.constants.Constants;
import com.paulbaker.album.core.utils.Utils;
import com.paulbaker.album.data.models.MediaStoreImage;
import com.paulbaker.album.data.models.Photo;
import com.paulbaker.album.feature.home.adapter.HorizonAdapter;
import com.paulbaker.album.feature.viewphoto.adapter.ViewPagerAdapter;
import com.paulbaker.album.feature.viewphoto.delete.DeletePhotoFragment;
import com.paulbaker.album.feature.viewmodel.EditViewModel;
import com.paulbaker.album.feature.viewmodel.HomeViewModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import album.R;
import album.databinding.FragmentViewPhotoBinding;

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

    @SuppressLint("NotifyDataSetChanged")
    private void loadAllPhoto() {
        viewModel.listPhoto.observe(getViewLifecycleOwner(), item -> {
            listData.clear();
            dataRaw.clear();
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
                getChildFragmentManager(), listData);
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
                deletePhotoFragment.show(getChildFragmentManager(), deletePhotoFragment.getClass().getName());
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

    private void shareImage()  {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), photo.getContentUri());
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/jpeg");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(requireContext().getContentResolver(), bitmap, "Title", null);
            Uri imageUri =  Uri.parse(path);
            share.putExtra(Intent.EXTRA_STREAM, imageUri);
            startActivity(Intent.createChooser(share, "Select"));
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(),"cannot share image",Toast.LENGTH_LONG).show();
        }
    }

    //dialog Delete
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onDeleteSuccess() {
        deletePhotoFragment.dismiss();
        requireActivity().runOnUiThread(() -> {
            int position  = listData.indexOf(photo);
            listData.remove(photo);
            adapter.notifyDataSetChanged();
            viewPagerAdapter.notifyDataSetChanged();
            viewModel.setPhoto(listData.get(position));
        });
    }

    @Override
    public void onCancel() {
        deletePhotoFragment.dismiss();
    }
}
