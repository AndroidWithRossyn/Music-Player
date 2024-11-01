package com.demo.music.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.demo.music.R;
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeAdapter;
import com.squareup.picasso.Picasso;

import com.demo.music.model.MPMSongModel;
import com.demo.music.utils.Util;

import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class MPMCurrentListAdapter extends DragDropSwipeAdapter<MPMSongModel, MPMCurrentListAdapter.MyViewHolder> {
    Context context;
    int currentIndex;
    ArrayList<MPMSongModel> dataSet;
    byte[] img;
    boolean isPlaying;
    MPMStatusAdapter.OnItemClickListener listener;
    MediaPlayer mediaPlayer;

    public MPMCurrentListAdapter(Context context, int i, ArrayList<MPMSongModel> arrayList, MPMStatusAdapter.OnItemClickListener onItemClickListener) {
        super(arrayList);
        this.context = context;
        this.dataSet = arrayList;
        this.currentIndex = i;
        this.listener = onItemClickListener;
    }

    @Override
    
    @NotNull
    public MyViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(this.context).inflate(R.layout.single_song_current_list, viewGroup, false));
    }

    
    @Override 
    @NotNull
    public MyViewHolder getViewHolder(@NotNull View view) {
        return new MyViewHolder(view);
    }

    
    @Nullable
    public View getViewToTouchToStartDraggingItem(MPMSongModel mPMSongModel, @NotNull MyViewHolder myViewHolder, int i) {
        return myViewHolder.more;
    }

    
    public boolean canBeSwiped(MPMSongModel mPMSongModel, @NotNull MyViewHolder myViewHolder, int i) {
        return i != this.currentIndex;
    }

    
    public void onBindViewHolder(MPMSongModel mPMSongModel, @NotNull MyViewHolder myViewHolder, int i) {
        StringBuilder sb;
        final int adapterPosition = i;
        TextView textView = myViewHolder.index;
        if (adapterPosition < 9) {
            sb = new StringBuilder();
            sb.append("0");
            sb.append(adapterPosition + 1);
        } else {
            sb = new StringBuilder();
            sb.append(adapterPosition + 1);
            sb.append("");
        }
        textView.setText(sb.toString());
        myViewHolder.title.setText(mPMSongModel.getTitle());
        myViewHolder.artist.setText(mPMSongModel.getArtist());
        if (adapterPosition == this.currentIndex) {
            myViewHolder.rel1.setBackground(this.context.getResources().getDrawable(R.drawable.queueselectedsong));
            if (this.isPlaying) {
                myViewHolder.lottieAnimationView.setVisibility(View.VISIBLE);
            } else {
                myViewHolder.lottieAnimationView.setVisibility(View.GONE);
            }
            myViewHolder.actionButton.setVisibility(View.VISIBLE);
            myViewHolder.index.setVisibility(View.GONE);
            myViewHolder.actionButton.setImageResource(this.isPlaying ? R.drawable.pause : R.drawable.play);
            myViewHolder.title.setTextColor(this.context.getResources().getColor(R.color.white));
            myViewHolder.artist.setTextColor(this.context.getResources().getColor(R.color.white));
            myViewHolder.more.setColorFilter(this.context.getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        } else {
            myViewHolder.rel1.setBackgroundResource(0);
            myViewHolder.lottieAnimationView.setVisibility(View.GONE);
            myViewHolder.actionButton.setVisibility(View.GONE);
            myViewHolder.index.setVisibility(View.VISIBLE);
            myViewHolder.title.setTextColor(this.context.getResources().getColor(R.color.black));
            myViewHolder.artist.setTextColor(Color.parseColor("#979797"));
            myViewHolder.more.setColorFilter(Color.parseColor("#888888"), PorterDuff.Mode.SRC_IN);
        }
        this.img = null;
        try {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(mPMSongModel.getData());
            this.img = mediaMetadataRetriever.getEmbeddedPicture();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (this.img != null) {
            ImageView imageView = myViewHolder.imageView;
            byte[] bArr = this.img;
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(bArr, 0, bArr.length));
        } else {
            Picasso.with(this.context).load(ContentUris.withAppendedId(Util.sArtworkUri, Long.parseLong(mPMSongModel.getAlbumid()))).placeholder(R.drawable.logo).error(R.drawable.logo).noFade().into(myViewHolder.imageView);
        }
        myViewHolder.parent.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                if (adapterPosition != MPMCurrentListAdapter.this.currentIndex) {
                    MPMCurrentListAdapter.this.listener.onItemClick(view, adapterPosition);
                }
            }
        });
        myViewHolder.actionButton.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                MPMCurrentListAdapter.this.listener.onPlayPauseClicked();
            }
        });
    }

    public void setCurrentIndex(int i) {
        this.currentIndex = i;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public void setPlayPause(boolean z) {
        this.isPlaying = z;
        notifyDataSetChanged();
    }

    
    public class MyViewHolder extends ViewHolder {
        TextView index;
        ImageView imageView;
        TextView title;
        TextView artist;
        ImageView more;
        View parent;
        View rel1;
        ImageView actionButton;
        LottieAnimationView lottieAnimationView;

        public MyViewHolder(@NotNull View itemView) {
            super(itemView);
            index = (TextView) itemView.findViewById(R.id.index);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            title = (TextView) itemView.findViewById(R.id.song_title);
            artist = (TextView) itemView.findViewById(R.id.artist);
            more = (ImageView) itemView.findViewById(R.id.more);
            parent = itemView.findViewById(R.id.parent);
            rel1 = itemView.findViewById(R.id.rel1);
            actionButton = (ImageView) itemView.findViewById(R.id.pause_play_queue);
            lottieAnimationView = (LottieAnimationView) itemView.findViewById(R.id.lottieAnimationView);


        }
    }
}
