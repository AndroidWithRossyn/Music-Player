package com.demo.music.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.PresetReverb;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import com.db.chart.model.LineSet;
import com.demo.music.R;

import com.demo.music.fragments.MPMEqualizerFragment;
import com.demo.music.model.MPMEqualizerModel;
import java.util.ArrayList;


public class MPMTestWidgetTest extends Service {
    public static final String ACTION_PAUSE = "com.wfreffect.loud.bassmusic.ACTION_PAUSE";
    public static final String ACTION_PLAY = "com.wfreffect.loud.bassmusic.ACTION_PLAY";
    public static final String ACTION_STOP = "com.wfreffect.loud.bassmusic.ACTION_STOP";
    public static final String CLOSE_EQUALIZER = "com.wfreffect.loud.bassmusic.CLOSE_EQUALIZER";
    private static final String DECREASE_BASS_NOTIFICATION = "com.wfreffect.loud.bassmusic.DECREASE_BASS_NOTIFICATION";
    private static final String DECREASE_REVERB_NOTIFICATION = "com.wfreffect.loud.bassmusic.DECREASE_REVERB_NOTIFICATION";
    private static final String INCREASE_BASS_NOTIFICATION = "com.wfreffect.loud.bassmusic.INCREASE_BASS_NOTIFICATION";
    private static final String INCREASE_REVERB_NOTIFICATION = "com.wfreffect.loud.bassmusic.INCREASE_REVERB_NOTIFICATION";
    public static final String STOP_EQUALIZER = "com.wfreffect.loud.bassmusic.STOP_EQUALIZER";
    public static AudioManager audioManager;
    public static BassBoost bassBoost;
    public static BassBoost.Settings bassBoostSetting;
    public static BassBoost.Settings bassBoostSettingTemp;
    public static int bassPROGRESS;
    public static short bassStrength;
    static RemoteViews contentView;
    public static Context context;
    public static LineSet dataset;
    public static Equalizer mEqualizer;
    public static Notification notification;
    public static NotificationCompat.Builder notificationBuilder;
    public static NotificationManager notificationManager;
    public static float[] points;
    public static int presetPos;
    public static PresetReverb presetReverb;
    public static int reverbPROGRESS;
    public static short reverbPreset;
    public static int y;
    short count = 200;
    private final IBinder iBinder = new LocalBinder();
    public static SeekBar[] seekBarFinal = new SeekBar[5];
    public static boolean isEqualizerEnabled = false;
    public static boolean isEqualizerReloaded = true;
    public static int[] seekbarpos = new int[5];
    public static MPMEqualizerModel equalizerModel = new MPMEqualizerModel();
    public static double ratio = 1.0d;
    public static String effect_name = "Custom";
    public static int effectID = 1;
    public static int effectID_forREC = 0;
    public static String effectNAME_forREC = "Custom";
    private static CharSequence string_progress_bass = "0%";
    private static CharSequence string_progress_reverb = "0%";

    public MPMTestWidgetTest() {
        try {
            mEqualizer = new Equalizer(Integer.MAX_VALUE, 0);
            bassBoost = new BassBoost(Integer.MAX_VALUE, 0);
            bassBoost.setStrength(this.count);
            presetReverb = new PresetReverb(Integer.MAX_VALUE, 0);
            presetReverb.setPreset((short) 0);
            bassBoostSettingTemp = bassBoost.getProperties();
            bassBoostSetting = new BassBoost.Settings(bassBoostSettingTemp.toString());
            bassBoostSetting.strength = (short) 52;
            bassBoost.setProperties(bassBoostSetting);
        } catch (Exception unused) {
        }
    }

    public MPMTestWidgetTest(Context context2) {
        try {
            context = context2;
            mEqualizer = new Equalizer(Integer.MAX_VALUE, 0);
            bassBoost = new BassBoost(Integer.MAX_VALUE, 0);
            presetReverb = new PresetReverb(Integer.MAX_VALUE, 0);
            presetReverb.setPreset((short) 0);
            bassBoostSettingTemp = bassBoost.getProperties();
            bassBoostSetting = new BassBoost.Settings(bassBoostSettingTemp.toString());
            bassBoostSetting.strength = (short) 52;
            bassBoost.setProperties(bassBoostSetting);
        } catch (Exception unused) {
        }
    }

    public static void reverbChanged(int i) {
        reverbPreset = (short) ((i * 6) / 19);
        equalizerModel.setReverbPreset(reverbPreset);
        try {
            presetReverb.setPreset(reverbPreset);
        } catch (Exception e) {
            e.printStackTrace();
        }
        y = i;
    }

