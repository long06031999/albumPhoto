package com.dev.android.album.feature.home.loading;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.dev.android.album.data.models.MediaStoreImage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class QueryImages extends AsyncTask<Void, Void, List<MediaStoreImage>> {
    private final ArrayList<MediaStoreImage> images = new ArrayList<>();
    private final Application application;
    private CallBack callback;

    public QueryImages(Application application) {
        this.application = application;
    }

    public void setCallBack(CallBack callback){
        this.callback=callback;
    }

    @Override
    protected List<MediaStoreImage> doInBackground(Void... voids) {
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED
        };
        String selection = MediaStore.Images.Media.DATE_ADDED + ">= ?";
        String[] selectionArgs = new String[]{
                dateToTimestamp(22, 10, 2008).toString()
        };
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";
        Cursor cursor =application.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder);
        int idColumn=cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
        int dateModifiedColumn =cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
        int displayNameColumn =cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
        while (cursor.moveToNext()){
            Long id =cursor.getLong(idColumn);
            Date dateModified=new Date(TimeUnit.SECONDS.toMillis(cursor.getLong(dateModifiedColumn)));
            String displayName= cursor.getString(displayNameColumn);
            Uri contentUri= ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
            );
            MediaStoreImage image =new MediaStoreImage(id,displayName,dateModified,contentUri);
            images.add(image);
        }
        cursor.close();
        return images;
    }

    @Override
    protected void onPostExecute(List<MediaStoreImage> mediaStoreImages) {
        super.onPostExecute(mediaStoreImages);
        if(mediaStoreImages.size()>0)
            callback.onSuccess(mediaStoreImages);//khi query data xong
        else
            callback.onError("No photos were found in your device");
    }

    @SuppressLint("SimpleDateFormat")
    private Long dateToTimestamp(int day, int month, int year) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date date = null;
        try {
            date = simpleDateFormat.parse(day + "." + month + "." + year);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return TimeUnit.MICROSECONDS.toSeconds(date != null ? date.getTime() : 0L);
    }

    public interface CallBack {
        void onSuccess(List<MediaStoreImage> data);
        void onError(String message);
    }
}
