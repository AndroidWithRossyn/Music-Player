package com.demo.music.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.palette.graphics.Palette;

import com.demo.music.R;

import com.demo.music.activity.MPMMainActivity;
import com.demo.music.model.MPMSongModel;
import com.demo.music.utils.PlaybackStatus;
import com.demo.music.utils.StorageUtil;
import com.demo.music.utils.Util;

import java.io.IOException;
import java.util.ArrayList;


public class MPMMediaPlayerService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener, AudioManager.OnAudioFocusChangeListener {
    private static final int NOTIFICATION_ID = 101;
    private static MediaPlayer mediaPlayer;
    private MPMSongModel activeAudio;
    private ArrayList<MPMSongModel> audioList;
    private AudioManager audioManager;
    private Bitmap bitmap;
    Context context;
    private ComponentName mMediaButtonReceiverComponent;
    NotificationManager manager;
    private MediaSessionCompat mediaSession;
    
    private PhoneStateListener phoneStateListener;
    private int radioIndex;
    private int resumePosition;
    private SharedPreferences sharedPreferences;
    private int songPosition;
    private TelephonyManager telephonyManager;
    private MediaControllerCompat.TransportControls transportControls;
    private boolean wasPlaying = false;
    private boolean isLastReached = false;
    private final IBinder iBinder = new LocalBinder();
    private int audioIndex = -1;
    private boolean ongoingCall = false;
    private String CHANNEL_ID = "1";
    private BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() { 
        @Override 
        public void onReceive(Context context, Intent intent) {
            MPMMediaPlayerService.this.pauseMedia();
            MPMMediaPlayerService.this.buildNotification(PlaybackStatus.PAUSED);
        }
    };
    private BroadcastReceiver playNewAudio = new BroadcastReceiver() { 
        @Override 
        public void onReceive(Context context, Intent intent) {
            Log.e("received", "here");
            StorageUtil storageUtil = new StorageUtil(MPMMediaPlayerService.this.getApplicationContext());
            MPMMediaPlayerService.this.songPosition = intent.getIntExtra(Util.SONG_POSITION, -1);
            MPMMediaPlayerService.this.audioIndex = storageUtil.loadAudioIndex();
            MPMMediaPlayerService.this.audioList = storageUtil.loadAudio();
            MPMMediaPlayerService.this.songPosition = 0;
            if (MPMMediaPlayerService.this.audioIndex == -1 || MPMMediaPlayerService.this.audioIndex >= MPMMediaPlayerService.this.audioList.size()) {
                MPMMediaPlayerService.this.stopSelf();
            } else {
                MPMMediaPlayerService mPMMediaPlayerService = MPMMediaPlayerService.this;
                mPMMediaPlayerService.activeAudio = (MPMSongModel) mPMMediaPlayerService.audioList.get(MPMMediaPlayerService.this.audioIndex);
            }
            MPMMediaPlayerService.this.stopMedia();
            if (MPMMediaPlayerService.mediaPlayer != null) {
                MPMMediaPlayerService.mediaPlayer.reset();
            }
            MPMMediaPlayerService.this.initMediaPlayer();
            MPMMediaPlayerService.this.buildNotification(PlaybackStatus.PLAYING);
            MPMMediaPlayerService.this.isLastReached = false;
        }
    };

    @Override 
    public void onBufferingUpdate(MediaPlayer mediaPlayer2, int i) {
    }

    @Override 
    public boolean onInfo(MediaPlayer mediaPlayer2, int i, int i2) {
        return false;
    }

    @Override 
    public void onSeekComplete(MediaPlayer mediaPlayer2) {
    }

    @Override 
    public IBinder onBind(Intent intent) {
        return this.iBinder;
    }

    @Override 
    public void onCreate() {
        super.onCreate();
        this.context = getApplicationContext();
        callStateListener();
        registerBecomingNoisyReceiver();
        createNotificationChannel();
        this.sharedPreferences = this.context.getSharedPreferences(Util.MySharedPref, 0);
        this.audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        register_playNewAudio();
    }

    private void createNotificationChannel() {
        if (Util.isOreo()) {
            String string = this.context.getString(R.string.app_name);
            this.manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            this.manager.createNotificationChannel(new NotificationChannel(this.CHANNEL_ID, string, NotificationManager.IMPORTANCE_LOW));
        }
    }

