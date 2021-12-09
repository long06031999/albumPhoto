package com.paulbaker.album.core.utils;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;


import androidx.annotation.NonNull;

import com.paulbaker.album.data.models.MediaStoreImage;
import com.paulbaker.album.data.models.Photo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import album.R;

public class Utils {
    public static int getDeviceHeight(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return metrics.heightPixels;
    }

    public static int getDeviceWidth(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return metrics.widthPixels;
    }

    public static List<Photo> groupPhotoByDate(Context context, List<MediaStoreImage> data) {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        List<Photo> listPhoto = new ArrayList<>();
        Map<String, List<MediaStoreImage>> gfg = data.stream().collect(Collectors.groupingBy(w -> formatter.format(w.getDateAdded())));
        for (Map.Entry<String, List<MediaStoreImage>> entry : gfg.entrySet()) {
            if (Locale.getDefault().getLanguage().equals("vi")) {
                listPhoto.add(new Photo(
                        entry.getKey().replaceFirst("-", " Th").replaceFirst("-", ", "),
                        entry.getValue(),
                        entry.getValue().stream().max(Comparator.comparingLong(MediaStoreImage::getTime)).get().getTime()));
            } else {
                listPhoto.add(new Photo(
                        entry.getKey(),
                        entry.getValue(),
                        entry.getValue().stream().max(Comparator.comparingLong(MediaStoreImage::getTime)).get().getTime()));
            }
        }

        listPhoto.sort(Collections.reverseOrder());
        if (formatter.format(calendar.getTime()).equals(formatter.format(listPhoto.get(0).getMaxTime())) && listPhoto.size() > 0) {
            listPhoto.get(0).setTitle(context.getString(R.string.today));
        }
        calendar.add(Calendar.DATE, -1);
        if (formatter.format(calendar.getTime()).equals(formatter.format(listPhoto.get(0).getMaxTime())) && listPhoto.size() > 0) {
            listPhoto.get(0).setTitle(context.getString(R.string.yesterday));
        }
        if (formatter.format(calendar.getTime()).equals(formatter.format(listPhoto.get(1).getMaxTime())) && listPhoto.size() > 1) {
            listPhoto.get(1).setTitle(context.getString(R.string.yesterday));
        }
        return listPhoto;
    }

    public static Uri saveImage(Context context, Bitmap bitmap, @NonNull String name) throws IOException {
        OutputStream fos;
        Uri imageUri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = context.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name + ".jpg");
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
        } else {
            String imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
            File image = new File(imagesDir, name + ".jpg");
            fos = new FileOutputStream(image);
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        Objects.requireNonNull(fos).close();
        return imageUri;
    }

}
