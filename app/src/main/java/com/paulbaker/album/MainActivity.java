package com.paulbaker.album;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.paulbaker.album.feature.controller.MainControllerFragment;
import com.paulbaker.album.feature.home.HomeFragment;
import com.paulbaker.album.feature.viewmodel.HomeViewModel;

import album.R;
import album.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    private HomeViewModel viewModel;

    MainPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_AlbumPhoto);
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        binding.viewPager.setPagingEnabled(false);
        binding.viewPager.setAdapter(pagerAdapter);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
    }


    @Override
    public void onBackPressed() {
        if (HomeFragment.isLongClick) {
            HomeFragment.isLongClick = false;
            viewModel.setIsHideCheckBox(true);
        } else  super.onBackPressed();
    }


    public void showTabBarLayout(int isShow){
        binding.containerTabLayout.setVisibility(isShow);
    }
}