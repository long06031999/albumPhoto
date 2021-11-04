package com.dev.android.album.feature.view.pager;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.dev.android.album.databinding.FragmentPagerBinding;
import com.dev.android.album.feature.viewmodel.EditViewModel;

public class ViewPagerFragment extends Fragment {
    private static final String BUNDLE_URI = "uri";
    private FragmentPagerBinding binding;
    private Uri uri;
    private boolean isShow = true;

    private EditViewModel editViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPagerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editViewModel = new ViewModelProvider(requireActivity()).get(EditViewModel.class);
        loadImage(savedInstanceState);
        binding.imageView.setOnClickListener(v -> {
            editViewModel.setIsShow(!isShow);
            isShow = !isShow;
        });
    }

    private void loadImage(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (uri == null && savedInstanceState.containsKey(BUNDLE_URI)) {
                uri = savedInstanceState.getParcelable(BUNDLE_URI);
            }
        }
        if (uri != null) {
            binding.imageView.setImage(ImageSource.uri(uri));
        }
    }

    public void setImageUri(Uri uri) {
        this.uri = uri;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        View rootView = getView();
        if (rootView != null) {
            outState.putParcelable(BUNDLE_URI, uri);
        }
    }
}
