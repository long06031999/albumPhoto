package com.dev.android.album.feature.home.section;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dev.android.album.R;
import com.dev.android.album.core.constants.Constants;
import com.dev.android.album.data.models.MediaStoreImage;;
import com.dev.android.album.core.platform.Section;
import com.dev.android.album.core.platform.SectionParameters;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotoSection extends Section {
    private String title;
    private List<MediaStoreImage> list;
    private int width;
    private ClickListener clickListener;

    public PhotoSection(@NonNull final String title, @NonNull List<MediaStoreImage> list, int width) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.item_photo)
                .headerResourceId(R.layout.item_header)
                .build());
        this.title = title;
        this.list = list;
        this.width = width;
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public int getContentItemsTotal() {
        return list.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }


    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.bindData(list.get(position).getContentUri());
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        final HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
        headerViewHolder.bindData(title);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView photo;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.photo);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemRootViewClicked(PhotoSection.this, getAdapterPosition());
        }

        public void bindData(Uri item) {
            photo.getLayoutParams().width = width / Constants.SPAN_COUNT;
            photo.getLayoutParams().height = width / Constants.SPAN_COUNT;
            Glide.with(photo)
                    .load(item)
                    .thumbnail(0.33f)
                    .centerCrop()
                    .into(photo);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView tvHeader;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.title);
        }

        public void bindData(String title) {
            tvHeader.setText(title);
        }
    }

    interface ClickListener {
        void onItemRootViewClicked(@NonNull final PhotoSection section, final int itemAdapterPosition);
    }
}
