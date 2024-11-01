package com.demo.music.model;


public class MPMAlbumModel {
    String albumName;
    String album_id;
    String artist;

    public MPMAlbumModel(String str, String str2, String str3) {
        this.albumName = str;
        this.album_id = str2;
        this.artist = str3;
    }

    public String getAlbumName() {
        return this.albumName;
    }

    public void setAlbumName(String str) {
        this.albumName = str;
    }

    public String getAlbum_id() {
        return this.album_id;
    }

    public void setAlbum_id(String str) {
        this.album_id = str;
    }

    public String getArtist() {
        return this.artist;
    }

    public void setArtist(String str) {
        this.artist = str;
    }
}
