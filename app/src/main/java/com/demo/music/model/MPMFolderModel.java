package com.demo.music.model;

import java.util.ArrayList;


public class MPMFolderModel {
    private String bucketDisplayName;
    private String folderName;
    ArrayList<MPMSongModel> songModels;

    public MPMFolderModel(String str, String str2, ArrayList<MPMSongModel> arrayList) {
        this.folderName = str;
        this.bucketDisplayName = str2;
        this.songModels = arrayList;
    }

    public String getBucketDisplayName() {
        return this.bucketDisplayName;
    }

    public void setBucketDisplayName(String str) {
        this.bucketDisplayName = str;
    }

    public String getFolderName() {
        return this.folderName;
    }

    public void setFolderName(String str) {
        this.folderName = str;
    }

    public ArrayList<MPMSongModel> getSongModels() {
        return this.songModels;
    }

    public void setSongModels(ArrayList<MPMSongModel> arrayList) {
        this.songModels = arrayList;
    }
}
