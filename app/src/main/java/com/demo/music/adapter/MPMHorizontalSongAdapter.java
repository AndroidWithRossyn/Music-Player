package com.demo.music.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.demo.music.R;

import com.demo.music.interfaces.MPMOnSingleSongClicked;
import com.demo.music.model.MPMSongModel;
import com.demo.music.utils.Util;

import java.util.ArrayList;


public class MPMHorizontalSongAdapter extends RecyclerView.Adapter<MPMHorizontalSongAdapter.MyViewHolder> {
    Context context;
    ArrayList<MPMSongModel> datas;
    MPMOnSingleSongClicked listener;

    public MPMHorizontalSongAdapter(Context context, ArrayList<MPMSongModel> arrayList, MPMOnSingleSongClicked mPMOnSingleSongClicked) {
        this.context = context;
        this.datas = arrayList;
        this.listener = mPMOnSingleSongClicked;
    }

    @Override 
    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(this.context).inflate(R.layout.single_horizontal_song_item, viewGroup, false));
    }

    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        byte[] bArr;
        final int adapterPosition = myViewHolder.getAdapterPosition();
        myViewHolder.artist.setText(this.datas.get(adapterPosition).getArtist());
        myViewHolder.title.setText(this.datas.get(adapterPosition).getTitle());
        String data = this.datas.get(adapterPosition).getData();
        try {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(data);
            bArr = mediaMetadataRetriever.getEmbeddedPicture();
        } catch (Exception e) {
            e.printStackTrace();
            bArr = null;
        }
        if (bArr != null) {
            Glide.with(this.context).load(bArr).thumbnail(0.5f).placeholder((int) R.drawable.logo).error(R.drawable.logo).into(myViewHolder.imageView);
        } else {
            Glide.with(this.context).load(ContentUris.withAppendedId(Util.sArtworkUri, Long.parseLong(this.datas.get(adapterPosition).getAlbumid()))).thumbnail(0.5f).placeholder((int) R.drawable.logo).error(R.drawable.logo).into(myViewHolder.imageView);
        }
        myViewHolder.parent.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                Log.e("here", "clicked");
                MPMHorizontalSongAdapter.this.listener.OnSingleSongClicked(MPMHorizontalSongAdapter.this.datas.get(adapterPosition));
            }
        });
    }

    @Override 
    public int getItemCount() {
        return this.datas.size();
    }

    
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView artist;
        ImageView imageView;
        View parent;
        TextView title;

        public MyViewHolder(@NonNull View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.image);
            this.title = (TextView) view.findViewById(R.id.title);
            this.artist = (TextView) view.findViewById(R.id.artist);
            this.parent = view.findViewById(R.id.parent);
        }
    }
}
