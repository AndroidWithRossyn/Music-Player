package com.demo.music.interfaces;

import com.demo.music.model.MPMPlaylistModel;
import com.demo.music.model.MPMSongModel;
import java.util.ArrayList;


public interface MPMOnPlayClicked {
    void onAddtoQueue(ArrayList<MPMSongModel> arrayList);

    void onEditPlaylist(MPMPlaylistModel mPMPlaylistModel);

    void onPlayAll(ArrayList<MPMSongModel> arrayList);
}
