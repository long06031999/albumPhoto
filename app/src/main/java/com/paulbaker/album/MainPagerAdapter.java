package com.paulbaker.album;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.navigation.fragment.NavHostFragment;

import com.paulbaker.album.feature.album.AlbumFragment;
import com.paulbaker.album.feature.controller.AlbumControllerFragment;
import com.paulbaker.album.feature.controller.MainControllerFragment;
import com.paulbaker.album.feature.home.HomeFragment;
import com.paulbaker.album.feature.secure.SecureFragment;

import album.R;

public class MainPagerAdapter extends FragmentPagerAdapter {

    public final int PAGE_COUNT = 2;

    private final String[] mTabsTitle = {"Hình ảnh", "Album"};

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int pos) {
        switch (pos) {
            case 0:
                return new MainControllerFragment();
            case 1:
                return new AlbumControllerFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabsTitle[position];
    }
}
