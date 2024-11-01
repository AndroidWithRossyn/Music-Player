package com.demo.music.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.music.R;
import com.squareup.picasso.Picasso;

import com.demo.music.interfaces.MPMPlaylistCallbacks;
import com.demo.music.interfaces.MPMSinglePlaylistCallback;
import com.demo.music.model.MPMPlaylistModel;
import com.demo.music.model.MPMSongModel;
import com.demo.music.utils.Util;

import java.util.ArrayList;


public class MPMPlaylistAdapter extends RecyclerView.Adapter<MPMPlaylistAdapter.ViewHolder> {
    MPMPlaylistCallbacks callbacks;
    Context context;
    byte[] img;
    ArrayList<MPMPlaylistModel> playlists;
    boolean showIcons;
    MPMSinglePlaylistCallback singlePlaylistCallback;
    ArrayList<MPMSongModel> songs;

    public MPMPlaylistAdapter(Context context, boolean z, ArrayList<MPMSongModel> arrayList, ArrayList<MPMPlaylistModel> arrayList2, MPMPlaylistCallbacks mPMPlaylistCallbacks, MPMSinglePlaylistCallback mPMSinglePlaylistCallback) {
        this.context = context;
        this.playlists = arrayList2;
        this.callbacks = mPMPlaylistCallbacks;
        this.showIcons = z;
        this.songs = arrayList;
        this.singlePlaylistCallback = mPMSinglePlaylistCallback;
    }

    @Override 
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(this.context).inflate(R.layout.single_playlist_item, viewGroup, false));
    }

    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final int adapterPosition = viewHolder.getAdapterPosition();
        viewHolder.title.setText(this.playlists.get(adapterPosition).getName());
        TextView textView = viewHolder.number;
        textView.setText(this.playlists.get(adapterPosition).getNo_of_songs() + " Songs");
        viewHolder.rename.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                MPMPlaylistAdapter.this.singlePlaylistCallback.onPlaylistEditClicked(adapterPosition, MPMPlaylistAdapter.this.playlists.get(adapterPosition));
            }
        });
        viewHolder.delete.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                MPMPlaylistAdapter.this.singlePlaylistCallback.onPlaylistDeleteClicked(adapterPosition, MPMPlaylistAdapter.this.playlists.get(adapterPosition));
            }
        });
        if (!this.showIcons) {
            viewHolder.rename.setVisibility(View.GONE);
            viewHolder.delete.setVisibility(View.GONE);
        }
        this.img = null;
        try {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(this.playlists.get(adapterPosition).getPath());
            this.img = mediaMetadataRetriever.getEmbeddedPicture();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (this.img != null) {
            ImageView imageView = viewHolder.imageView;
            byte[] bArr = this.img;
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(bArr, 0, bArr.length));
        } else {
            Picasso.with(this.context).load(ContentUris.withAppendedId(Util.sArtworkUri, Long.parseLong(this.playlists.get(adapterPosition).getAlbumId()))).placeholder(R.drawable.logo).error(R.drawable.logo).noFade().into(viewHolder.imageView);
        }
        viewHolder.parent.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                MPMPlaylistAdapter.this.callbacks.onPlaylistClicked(MPMPlaylistAdapter.this.playlists.get(adapterPosition), MPMPlaylistAdapter.this.showIcons, MPMPlaylistAdapter.this.songs);
            }
        });
    }

    @Override 
    public int getItemCount() {
        return this.playlists.size();
    }

    
    public class ViewHolder extends RecyclerView.ViewHolder {
        View delete;
        ImageView imageView;
        TextView number;
        View parent;
        View rename;
        TextView title;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.playlist_image);
            this.rename = view.findViewById(R.id.rename);
            this.delete = view.findViewById(R.id.delete);
            this.title = (TextView) view.findViewById(R.id.title);
            this.number = (TextView) view.findViewById(R.id.number);
            this.parent = view.findViewById(R.id.parent);
        }
    }
}