    @Override 
    public int onStartCommand(Intent intent, int i, int i2) {
        Log.e("onStartCommand", ""+intent.getAction() + "got Started");
        String action = intent.getAction();
        int i3 = 0;
        if (action == null || !action.equals("JUST_START")) {
            StorageUtil storageUtil = new StorageUtil(getApplicationContext());
            if (intent.getAction() == null) {
                this.songPosition = -1;
            } else if (intent.getAction().equals(Util.ACTION_NEXT) || intent.getAction().equals(Util.ACTION_PREVIOUS)) {
                this.songPosition = -1;
            } else {
                this.songPosition = intent.getIntExtra(Util.SONG_POSITION, -1);
            }
            if (!getSharedPreferences(Util.MySharedPref, 0).getBoolean(Util.SHUFFLE, false)) {
                this.audioList = storageUtil.loadAudio();
                this.audioIndex = storageUtil.loadAudioIndex();
            } else {
                this.audioList = storageUtil.getSuffledSongs();
                MPMSongModel current = storageUtil.getCurrent();
                while (true) {
                    if (i3 >= this.audioList.size()) {
                        break;
                    } else if (current.getData().equals(this.audioList.get(i3).getData())) {
                        this.audioIndex = i3;
                        break;
                    } else {
                        i3++;
                    }
                }
            }
            Log.e("onPLay123", this.audioList.size() + "");
            Log.e("onPLay", "got Started");
            int i4 = this.audioIndex;
            if (i4 == -1 || i4 >= this.audioList.size()) {
                stopSelf();
            } else {
                this.activeAudio = this.audioList.get(this.audioIndex);
            }
            if (!requestAudioFocus()) {
                stopSelf();
            }

            try {
                initMediaSession();
                initMediaPlayer();
            } catch (RemoteException e) {
                Log.e("error123", e.getMessage() + "");
                e.printStackTrace();
                stopSelf();
            }
            buildNotification(PlaybackStatus.PLAYING);

            handleIncomingActions(intent);
        }
        return super.onStartCommand(intent, i, i2);
    }

    @Override 
    public boolean onUnbind(Intent intent) {
        this.mediaSession.release();
        removeNotification();
        return super.onUnbind(intent);
    }

