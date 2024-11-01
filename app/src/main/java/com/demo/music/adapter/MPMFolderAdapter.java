package com.demo.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.music.R;

import com.demo.music.interfaces.MPMOnSingleSongClicked;
import com.demo.music.interfaces.MPMonSeeMoreClicked;
import com.demo.music.model.MPMFolderModel;
import com.demo.music.model.MPMSongModel;

import java.util.ArrayList;


public class MPMFolderAdapter extends RecyclerView.Adapter<MPMFolderAdapter.MyViewHolder> {
    Context context;
    ArrayList<MPMFolderModel> datas;
    boolean isFav;
    MPMonSeeMoreClicked listener;
    MPMOnSingleSongClicked listener1;

    public MPMFolderAdapter(Context context, ArrayList<MPMFolderModel> arrayList, MPMonSeeMoreClicked mPMonSeeMoreClicked, MPMOnSingleSongClicked mPMOnSingleSongClicked, boolean z) {
        this.context = context;
        this.datas = arrayList;
        this.listener = mPMonSeeMoreClicked;
        this.listener1 = mPMOnSingleSongClicked;
        this.isFav = z;
    }

    @Override 
    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(this.context).inflate(R.layout.single_folder_view, viewGroup, false));
    }

    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        final int adapterPosition = myViewHolder.getAdapterPosition();
        myViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false));
        if (!this.isFav || adapterPosition != 0) {
            myViewHolder.recyclerView.setAdapter(new MPMHorizontalSongAdapter(this.context, this.datas.get(adapterPosition).getSongModels(), this.listener1));
        } else {
            myViewHolder.recyclerView.setAdapter(new MPMFavHorizontalAdapter(this.context, this.datas.get(adapterPosition).getSongModels(), this.listener1));
        }
        myViewHolder.title.setText(this.datas.get(adapterPosition).getFolderName());
        myViewHolder.seeAll.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                String folderName = MPMFolderAdapter.this.datas.get(adapterPosition).getFolderName();
                boolean z = false;
                MPMSongModel mPMSongModel = MPMFolderAdapter.this.datas.get(adapterPosition).getSongModels().get(0);
                MPMonSeeMoreClicked mPMonSeeMoreClicked = MPMFolderAdapter.this.listener;
                if (MPMFolderAdapter.this.isFav && adapterPosition == 0) {
                    z = true;
                }
                mPMonSeeMoreClicked.onSeeMoreClicked(folderName, mPMSongModel, z);
            }
        });
    }

    public void setFavAdded(boolean z) {
        this.isFav = z;
    }

    @Override 
    public int getItemCount() {
        return this.datas.size();
    }

    
    public class MyViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;
        TextView seeAll;
        TextView title;

        public MyViewHolder(@NonNull View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.album_name);
            this.recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            this.seeAll = (TextView) view.findViewById(R.id.seeAll);
        }
    }
}
