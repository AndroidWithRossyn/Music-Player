package com.demo.music.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.demo.music.database.MPMDatabaseHelper;
import com.demo.music.model.MPMEqualizerModel;
import com.demo.music.model.MPMSongModel;
import java.util.ArrayList;
import java.util.Collections;


public class StorageUtil {
    private final String STORAGE = "STORAGE";
    private Context context;
    private SharedPreferences preferences;

    public StorageUtil(Context context) {
        this.context = context;
    }

    public void storeEqualizer(MPMEqualizerModel mPMEqualizerModel) {
        this.preferences = this.context.getSharedPreferences("STORAGE", 0);
        SharedPreferences.Editor edit = this.preferences.edit();
        edit.putString("equalizer", new Gson().toJson(mPMEqualizerModel));
        edit.apply();
    }

    public MPMEqualizerModel loadEqualizer() {
        this.preferences = this.context.getSharedPreferences("STORAGE", 0);
        return (MPMEqualizerModel) new Gson().fromJson(this.preferences.getString("equalizer", null), new TypeToken<MPMEqualizerModel>() { 
        }.getType());
    }

    public void storeAudio(ArrayList<MPMSongModel> arrayList) {
        this.preferences = this.context.getSharedPreferences("STORAGE", 0);
        SharedPreferences.Editor edit = this.preferences.edit();
        edit.putString("audioArrayList", new Gson().toJson(arrayList));
        edit.apply();
    }

    public ArrayList<MPMSongModel> loadAudio() {
        this.preferences = this.context.getSharedPreferences("STORAGE", 0);
        Gson gson = new Gson();
        String string = this.preferences.getString("audioArrayList", null);
        if (string == null) {
            return new ArrayList<>();
        }
        return (ArrayList) gson.fromJson(string, new TypeToken<ArrayList<MPMSongModel>>() { 
        }.getType());
    }

    public void storeAudioIndex(int i) {
        this.preferences = this.context.getSharedPreferences("STORAGE", 0);
        SharedPreferences.Editor edit = this.preferences.edit();
        edit.putInt("audioIndex", i);
        edit.apply();
    }

    public void storeCurrent(MPMSongModel mPMSongModel) {
        this.preferences = this.context.getSharedPreferences("STORAGE", 0);
        SharedPreferences.Editor edit = this.preferences.edit();
        edit.putString("current", new Gson().toJson(mPMSongModel));
        edit.apply();
    }

    public MPMSongModel getCurrent() {
        this.preferences = this.context.getSharedPreferences("STORAGE", 0);
        Gson gson = new Gson();
        String string = this.preferences.getString("current", null);
        if (string == null) {
            return null;
        }
        return (MPMSongModel) gson.fromJson(string, new TypeToken<MPMSongModel>() { 
        }.getType());
    }

    public ArrayList<MPMSongModel> createNewShuffle() {
        ArrayList<MPMSongModel> loadAudio = loadAudio();
        Collections.shuffle(loadAudio);
        storeSuffledSongs(loadAudio);
        return loadAudio;
    }

    public ArrayList<MPMSongModel> getSuffledSongs() {
        this.preferences = this.context.getSharedPreferences("STORAGE", 0);
        Gson gson = new Gson();
        String string = this.preferences.getString("suffledList", null);
        if (string == null) {
            return new ArrayList<>();
        }
        return (ArrayList) gson.fromJson(string, new TypeToken<ArrayList<MPMSongModel>>() { 
        }.getType());
    }

    public void storeSuffledSongs(ArrayList<MPMSongModel> arrayList) {
        this.preferences = this.context.getSharedPreferences("STORAGE", 0);
        SharedPreferences.Editor edit = this.preferences.edit();
        edit.putString("suffledList", new Gson().toJson(arrayList));
        edit.apply();
    }

    public int loadAudioIndex() {
        this.preferences = this.context.getSharedPreferences("STORAGE", 0);
        return this.preferences.getInt("audioIndex", 0);
    }

    private void storeFav(ArrayList<MPMSongModel> arrayList, boolean z) {
        this.preferences = this.context.getSharedPreferences("STORAGE", 0);
        SharedPreferences.Editor edit = this.preferences.edit();
        edit.putString("favArrayList", new Gson().toJson(arrayList));
        edit.apply();
    }

    public void storeFav(MPMSongModel mPMSongModel) {
        ArrayList<MPMSongModel> fav = getFav();
        fav.add(mPMSongModel);
        storeFav(fav, true);
    }

