package com.paulbaker.album.feature.home;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.os.Build.VERSION.SDK_INT;
import static android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS;


import static com.paulbaker.album.core.constants.Constants.READ_EXTERNAL_STORAGE_REQUEST;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.appbar.AppBarLayout;
import com.paulbaker.album.MainActivity;
import com.paulbaker.album.core.constants.Constants;
import com.paulbaker.album.core.platform.SectionedRecyclerViewAdapter;
import com.paulbaker.album.core.utils.Utils;
import com.paulbaker.album.data.models.MediaStoreImage;
import com.paulbaker.album.data.models.Photo;
import com.paulbaker.album.feature.home.loading.QueryImages;
import com.paulbaker.album.feature.home.section.PhotoSection;
import com.paulbaker.album.feature.viewmodel.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

import album.R;
import album.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment implements
        View.OnClickListener,
        PhotoSection.ClickListener {

    public static boolean isLongClick = false;

    private final List<Photo> data = new ArrayList<>();

    private FragmentHomeBinding binding;
    private SectionedRecyclerViewAdapter sectionedAdapter;
    private HomeViewModel viewModel;
    private PhotoSection section = null;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        setupListener();
        setUpAdapter();
        onHideCheckBoxMode();
        handleTitleToolBar();
        ((MainActivity) getActivity()).showTabBarLayout(View.VISIBLE);
    }


    private void loadAllPhoto() {
        if (haveStoragePermission()) {
            showImages();
        } else {
            requestPermission();
        }
    }

    private void requestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s",requireContext().getApplicationContext().getPackageName())));
                startActivityForResult(intent, READ_EXTERNAL_STORAGE_REQUEST);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, READ_EXTERNAL_STORAGE_REQUEST);
            }
        } else {
            //below android 11
            String[] permissions = new String[]{
                    READ_EXTERNAL_STORAGE,
                    WRITE_EXTERNAL_STORAGE
            };
            ActivityCompat.requestPermissions(requireActivity(), permissions, READ_EXTERNAL_STORAGE_REQUEST);
        }
    }


    private boolean haveStoragePermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int result = ContextCompat.checkSelfPermission(requireContext(), READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(requireContext(), WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
        }
    }


    private void showImages() {
        QueryImages query = new QueryImages(requireActivity().getApplication());
        query.setCallBack(new QueryImages.CallBack() {
            @Override
            public void onSuccess(List<MediaStoreImage> allPhoto) {
                data.clear();
                data.addAll(Utils.groupPhotoByDate(requireContext(), allPhoto));
                setUpAdapter();
                binding.rcvHome.setVisibility(View.VISIBLE);
                binding.noData.setVisibility(View.GONE);
            }

            @Override
            public void onError(String message) {
                binding.noData.setVisibility(View.VISIBLE);
                binding.rcvHome.setVisibility(View.GONE);
            }
        });
        query.execute();
    }

    private void setUpAdapter() {
        sectionedAdapter = new SectionedRecyclerViewAdapter();
        data.forEach(item -> {
            sectionedAdapter.addSection(
                    new PhotoSection(
                            item.getTitle(),
                            item.getItems(),
                            Utils.getDeviceWidth(requireContext()), this));
        });
        GridLayoutManager glm = new GridLayoutManager(requireContext(), Constants.SPAN_COUNT);
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (sectionedAdapter.getSectionItemViewType(position) == SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER) {
                    return Constants.SPAN_COUNT;
                }
                return 1;
            }
        });
        binding.rcvHome.setLayoutManager(glm);
        binding.rcvHome.setAdapter(sectionedAdapter);
    }

    private void setupListener() {

    }


    private void handleTitleToolBar() {
        binding.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset < 250) {
                    if (binding.titleToolBar.getVisibility() == View.GONE)
                        binding.titleToolBar.setVisibility(View.VISIBLE);
                    isShow = true;
                } else if (isShow) {
                    binding.titleToolBar.setVisibility(View.GONE);
                    isShow = false;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        loadAllPhoto();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("TAG", "onRequestPermissionsResult: "+requestCode);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                showImages();
            } else {
                boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                        READ_EXTERNAL_STORAGE);
                if (showRationale) {

                } else {
                    goToSettings();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    loadAllPhoto();
                } else {
                    Toast.makeText(requireContext(), "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void goToSettings() {
        Intent intent = new Intent(ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + requireContext().getPackageName()));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onItemRootViewClicked(View view, @NonNull PhotoSection section, int itemAdapterPosition) {
        if (!isLongClick) {
            int pos = sectionedAdapter.getAdapterForSection(section).getPositionInSection(itemAdapterPosition);
            Navigation.findNavController(view).navigate(R.id.navigateToViewPhoto);
            viewModel.setPhoto(section.getList().get(pos));
            viewModel.setListPhoto(data);
        }
    }

    @Override
    public void onItemLongClick(View view, @NonNull PhotoSection section, int itemAdapterPosition) {
        this.section = section;
        sectionedAdapter.getAdapterForSection(section).notifyAllItemsChanged(new PhotoSection.ItemPhotoUpdate());
        isLongClick = true;
    }

    private void onHideCheckBoxMode() {
        viewModel.isHideCheckBox.observe(getViewLifecycleOwner(), isHide -> {
            if (isHide)
                sectionedAdapter.getAdapterForSection(section).notifyAllItemsChanged();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