    public static void bassBoostChangE(int i) {
        bassStrength = (short) (i * 52.63158f);
        try {
            Log.d("MPMEqualizer", "STRENTGH  " + ((int) bassStrength));
            bassBoost.setStrength(bassStrength);
            equalizerModel.setBassStrength(bassStrength);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateSeekBarsValues(int i, Context context2) {
        effectID = i;
        if (i == 2) {
            effect_name = "Classical";
        }
        if (i == 9) {
            effect_name = "Pop";
        }
        if (i == 7) {
            effect_name = "Hip Hop";
        }
        if (i == 5) {
            effect_name = "Folk";
        }
        if (i == 3) {
            effect_name = "Dance";
        }
        if (i == 1) {
            effect_name = "Custom";
        }
        if (i != 1) {
            try {
                mEqualizer.usePreset((short) (i - 1));
                presetPos = i;
                short s = mEqualizer.getBandLevelRange()[0];
                for (short s2 = 0; s2 < 5; s2 = (short) (s2 + 1)) {
                    seekBarFinal[s2].setProgress(mEqualizer.getBandLevel(s2) - s);
                    seekbarpos[s2] = mEqualizer.getBandLevel(s2);
                    equalizerModel.getSeekbarpos()[s2] = mEqualizer.getBandLevel(s2);
                }
            } catch (Exception e) {
                Toast.makeText(context2, "Error while updating MPMEqualizer", Toast.LENGTH_SHORT).show();
                Log.d("TEST_CRASH_LOG_403", "Exception---> " + e);
            }
        }
        equalizerModel.setPresetPos(i);
        equalizeSound(context2);
    }

    public static void equalizeSound(Context context2) {
        ArrayList arrayList = new ArrayList();
        new ArrayAdapter(context2, (int) R.layout.spinner_item, arrayList).setDropDownViewResource(17367049);
        arrayList.add("Custom");
        for (short s = 0; s < mEqualizer.getNumberOfPresets(); s = (short) (s + 1)) {
            arrayList.add(mEqualizer.getPresetName(s));
        }
    }

    @Override 
    public void onDestroy() {
        super.onDestroy();
        Log.e("MPMEqualizer", "destroyed");
    }

    @Override 
    @RequiresApi(api = 26)
    public int onStartCommand(Intent intent, int i, int i2) {
        context = getApplicationContext();
        Log.e("MPMEqualizer", "started");
        try {
            String action = intent.getAction();
            Log.d("Notif_Intent_Action", "" + action);
            if (action.equals(STOP_EQUALIZER)) {
                mEqualizer.setEnabled(!mEqualizer.getEnabled());
                isEqualizerEnabled = mEqualizer.getEnabled();
                presetReverb.setEnabled(isEqualizerEnabled);
                bassBoost.setEnabled(isEqualizerEnabled);
                mEqualizer.setEnabled(isEqualizerEnabled);
            }
            if (action.equals(CLOSE_EQUALIZER)) {
                mEqualizer.setEnabled(false);
                isEqualizerEnabled = false;
                presetReverb.setEnabled(isEqualizerEnabled);
                bassBoost.setEnabled(isEqualizerEnabled);
                mEqualizer.setEnabled(isEqualizerEnabled);
                if (isEqualizerEnabled) {
                } else {
                }
                notificationManager.cancelAll();
            }
            if (action.equals(INCREASE_BASS_NOTIFICATION) && 19 > bassPROGRESS) {
                bassPROGRESS++;
                Log.d("VOlumeEqualizer", "sfdsfcs____" + bassPROGRESS);
                bassBoostChangE(bassPROGRESS);
            }
            if (action.equals(DECREASE_BASS_NOTIFICATION) && 1 < bassPROGRESS) {
                bassPROGRESS--;
                Log.d("VOlumeEqualizer", "sfdsfcs____" + bassPROGRESS);
                bassBoostChangE(bassPROGRESS);
            }
            if (action.equals(DECREASE_REVERB_NOTIFICATION)) {
                Log.d("VOlumeEqualizer", "sfdsfcs____" + reverbPROGRESS);
                if (1 < reverbPROGRESS) {
                    reverbPROGRESS--;
                    Log.d("VOlumeEqualizer", "sfdsfcs____" + reverbPROGRESS);
                    bassBoostChangE(reverbPROGRESS);
                }
            }
            if (action.equals(INCREASE_REVERB_NOTIFICATION)) {
                Log.d("VOlumeEqualizer", "sfdsfcs____" + reverbPROGRESS);
                if (19 > reverbPROGRESS) {
                    reverbPROGRESS++;
                    Log.d("VOlumeEqualizer", "sfdsfcs____" + reverbPROGRESS);
                    bassBoostChangE(reverbPROGRESS);
                }
            }
        } catch (Exception unused) {
        }
        if (MPMEqualizerFragment.bassController != null) {
            MPMEqualizerFragment.bassController.setProgress(bassPROGRESS);
            Log.d("LOG_FROM_NOTIFICATION", "" + bassPROGRESS);
            MPMEqualizerFragment.bassController.invalidate();
        }
        if (MPMEqualizerFragment.reverbController != null) {
            MPMEqualizerFragment.reverbController.setProgress(reverbPROGRESS);
            Log.d("LOG_FROM_NOTIFICATION", "" + reverbPROGRESS);
            Log.d("", "");
            MPMEqualizerFragment.reverbController.invalidate();
        }
        string_progress_reverb = ((reverbPROGRESS * 100) / 19) + "%";
        string_progress_bass = ((bassPROGRESS * 100) / 19) + "%";
        return super.onStartCommand(intent, START_FLAG_REDELIVERY, i2);
    }

    @Override 
    public IBinder onBind(Intent intent) {
        return this.iBinder;
    }

    
    public class LocalBinder extends Binder {
        public LocalBinder() {
        }

        public MPMTestWidgetTest getService() {
            return MPMTestWidgetTest.this;
        }
    }
}
