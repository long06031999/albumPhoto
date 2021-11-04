package com.paulbaker.album.core.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;


import com.paulbaker.album.data.models.MediaStoreImage;
import com.paulbaker.album.data.models.Photo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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


}