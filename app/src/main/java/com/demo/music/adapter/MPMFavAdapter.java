package com.demo.music.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chibde.visualizer.BarVisualizer;
import com.demo.music.R;
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeAdapter;
import com.squareup.picasso.Picasso;

import com.demo.music.interfaces.MPMOnSingleSongClicked;
import com.demo.music.model.MPMSongModel;
import com.demo.music.utils.Util;

import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class MPMFavAdapter extends DragDropSwipeAdapter<MPMSongModel, MPMFavAdapter.MyViewHolder> {
    Context context;
    ArrayList<MPMSongModel> dataSet;
    byte[] img;
    MPMOnSingleSongClicked listener;

    
    public boolean canBeDragged(MPMSongModel mPMSongModel, @NotNull MyViewHolder myViewHolder, int i) {
        return false;
    }

    
    @Nullable
    public View getViewToTouchToStartDraggingItem(MPMSongModel mPMSongModel, @NotNull MyViewHolder myViewHolder, int i) {
        return null;
    }

    public MPMFavAdapter(Context context, ArrayList<MPMSongModel> arrayList, MPMOnSingleSongClicked mPMOnSingleSongClicked) {
        super(arrayList);
        this.context = context;
        this.dataSet = arrayList;
        this.listener = mPMOnSingleSongClicked;
    }

    @Override
    
    @NotNull
    public MyViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(this.context).inflate(R.layout.single_album_song_list, viewGroup, false));
    }

    
    @Override 
    @NotNull
    public MyViewHolder getViewHolder(@NotNull View view) {
        return new MyViewHolder(view);
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
        myViewHolder.rel1.setBackgroundResource(0);
        myViewHolder.visualizer.setVisibility(View.GONE);
        myViewHolder.actionButton.setVisibility(View.GONE);
        myViewHolder.index.setVisibility(View.VISIBLE);
        myViewHolder.title.setTextColor(this.context.getResources().getColor(R.color.black));
        myViewHolder.artist.setTextColor(Color.parseColor("#979797"));
        myViewHolder.more.setColorFilter(this.context.getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
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
                MPMFavAdapter.this.listener.OnSingleSongClicked(MPMFavAdapter.this.dataSet.get(adapterPosition));
            }
        });
        myViewHolder.more.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                MPMFavAdapter.this.listener.onMoreClicked(MPMFavAdapter.this.dataSet.get(adapterPosition));
            }
        });
        myViewHolder.actionButton.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
            }
        });
    }

    public void filter(ArrayList<MPMSongModel> arrayList, String str) {
        ArrayList arrayList2 = new ArrayList();
        if (!TextUtils.isEmpty(str)) {
            Log.e("copy", String.valueOf(arrayList.size()));
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i).getName().toLowerCase().contains(str) || arrayList.get(i).getName().toUpperCase().contains(str)) {
                    arrayList2.add(arrayList.get(i));
                }
            }
        } else {
            arrayList2.addAll(arrayList);
        }
        if (arrayList2.isEmpty()) {
            Toast.makeText(this.context, "No songs found", Toast.LENGTH_SHORT).show();
        }
        setDataSet(arrayList2);
        arrayList2.clear();
    }

    
    public class MyViewHolder extends ViewHolder {
        TextView index ;
        ImageView imageView;
        TextView title;
        TextView artist ;
        ImageView more ;
        View parent ;
        BarVisualizer visualizer ;
        View rel1 ;
        ImageView actionButton ;

        public MyViewHolder(@NotNull View view) {
            super(view);


            index = (TextView) view.findViewById(R.id.index);
            imageView = (ImageView) view.findViewById(R.id.imageView);
            title = (TextView) view.findViewById(R.id.song_title);
            artist = (TextView) view.findViewById(R.id.artist);
            more = (ImageView) view.findViewById(R.id.more);
            parent = view.findViewById(R.id.parent);
            visualizer = (BarVisualizer) view.findViewById(R.id.visualizer);
            rel1 = view.findViewById(R.id.rel1);
            actionButton = (ImageView) view.findViewById(R.id.pause_play_queue);
        }
    }
}
