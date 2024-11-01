package com.demo.music.model;

import android.os.Parcel;
import android.os.Parcelable;


public class MPMSongModel implements Parcelable, Comparable<MPMSongModel> {
    public static final Creator<MPMSongModel> CREATOR = new Creator<MPMSongModel>() { 
        
        @Override 
        public MPMSongModel createFromParcel(Parcel parcel) {
            return new MPMSongModel(parcel);
        }

        
        @Override 
        public MPMSongModel[] newArray(int i) {
            return new MPMSongModel[i];
        }
    };
    private String album;
    private String albumid;
    private String artist;
    private String data;
    private String duration;
    private boolean isSelected;
    private String name;
    private long size;
    private String title;
    private String year;

    @Override 
    public int describeContents() {
        return 0;
    }

    public MPMSongModel(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, long j) {
        this(str, str2, str3, str4, str5, str6, str7, str8, j, false);
    }

    public MPMSongModel(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, long j, boolean z) {
        this.title = str;
        this.data = str2;
        this.name = str3;
        this.duration = str4;
        this.albumid = str6;
        this.year = str7;
        this.artist = str8;
        this.album = str5;
        this.size = j;
        this.isSelected = z;
    }

    protected MPMSongModel(Parcel parcel) {
        this.title = parcel.readString();
        this.data = parcel.readString();
        this.name = parcel.readString();
        this.duration = parcel.readString();
        this.albumid = parcel.readString();
        this.year = parcel.readString();
        this.artist = parcel.readString();
        this.album = parcel.readString();
        this.size = parcel.readLong();
    }

    public String getAlbum() {
        return this.album;
    }

    public void setAlbum(String str) {
        this.album = str;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String str) {
        this.title = str;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String str) {
        this.data = str;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getDuration() {
        return this.duration;
    }

    public void setDuration(String str) {
        this.duration = str;
    }

    public String getAlbumid() {
        return this.albumid;
    }

    public void setAlbumid(String str) {
        this.albumid = str;
    }

    public String getYear() {
        return this.year;
    }

    public void setYear(String str) {
        this.year = str;
    }

    public String getArtist() {
        return this.artist;
    }

    public void setArtist(String str) {
        this.artist = str;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(long j) {
        this.size = j;
    }

    public boolean isSelected() {
        return this.isSelected;
    }

    public void setSelected(boolean z) {
        this.isSelected = z;
    }

    @Override 
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.title);
        parcel.writeString(this.data);
        parcel.writeString(this.name);
        parcel.writeString(this.duration);
        parcel.writeString(this.albumid);
        parcel.writeString(this.year);
        parcel.writeString(this.artist);
        parcel.writeString(this.album);
        parcel.writeLong(this.size);
    }

    public int compareTo(MPMSongModel mPMSongModel) {
        return this.data.compareTo(mPMSongModel.getData());
    }
}
