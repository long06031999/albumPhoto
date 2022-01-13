package com.paulbaker.album.core.utils;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.View;


import androidx.annotation.NonNull;

import com.paulbaker.album.data.models.MediaStoreImage;
import com.paulbaker.album.data.models.Photo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
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

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static String getResolutionImage(Context context, Uri uri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(
                    context.getContentResolver().openInputStream(uri),
                    null,
                    options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        }
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        return imageWidth + "x" + imageHeight;
    }

    public static float getImageSize(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
            cursor.moveToFirst();
            float imageSize = cursor.getLong(sizeIndex);
            cursor.close();
            return imageSize; // returns size in bytes
        }
        return 0;
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }

        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }
    }

    public static int dpToPx(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

}
