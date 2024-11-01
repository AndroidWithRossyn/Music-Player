package com.demo.music.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;


public class MPMPlaylistModel implements Parcelable {
    public static final Creator<MPMPlaylistModel> CREATOR = new Creator<MPMPlaylistModel>() { 
        
        @Override 
        public MPMPlaylistModel createFromParcel(Parcel parcel) {
            return new MPMPlaylistModel(parcel);
        }

        
        @Override 
        public MPMPlaylistModel[] newArray(int i) {
            return new MPMPlaylistModel[i];
        }
    };
    String albumId;
    int id;
    String name;
    int no_of_songs;
    String path;
    ArrayList<MPMSongModel> songModels;

    @Override 
    public int describeContents() {
        return 0;
    }

    public MPMPlaylistModel(int i, String str, int i2, ArrayList<MPMSongModel> arrayList, String str2, String str3) {
        this.songModels = new ArrayList<>();
        this.id = i;
        this.name = str;
        this.no_of_songs = i2;
        this.songModels = arrayList;
        this.path = str2;
        this.albumId = str3;
    }

    protected MPMPlaylistModel(Parcel parcel) {
        this.songModels = new ArrayList<>();
        this.id = parcel.readInt();
        this.name = parcel.readString();
        this.no_of_songs = parcel.readInt();
        this.songModels = parcel.createTypedArrayList(MPMSongModel.CREATOR);
        this.path = parcel.readString();
        this.albumId = parcel.readString();
    }

    public String getAlbumId() {
        return this.albumId;
    }

    public void setAlbumId(String str) {
        this.albumId = str;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int i) {
        this.id = i;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public int getNo_of_songs() {
        return this.no_of_songs;
    }

    public void setNo_of_songs(int i) {
        this.no_of_songs = i;
    }

    public ArrayList<MPMSongModel> getSongModels() {
        return this.songModels;
    }

    public void setSongModels(ArrayList<MPMSongModel> arrayList) {
        this.songModels = arrayList;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String str) {
        this.path = str;
    }

    @Override 
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeString(this.name);
        parcel.writeInt(this.no_of_songs);
        parcel.writeTypedList(this.songModels);
        parcel.writeString(this.path);
        parcel.writeString(this.albumId);
    }
}
