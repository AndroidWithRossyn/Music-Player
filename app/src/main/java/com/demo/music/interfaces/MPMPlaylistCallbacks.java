package com.demo.music.interfaces;

import androidx.annotation.Nullable;
import com.demo.music.model.MPMPlaylistModel;
import com.demo.music.model.MPMSongModel;
import java.util.ArrayList;


public interface MPMPlaylistCallbacks {
    void onCreatePlaylistClicked(boolean z, @Nullable ArrayList<MPMSongModel> arrayList);

    void onPlaylistClicked(MPMPlaylistModel mPMPlaylistModel, boolean z, @Nullable ArrayList<MPMSongModel> arrayList);

    void onPlaylistCreated();
}
