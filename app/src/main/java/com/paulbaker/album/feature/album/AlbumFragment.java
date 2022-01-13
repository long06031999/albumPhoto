package com.paulbaker.album.feature.album;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.os.Build.VERSION.SDK_INT;
import static android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS;
import static com.paulbaker.album.core.constants.Constants.READ_EXTERNAL_STORAGE_REQUEST;

import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.paulbaker.album.MainActivity;
import com.paulbaker.album.core.utils.Utils;
import com.paulbaker.album.data.models.MediaStoreImage;
import com.paulbaker.album.data.models.Photo;
import com.paulbaker.album.feature.album.adapter.AlbumAdapter;
import com.paulbaker.album.feature.album.adapter.onItemAlbumClick;
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

        ((MainActivity) getActivity()).showTabBarLayout(View.VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadAllAlbum();
    }

    private void loadAllAlbum() {
        if (haveStoragePermission()) {
            queryAlbum();
        } else {
            requestPermission();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("TAG", "onRequestPermissionsResult: "+requestCode);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                queryAlbum();
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

    private void goToSettings() {
        Intent intent = new Intent(ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + requireContext().getPackageName()));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    loadAllAlbum();
                } else {
                    Toast.makeText(requireContext(), "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void setupAdapter() {
        albumAdapter = new AlbumAdapter(requireContext(), data, Utils.getDeviceWidth(requireContext()), this);
        binding.rcvAlbum.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        binding.rcvAlbum.setAdapter(albumAdapter);
    }

    private void queryAlbum() {
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
        albumViewModel.setTitle(album.getTitle());
    }
}
