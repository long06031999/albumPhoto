package com.paulbaker.album.feature.album.adapter;

import android.app.ActionBar;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.paulbaker.album.core.utils.Utils;
import com.paulbaker.album.data.models.Album;
import com.paulbaker.album.data.models.Photo;

import java.util.List;

import album.databinding.ItemAlbumBinding;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private Context context;
    private List<Photo> data;
    private int width;
    private onItemAlbumClick itemClick;
    private Boolean isInViewMode = false;

    public AlbumAdapter(Context context, List<Photo> listData, int width, onItemAlbumClick itemClick) {
        this.context = context;
        this.data = listData;
        this.width = width;
        this.itemClick = itemClick;
    }

    public AlbumAdapter(Context context, List<Photo> data, int width, onItemAlbumClick itemClick, Boolean isInViewMode) {
        this.context = context;
        this.data = data;
        this.width = width;
        this.itemClick = itemClick;
        this.isInViewMode = isInViewMode;
    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAlbumBinding binding = ItemAlbumBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(data.get(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ItemAlbumBinding binding;

        public ViewHolder(@NonNull ItemAlbumBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
            binding.getRoot().setOnClickListener(this);
        }

        public void bindData(Photo album) {
            if (isInViewMode) {
                binding.groupImage.getLayoutParams().width = Utils.dpToPx(context,70);
                binding.groupImage.getLayoutParams().height = Utils.dpToPx(context,70);
            }
            Glide.with(context)
                    .load(album.getItems().get(0).getContentUri())
                    .thumbnail(0.33f)
                    .centerCrop()
                    .into(binding.groupImage);
            binding.tvTitleGroup.setText(album.getTitle());
            binding.tvCount.setText(String.valueOf(album.getItems().size()));
        }

        @Override
        public void onClick(View v) {
            itemClick.onClick(v, data.get(getAdapterPosition()));
        }
    }
}



