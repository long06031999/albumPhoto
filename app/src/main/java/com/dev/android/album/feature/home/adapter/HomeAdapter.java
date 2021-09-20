package com.dev.android.album.feature.home.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dev.android.album.core.constants.Constants;
import com.dev.android.album.data.models.MediaStoreImage;
import com.dev.android.album.data.models.Photo;
import com.dev.android.album.databinding.ItemPhotoBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.RootViewHolder> {

    private Context context;
    private List<MediaStoreImage> data;
    private int width;


    public HomeAdapter(Context context, List<MediaStoreImage> listData, int width) {
        this.context = context;
        this.data = listData;
        this.width = width;
    }


    @Override
    public int getItemCount() {
        return data.size();
    }



    @NonNull
    @Override
    public RootViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPhotoBinding binding = ItemPhotoBinding.inflate(LayoutInflater.from(context), parent, false);
        return new RootViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RootViewHolder holder, int position) {
        holder.bindDataTitle(holder,data.get(position).getContentUri());
    }

    class RootViewHolder extends RecyclerView.ViewHolder {
        private ItemPhotoBinding binding;

        public RootViewHolder(@NonNull ItemPhotoBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void bindDataTitle(RootViewHolder holder,Uri item) {
            binding.photo.getLayoutParams().width = width / Constants.SPAN_COUNT;
            binding.photo.getLayoutParams().height = width / Constants.SPAN_COUNT;
            Glide.with(context)
                    .load(item)
                    .thumbnail(0.25f)
                    .centerCrop()
                    .into(binding.photo);
        }
    }

}
