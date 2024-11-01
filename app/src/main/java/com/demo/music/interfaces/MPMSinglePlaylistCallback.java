package com.demo.music.interfaces;

import com.demo.music.model.MPMPlaylistModel;


public interface MPMSinglePlaylistCallback {
    void onPlaylistDeleteClicked(int i, MPMPlaylistModel mPMPlaylistModel);

    void onPlaylistEditClicked(int i, MPMPlaylistModel mPMPlaylistModel);
}
