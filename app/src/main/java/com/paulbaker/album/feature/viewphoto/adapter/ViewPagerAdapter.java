package com.paulbaker.album.feature.viewphoto.adapter;

import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;


import com.paulbaker.album.data.models.MediaStoreImage;
import com.paulbaker.album.feature.viewphoto.pager.ViewPagerFragment;

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

    @Override
    public int getItemPosition(Object object){
        return PagerAdapter.POSITION_NONE;
    }

}
