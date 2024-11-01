package com.demo.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.demo.music.R;
import com.yalantis.multiselection.lib.adapter.BaseRightAdapter;

import com.demo.music.interfaces.MPMCallback;
import com.demo.music.model.MPMSongModel;
import com.demo.music.viewholders.MPMPlaylistViewHolder;

import org.jetbrains.annotations.NotNull;


public class MPMRightAdapter extends BaseRightAdapter<MPMSongModel, MPMPlaylistViewHolder> {
    private final MPMCallback callback;
    private final Context context;

    public MPMRightAdapter(Context context, MPMCallback mPMCallback) {
        this.callback = mPMCallback;
        this.context = context;
    }

    public MPMPlaylistViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MPMPlaylistViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_album_song_list, viewGroup, false));
    }

    public void onBindViewHolder(@NotNull final MPMPlaylistViewHolder mPMPlaylistViewHolder, int i) {
        super.onBindViewHolder(mPMPlaylistViewHolder, i);
        MPMPlaylistViewHolder.bind(this.context, mPMPlaylistViewHolder, getItemAt(i));
        mPMPlaylistViewHolder.itemView.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public final void onClick(View view) {
                MPMRightAdapter.lambda$onBindViewHolder$1(MPMRightAdapter.this, mPMPlaylistViewHolder, view);
            }
        });
    }

    public static  void lambda$onBindViewHolder$1(final MPMRightAdapter mPMRightAdapter, final MPMPlaylistViewHolder mPMPlaylistViewHolder, final View view) {
        view.setPressed(true);
        view.postDelayed(new Runnable() { 
            @Override 
            public final void run() {
                view.setPressed(false);
                mPMRightAdapter.callback.onClick(mPMPlaylistViewHolder.getAdapterPosition());
            }
        }, 200L);
    }

}
