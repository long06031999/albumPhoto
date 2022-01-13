package com.paulbaker.album.feature.album.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.paulbaker.album.core.utils.Utils;
import com.paulbaker.album.data.models.Album;
import com.paulbaker.album.data.models.MediaStoreImage;

import java.util.List;

import album.databinding.ItemHorizonBinding;

public class ViewAlbumAdapter extends RecyclerView.Adapter<ViewAlbumAdapter.ViewHolder> {

    private Context context;
    private List<MediaStoreImage> data;
    private int width;
    private OnItemPhotoClick onItemPhotoClick;


    public ViewAlbumAdapter(Context context, List<MediaStoreImage> listData, int width, OnItemPhotoClick onItemPhotoClick) {
        this.context = context;
        this.data = listData;
        this.width = width;
        this.onItemPhotoClick = onItemPhotoClick;
    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHorizonBinding binding = ItemHorizonBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(data.get(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ItemHorizonBinding binding;

        public ViewHolder(@NonNull ItemHorizonBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
            binding.getRoot().setOnClickListener(this);
        }

        public void bindData(MediaStoreImage album) {
            binding.image.getLayoutParams().width = Utils.getDeviceWidth(context) / 3;
            binding.image.getLayoutParams().height = Utils.getDeviceWidth(context) / 5;
            Glide.with(context)
                    .load(album.getContentUri())
                    .thumbnail(0.33f)
                    .centerCrop()
                    .into(binding.image);
        }

        @Override
        public void onClick(View v) {
            onItemPhotoClick.onItemPhotoClick(v,data.get(getAdapterPosition()));
        }
    }
}