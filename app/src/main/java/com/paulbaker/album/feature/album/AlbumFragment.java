package com.paulbaker.album.feature.album;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.paulbaker.album.MainActivity;
import com.paulbaker.album.core.utils.Utils;
import com.paulbaker.album.data.models.Album;
import com.paulbaker.album.data.models.MediaStoreImage;
import com.paulbaker.album.data.models.Photo;
import com.paulbaker.album.feature.album.adapter.AlbumAdapter;
import com.paulbaker.album.feature.album.adapter.onItemAlbumClick;
import com.paulbaker.album.feature.album.fragment.ViewAlbumFragment;
import com.paulbaker.album.feature.album.viewmodel.AlbumViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import album.R;
import album.databinding.AlbumFragmentBinding;

public class AlbumFragment extends Fragment implements onItemAlbumClick {

    private AlbumFragmentBinding binding;
    private AlbumAdapter albumAdapter;
    private ArrayList<Photo> data = new ArrayList<>();

    private AlbumViewModel albumViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = AlbumFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        albumViewModel = new ViewModelProvider(requireActivity()).get(AlbumViewModel.class);
        setupAdapter();
        querySecureImageFolder();
        ((MainActivity) getActivity()).showTabBarLayout(View.VISIBLE);
    }

    private void setupAdapter() {
        albumAdapter = new AlbumAdapter(requireContext(), data, Utils.getDeviceWidth(requireContext()), this);
        binding.rcvAlbum.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        binding.rcvAlbum.setAdapter(albumAdapter);
    }

    private void querySecureImageFolder() {
        ArrayList<String> albumName = new ArrayList<>();
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.DATA,
        };
        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cur = requireActivity().getContentResolver().query(images,
                projection, // Which columns to return
                null,       // Which rows to return (all rows)
                null,       // Selection arguments (none)
                null        // Ordering
        );

        if (cur.moveToFirst()) {
            String bucket;
            Date dateModified;
            Uri data;
            Long imageId;
            String name;
            int bucketColumn = cur.getColumnIndex(
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            int dateModifiedColumn = cur.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);

            int imageIdColumn = cur.getColumnIndex(
                    MediaStore.Images.Media._ID);

            int displayNameColumn = cur.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);

            do {
                imageId = cur.getLong(imageIdColumn);
                bucket = cur.getString(bucketColumn);
                dateModified = new Date(TimeUnit.SECONDS.toMillis(cur.getLong(dateModifiedColumn)));
                data = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        imageId
                );

                name = cur.getString(displayNameColumn);
                MediaStoreImage photo = new MediaStoreImage(
                        imageId, name, dateModified, data);
                if (albumName.contains(bucket)) {
                    for (Photo album : this.data) {
                        if (album.getTitle().equals(bucket)) {
                            album.getItems().add(photo);
                            break;
                        }
                    }
                } else {
                    Photo album = new Photo(
                            "", new ArrayList<>(),
                            0L
                    );
                    album.setTitle(bucket);
                    album.getItems().add(photo);
                    this.data.add(album);
                    albumName.add(bucket);
                }
            } while (cur.moveToNext());
        }
        albumAdapter.notifyDataSetChanged();
        albumViewModel.setListAlbumPhoto(data);
    }

    @Override
    public void onClick(View view, Photo album) {
        albumViewModel.setListPhoto(album.getItems());
        Navigation.findNavController(view).navigate(R.id.navigateToViewAlbum);
    }
}
