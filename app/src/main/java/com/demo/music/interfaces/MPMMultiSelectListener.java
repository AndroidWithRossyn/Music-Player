package com.demo.music.interfaces;

import com.demo.music.model.MPMSongModel;


public interface MPMMultiSelectListener {
    void multiSelect(boolean z);

    void onItemSelected(MPMSongModel mPMSongModel);

    void onItemUnselected(MPMSongModel mPMSongModel);
}
