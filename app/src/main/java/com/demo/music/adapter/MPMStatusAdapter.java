package com.demo.music.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.demo.music.R;
import com.squareup.picasso.Picasso;

import com.demo.music.model.MPMSongModel;
import com.demo.music.utils.Util;

import java.util.ArrayList;


public class MPMStatusAdapter extends RecyclerView.Adapter<MPMStatusAdapter.ViewHolder> {
    Context context;
    int currentIndex;
    ArrayList<MPMSongModel> datas;
    byte[] img;
    public OnItemClickListener onItemClickListener;

    
    public interface OnItemClickListener {
        void onItemClick(View view, int i);

        void onItemLoaded(MPMSongModel mPMSongModel);

        void onPlayPauseClicked();
    }

    public MPMStatusAdapter(Context context, ArrayList<MPMSongModel> arrayList, int i, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.datas = arrayList;
        this.currentIndex = i;
        this.onItemClickListener = onItemClickListener;
    }

    @Override 
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_image, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        int adapterPosition = viewHolder.getAdapterPosition() % this.datas.size();
        if (adapterPosition != this.currentIndex) {
            viewHolder.playSong.setVisibility(View.VISIBLE);
        } else {
            Log.e("update", "yes " + adapterPosition);
            viewHolder.playSong.setVisibility(View.GONE);
        }
        OnItemClickListener onItemClickListener = this.onItemClickListener;
        if (onItemClickListener != null) {
            if (adapterPosition != 0) {
                onItemClickListener.onItemLoaded(this.datas.get(adapterPosition - 1));
            } else {
                onItemClickListener.onItemLoaded(this.datas.get(adapterPosition));
            }
        }
        try {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(this.datas.get(adapterPosition).getData());
            this.img = mediaMetadataRetriever.getEmbeddedPicture();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (this.img != null) {
            ImageView imageView = viewHolder.imageView;
            byte[] bArr = this.img;
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(bArr, 0, bArr.length));
            return;
        }
        Picasso.with(this.context).load(ContentUris.withAppendedId(Util.sArtworkUri, Long.parseLong(this.datas.get(adapterPosition).getAlbumid()))).placeholder(R.drawable.logo).error(R.drawable.logo).noFade().into(viewHolder.imageView);
    }

    @Override 
    public int getItemCount() {
        return this.datas.isEmpty() ? 0 : Integer.MAX_VALUE;
    }

    
    
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView playSong;

        ViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.image);
            this.playSong = (ImageView) view.findViewById(R.id.playSong);
            this.playSong.setOnClickListener(new View.OnClickListener() { 
                @Override 
                public void onClick(View view2) {
                    if (MPMStatusAdapter.this.onItemClickListener != null) {
                        MPMStatusAdapter.this.onItemClickListener.onItemClick(view2, ViewHolder.this.getAdapterPosition() % MPMStatusAdapter.this.datas.size());
                    }
                }
            });
        }
    }

    public void updateData(ArrayList<MPMSongModel> arrayList) {
        this.datas = arrayList;
        notifyDataSetChanged();
    }

    public void setCurrentIndex(int i) {
        this.currentIndex = i;
        notifyDataSetChanged();
    }
}
