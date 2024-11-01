package com.demo.music.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.demo.music.model.MPMSongModel;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class Util {
    public static final String ACTION_NEXT = "comm.mucply.aladioply.ACTION_NEXT";
    public static final String ACTION_PAUSE = "comm.mucply.aladioply.ACTION_PAUSE";
    public static final String ACTION_PLAY = "comm.mucply.aladioply.ACTION_PLAY";
    public static final String ACTION_PREVIOUS = "comm.mucply.aladioply.ACTION_PREVIOUS";
    public static final String ACTION_SONG_CHANGE = "comm.mucply.aladioplyACTION_SONG_CHANGE";
    public static final String ACTION_STOP = "comm.mucply.aladioply.ACTION_STOP";
    public static final String CURRENT_INDEX = "comm.mucply.aladioplyCURRENT_INDEX";
    public static final String CURRENT_SONG = "comm.mucply.aladioplyCURRENT_SONG";
    public static final String IS_PLAYING = "comm.mucply.aladioplyIS_PLAYING";
    public static final String MySharedPref = "MySharedPref";
    public static final String REPEAT = "comm.mucply.aladioplyREPEAT";
    public static final String REPEAT_ALL = "comm.mucply.aladioplyREPEAT_ALL";
    public static final String REPEAT_NONE = "comm.mucply.aladioplyREPEAT_NONE";
    public static final String REPEAT_ONE = "comm.mucply.aladioplyREPEAT_ONE";
    public static final String SHUFFLE = "comm.mucply.aladioplySHUFFLE";
    public static final boolean SHUFFLE_OFF = false;
    public static final boolean SHUFFLE_ON = true;
    public static final String SONG_ACTION = "comm.mucply.aladioplySONG_ACTION";
    public static final String SONG_POSITION = "comm.mucply.aladioplySONG_POSITION";
    public static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

    public static int dpToPx(Context context, int i) {
        context.getResources().getDisplayMetrics();
        return (int) (i * context.getResources().getDisplayMetrics().density);
    }

    public static String getStringTime(long j) {
        if (j >= 3600000) {
            return String.format("%02d:%02d:%02d", Long.valueOf(TimeUnit.MILLISECONDS.toHours(j)), Long.valueOf(TimeUnit.MILLISECONDS.toMinutes(j) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(j))), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(j) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(j))));
        }
        return String.format("%02d:%02d", Long.valueOf(TimeUnit.MILLISECONDS.toMinutes(j)), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(j) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(j))));
    }

    public static void shareMultipleSongs(Context context, ArrayList<MPMSongModel> arrayList) {
        try {
            ArrayList<Uri> arrayList2 = new ArrayList<>();
            for (int i = 0; i < arrayList.size(); i++) {
                arrayList2.add(FileProvider.getUriForFile(context, context.getPackageName() + ".provider", new File(arrayList.get(i).getData())));
            }
            Intent intent = new Intent("android.intent.action.SEND_MULTIPLE");
            intent.setType("audio/*");
            intent.putParcelableArrayListExtra("android.intent.extra.STREAM", arrayList2);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(intent, "Share Song(s)"));
        } catch (Exception e) {
            Log.e(NotificationCompat.CATEGORY_MESSAGE, e.getMessage());
            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
    }

    public static void shareSong(Context context, MPMSongModel mPMSongModel) {
        try {
            Log.e("actual path", new File(mPMSongModel.getData()).getAbsolutePath());
            Uri uriForFile = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", new File(mPMSongModel.getData()));
            Log.e("uri", uriForFile.toString());
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("audio/*");
            intent.putExtra("android.intent.extra.STREAM", uriForFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(intent, "Share Song(s)"));
        } catch (Exception e) {
            Log.e(NotificationCompat.CATEGORY_MESSAGE, e.getMessage());
            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isOreo() {
        return Build.VERSION.SDK_INT >= 26;
    }

    public static boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= 23;
    }

    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= 21;
    }

    public static boolean isJellyBeanMR2() {
        return Build.VERSION.SDK_INT >= 18;
    }

    public static boolean isJellyBean() {
        return Build.VERSION.SDK_INT >= 16;
    }

    public static boolean isJellyBeanMR1() {
        return Build.VERSION.SDK_INT >= 17;
    }
}
