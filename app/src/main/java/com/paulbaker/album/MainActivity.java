package com.paulbaker.album;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;

import com.paulbaker.album.feature.home.HomeFragment;
import com.paulbaker.album.feature.viewmodel.HomeViewModel;

import album.R;
import album.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private NavController controller;
    private HomeViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_AlbumPhoto);
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        controller = Navigation.findNavController(this, R.id.nav_host_fragment);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }


    @Override
    public boolean onSupportNavigateUp() {
        return controller.navigateUp() || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (HomeFragment.isLongClick) {
            HomeFragment.isLongClick = false;
            viewModel.setIsHideCheckBox(true);
        } else super.onBackPressed();
    }
}