package com.demo.music.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.music.R;
import com.squareup.picasso.Picasso;

import com.demo.music.interfaces.MPMMultiSelectListener;
import com.demo.music.interfaces.MPMOnSingleSongClicked;
import com.demo.music.model.MPMSongModel;
import com.demo.music.utils.Util;

import java.util.ArrayList;


public class MPMAlbumSongListAdapter extends RecyclerView.Adapter<MPMAlbumSongListAdapter.MyViewHolder> {
    Context context;
    MPMOnSingleSongClicked listener;
    MPMMultiSelectListener listener1;
    ArrayList<MPMSongModel> songs;
    ArrayList<MPMSongModel> songsCopy;
    byte[] img = null;
    boolean isLongPressed = false;
    int selected = 0;

    public MPMAlbumSongListAdapter(Context context, ArrayList<MPMSongModel> arrayList, MPMOnSingleSongClicked mPMOnSingleSongClicked, MPMMultiSelectListener mPMMultiSelectListener) {
        this.context = context;
        this.songs = arrayList;
        this.listener = mPMOnSingleSongClicked;
        this.listener1 = mPMMultiSelectListener;
        this.songsCopy = new ArrayList<>(arrayList);
    }

    @Override 
    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(this.context).inflate(R.layout.single_album_song_list, viewGroup, false));
    }

    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        StringBuilder sb;
        final int adapterPosition = myViewHolder.getAdapterPosition();
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
        myViewHolder.title.setText(this.songs.get(adapterPosition).getTitle());
        myViewHolder.artist.setText(this.songs.get(adapterPosition).getArtist());
        try {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(this.songs.get(adapterPosition).getData());
            this.img = mediaMetadataRetriever.getEmbeddedPicture();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (this.isLongPressed) {
            myViewHolder.more.setVisibility(View.GONE);
            if (this.songs.get(adapterPosition).isSelected()) {
                myViewHolder.index.setVisibility(View.GONE);
                myViewHolder.checkbox.setVisibility(View.VISIBLE);
            } else {
                myViewHolder.index.setVisibility(View.VISIBLE);
                myViewHolder.checkbox.setVisibility(View.GONE);
            }
        } else {
            myViewHolder.more.setVisibility(View.VISIBLE);
            myViewHolder.index.setVisibility(View.VISIBLE);
            myViewHolder.checkbox.setVisibility(View.GONE);
        }
        if (this.img != null) {
            ImageView imageView = myViewHolder.imageView;
            byte[] bArr = this.img;
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(bArr, 0, bArr.length));
        } else {
            Picasso.with(this.context).load(ContentUris.withAppendedId(Util.sArtworkUri, Long.parseLong(this.songs.get(adapterPosition).getAlbumid()))).placeholder(R.drawable.logo).error(R.drawable.logo).noFade().into(myViewHolder.imageView);
        }
        myViewHolder.more.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                MPMAlbumSongListAdapter.this.listener.onMoreClicked(MPMAlbumSongListAdapter.this.songs.get(adapterPosition));
            }
        });
        myViewHolder.parent.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                if (MPMAlbumSongListAdapter.this.isLongPressed) {
                    boolean isSelected = MPMAlbumSongListAdapter.this.songs.get(adapterPosition).isSelected();
                    MPMAlbumSongListAdapter.this.songs.get(adapterPosition).setSelected(!isSelected);
                    if (!isSelected) {
                        MPMAlbumSongListAdapter.this.listener1.onItemSelected(MPMAlbumSongListAdapter.this.songs.get(adapterPosition));
                        MPMAlbumSongListAdapter.this.selected++;
                    } else {
                        MPMAlbumSongListAdapter.this.listener1.onItemUnselected(MPMAlbumSongListAdapter.this.songs.get(adapterPosition));
                        MPMAlbumSongListAdapter mPMAlbumSongListAdapter = MPMAlbumSongListAdapter.this;
                        mPMAlbumSongListAdapter.selected--;
                    }
                    MPMAlbumSongListAdapter.this.notifyItemChanged(adapterPosition);
                    if (MPMAlbumSongListAdapter.this.selected == 0) {
                        MPMAlbumSongListAdapter.this.setLongPressed(false);
                        return;
                    }
                    return;
                }
                MPMAlbumSongListAdapter.this.listener.OnSingleSongClicked(MPMAlbumSongListAdapter.this.songs.get(adapterPosition));
            }
        });
        myViewHolder.parent.setOnLongClickListener(new View.OnLongClickListener() { 
            @Override 
            public boolean onLongClick(View view) {
                if (MPMAlbumSongListAdapter.this.isLongPressed) {
                    return true;
                }
                MPMAlbumSongListAdapter mPMAlbumSongListAdapter = MPMAlbumSongListAdapter.this;
                mPMAlbumSongListAdapter.isLongPressed = true;
                mPMAlbumSongListAdapter.songs.get(adapterPosition).setSelected(true);
                MPMAlbumSongListAdapter.this.listener1.multiSelect(true);
                MPMAlbumSongListAdapter.this.selected++;
                MPMAlbumSongListAdapter.this.listener1.onItemSelected(MPMAlbumSongListAdapter.this.songs.get(adapterPosition));
                MPMAlbumSongListAdapter.this.notifyDataSetChanged();
                return false;
            }
        });
    }

    @Override 
    public int getItemCount() {
        return this.songs.size();
    }

    public void filter(String str) {
        ArrayList arrayList = new ArrayList();
        if (!TextUtils.isEmpty(str)) {
            Log.e("copy", String.valueOf(this.songsCopy.size()));
            for (int i = 0; i < this.songsCopy.size(); i++) {
                if (this.songsCopy.get(i).getName().toLowerCase().contains(str) || this.songsCopy.get(i).getName().toUpperCase().contains(str)) {
                    arrayList.add(this.songsCopy.get(i));
                }
            }
        } else {
            arrayList.addAll(this.songsCopy);
        }
        if (arrayList.isEmpty()) {
            Toast.makeText(this.context, "No songs found", Toast.LENGTH_SHORT).show();
        }
        this.songs.clear();
        this.songs.addAll(arrayList);
        notifyDataSetChanged();
        arrayList.clear();
    }

    public void unSelectAll() {
        for (int i = 0; i < this.songs.size(); i++) {
            this.songs.get(i).setSelected(false);
        }
        notifyDataSetChanged();
    }

    public void selectAll() {
        for (int i = 0; i < this.songs.size(); i++) {
            this.songs.get(i).setSelected(true);
        }
        notifyDataSetChanged();
    }

    public void setLongPressed(boolean z) {
        this.isLongPressed = z;
        for (int i = 0; i < this.songs.size(); i++) {
            this.songs.get(i).setSelected(false);
        }
        if (!z) {
            this.listener1.multiSelect(false);
        }
        this.selected = 0;
        notifyDataSetChanged();
    }

    
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView artist;
        View checkbox;
        ImageView imageView;
        TextView index;
        View more;
        View parent;
        TextView title;

        public MyViewHolder(@NonNull View view) {
            super(view);
            this.index = (TextView) view.findViewById(R.id.index);
            this.imageView = (ImageView) view.findViewById(R.id.imageView);
            this.title = (TextView) view.findViewById(R.id.song_title);
            this.artist = (TextView) view.findViewById(R.id.artist);
            this.more = view.findViewById(R.id.more);
            this.parent = view.findViewById(R.id.parent);
            this.checkbox = view.findViewById(R.id.checkbox);
        }
    }
}