    @Override 
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            stopMedia();
            mediaPlayer.release();
        }
        mediaPlayer = null;
        removeAudioFocus();
        PhoneStateListener phoneStateListener = this.phoneStateListener;
        if (phoneStateListener != null) {
            this.telephonyManager.listen(phoneStateListener, 0);
        }
        removeNotification();
        unregisterReceiver(this.becomingNoisyReceiver);
        unregisterReceiver(this.playNewAudio);
    }

    public void setDatas(ArrayList<MPMSongModel> arrayList, int i) {
        if (this.audioList.isEmpty()) {
            this.audioList = new ArrayList<>(arrayList);
        } else {
            this.audioList.clear();
            this.audioList.addAll(arrayList);
        }
        this.audioIndex = i;
    }

    
    public class LocalBinder extends Binder {
        public LocalBinder() {
        }

        public MPMMediaPlayerService getService() {
            return MPMMediaPlayerService.this;
        }
    }

    public static boolean isMediaPlaying() {
        MediaPlayer mediaPlayer2 = mediaPlayer;
        if (mediaPlayer2 != null) {
            return mediaPlayer2.isPlaying();
        }
        return false;
    }

    public boolean isLastReached() {
        return this.isLastReached;
    }

    public boolean isDataLoaded() {
        ArrayList<MPMSongModel> arrayList = this.audioList;
        if (arrayList != null) {
            return !arrayList.isEmpty();
        }
        return false;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public boolean isPlaying() {
        MediaPlayer mediaPlayer2 = mediaPlayer;
        if (mediaPlayer2 != null) {
            return mediaPlayer2.isPlaying();
        }
        return false;
    }

    public void setSongPosition(int i) {
        this.songPosition = i;
        Log.e("positionChange", i + "");
    }

    @Override 
    public void onCompletion(MediaPlayer mediaPlayer2) {
        stopMedia();
        String string = this.sharedPreferences.getString(Util.REPEAT, Util.REPEAT_NONE);
        if (this.audioIndex == this.audioList.size() - 1) {
            if (string.equals(Util.REPEAT_ONE)) {
                if (this.audioList.size() != 1) {
                    this.audioIndex--;
                    StorageUtil storageUtil = new StorageUtil(getApplicationContext());
                    storageUtil.storeAudioIndex(this.audioIndex);
                    storageUtil.storeCurrent(this.audioList.get(this.audioIndex));
                }
                Intent intent = new Intent(this, MPMMediaPlayerService.class);
                intent.setAction(Util.ACTION_NEXT);
                startService(intent);
            } else if (string.equals(Util.REPEAT_ALL)) {
                Intent intent2 = new Intent(this, MPMMediaPlayerService.class);
                intent2.setAction(Util.ACTION_NEXT);
                startService(intent2);
            } else {
                this.isLastReached = true;
                Log.e("pause", "called");
                mediaPlayer.seekTo(0);
                Intent intent3 = new Intent(this, MPMMediaPlayerService.class);
                intent3.setAction(Util.ACTION_PAUSE);
                startService(intent3);
            }
        } else if (string.equals(Util.REPEAT_ONE)) {
            if (this.audioList.size() != 1) {
                int i = this.audioIndex;
                if (i == 0) {
                    i = this.audioList.size();
                }
                this.audioIndex = i - 1;
                StorageUtil storageUtil2 = new StorageUtil(getApplicationContext());
                storageUtil2.storeAudioIndex(this.audioIndex);
                storageUtil2.storeCurrent(this.audioList.get(this.audioIndex));
            }
            Intent intent4 = new Intent(this, MPMMediaPlayerService.class);
            intent4.setAction(Util.ACTION_NEXT);
            startService(intent4);
        } else {
            Intent intent5 = new Intent(this, MPMMediaPlayerService.class);
            intent5.setAction(Util.ACTION_NEXT);
            startService(intent5);
        }
        removeNotification();
        stopSelf();
    }

    @Override 
    public boolean onError(MediaPlayer mediaPlayer2, int i, int i2) {
        if (i == 1) {
            Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + i2);
            return false;
        } else if (i == 100) {
            Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + i2);
            return false;
        } else if (i != 200) {
            return false;
        } else {
            Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + i2);
            return false;
        }
    }

    @Override 
    public void onPrepared(MediaPlayer mediaPlayer2) {
        playMedia();
        updateMediaSession(true);
        Intent intent = new Intent();
        intent.setAction(Util.ACTION_SONG_CHANGE);
        intent.putExtra(Util.CURRENT_SONG, this.activeAudio);
        intent.putExtra(Util.CURRENT_INDEX, this.audioIndex);
        sendBroadcast(intent);
        Intent intent2 = new Intent();
        intent2.setAction(Util.SONG_ACTION);
        intent2.putExtra(Util.IS_PLAYING, isMediaPlaying());
        sendBroadcast(intent2);
    }

    @Override 
    public void onAudioFocusChange(int i) {
        if (i != 1) {
            switch (i) {
                case -3:
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.setVolume(0.1f, 0.1f);
                        this.wasPlaying = true;
                        return;
                    }
                    return;
                case -2:
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        if (this.isLastReached) {
                            this.resumePosition = 0;
                        } else {
                            this.resumePosition = mediaPlayer.getCurrentPosition();
                        }
                        this.wasPlaying = true;
                        buildNotification(PlaybackStatus.PAUSED);
                        return;
                    }
                    return;
                case -1:
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        this.wasPlaying = true;
                        if (this.isLastReached) {
                            this.resumePosition = 0;
                        } else {
                            this.resumePosition = mediaPlayer.getCurrentPosition();
                        }
                        buildNotification(PlaybackStatus.PAUSED);
                        return;
                    }
                    return;
                default:
                    return;
            }
        } else if (this.wasPlaying) {
            MediaPlayer mediaPlayer2 = mediaPlayer;
            if (mediaPlayer2 == null) {
                initMediaPlayer();
            } else if (!mediaPlayer2.isPlaying()) {
                int i2 = this.songPosition;
                if (i2 != -1) {
                    mediaPlayer.seekTo(i2);
                }
                mediaPlayer.start();
            }
            mediaPlayer.setVolume(1.0f, 1.0f);
            this.wasPlaying = false;
            buildNotification(PlaybackStatus.PLAYING);
        }
    }

    private boolean requestAudioFocus() {
        this.audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        return this.audioManager.requestAudioFocus(this, 3, 1) == 1;
    }

    private boolean removeAudioFocus() {
        try {
            return 1 == this.audioManager.abandonAudioFocus(this);
        } catch (Exception unused) {
            return true;
        }
    }

    
    public void initMediaPlayer() {
        Log.e("inited", "here");
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(this.activeAudio.getData());
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.e("aashit error", e.getMessage() + "");
            stopSelf();
        }
    }

    public void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            int i = this.songPosition;
            if (i != -1) {
                mediaPlayer.seekTo(i);
            }
            mediaPlayer.start();
        }
    }

    
    public void stopMedia() {
        MediaPlayer mediaPlayer2 = mediaPlayer;
        if (mediaPlayer2 != null && mediaPlayer2.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    public void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            Log.e("pause", "called 1");
            if (this.isLastReached) {
                this.resumePosition = 0;
            } else {
                this.resumePosition = mediaPlayer.getCurrentPosition();
            }
        }
    }

    
    public void resumeMedia() {
        int i = this.songPosition;
        if (i != -1) {
            mediaPlayer.seekTo(i);
        } else {
            mediaPlayer.seekTo(this.resumePosition);
        }
        mediaPlayer.start();
    }

    
    public void skipToNext() {
        Log.e("next", "here");
        if (this.audioIndex == this.audioList.size() - 1) {
            Log.e("next", "here 1");
            this.audioIndex = 0;
            this.activeAudio = this.audioList.get(this.audioIndex);
        } else {
            Log.e("next", "here 2");
            ArrayList<MPMSongModel> arrayList = this.audioList;
            int i = this.audioIndex + 1;
            this.audioIndex = i;
            this.activeAudio = arrayList.get(i);
        }
        new StorageUtil(getApplicationContext()).storeAudioIndex(this.audioIndex);
        stopMedia();
        mediaPlayer.reset();
        initMediaPlayer();
    }

    
    public void skipToPrevious() {
        int i = this.audioIndex;
        if (i == 0) {
            this.audioIndex = this.audioList.size() - 1;
            this.activeAudio = this.audioList.get(this.audioIndex);
        } else {
            ArrayList<MPMSongModel> arrayList = this.audioList;
            int i2 = i - 1;
            this.audioIndex = i2;
            this.activeAudio = arrayList.get(i2);
        }
        new StorageUtil(getApplicationContext()).storeAudioIndex(this.audioIndex);
        stopMedia();
        mediaPlayer.reset();
        initMediaPlayer();
    }

    private void registerBecomingNoisyReceiver() {
        registerReceiver(this.becomingNoisyReceiver, new IntentFilter("android.media.AUDIO_BECOMING_NOISY"));
    }

    private void callStateListener() {
        this.telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        this.phoneStateListener = new PhoneStateListener() { 
            @Override 
            public void onCallStateChanged(int i, String str) {
                switch (i) {
                    case 0:
                        if (MPMMediaPlayerService.mediaPlayer != null && MPMMediaPlayerService.this.ongoingCall) {
                            Log.e(NotificationCompat.CATEGORY_CALL, "play");
                            MPMMediaPlayerService.this.ongoingCall = false;
                            MPMMediaPlayerService.this.resumeMedia();
                            MPMMediaPlayerService.this.buildNotification(PlaybackStatus.PLAYING);
                            return;
                        }
                        return;
                    case 1:
                    case 2:
                        if (MPMMediaPlayerService.mediaPlayer != null && MPMMediaPlayerService.mediaPlayer.isPlaying()) {
                            Log.e(NotificationCompat.CATEGORY_CALL, "pause");
                            MPMMediaPlayerService.this.pauseMedia();
                            MPMMediaPlayerService.this.ongoingCall = true;
                            return;
                        }
                        return;
                    default:
                        return;
                }
            }
        };
        this.telephonyManager.listen(this.phoneStateListener, 32);
    }

    @SuppressLint({"ServiceCast"})
    private void initMediaSession() throws RemoteException {


            this.mediaSession = new MediaSessionCompat(getApplicationContext(), "AudioPlayer");
            this.transportControls = this.mediaSession.getController().getTransportControls();
            this.mediaSession.setActive(true);
            this.mediaSession.setFlags(3);
            updateMetaData(false);
            this.mediaSession.setCallback(new MediaSessionCompat.Callback() { 
                @Override 
                public void onPlay() {
                    super.onPlay();
                    Log.e("pause", "complete 2");
                    MPMMediaPlayerService.this.resumeMedia();
                    MPMMediaPlayerService.this.updateMediaSession(true);
                    MPMMediaPlayerService.this.buildNotification(PlaybackStatus.PLAYING);
                }

                @Override 
                public void onPause() {
                    super.onPause();
                    MPMMediaPlayerService.this.updateMediaSession(false);
                    MPMMediaPlayerService.this.pauseMedia();
                    MPMMediaPlayerService.this.buildNotification(PlaybackStatus.PAUSED);
                }

                @Override 
                public void onSkipToNext() {
                    super.onSkipToNext();
                    MPMMediaPlayerService.this.skipToNext();
                    MPMMediaPlayerService.this.buildNotification(PlaybackStatus.PLAYING);
                }

                @Override 
                public void onSkipToPrevious() {
                    super.onSkipToPrevious();
                    MPMMediaPlayerService.this.skipToPrevious();
                    MPMMediaPlayerService.this.buildNotification(PlaybackStatus.PLAYING);
                }

                @Override 
                public void onStop() {
                    super.onStop();
                    MPMMediaPlayerService.this.removeNotification();
                    MPMMediaPlayerService.this.stopSelf();
                }

                @Override 
                public void onSeekTo(long j) {
                    super.onSeekTo(j);
                    MPMMediaPlayerService.mediaPlayer.seekTo((int) j);
                    MPMMediaPlayerService mPMMediaPlayerService = MPMMediaPlayerService.this;
                    mPMMediaPlayerService.updateMediaSession(mPMMediaPlayerService.isPlaying());
                }
            });

    }

    private void updateMetaData(boolean z) {
        byte[] bArr;
        Bitmap bitmap = null;
        try {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(this.activeAudio.getData());
            bArr = mediaMetadataRetriever.getEmbeddedPicture();
        } catch (Exception e) {
            e.printStackTrace();
            bArr = null;
        }
        if (bArr != null) {
            bitmap = BitmapFactory.decodeByteArray(bArr, 0, bArr.length);
        } else if (this.activeAudio != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), ContentUris.withAppendedId(Util.sArtworkUri, Long.parseLong(this.activeAudio.getAlbumid())));
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        }
        Log.e("isPlaying", z + "");
        if (this.activeAudio != null) {
            Log.e("in update", "helloo");
            this.mediaSession.setMetadata(new MediaMetadataCompat.Builder().putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap).putBitmap(MediaMetadataCompat.METADATA_KEY_ART, bitmap).putString(MediaMetadataCompat.METADATA_KEY_ARTIST, this.activeAudio.getArtist()).putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, this.activeAudio.getArtist()).putString(MediaMetadataCompat.METADATA_KEY_ALBUM, this.activeAudio.getAlbum()).putString(MediaMetadataCompat.METADATA_KEY_TITLE, this.activeAudio.getTitle()).putLong(MediaMetadataCompat.METADATA_KEY_DURATION, Long.parseLong(this.activeAudio.getDuration())).build());
        }
    }

    
    @SuppressLint("WrongConstant")
    public void updateMediaSession(boolean z) {
        MediaSessionCompat mediaSessionCompat = this.mediaSession;
        PlaybackStateCompat.Builder builder = new PlaybackStateCompat.Builder();
        int i = z ? 3 : 2;
        MediaPlayer mediaPlayer2 = mediaPlayer;
        mediaSessionCompat.setPlaybackState(builder.setState(i, mediaPlayer2 != null ? mediaPlayer2.getCurrentPosition() : -1L, 1.0f).setActions(822L).build());
    }

    
    public void buildNotification(PlaybackStatus playbackStatus) {
        PendingIntent pendingIntent;
        int i;
        String str;
        byte[] bArr;
        Log.e("notification", "here");
        Bitmap bitmap = null;
        if (playbackStatus == PlaybackStatus.PLAYING) {
            pendingIntent = playbackAction(1);
            i = R.drawable.pause1;
        } else if (playbackStatus == PlaybackStatus.PAUSED) {
            pendingIntent = playbackAction(0);
            i = R.drawable.play1;
        } else {
            pendingIntent = null;
            i = R.drawable.pause1;
        }
        Intent intent = new Intent();
        intent.setAction(Util.SONG_ACTION);
        intent.putExtra(Util.IS_PLAYING, playbackStatus == PlaybackStatus.PLAYING);
        sendBroadcast(intent);
        boolean z = playbackStatus == PlaybackStatus.PLAYING;
        String album = this.activeAudio.getAlbum();
        String artist = this.activeAudio.getArtist();
        if (TextUtils.isEmpty(album)) {
            str = artist;
        } else {
            str = artist + " - " + album;
        }
        PendingIntent activity = PendingIntent.getActivity(this, 0, new Intent(this, MPMMainActivity.class), PendingIntent.FLAG_IMMUTABLE);
        try {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(this.activeAudio.getData());
            bArr = mediaMetadataRetriever.getEmbeddedPicture();
        } catch (Exception e) {
            e.printStackTrace();
            bArr = null;
        }
        if (bArr != null) {
            bitmap = BitmapFactory.decodeByteArray(bArr, 0, bArr.length);
        } else if (this.activeAudio != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), ContentUris.withAppendedId(Util.sArtworkUri, Long.parseLong(this.activeAudio.getAlbumid())));
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        showNotification(bitmap == null ? BitmapFactory.decodeResource(getResources(), R.drawable.logo) : bitmap, activity, str, i, pendingIntent, z);
    }

    private void showNotification(Bitmap bitmap, PendingIntent pendingIntent, String str, int i, PendingIntent pendingIntent2, boolean z) {
        NotificationCompat.Builder addAction = new NotificationCompat.Builder(this, this.CHANNEL_ID).setSmallIcon(R.drawable.logo).setLargeIcon(bitmap).setContentIntent(pendingIntent).setContentTitle(this.activeAudio.getTitle()).setContentText(str).addAction(R.drawable.prev, "", playbackAction(3)).addAction(i, "", pendingIntent2).addAction(R.drawable.next, "", playbackAction(2));
        if (Util.isJellyBeanMR1()) {
            addAction.setShowWhen(false);
        }
        if (Util.isLollipop()) {
            addAction.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            addAction.setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(this.mediaSession.getSessionToken()).setShowActionsInCompactView(0, 1, 2, 3));
        }
        if (bitmap != null && Util.isLollipop()) {
            addAction.setColor(Palette.from(bitmap).generate().getVibrantColor(Color.parseColor("#403f4d")));
        }
        if (Util.isOreo()) {
            addAction.setColorized(true);
        }
        Notification build = addAction.build();
        updateMetaData(z);
        startForeground(1, build);
        if (!z) {
            stopForeground(false);
        }
    }

    private PendingIntent playbackAction(int i) {
        Intent intent = new Intent(this, MPMMediaPlayerService.class);
        switch (i) {
            case 0:
                intent.setAction(Util.ACTION_PLAY);
                return PendingIntent.getService(this, i, intent,  PendingIntent.FLAG_IMMUTABLE);
            case 1:
                intent.setAction(Util.ACTION_PAUSE);
                return PendingIntent.getService(this, i, intent, PendingIntent.FLAG_IMMUTABLE);
            case 2:
                intent.setAction(Util.ACTION_NEXT);
                return PendingIntent.getService(this, i, intent, PendingIntent.FLAG_IMMUTABLE);
            case 3:
                intent.setAction(Util.ACTION_PREVIOUS);
                return PendingIntent.getService(this, i, intent, PendingIntent.FLAG_IMMUTABLE);
            default:
                return null;
        }
    }

    
    public void removeNotification() {
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(101);
    }

    private void handleIncomingActions(Intent intent) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            if (action.equalsIgnoreCase(Util.ACTION_PLAY)) {
                this.transportControls.play();
            } else if (action.equalsIgnoreCase(Util.ACTION_PAUSE)) {
                this.transportControls.pause();
            } else if (action.equalsIgnoreCase(Util.ACTION_NEXT)) {
                Log.e("here", "hello");
                this.transportControls.skipToNext();
            } else if (action.equalsIgnoreCase(Util.ACTION_PREVIOUS)) {
                this.transportControls.skipToPrevious();
            } else if (action.equalsIgnoreCase(Util.ACTION_STOP)) {
                this.transportControls.stop();
            }
        }
    }

    private void register_playNewAudio() {
        registerReceiver(this.playNewAudio, new IntentFilter(MPMMainActivity.Broadcast_PLAY_NEW_AUDIO));
    }
}
