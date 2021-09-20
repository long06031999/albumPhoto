package com.dev.android.album.feature.home.adapter;


import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dev.android.album.core.constants.Constants;
import com.dev.android.album.data.models.MediaStoreImage;
import com.dev.android.album.databinding.ItemPhotoBinding;
import com.jiajunhui.xapp.medialoader.bean.PhotoItem;
import com.squareup.picasso.Picasso;
import java.util.List;

public class HorizonAdapter extends RecyclerView.Adapter<HorizonAdapter.ViewHolder> {

    private List<MediaStoreImage> listData;
    private int width;

    public HorizonAdapter(List<MediaStoreImage> listData, int width) {
        this.listData = listData;
        this.width = width;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPhotoBinding binding = ItemPhotoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri item = listData.get(position).getContentUri();
        holder.bindData(item);
    }


    @Override
    public int getItemCount() {
        return listData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ItemPhotoBinding binding;

        public ViewHolder(@NonNull ItemPhotoBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
            itemView.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }

        public void bindData(Uri item) {
            binding.photo.getLayoutParams().width = width / Constants.SPAN_COUNT;
            binding.photo.getLayoutParams().height = width / Constants.SPAN_COUNT;
            Picasso.get().load(item).into(binding.photo);
        }
    }
}
