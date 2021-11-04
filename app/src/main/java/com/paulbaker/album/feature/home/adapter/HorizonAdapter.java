package com.paulbaker.album.feature.home.adapter;


import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.paulbaker.album.data.models.MediaStoreImage;

import java.util.List;

import album.databinding.ItemPhotoBoundBinding;


public class HorizonAdapter extends RecyclerView.Adapter<HorizonAdapter.ViewHolder> {
    private List<MediaStoreImage> listData;
    private int width;
    private ListenerItemClick listener;
    int selected = -1;
    int preSelected;

    public HorizonAdapter(List<MediaStoreImage> listData, int width) {
        this.listData = listData;
        this.width = width;
    }

    public void setListener(ListenerItemClick listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPhotoBoundBinding binding = ItemPhotoBoundBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri item = listData.get(position).getContentUri();
        holder.bindData(item);
        if (position == selected) {
            holder.binding.selectPhoto.setVisibility(View.VISIBLE);
        } else {
            holder.binding.selectPhoto.setVisibility(View.INVISIBLE);
        }
    }

    public void setSelected(int i) {
        preSelected = selected;
        selected = i;
        notifyItemChanged(selected);
        notifyItemChanged(preSelected);
    }


    @Override
    public int getItemCount() {
        return listData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ItemPhotoBoundBinding binding;

        public ViewHolder(@NonNull ItemPhotoBoundBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
            binding.photo.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.itemClick(getAdapterPosition());
        }

        public void bindData(Uri item) {
            Glide.with(binding.photo).load(item).centerCrop().into(binding.photo);
        }
    }

    public interface ListenerItemClick {
        void itemClick(int position);
    }
}
