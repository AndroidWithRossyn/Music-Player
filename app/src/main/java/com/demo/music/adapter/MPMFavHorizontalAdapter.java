package com.demo.music.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.demo.music.R;
import com.makeramen.roundedimageview.RoundedImageView;

import com.demo.music.interfaces.MPMOnSingleSongClicked;
import com.demo.music.model.MPMSongModel;
import com.demo.music.utils.Util;

import java.util.ArrayList;


public class MPMFavHorizontalAdapter extends RecyclerView.Adapter<MPMFavHorizontalAdapter.MyViewHolder> {
    Context context;
    ArrayList<MPMSongModel> datas;
    MPMOnSingleSongClicked listener;

    public MPMFavHorizontalAdapter(Context context, ArrayList<MPMSongModel> arrayList, MPMOnSingleSongClicked mPMOnSingleSongClicked) {
        this.context = context;
        this.datas = arrayList;
        this.listener = mPMOnSingleSongClicked;
    }

    @Override 
    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(this.context).inflate(R.layout.single_horizontal_fav, viewGroup, false));
    }

    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {
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
            Glide.with(this.context).load(bArr).thumbnail(0.5f).placeholder((int) R.drawable.logo_round).into(myViewHolder.imageView);
        } else {
            Glide.with(this.context).load(ContentUris.withAppendedId(Util.sArtworkUri, Long.parseLong(this.datas.get(adapterPosition).getAlbumid()))).thumbnail(0.5f).placeholder((int) R.drawable.logo_round).into(myViewHolder.imageView);


        }
    }

    @Override 
    public int getItemCount() {
        if (this.datas.size() > 6) {
            return 6;
        }
        return this.datas.size();
    }

    
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView artist;
        RoundedImageView imageView;
        View parent;
        TextView title;

        public MyViewHolder(@NonNull View view) {
            super(view);
            this.imageView = (RoundedImageView) view.findViewById(R.id.shadowImage);
            this.title = (TextView) view.findViewById(R.id.title);
            this.artist = (TextView) view.findViewById(R.id.artist);
            this.parent = view.findViewById(R.id.parent);
        }
    }
}
