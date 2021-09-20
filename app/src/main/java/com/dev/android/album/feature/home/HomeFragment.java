package com.dev.android.album.feature.home;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS;

import static com.dev.android.album.core.constants.Constants.READ_EXTERNAL_STORAGE_REQUEST;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.dev.android.album.core.constants.Constants;
import com.dev.android.album.core.utils.Utils;
import com.dev.android.album.data.models.MediaStoreImage;
import com.dev.android.album.data.models.Photo;
import com.dev.android.album.databinding.FragmentHomeBinding;

import com.dev.android.album.feature.home.adapter.HomeAdapter;
import com.dev.android.album.feature.home.loading.QueryImages;
import com.dev.android.album.feature.home.section.PhotoSection;
import com.dev.android.album.core.platform.SectionedRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener, QueryImages.CallBack {
    private FragmentHomeBinding binding;
    private SectionedRecyclerViewAdapter sectionedAdapter;
    private List<Photo> data = new ArrayList<>();
    private GridLayoutManager glm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadAllPhoto();
        setupListener();
    }

    private void loadAllPhoto() {
        if (haveStoragePermission()) {
            showImages();
        } else {
            requestPermission();
        }
    }

    private void requestPermission() {
        if (!haveStoragePermission()) {
            String[] permissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            ActivityCompat.requestPermissions(requireActivity(), permissions, READ_EXTERNAL_STORAGE_REQUEST);
        }
    }


    private boolean haveStoragePermission() {
        return ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED;
    }


    private void showImages() {
        QueryImages query = new QueryImages(getActivity().getApplication());
        query.setCallBack(this);
        query.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setUpAdapter() {
        sectionedAdapter = new SectionedRecyclerViewAdapter();
        data.forEach(item -> sectionedAdapter.
                addSection(new PhotoSection(
                        item.getTitle(),
                        item.getItems(),
                        Utils.getDeviceWidth(requireContext()))));
        glm=new GridLayoutManager(requireContext(), Constants.SPAN_COUNT);
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (sectionedAdapter.getSectionItemViewType(position) == SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER) {
                    return 4;
                }
                return 1;
            }
        });
        binding.rcvHome.setLayoutManager(glm);
        binding.rcvHome.setAdapter(sectionedAdapter);
    }

    private void setupListener() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                    showImages();
                } else {
                    boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE);
                    if (showRationale) {

                    } else {
                        goToSettings();
                    }
                }
                break;
        }
    }

    private void goToSettings() {
        Intent intent = new Intent(ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + requireContext().getPackageName()));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onSuccess(List<MediaStoreImage> data) {
        this.data.addAll(Utils.convertMediaStoreImageToListPhoto(data));
        setUpAdapter();

//        List<MediaStoreImage> list=new ArrayList<>();
//        list.addAll(data);
//        HomeAdapter adapter=new HomeAdapter(requireContext(),list,Utils.getDeviceWidth(requireContext()));
//        glm=new GridLayoutManager(requireContext(), Constants.SPAN_COUNT);
//        binding.rcvHome.setLayoutManager(glm);
//        binding.rcvHome.setAdapter(adapter);
    }

    @Override
    public void onError(String message) {

    }
}
