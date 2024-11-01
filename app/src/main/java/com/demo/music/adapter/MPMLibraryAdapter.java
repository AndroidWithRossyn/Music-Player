package com.demo.music.adapter;

import android.content.ContentUris;
import android.content.Context;
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

import com.demo.music.interfaces.MPMOnAlbumClicked;
import com.demo.music.model.MPMAlbumModel;
import com.demo.music.utils.Util;

import java.util.ArrayList;


public class MPMLibraryAdapter extends RecyclerView.Adapter<MPMLibraryAdapter.MyViewHolder> {
    ArrayList<MPMAlbumModel> albums;
    Context context;
    MPMOnAlbumClicked listener;

    public MPMLibraryAdapter(Context context, ArrayList<MPMAlbumModel> arrayList, MPMOnAlbumClicked mPMOnAlbumClicked) {
        this.context = context;
        this.albums = arrayList;
        this.listener = mPMOnAlbumClicked;
    }

    @Override 
    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(this.context).inflate(R.layout.single_album_song_item, viewGroup, false));
    }

    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        final int adapterPosition = myViewHolder.getAdapterPosition();
        myViewHolder.artist.setText(this.albums.get(adapterPosition).getArtist());
        myViewHolder.title.setText(this.albums.get(adapterPosition).getAlbumName());
        Glide.with(this.context).load(ContentUris.withAppendedId(Util.sArtworkUri, Long.parseLong(this.albums.get(adapterPosition).getAlbum_id()))).thumbnail(0.5f).placeholder((int) R.drawable.logo).error(R.drawable.logo).into(myViewHolder.imageView);
        myViewHolder.parent.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                Log.e("here", "clicked");
                MPMLibraryAdapter.this.listener.OnAlbumClicked(MPMLibraryAdapter.this.albums.get(adapterPosition));
            }
        });
    }

    @Override 
    public int getItemCount() {
        return this.albums.size();
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
