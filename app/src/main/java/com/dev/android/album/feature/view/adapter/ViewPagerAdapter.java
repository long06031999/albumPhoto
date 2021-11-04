package com.dev.android.album.feature.view.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.dev.android.album.data.models.MediaStoreImage;
import com.dev.android.album.feature.view.pager.ViewPagerFragment;

import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter  {
    private List<MediaStoreImage> listData;

    public ViewPagerAdapter(@NonNull FragmentManager fm, List<MediaStoreImage> listData) {
        super(fm);
        this.listData = listData;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        ViewPagerFragment fragment = new ViewPagerFragment();
        fragment.setImageUri(listData.get(position).getContentUri());
        return fragment;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

}