    public void storeFav(ArrayList<MPMSongModel> arrayList) {
        ArrayList<MPMSongModel> fav = getFav();
        for (int i = 0; i < arrayList.size(); i++) {
            if (!isFav(arrayList.get(i))) {
                fav.add(arrayList.get(i));
            }
        }
        storeFav(fav, true);
    }

    public void removeFav(MPMSongModel mPMSongModel) {
        ArrayList<MPMSongModel> fav = getFav();
        ArrayList<MPMSongModel> arrayList = new ArrayList<>();
        Log.e("songsSIze", fav.size() + "");
        for (int i = 0; i < fav.size(); i++) {
            if (fav.get(i).getData().equals(mPMSongModel.getData())) {
                Log.e("here123", "hey");
            } else {
                arrayList.add(fav.get(i));
            }
        }
        storeFav(arrayList, true);
    }

    public void removeFav(ArrayList<MPMSongModel> arrayList) {
        ArrayList<MPMSongModel> fav = getFav();
        ArrayList<MPMSongModel> arrayList2 = new ArrayList<>();
        Log.e("songsSIze", fav.size() + "");
        int i = 0;
        while (true) {
            boolean z = true;
            if (i < fav.size()) {
                int i2 = 0;
                while (true) {
                    if (i2 >= arrayList.size()) {
                        break;
                    } else if (fav.get(i).getData().equals(arrayList.get(i2).getData())) {
                        z = false;
                        break;
                    } else {
                        i2++;
                    }
                }
                if (z) {
                    Log.e("here123", fav.get(i).getTitle());
                    arrayList2.add(fav.get(i));
                }
                i++;
            } else {
                storeFav(arrayList2, true);
                return;
            }
        }
    }

    public ArrayList<MPMSongModel> getFav() {
        this.preferences = this.context.getSharedPreferences("STORAGE", 0);
        Gson gson = new Gson();
        String string = this.preferences.getString("favArrayList", null);
        if (string == null) {
            return new ArrayList<>();
        }
        return (ArrayList) gson.fromJson(string, new TypeToken<ArrayList<MPMSongModel>>() { 
        }.getType());
    }

    public boolean isFav(MPMSongModel mPMSongModel) {
        this.preferences = this.context.getSharedPreferences("STORAGE", 0);
        Gson gson = new Gson();
        String string = this.preferences.getString("favArrayList", null);
        if (string == null) {
            Log.e(MPMDatabaseHelper.FAV_TABLE_NAME, "1");
            return false;
        }
        ArrayList arrayList = (ArrayList) gson.fromJson(string, new TypeToken<ArrayList<MPMSongModel>>() { 
        }.getType());
        if (arrayList != null) {
            Log.e(MPMDatabaseHelper.FAV_TABLE_NAME, "2");
            Log.e(MPMDatabaseHelper.FAV_TABLE_NAME, "size" + arrayList.size());
            for (int i = 0; i < arrayList.size(); i++) {
                if (mPMSongModel.getData().equals(((MPMSongModel) arrayList.get(i)).getData())) {
                    return true;
                }
            }
            return false;
        }
        Log.e(MPMDatabaseHelper.FAV_TABLE_NAME, "3");
        return false;
    }

    public boolean isFav(ArrayList<MPMSongModel> arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            if (!isFav(arrayList.get(i))) {
                return false;
            }
        }
        return true;
    }

    public void clearCachedAudioPlaylist() {
        this.preferences = this.context.getSharedPreferences("STORAGE", 0);
        SharedPreferences.Editor edit = this.preferences.edit();
        edit.putString("audioArrayList", null);
        edit.putString("current", null);
        edit.putInt("audioIndex", 0);
        edit.putString("suffledList", null);
        edit.apply();
    }

    public int deleteFromList(MPMSongModel mPMSongModel) {
        ArrayList<MPMSongModel> loadAudio = loadAudio();
        int i = 0;
        int i2 = 0;
        while (true) {
            if (i2 >= loadAudio.size()) {
                break;
            } else if (mPMSongModel.getData().equals(loadAudio.get(i2).getData())) {
                i = i2;
                break;
            } else {
                i2++;
            }
        }
        loadAudio.remove(i);
        storeAudio(loadAudio);
        return i;
    }

    public void restoreAudio(MPMSongModel mPMSongModel, int i) {
        ArrayList<MPMSongModel> loadAudio = loadAudio();
        ArrayList<MPMSongModel> arrayList = new ArrayList<>();
        for (int i2 = 0; i2 < i; i2++) {
            arrayList.add(loadAudio.get(i2));
        }
        arrayList.add(mPMSongModel);
        while (i < loadAudio.size()) {
            arrayList.add(loadAudio.get(i));
            i++;
        }
        storeAudio(arrayList);
    }
}
