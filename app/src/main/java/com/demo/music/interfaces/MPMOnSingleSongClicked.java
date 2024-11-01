package com.demo.music.interfaces;

import com.demo.music.model.MPMSongModel;


public interface MPMOnSingleSongClicked {
    void OnSingleSongClicked(MPMSongModel mPMSongModel);

    void onAddtoPlaylist(MPMSongModel mPMSongModel);

    void onAddtoQueue(MPMSongModel mPMSongModel);

    void onMoreClicked(MPMSongModel mPMSongModel);

    void onPlayNextClicked(MPMSongModel mPMSongModel);
}
