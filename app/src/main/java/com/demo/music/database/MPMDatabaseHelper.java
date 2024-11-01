package com.demo.music.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.demo.music.model.MPMPlaylistModel;
import com.demo.music.model.MPMSongModel;
import java.util.ArrayList;


public class MPMDatabaseHelper extends SQLiteOpenHelper {
    public static final String DBNAME = "Music Player.db";
    public static final String FAV_TABLE_ID = "id";
    public static final String FAV_TABLE_NAME = "fav";
    public static final String FAV_TABLE_OBJECT = "object";
    public static final String PLAYLIST_TABLE_ALBUMID = "albumid";
    public static final String PLAYLIST_TABLE_ID = "id";
    public static final String PLAYLIST_TABLE_NAME = "playlists";
    public static final String PLAYLIST_TABLE_NO_OF_SONGS = "number_of_songs";
    public static final String PLAYLIST_TABLE_PATH = "path";
    public static final String PLAYLIST_TABLE_PLNAME = "name";
    public static final String PLAYLIST_TABLE_SONGS = "songs";

    public MPMDatabaseHelper(Context context) {
        super(context, DBNAME, (SQLiteDatabase.CursorFactory) null, 1);
    }

    @Override 
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("create table playlists (id integer primary key,name text,number_of_songs text,songs text,path text,albumid text)");
    }

    @Override 
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        sQLiteDatabase.execSQL("drop table if exists playlists");
        onCreate(sQLiteDatabase);
    }

    public void createPlaylist(MPMPlaylistModel mPMPlaylistModel) {
        ArrayList<MPMSongModel> songModels = mPMPlaylistModel.getSongModels();
        for (int i = 0; i < songModels.size(); i++) {
            songModels.get(i).setSelected(false);
        }
        mPMPlaylistModel.setSongModels(songModels);
        SQLiteDatabase writableDatabase = getWritableDatabase();
        String json = new Gson().toJson(mPMPlaylistModel.getSongModels());
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", mPMPlaylistModel.getName());
        contentValues.put(PLAYLIST_TABLE_NO_OF_SONGS, Integer.valueOf(mPMPlaylistModel.getNo_of_songs()));
        contentValues.put(PLAYLIST_TABLE_SONGS, json);
        contentValues.put(PLAYLIST_TABLE_PATH, mPMPlaylistModel.getSongModels().get(0).getData());
        contentValues.put(PLAYLIST_TABLE_ALBUMID, mPMPlaylistModel.getSongModels().get(0).getAlbumid());
        writableDatabase.insert(PLAYLIST_TABLE_NAME, null, contentValues);
    }

    public ArrayList<MPMPlaylistModel> getPlaylists() {
        ArrayList<MPMPlaylistModel> arrayList = new ArrayList<>();
        Cursor rawQuery = getReadableDatabase().rawQuery("select * from playlists", null);
        if (!rawQuery.moveToFirst()) {
            return arrayList;
        }
        do {
            int i = rawQuery.getInt(rawQuery.getColumnIndex("id"));
            String string = rawQuery.getString(rawQuery.getColumnIndex("name"));
            int i2 = rawQuery.getInt(rawQuery.getColumnIndex(PLAYLIST_TABLE_NO_OF_SONGS));
            String string2 = rawQuery.getString(rawQuery.getColumnIndex(PLAYLIST_TABLE_SONGS));
            String string3 = rawQuery.getString(rawQuery.getColumnIndex(PLAYLIST_TABLE_PATH));
            String string4 = rawQuery.getString(rawQuery.getColumnIndex(PLAYLIST_TABLE_ALBUMID));
            ArrayList arrayList2 = (ArrayList) new Gson().fromJson(string2, new TypeToken<ArrayList<MPMSongModel>>() { 
            }.getType());
            for (int i3 = 0; i3 < arrayList2.size(); i3++) {
                Log.e("name:", ((MPMSongModel) arrayList2.get(i3)).getTitle());
                Log.e("data:", ((MPMSongModel) arrayList2.get(i3)).getData());
            }
            arrayList.add(new MPMPlaylistModel(i, string, i2, arrayList2, string3, string4));
        } while (rawQuery.moveToNext());
        return arrayList;
    }

    public void editPlaylist(MPMPlaylistModel mPMPlaylistModel) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        String json = new Gson().toJson(mPMPlaylistModel.getSongModels());
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", mPMPlaylistModel.getName());
        contentValues.put(PLAYLIST_TABLE_NO_OF_SONGS, Integer.valueOf(mPMPlaylistModel.getNo_of_songs()));
        contentValues.put(PLAYLIST_TABLE_SONGS, json);
        contentValues.put(PLAYLIST_TABLE_PATH, mPMPlaylistModel.getSongModels().get(0).getData());
        contentValues.put(PLAYLIST_TABLE_ALBUMID, mPMPlaylistModel.getSongModels().get(0).getAlbumid());
        writableDatabase.update(PLAYLIST_TABLE_NAME, contentValues, "id = ? ", new String[]{Integer.toString(mPMPlaylistModel.getId())});
    }

    public void deletePlaylist(MPMPlaylistModel mPMPlaylistModel) {
        getWritableDatabase().delete(PLAYLIST_TABLE_NAME, "id = ? ", new String[]{Integer.toString(mPMPlaylistModel.getId())});
    }
}
