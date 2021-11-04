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
import com.dev.android.album.databinding.ItemRootBinding;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.RootViewHolder> {

    private Context context;
    private List<Photo> data;
    private int width;

    public HomeAdapter(Context context, List<Photo> listData, int width) {
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
        ItemRootBinding binding = ItemRootBinding.inflate(LayoutInflater.from(context), parent, false);
        return new RootViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RootViewHolder holder, int position) {
        holder.bindDataTitle(data.get(position).getTitle());
        holder.bindDataHorizon(data.get(position).getItems());
    }

    class RootViewHolder extends RecyclerView.ViewHolder {
        private ItemRootBinding binding;

        public RootViewHolder(@NonNull ItemRootBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void bindDataTitle(String item) {
            binding.title.setText(item);
        }
        public void bindDataHorizon(List<MediaStoreImage> data){
            HorizonAdapter adapter =new HorizonAdapter(data,width);
            binding.rcvListPhoto.setAdapter(adapter);
            binding.rcvListPhoto.setLayoutManager(new GridLayoutManager(context, Constants.SPAN_COUNT));
        }
    }

}
