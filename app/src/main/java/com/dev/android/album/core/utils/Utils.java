package com.dev.android.album.core.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

import androidx.annotation.RequiresApi;

import com.dev.android.album.data.models.MediaStoreImage;
import com.dev.android.album.data.models.Photo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<Photo> convertMediaStoreImageToListPhoto(List<MediaStoreImage> data) {
        ArrayList<Photo> listPhoto = new ArrayList<>();
        Map<String, List<MediaStoreImage>> gfg = data.stream().collect(Collectors.groupingBy(w -> w.getDateAdded().toString().substring(0,10)));
        for (Map.Entry<String, List<MediaStoreImage>> entry : gfg.entrySet()) {
            listPhoto.add(new Photo(entry.getKey(), entry.getValue()));
        }
        return listPhoto;
    }
}
