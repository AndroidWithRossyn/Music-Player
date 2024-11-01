package com.demo.music.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.demo.music.AdAdmob;
import com.demo.music.R;
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeAdapter;
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeRecyclerView;
import com.ernestoyaquello.dragdropswiperecyclerview.listener.OnItemDragListener;
import com.ernestoyaquello.dragdropswiperecyclerview.listener.OnItemSwipeListener;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import com.demo.music.MPMApp;
import com.demo.music.adapter.MPMCurrentListAdapter;
import com.demo.music.adapter.MPMStatusAdapter;
import com.demo.music.database.MPMDatabaseHelper;
import com.demo.music.dialog.MPMPlaylistNameDialog;
import com.demo.music.fragments.MPMAlbumSongsFragment;
import com.demo.music.fragments.MPMEqualizerFragment;
import com.demo.music.fragments.MPMFavSongsFragment;
import com.demo.music.fragments.MPMHomeFragment;
import com.demo.music.fragments.MPMLibraryFragment;
import com.demo.music.fragments.MPMPermissionFragment;
import com.demo.music.fragments.MPMPlaylistCreatorFragment;
import com.demo.music.fragments.MPMPlaylistFragment;
import com.demo.music.fragments.MPMPlaylistSongsFragment;
import com.demo.music.interfaces.MPMMultiSelectListener;
import com.demo.music.interfaces.MPMOnAlbumClicked;
import com.demo.music.interfaces.MPMOnFavRemoved;
import com.demo.music.interfaces.MPMOnPlayClicked;
import com.demo.music.interfaces.MPMOnSingleSongClicked;
import com.demo.music.interfaces.MPMPlaylistCallbacks;
import com.demo.music.interfaces.MPMPlaylistDialogListener;
import com.demo.music.interfaces.MPMonSeeMoreClicked;
import com.demo.music.model.MPMAlbumModel;
import com.demo.music.model.MPMPlaylistModel;
import com.demo.music.model.MPMSongModel;
import com.demo.music.service.MPMMediaPlayerService;
import com.demo.music.service.MPMTestWidgetTest;
import com.demo.music.utils.AppRater;
import com.demo.music.utils.HorizontalMarginItemDecoration;
import com.demo.music.utils.LockableBottomSheetBehavior;
import com.demo.music.utils.StorageUtil;
import com.demo.music.utils.Util;

import de.hdodenhof.circleimageview.CircleImageView;

import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;
import org.michaelbel.bottomsheet.BottomSheet;

public class MPMMainActivity extends AppCompatActivity implements MPMonSeeMoreClicked, MPMOnPlayClicked, MPMOnSingleSongClicked, MPMOnAlbumClicked, MPMStatusAdapter.OnItemClickListener, MPMPlaylistCallbacks, MPMMultiSelectListener, MPMOnFavRemoved {
    public static final String Broadcast_PLAY_NEW_AUDIO = "PlayNewAudio";
    MPMCurrentListAdapter adapter;
    View add_to_playlist_bottom_sheet;
    View add_to_playlist_multi_select;
    MPMAlbumSongsFragment albumSongsFragment;
    BottomNavigationView bottomNavigationView;
    private RelativeLayout bottomSheetParentLayout;
    View bottom_content;
    private RelativeLayout bottom_sheet_dialog;
    private BottomSheetBehavior bottom_sheet_dialog_behavior;
    private RelativeLayout bottom_show;
    View bottomnav_toolbar;
    View clear;
    DragDropSwipeRecyclerView current_list;
    TextView current_time;
    MPMStatusAdapter dataAdapter;
    MPMDatabaseHelper databaseHelper;
    MPMEqualizerFragment equalizerFragment;
    MPMFavSongsFragment favFragment;
    ImageView favImage;
    View fav_bottom_sheet;
    View fav_multi_select;
    ImageView fav_multi_select_image;
    MPMHomeFragment homeFragment;
    ImageView image2;
    CircleImageView imageView;
    boolean isMultiSelectedFav;
    private BottomSheetBehavior mBottomSheetBehaviour;
    RelativeLayout mainLayout;
    MediaPlayer mediaPlayer;
    View more;
    View more_multi_select;
    private boolean multiSelect;
    View multipleSelect;
    ImageView next;
    ImageView playImage;
    FloatingActionButton play_pause;
    private MPMMediaPlayerService player;
    MPMPlaylistSongsFragment playlistSongFragment;
    ImageView prev;
    View queue;
    private RelativeLayout queue_bottom_sheet;
    private BottomSheetBehavior queue_bottom_sheet_behaviour;
    ImageView repeat;
    View save;
    IndicatorSeekBar seekbar;
    View share_bottom_sheet;
    View share_multi_select;
    SharedPreferences sharedPreferences;
    ImageView shuffle;
    private MPMSongModel songModel;
    ArrayList<MPMSongModel> songModels;
    TextView song_artist;
    TextView song_artist_bottom_sheet;
    TextView song_title;
    TextView song_title_bottom_sheet;
    StorageUtil storageUtil;
    TextView total_time;
    View transparent_view;
    ViewPager2 viewPager2;
    boolean serviceBound = false;
    boolean isPlaying = false;
    int index = 0;
    int deletedFromIndex = 0;
    Handler handler = new Handler();
    boolean updateViewPager = false;
    MPMPlaylistNameDialog dialog = null;
    ArrayList<MPMSongModel> multiSelected = new ArrayList<>();
    boolean fromUserTouch = true;
    String[] permission1 = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_PHONE_STATE", "android.permission.RECORD_AUDIO"};
    Runnable calculateProgress = new Runnable() {
        @Override
        public void run() {
            if (MPMMainActivity.this.mediaPlayer != null) {
                try {
                    if (MPMMainActivity.this.mediaPlayer.isPlaying()) {
                        MPMMainActivity.this.seekbar.setProgress(MPMMainActivity.this.mediaPlayer.getCurrentPosition());
                        MPMMainActivity.this.current_time.setText(Util.getStringTime(MPMMainActivity.this.mediaPlayer.getCurrentPosition()));
                    }
                } catch (Exception unused) {
                }
            }
            MPMMainActivity.this.handler.postDelayed(this, 100L);
        }
    };
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int i;
            Log.e("here", "hello");
            if (intent.getAction().equals(Util.ACTION_SONG_CHANGE)) {
                MPMMainActivity.this.songModel = (MPMSongModel) intent.getParcelableExtra(Util.CURRENT_SONG);
                new StorageUtil(MPMMainActivity.this).storeCurrent(MPMMainActivity.this.songModel);
                MPMMainActivity.this.index = intent.getIntExtra(Util.CURRENT_INDEX, 0);
                MPMMainActivity.this.seekbar.setMax((float) Long.parseLong(MPMMainActivity.this.songModel.getDuration()));
                MPMMainActivity mPMMainActivity = MPMMainActivity.this;
                mPMMainActivity.changeBottomSheetData(mPMMainActivity.songModel);
                MPMMainActivity.this.dataAdapter.setCurrentIndex(MPMMainActivity.this.index);
                MPMMainActivity.this.viewPager2.setAdapter(MPMMainActivity.this.dataAdapter);
                ViewPager2 viewPager2 = MPMMainActivity.this.viewPager2;
                if (MPMMainActivity.this.songModels.isEmpty()) {
                    i = 0;
                } else {
                    i = MPMMainActivity.this.index + (1073741823 - (1073741823 % MPMMainActivity.this.songModels.size()));
                }
                viewPager2.setCurrentItem(i, false);
                if (MPMMainActivity.this.player != null) {
                    Log.e("mediaPlayer", "hello");
                    MPMMainActivity mPMMainActivity2 = MPMMainActivity.this;
                    mPMMainActivity2.mediaPlayer = mPMMainActivity2.player.getMediaPlayer();
                    MPMMainActivity.this.adapter.setCurrentIndex(MPMMainActivity.this.index);
                    MPMMainActivity.this.adapter.setMediaPlayer(MPMMainActivity.this.mediaPlayer);
                }
            } else if (intent.getAction().equals(Util.SONG_ACTION)) {
                MPMMainActivity.this.isPlaying = intent.getBooleanExtra(Util.IS_PLAYING, true);
                MPMMainActivity mPMMainActivity3 = MPMMainActivity.this;
                mPMMainActivity3.changeBottomSheetButton(mPMMainActivity3.isPlaying);
                MPMMainActivity.this.adapter.setPlayPause(MPMMainActivity.this.isPlaying);
            }
        }
    };
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MPMMainActivity.this.player = ((MPMMediaPlayerService.LocalBinder) iBinder).getService();
            MPMMainActivity mPMMainActivity = MPMMainActivity.this;
            mPMMainActivity.mediaPlayer = mPMMainActivity.player.getMediaPlayer();
            MPMMainActivity.this.adapter.setCurrentIndex(MPMMainActivity.this.index);
            MPMMainActivity.this.adapter.setMediaPlayer(MPMMainActivity.this.mediaPlayer);
            if (MPMMainActivity.this.mediaPlayer != null) {
                Log.e("mediaPlayer", "mil gaya");
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MPMMainActivity.this.mediaPlayer = MPMMainActivity.this.player.getMediaPlayer();
                    }
                }, 200L);
            }
            MPMMainActivity.this.serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            MPMMainActivity.this.serviceBound = false;
        }
    };

    @Override
    public void onAddtoPlaylist(MPMSongModel mPMSongModel) {
    }

    @Override
    public void onItemLoaded(MPMSongModel mPMSongModel) {
    }


    @Override

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);


        AdAdmob adAdmob = new AdAdmob(this);
        adAdmob.FullscreenAd(this);


        this.bottomSheetParentLayout = (RelativeLayout) findViewById(R.id.bottom_sheet_parent);
        this.queue_bottom_sheet = (RelativeLayout) findViewById(R.id.queue_bottom_sheet);
        this.bottom_sheet_dialog = (RelativeLayout) findViewById(R.id.bottom_sheet_dialog);
        this.mBottomSheetBehaviour = BottomSheetBehavior.from(this.bottomSheetParentLayout);
        this.queue_bottom_sheet_behaviour = BottomSheetBehavior.from(this.queue_bottom_sheet);
        this.bottom_sheet_dialog_behavior = BottomSheetBehavior.from(this.bottom_sheet_dialog);
        this.queue_bottom_sheet_behaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
        this.bottom_sheet_dialog_behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        this.mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
        this.mBottomSheetBehaviour.setPeekHeight(Util.dpToPx(this, 0));
        new MPMPermissionFragment().CheckForPermission(this.permission1, "MPMApp Needs You to Grant the Permission.", new MPMPermissionFragment.OnPermissionResult() {
            @Override
            public void onSuccess() {
                MPMMainActivity.this.init();
            }

            @Override
            public void onFail() {
                Toast.makeText(MPMMainActivity.this, "Permission Not given!", Toast.LENGTH_SHORT).show();
            }
        }).show(getFragmentManager(), "dialog_fragment");
    }


    public void init() {
        char c;
        this.databaseHelper = new MPMDatabaseHelper(this);
        this.bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);
        this.bottom_show = (RelativeLayout) findViewById(R.id.bottom_show);
        this.song_title = (TextView) findViewById(R.id.song_title);
        this.song_artist = (TextView) findViewById(R.id.song_artist);
        this.playImage = (ImageView) findViewById(R.id.playImage);
        this.imageView = (CircleImageView) findViewById(R.id.imageView);
        this.bottomnav_toolbar = findViewById(R.id.toolbar);
        this.bottom_content = findViewById(R.id.bottom_content);
        this.seekbar = (IndicatorSeekBar) findViewById(R.id.seekbar);
        this.current_time = (TextView) findViewById(R.id.current_time);
        this.total_time = (TextView) findViewById(R.id.total_time);
        this.song_artist_bottom_sheet = (TextView) findViewById(R.id.song_artist_bottom_sheet);
        this.play_pause = (FloatingActionButton) findViewById(R.id.play_pause);
        this.repeat = (ImageView) findViewById(R.id.repeat);
        this.prev = (ImageView) findViewById(R.id.prev);
        this.next = (ImageView) findViewById(R.id.next);
        this.queue = findViewById(R.id.queue);
        this.current_list = (DragDropSwipeRecyclerView) findViewById(R.id.current_list);
        this.mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        this.shuffle = (ImageView) findViewById(R.id.shuffle);
        this.more = findViewById(R.id.more);
        this.favImage = (ImageView) findViewById(R.id.favImage);
        this.fav_bottom_sheet = findViewById(R.id.fav_bottom_sheet);
        this.share_bottom_sheet = findViewById(R.id.share_bottom_sheet);
        this.add_to_playlist_bottom_sheet = findViewById(R.id.add_to_playlist_bottom_sheet);
        this.image2 = (ImageView) findViewById(R.id.image2);
        this.transparent_view = findViewById(R.id.transparent_view);
        this.fav_multi_select = findViewById(R.id.fav_multi_select);
        this.share_multi_select = findViewById(R.id.share_multi_select);
        this.add_to_playlist_multi_select = findViewById(R.id.add_to_playlist_multi_select);
        this.more_multi_select = findViewById(R.id.more_multi_select);
        this.multipleSelect = findViewById(R.id.multipleSelect);
        this.fav_multi_select_image = (ImageView) findViewById(R.id.fav_multi_select_image);
        this.clear = findViewById(R.id.clear);
        this.save = findViewById(R.id.save);
        this.sharedPreferences = getSharedPreferences(Util.MySharedPref, 0);
        this.storageUtil = new StorageUtil(this);
        if (!MPMApp.equalizerBound) {
            getApplicationContext().bindService(new Intent(this, MPMTestWidgetTest.class), MPMApp.equalizerServiceConnection, BIND_AUTO_CREATE);
            Log.e("bound", "here");
        }
        this.transparent_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                MPMMainActivity.this.bottom_sheet_dialog_behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                return false;
            }
        });
        this.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MPMMainActivity.this).setMessage("Do you want to clear whole list?").setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MPMMainActivity.this.mediaPlayer.stop();
                        MPMMainActivity.this.storageUtil.clearCachedAudioPlaylist();
                        MPMMainActivity.this.queue_bottom_sheet_behaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
                        MPMMainActivity.this.bottom_sheet_dialog_behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        MPMMainActivity.this.mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        MPMMainActivity.this.mBottomSheetBehaviour.setPeekHeight(Util.dpToPx(MPMMainActivity.this, 0));
                        MPMMainActivity.this.songModels.clear();
                        MPMMainActivity.this.sharedPreferences.edit().putBoolean(Util.SHUFFLE, false).apply();
                        MPMMainActivity.this.songModel = null;
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
            }
        });
        this.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MPMMainActivity.this.queue_bottom_sheet_behaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
                MPMMainActivity.this.bottom_sheet_dialog_behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                MPMMainActivity.this.mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                MPMMainActivity mPMMainActivity = MPMMainActivity.this;
                mPMMainActivity.changeFragment(MPMPlaylistFragment.getInstance(false, mPMMainActivity.songModels));
                MPMMainActivity mPMMainActivity2 = MPMMainActivity.this;
                mPMMainActivity2.fromUserTouch = false;
                mPMMainActivity2.bottomNavigationView.setSelectedItemId(R.id.playlist);
            }
        });
        this.fav_multi_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean z = false;
                if (MPMMainActivity.this.isMultiSelectedFav) {
                    MPMMainActivity.this.storageUtil.removeFav(MPMMainActivity.this.multiSelected);
                    MPMMainActivity mPMMainActivity = MPMMainActivity.this;
                    mPMMainActivity.isMultiSelectedFav = false;
                    mPMMainActivity.fav_multi_select_image.setImageResource(R.drawable.fav_multi_select);
                } else {
                    MPMMainActivity.this.storageUtil.storeFav(MPMMainActivity.this.multiSelected);
                    MPMMainActivity mPMMainActivity2 = MPMMainActivity.this;
                    mPMMainActivity2.isMultiSelectedFav = true;
                    mPMMainActivity2.fav_multi_select_image.setImageResource(R.drawable.fav_multi_select_pressed);
                }
                int i = 0;
                while (true) {
                    if (i >= MPMMainActivity.this.multiSelected.size()) {
                        break;
                    } else if (MPMMainActivity.this.multiSelected.get(i).getData().equals(MPMMainActivity.this.songModel.getData())) {
                        z = true;
                        break;
                    } else {
                        i++;
                    }
                }
                if (z) {
                    if (MPMMainActivity.this.storageUtil.isFav(MPMMainActivity.this.songModel)) {
                        MPMMainActivity.this.favImage.setImageResource(R.drawable.fav_selected);
                        MPMMainActivity.this.image2.setImageResource(R.drawable.ic_favorite___fill);
                    } else {
                        MPMMainActivity.this.favImage.setImageResource(R.drawable.favourite);
                        MPMMainActivity.this.image2.setImageResource(R.drawable.ic_favorite);
                    }
                    if (MPMMainActivity.this.getSupportFragmentManager().findFragmentById(R.id.container) instanceof MPMFavSongsFragment) {
                        MPMMainActivity.this.favFragment.updateData();
                    }
                }
            }
        });
        this.share_multi_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MPMMainActivity mPMMainActivity = MPMMainActivity.this;
                Util.shareMultipleSongs(mPMMainActivity, mPMMainActivity.multiSelected);
                MPMMainActivity.this.onBackPressed();
            }
        });
        this.add_to_playlist_multi_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MPMMainActivity mPMMainActivity = MPMMainActivity.this;
                mPMMainActivity.changeFragment(MPMPlaylistFragment.getInstance(false, mPMMainActivity.multiSelected));
                MPMMainActivity.this.onBackPressed();
                MPMMainActivity mPMMainActivity2 = MPMMainActivity.this;
                mPMMainActivity2.fromUserTouch = false;
                mPMMainActivity2.bottomNavigationView.setSelectedItemId(R.id.playlist);
            }
        });
        this.more_multi_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(MPMMainActivity.this, view);
                MenuInflater menuInflater = popupMenu.getMenuInflater();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int i;
                        int itemId = menuItem.getItemId();
                        if (itemId == R.id.add_to_queue) {
                            Log.e("hereHeya", "hello");
                            StorageUtil storageUtil = new StorageUtil(MPMMainActivity.this);
                            ArrayList<MPMSongModel> loadAudio = storageUtil.loadAudio();
                            ArrayList<MPMSongModel> suffledSongs = storageUtil.getSuffledSongs();
                            boolean z = MPMMainActivity.this.sharedPreferences.getBoolean(Util.SHUFFLE, false);
                            loadAudio.addAll(MPMMainActivity.this.multiSelected);
                            suffledSongs.addAll(MPMMainActivity.this.multiSelected);
                            storageUtil.storeAudio(loadAudio);
                            storageUtil.storeSuffledSongs(suffledSongs);
                            if (MPMMainActivity.this.serviceBound) {
                                if (z) {
                                    MPMMainActivity.this.player.setDatas(suffledSongs, storageUtil.loadAudioIndex());
                                } else {
                                    MPMMainActivity.this.player.setDatas(loadAudio, storageUtil.loadAudioIndex());
                                }
                            }
                            if (z) {
                                MPMMainActivity.this.songModels.clear();
                                MPMMainActivity.this.songModels.addAll(suffledSongs);
                            } else {
                                MPMMainActivity.this.songModels.clear();
                                MPMMainActivity.this.songModels.addAll(loadAudio);
                            }
                            if (MPMMainActivity.this.dataAdapter == null) {
                                MPMMainActivity.this.dataAdapter = new MPMStatusAdapter(MPMMainActivity.this, MPMMainActivity.this.songModels, MPMMainActivity.this.index, MPMMainActivity.this);
                            } else {
                                MPMMainActivity.this.dataAdapter.updateData(MPMMainActivity.this.songModels);
                            }
                            MPMMainActivity.this.viewPager2.setAdapter(MPMMainActivity.this.dataAdapter);
                            ViewPager2 viewPager2 = MPMMainActivity.this.viewPager2;
                            if (MPMMainActivity.this.songModels.isEmpty()) {
                                i = 0;
                            } else {
                                i = MPMMainActivity.this.index + (1073741823 - (1073741823 % MPMMainActivity.this.songModels.size()));
                            }
                            viewPager2.setCurrentItem(i, false);
                            MPMMainActivity.this.adapter.setDataSet(MPMMainActivity.this.songModels);
                            MPMMainActivity.this.adapter.notifyDataSetChanged();
                            MPMMainActivity.this.onBackPressed();
                        } else if (itemId == R.id.play_selected) {
                            MPMMainActivity.this.playSongs(MPMMainActivity.this.multiSelected);
                            MPMMainActivity.this.onBackPressed();
                        }
                        return false;
                    }
                });
                menuInflater.inflate(R.menu.more_multi_click, popupMenu.getMenu());
                popupMenu.show();
            }
        });

        this.fav_bottom_sheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MPMMainActivity.this.favImage.performClick();
                MPMMainActivity.this.bottom_sheet_dialog_behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
        this.share_bottom_sheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MPMMainActivity.this.bottom_sheet_dialog_behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                MPMMainActivity mPMMainActivity = MPMMainActivity.this;
                Util.shareSong(mPMMainActivity, mPMMainActivity.songModel);
            }
        });
        this.add_to_playlist_bottom_sheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList arrayList = new ArrayList();
                arrayList.add(MPMMainActivity.this.songModel);
                MPMMainActivity.this.changeFragment(MPMPlaylistFragment.getInstance(false, arrayList));
                MPMMainActivity.this.bottom_sheet_dialog_behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                MPMMainActivity mPMMainActivity = MPMMainActivity.this;
                mPMMainActivity.fromUserTouch = false;
                mPMMainActivity.bottomNavigationView.setSelectedItemId(R.id.playlist);
            }
        });
        this.bottom_sheet_dialog_behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override

            public void onSlide(@NonNull View view, float f) {
            }

            @Override

            public void onStateChanged(@NonNull View view, int i) {
                if (i == 1) {
                    ((LockableBottomSheetBehavior) MPMMainActivity.this.mBottomSheetBehaviour).setSwipeEnabled(false);
                    MPMMainActivity.this.transparent_view.setVisibility(View.VISIBLE);
                } else if (i == 3) {
                    MPMMainActivity.this.transparent_view.setVisibility(View.VISIBLE);
                } else if (i == 5) {
                    ((LockableBottomSheetBehavior) MPMMainActivity.this.mBottomSheetBehaviour).setSwipeEnabled(true);
                    MPMMainActivity.this.transparent_view.setVisibility(View.GONE);
                }
            }
        });
        this.song_title_bottom_sheet = (TextView) findViewById(R.id.song_title_bottom_sheet);
        this.viewPager2 = (ViewPager2) findViewById(R.id.viewPager);
        this.handler.postDelayed(this.calculateProgress, 100L);
        this.equalizerFragment = new MPMEqualizerFragment();
        this.shuffle.setColorFilter(this.sharedPreferences.getBoolean(Util.SHUFFLE, false) ? getResources().getColor(R.color.colorPrimary) : Color.parseColor("#919FAF"), PorterDuff.Mode.SRC_IN);
        String string = this.sharedPreferences.getString(Util.REPEAT, Util.REPEAT_NONE);
        int hashCode = string.hashCode();
        if (hashCode == -1679839331) {
            if (string.equals(Util.REPEAT_NONE)) {
                c = 0;
            }
            c = 65535;
        } else if (hashCode != -331295620) {
            if (hashCode == -331282111 && string.equals(Util.REPEAT_ONE)) {
                c = 2;
            }
            c = 65535;
        } else {
            if (string.equals(Util.REPEAT_ALL)) {
                c = 1;
            }
            c = 65535;
        }
        switch (c) {
            case 0:
                this.repeat.setImageResource(R.drawable.repeat);
                this.repeat.setColorFilter(Color.parseColor("#919FAF"), PorterDuff.Mode.SRC_IN);
                break;
            case 1:
                this.repeat.setImageResource(R.drawable.repeat);
                this.repeat.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                break;
            case 2:
                this.repeat.setImageResource(R.drawable.repeat_once);
                this.repeat.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                break;
        }
        this.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int i, float f, int i2) {
                super.onPageScrolled(i, f, i2);
            }

            @Override
            public void onPageSelected(int i) {
                super.onPageSelected(i);
                if (!MPMMainActivity.this.songModels.isEmpty()) {
                    MPMMainActivity.this.song_title_bottom_sheet.setText(MPMMainActivity.this.songModels.get(i % MPMMainActivity.this.songModels.size()).getTitle());
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                super.onPageScrollStateChanged(i);
            }
        });
        this.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MPMMainActivity.this.bottom_sheet_dialog_behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        this.shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StorageUtil storageUtil = new StorageUtil(MPMMainActivity.this);
                if (!MPMMainActivity.this.sharedPreferences.getBoolean(Util.SHUFFLE, false)) {
                    MPMMainActivity.this.songModels = storageUtil.createNewShuffle();
                    MPMMainActivity.this.sharedPreferences.edit().putBoolean(Util.SHUFFLE, true).apply();
                    MPMMainActivity.this.shuffle.setColorFilter(MPMMainActivity.this.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                } else {
                    MPMMainActivity.this.songModels = storageUtil.loadAudio();
                    MPMMainActivity.this.sharedPreferences.edit().putBoolean(Util.SHUFFLE, false).apply();
                    MPMMainActivity.this.shuffle.setColorFilter(Color.parseColor("#919FAF"), PorterDuff.Mode.SRC_IN);
                }
                int i = -1;
                MPMMainActivity.this.dataAdapter.updateData(MPMMainActivity.this.songModels);
                int i2 = 0;
                while (true) {
                    if (i2 >= MPMMainActivity.this.songModels.size()) {
                        break;
                    } else if (MPMMainActivity.this.songModels.get(i2).getData().equals(MPMMainActivity.this.songModel.getData())) {
                        MPMMainActivity.this.dataAdapter.setCurrentIndex(i2);
                        i = i2;
                        break;
                    } else {
                        i2++;
                    }
                }
                MPMMainActivity.this.viewPager2.setAdapter(MPMMainActivity.this.dataAdapter);
                MPMMainActivity.this.viewPager2.setCurrentItem(MPMMainActivity.this.songModels.isEmpty() ? 0 : (1073741823 - (1073741823 % MPMMainActivity.this.songModels.size())) + i, false);
                MPMMainActivity.this.adapter.setDataSet(MPMMainActivity.this.songModels);
                if (MPMMainActivity.this.serviceBound) {
                    MPMMainActivity.this.player.setDatas(MPMMainActivity.this.songModels, i);
                }
                MPMMainActivity mPMMainActivity = MPMMainActivity.this;
                mPMMainActivity.index = i;
                mPMMainActivity.adapter.setCurrentIndex(MPMMainActivity.this.index);
            }
        });
        this.favImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("click", "hello");
                StorageUtil storageUtil = new StorageUtil(MPMMainActivity.this);
                if (storageUtil.isFav(MPMMainActivity.this.songModel)) {
                    storageUtil.removeFav(MPMMainActivity.this.songModel);
                    MPMMainActivity.this.favImage.setImageResource(R.drawable.favourite);
                    MPMMainActivity.this.image2.setImageResource(R.drawable.ic_favorite);
                } else {
                    storageUtil.storeFav(MPMMainActivity.this.songModel);
                    MPMMainActivity.this.favImage.setImageResource(R.drawable.fav_selected);
                    MPMMainActivity.this.image2.setImageResource(R.drawable.ic_favorite___fill);
                }
                MPMMainActivity.this.updateFav();
                if (MPMMainActivity.this.getSupportFragmentManager().findFragmentById(R.id.container) instanceof MPMFavSongsFragment) {
                    MPMMainActivity.this.favFragment.updateData();
                }
            }
        });
        this.seekbar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onStartTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
            }

            @Override
            public void onSeeking(SeekParams seekParams) {
                if (seekParams.fromUser) {
                    if (MPMMainActivity.this.mediaPlayer != null) {
                        MPMMainActivity.this.mediaPlayer.seekTo(seekParams.progress);
                    }
                    MPMMainActivity.this.current_time.setText(Util.getStringTime(seekParams.progress));
                }
                if (MPMMainActivity.this.player != null) {
                    MPMMainActivity.this.player.setSongPosition(seekParams.progress);
                }
            }
        });
        this.play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MPMMainActivity.this.playImage.performClick();
            }
        });
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Util.SONG_ACTION);
        intentFilter.addAction(Util.ACTION_SONG_CHANGE);
        registerReceiver(this.broadcastReceiver, intentFilter);
        this.homeFragment = new MPMHomeFragment();
        changeFragment(this.homeFragment);
        this.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override

            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                MPMMainActivity.this.mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                MPMMainActivity.this.queue_bottom_sheet_behaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
                MPMMainActivity.this.multiSelect = false;
                if (MPMMainActivity.this.fromUserTouch) {
                    int itemId = menuItem.getItemId();
                    if (itemId == R.id.favourite) {
                        MPMMainActivity.this.favFragment = new MPMFavSongsFragment();
                        MPMMainActivity mPMMainActivity = MPMMainActivity.this;
                        mPMMainActivity.changeFragment(mPMMainActivity.favFragment);

                    } else if (itemId == R.id.home) {
                        MPMMainActivity.this.homeFragment = new MPMHomeFragment();
                        MPMMainActivity mPMMainActivity2 = MPMMainActivity.this;
                        mPMMainActivity2.changeFragment(mPMMainActivity2.homeFragment);
                    } else if (itemId == R.id.library) {
                        MPMMainActivity.this.changeFragment(new MPMLibraryFragment());
                    } else if (itemId == R.id.playlist) {
                        MPMMainActivity.this.changeFragment(MPMPlaylistFragment.getInstance(true, null));
                    }
                } else {
                    MPMMainActivity.this.fromUserTouch = true;
                }
                return true;
            }
        });
        this.bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override

            public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
            }
        });
        this.mBottomSheetBehaviour.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override

            public void onSlide(@NonNull View view, float f) {
            }

            @Override

            public void onStateChanged(@NonNull View view, int i) {
                int i2;
                if (i != 1) {
                    switch (i) {
                        case 3:
                            MPMMainActivity.this.bottom_show.setVisibility(View.GONE);
                            MPMMainActivity.this.bottomnav_toolbar.setVisibility(View.VISIBLE);
                            MPMMainActivity.this.bottom_content.setVisibility(View.VISIBLE);
                            MPMMainActivity.this.song_title_bottom_sheet.setSelected(true);
                            Log.e("bottomsheet", "STATE_EXPANDED");
                            return;
                        case 4:
                            Log.e("bottomsheet", "STATE_COLLAPSED");
                            MPMMainActivity.this.bottom_show.setVisibility(View.VISIBLE);
                            MPMMainActivity.this.bottomnav_toolbar.setVisibility(View.GONE);
                            MPMMainActivity.this.song_title.setSelected(true);
                            return;
                        default:
                            return;
                    }
                } else {
                    MPMMainActivity.this.bottom_show.setVisibility(View.VISIBLE);
                    MPMMainActivity.this.bottomnav_toolbar.setVisibility(View.GONE);
                    MPMMainActivity.this.bottom_content.setVisibility(View.VISIBLE);
                    ViewPager2 viewPager2 = MPMMainActivity.this.viewPager2;
                    if (MPMMainActivity.this.songModels.isEmpty()) {
                        i2 = 0;
                    } else {
                        i2 = MPMMainActivity.this.index + (1073741823 - (1073741823 % MPMMainActivity.this.songModels.size()));
                    }
                    viewPager2.setCurrentItem(i2, false);
                    Log.e("bottomsheet", "STATE_DRAGGING");
                }
            }
        });
        this.queue_bottom_sheet_behaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
        this.bottom_sheet_dialog_behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        this.queue_bottom_sheet_behaviour.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override

            public void onStateChanged(@NonNull View view, int i) {
                if (i != 1) {
                    switch (i) {
                        case 3:
                            MPMMainActivity.this.current_list.scrollToPosition(MPMMainActivity.this.index);
                            ((LockableBottomSheetBehavior) MPMMainActivity.this.mBottomSheetBehaviour).setSwipeEnabled(false);
                            return;
                        case 4:
                        default:
                            return;
                        case 5:
                            ((LockableBottomSheetBehavior) MPMMainActivity.this.mBottomSheetBehaviour).setSwipeEnabled(true);
                            return;
                    }
                }
            }

            @Override

            public void onSlide(@NonNull View view, float f) {
                Log.e("slide", f + "");
            }
        });
        this.queue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MPMMainActivity.this.queue_bottom_sheet_behaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        final StorageUtil storageUtil = new StorageUtil(this);
        if (!this.sharedPreferences.getBoolean(Util.SHUFFLE, false)) {
            Log.e("sharedPrefrence", "hey");
            this.songModels = storageUtil.loadAudio();
            this.index = storageUtil.loadAudioIndex();
        } else {
            Log.e("sharedPrefrence", "heya");
            this.songModels = storageUtil.getSuffledSongs();
            MPMSongModel current = storageUtil.getCurrent();
            int i = 0;
            while (true) {
                if (i < this.songModels.size()) {
                    if (current.getData().equals(this.songModels.get(i).getData())) {
                        this.index = i;
                    } else {
                        i++;
                    }
                }
            }
        }
        this.adapter = new MPMCurrentListAdapter(this, this.index, this.songModels, this);
        if (!this.songModels.isEmpty()) {
            this.songModel = storageUtil.getCurrent();
            changeBottomSheetData(this.songModels.get(this.index));
            showBottomSheet();
            setUpMusicPlayerPage(this.songModels, this.index);
        } else {
            this.mBottomSheetBehaviour.setPeekHeight(Util.dpToPx(this, 0));
        }
        this.current_list.setLayoutManager(new LinearLayoutManager(this));
        this.current_list.setAdapter((DragDropSwipeAdapter<?, ?>) this.adapter);
        Log.e("songsModels", this.adapter.getItemCount() + "");
        this.current_list.setOrientation(DragDropSwipeRecyclerView.ListOrientation.VERTICAL_LIST_WITH_VERTICAL_DRAGGING);
        this.current_list.setSwipeListener(new AnonymousClass28());
        this.current_list.setDragListener(new OnItemDragListener<MPMSongModel>() {
            public void onItemDragged(int i2, int i3, MPMSongModel mPMSongModel) {
                Log.e("dragged", "dragging");
            }

            public void onItemDropped(int i2, int i3, MPMSongModel mPMSongModel) {
                MPMMainActivity.this.adapter.notifyDataSetChanged();
                MPMMainActivity mPMMainActivity = MPMMainActivity.this;
                mPMMainActivity.songModels = new ArrayList<>(mPMMainActivity.adapter.getDataSet());
                MPMMainActivity.this.dataAdapter.updateData(MPMMainActivity.this.songModels);
                int i4 = -1;
                for (int i5 = 0; i5 < MPMMainActivity.this.songModels.size(); i5++) {
                    if (MPMMainActivity.this.songModels.get(i5).getData().equals(MPMMainActivity.this.songModel.getData())) {
                        MPMMainActivity.this.dataAdapter.setCurrentIndex(i5);
                        i4 = i5;
                    }
                }
                MPMMainActivity.this.viewPager2.setAdapter(MPMMainActivity.this.dataAdapter);
                MPMMainActivity.this.viewPager2.setCurrentItem(MPMMainActivity.this.songModels.isEmpty() ? 0 : (1073741823 - (1073741823 % MPMMainActivity.this.songModels.size())) + i4, false);
                if (MPMMainActivity.this.serviceBound) {
                    MPMMainActivity.this.player.setDatas(MPMMainActivity.this.songModels, i4);
                }
                StorageUtil storageUtil2 = new StorageUtil(MPMMainActivity.this);
                if (!MPMMainActivity.this.sharedPreferences.getBoolean(Util.SHUFFLE, false)) {
                    storageUtil2.storeAudioIndex(i4);
                    storageUtil2.storeAudio(MPMMainActivity.this.songModels);
                } else {
                    storageUtil2.storeCurrent(MPMMainActivity.this.songModels.get(i4));
                    storageUtil2.storeSuffledSongs(MPMMainActivity.this.songModels);
                }
                MPMMainActivity mPMMainActivity2 = MPMMainActivity.this;
                mPMMainActivity2.index = i4;
                mPMMainActivity2.adapter.setCurrentIndex(MPMMainActivity.this.index);
            }
        });
        this.dataAdapter = new MPMStatusAdapter(this, this.songModels, this.index, this);
        this.viewPager2.setAdapter(this.dataAdapter);
        this.viewPager2.setOffscreenPageLimit(1);
        final float dimension = getResources().getDimension(R.dimen.viewpager_next_item_visible) + getResources().getDimension(R.dimen.viewpager_current_item_horizontal_margin);
        this.viewPager2.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View view, float f) {
                Log.e("position", f + "");
                view.setTranslationX((-dimension) * f);
                view.setScaleY(1.0f - (Math.abs(f) * 0.25f));
            }
        });
        this.viewPager2.addItemDecoration(new HorizontalMarginItemDecoration(this, R.dimen.viewpager_current_item_horizontal_margin));
        this.viewPager2.setCurrentItem(this.songModels.isEmpty() ? 0 : this.index + (1073741823 - (1073741823 % this.songModels.size())), false);
        this.playImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!MPMMainActivity.this.serviceBound) {
                    Log.e("button", "here 4");
                    Intent intent = new Intent(MPMMainActivity.this, MPMMediaPlayerService.class);
                    intent.putExtra(Util.SONG_POSITION, MPMMainActivity.this.seekbar.getProgress());
                    if (Build.VERSION.SDK_INT >= 26) {
                        MPMMainActivity.this.startForegroundService(intent);
                    } else {
                        MPMMainActivity.this.startService(intent);
                    }
                    MPMMainActivity mPMMainActivity = MPMMainActivity.this;
                    mPMMainActivity.bindService(intent, mPMMainActivity.serviceConnection, BIND_AUTO_CREATE);
                    return;
                }
                Log.e("button", "here");
                if (MPMMainActivity.this.player.isDataLoaded()) {
                    Log.e("button", "here 1");
                    Intent intent2 = new Intent(MPMMainActivity.this, MPMMediaPlayerService.class);
                    if (MPMMainActivity.this.isPlaying) {
                        Log.e("button", "here 2");
                        intent2.setAction(Util.ACTION_PAUSE);
                    } else if (MPMMainActivity.this.player.isLastReached()) {
                        Log.e("lastReaced", "yes");
                        storageUtil.storeAudioIndex(0);
                        MPMMainActivity.this.sendBroadcast(new Intent(MPMMainActivity.Broadcast_PLAY_NEW_AUDIO));
                    } else {
                        intent2.setAction(Util.ACTION_PLAY);
                        intent2.putExtra(Util.SONG_POSITION, MPMMainActivity.this.seekbar.getProgress());
                    }
                    if (Build.VERSION.SDK_INT >= 26) {
                        MPMMainActivity.this.startForegroundService(intent2);
                    } else {
                        MPMMainActivity.this.startService(intent2);
                    }
                } else {
                    Log.e("button", "here 3");
                    MPMMainActivity.this.sendBroadcast(new Intent(MPMMainActivity.Broadcast_PLAY_NEW_AUDIO));
                }
            }
        });
        this.repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                char c2;
                String string2 = MPMMainActivity.this.sharedPreferences.getString(Util.REPEAT, Util.REPEAT_NONE);
                SharedPreferences.Editor edit = MPMMainActivity.this.sharedPreferences.edit();
                int hashCode2 = string2.hashCode();
                if (hashCode2 == -1679839331) {
                    if (string2.equals(Util.REPEAT_NONE)) {
                        c2 = 0;
                    }
                    c2 = 65535;
                } else if (hashCode2 != -331295620) {
                    if (hashCode2 == -331282111 && string2.equals(Util.REPEAT_ONE)) {
                        c2 = 2;
                    }
                    c2 = 65535;
                } else {
                    if (string2.equals(Util.REPEAT_ALL)) {
                        c2 = 1;
                    }
                    c2 = 65535;
                }
                switch (c2) {
                    case 0:
                        edit.putString(Util.REPEAT, Util.REPEAT_ALL);
                        Toast.makeText(MPMMainActivity.this, "Repeat all", Toast.LENGTH_SHORT).show();
                        MPMMainActivity.this.repeat.setImageResource(R.drawable.repeat);
                        MPMMainActivity.this.repeat.setColorFilter(MPMMainActivity.this.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                        break;
                    case 1:
                        edit.putString(Util.REPEAT, Util.REPEAT_ONE);
                        Toast.makeText(MPMMainActivity.this, "Repeat one", Toast.LENGTH_SHORT).show();
                        MPMMainActivity.this.repeat.setImageResource(R.drawable.repeat_once);
                        MPMMainActivity.this.repeat.setColorFilter(MPMMainActivity.this.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                        break;
                    case 2:
                        edit.putString(Util.REPEAT, Util.REPEAT_NONE);
                        Toast.makeText(MPMMainActivity.this, "Repeat none", Toast.LENGTH_SHORT).show();
                        MPMMainActivity.this.repeat.setImageResource(R.drawable.repeat);
                        MPMMainActivity.this.repeat.setColorFilter(Color.parseColor("#919FAF"), PorterDuff.Mode.SRC_IN);
                        break;
                }
                edit.apply();
            }
        });
        this.prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!MPMMainActivity.this.serviceBound) {
                    if (storageUtil.loadAudioIndex() > 0) {
                        StorageUtil storageUtil2 = storageUtil;
                        storageUtil2.storeAudioIndex(storageUtil2.loadAudioIndex() - 1);
                    } else {
                        StorageUtil storageUtil3 = storageUtil;
                        storageUtil3.storeAudioIndex(storageUtil3.loadAudio().size() - 1);
                    }
                    Log.e("button", "here 4");
                    Intent intent = new Intent(MPMMainActivity.this, MPMMediaPlayerService.class);
                    if (Build.VERSION.SDK_INT >= 26) {
                        MPMMainActivity.this.startForegroundService(intent);
                    } else {
                        MPMMainActivity.this.startService(intent);
                    }
                    MPMMainActivity mPMMainActivity = MPMMainActivity.this;
                    mPMMainActivity.bindService(intent, mPMMainActivity.serviceConnection, BIND_AUTO_CREATE);
                    return;
                }
                Intent intent2 = new Intent(MPMMainActivity.this, MPMMediaPlayerService.class);
                intent2.setAction(Util.ACTION_PREVIOUS);
                if (Build.VERSION.SDK_INT >= 26) {
                    MPMMainActivity.this.startForegroundService(intent2);
                } else {
                    MPMMainActivity.this.startService(intent2);
                }
            }
        });
        this.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!MPMMainActivity.this.serviceBound) {
                    Log.e("button", "here 4");
                    if (storageUtil.loadAudioIndex() < storageUtil.loadAudio().size() - 1) {
                        StorageUtil storageUtil2 = storageUtil;
                        storageUtil2.storeAudioIndex(storageUtil2.loadAudioIndex() - 1);
                    } else {
                        storageUtil.storeAudioIndex(0);
                    }
                    Intent intent = new Intent(MPMMainActivity.this, MPMMediaPlayerService.class);
                    if (Build.VERSION.SDK_INT >= 26) {
                        MPMMainActivity.this.startForegroundService(intent);
                    } else {
                        MPMMainActivity.this.startService(intent);
                    }
                    MPMMainActivity mPMMainActivity = MPMMainActivity.this;
                    mPMMainActivity.bindService(intent, mPMMainActivity.serviceConnection, BIND_AUTO_CREATE);
                    return;
                }
                Intent intent2 = new Intent(MPMMainActivity.this, MPMMediaPlayerService.class);
                intent2.setAction(Util.ACTION_NEXT);
                if (Build.VERSION.SDK_INT >= 26) {
                    MPMMainActivity.this.startForegroundService(intent2);
                } else {
                    MPMMainActivity.this.startService(intent2);
                }
            }
        });
    }


    public class AnonymousClass28 implements OnItemSwipeListener<MPMSongModel> {
        AnonymousClass28() {
        }

        public boolean onItemSwiped(final int i, @NotNull SwipeDirection swipeDirection, final MPMSongModel mPMSongModel) {
            Log.e("swiped", "here");
            if (swipeDirection == SwipeDirection.RIGHT_TO_LEFT) {
                Snackbar.make(MPMMainActivity.this.mainLayout, "Song removed from List", BaseTransientBottomBar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MPMMainActivity.this.adapter.insertItem(i, mPMSongModel);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    MPMMainActivity.this.adapter.notifyDataSetChanged();
                                    MPMMainActivity.this.songModels = new ArrayList<>(MPMMainActivity.this.adapter.getDataSet());
                                    MPMMainActivity.this.dataAdapter.updateData(MPMMainActivity.this.songModels);
                                    int i2 = -1;
                                    for (int i3 = 0; i3 < MPMMainActivity.this.songModels.size(); i3++) {
                                        if (MPMMainActivity.this.songModels.get(i3).getData().equals(MPMMainActivity.this.songModel.getData())) {
                                            MPMMainActivity.this.dataAdapter.setCurrentIndex(i3);
                                            i2 = i3;
                                        }
                                    }
                                    MPMMainActivity.this.viewPager2.setAdapter(MPMMainActivity.this.dataAdapter);
                                    MPMMainActivity.this.viewPager2.setCurrentItem(MPMMainActivity.this.songModels.isEmpty() ? 0 : (1073741823 - (1073741823 % MPMMainActivity.this.songModels.size())) + i2, false);
                                    if (MPMMainActivity.this.serviceBound) {
                                        MPMMainActivity.this.player.setDatas(MPMMainActivity.this.songModels, i2);
                                    }
                                    StorageUtil storageUtil = new StorageUtil(MPMMainActivity.this);
                                    if (!MPMMainActivity.this.sharedPreferences.getBoolean(Util.SHUFFLE, false)) {
                                        storageUtil.storeAudioIndex(i2);
                                        storageUtil.storeAudio(MPMMainActivity.this.songModels);
                                    } else {
                                        storageUtil.storeCurrent(MPMMainActivity.this.songModels.get(i2));
                                        storageUtil.storeSuffledSongs(MPMMainActivity.this.songModels);
                                        storageUtil.restoreAudio(MPMMainActivity.this.songModels.get(i2), MPMMainActivity.this.deletedFromIndex);
                                    }
                                    MPMMainActivity.this.index = i2;
                                    MPMMainActivity.this.adapter.setCurrentIndex(MPMMainActivity.this.index);
                                } catch (Exception e) {
                                    Log.e("exception", e.getMessage() + "");
                                }
                            }
                        }, 500L);
                    }
                }).show();
                MPMMainActivity.this.adapter.removeItem(i);
                MPMMainActivity.this.adapter.notifyDataSetChanged();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MPMMainActivity.this.songModels = new ArrayList<>(MPMMainActivity.this.adapter.getDataSet());
                        MPMMainActivity.this.dataAdapter.updateData(MPMMainActivity.this.songModels);
                        int i2 = -1;
                        for (int i3 = 0; i3 < MPMMainActivity.this.songModels.size(); i3++) {
                            if (MPMMainActivity.this.songModels.get(i3).getData().equals(MPMMainActivity.this.songModel.getData())) {
                                MPMMainActivity.this.dataAdapter.setCurrentIndex(i3);
                                i2 = i3;
                            }
                        }
                        MPMMainActivity.this.viewPager2.setAdapter(MPMMainActivity.this.dataAdapter);
                        MPMMainActivity.this.viewPager2.setCurrentItem(MPMMainActivity.this.songModels.isEmpty() ? 0 : (1073741823 - (1073741823 % MPMMainActivity.this.songModels.size())) + i2, false);
                        if (MPMMainActivity.this.serviceBound) {
                            MPMMainActivity.this.player.setDatas(MPMMainActivity.this.songModels, i2);
                        }
                        StorageUtil storageUtil = new StorageUtil(MPMMainActivity.this);
                        if (!MPMMainActivity.this.sharedPreferences.getBoolean(Util.SHUFFLE, false)) {
                            storageUtil.storeAudioIndex(i2);
                            storageUtil.storeAudio(MPMMainActivity.this.songModels);
                        } else {
                            storageUtil.storeCurrent(MPMMainActivity.this.songModels.get(i2));
                            storageUtil.storeSuffledSongs(MPMMainActivity.this.songModels);
                            MPMMainActivity.this.deletedFromIndex = storageUtil.deleteFromList(MPMMainActivity.this.songModels.get(i2));
                        }
                        MPMMainActivity.this.index = i2;
                    }
                }, 100L);
                MPMMainActivity.this.adapter.setCurrentIndex(MPMMainActivity.this.index);
                return true;
            } else if (swipeDirection != SwipeDirection.LEFT_TO_RIGHT) {
                return false;
            } else {
                StorageUtil storageUtil = new StorageUtil(MPMMainActivity.this);
                MPMMainActivity.this.adapter.notifyDataSetChanged();
                if (!storageUtil.isFav(mPMSongModel)) {
                    storageUtil.storeFav(mPMSongModel);
                    Toast.makeText(MPMMainActivity.this, "Added to Favorite", Toast.LENGTH_SHORT).show();
                } else {
                    storageUtil.removeFav(mPMSongModel);
                    Toast.makeText(MPMMainActivity.this, "Removed from Favorite", Toast.LENGTH_SHORT).show();
                }
                MPMMainActivity.this.updateFav();
                return true;
            }
        }
    }


    public void updateFav() {
        this.homeFragment.updateFav(new StorageUtil(this).getFav());
    }

    private void setUpMusicPlayerPage(ArrayList<MPMSongModel> arrayList, int i) {
        this.dataAdapter = new MPMStatusAdapter(this, arrayList, i, this);
        this.viewPager2.setCurrentItem(arrayList.isEmpty() ? 0 : (1073741823 - (1073741823 % arrayList.size())) + i, false);
        this.total_time.setText(Util.getStringTime(Long.parseLong(arrayList.get(i).getDuration())));
        this.current_time.setText(Util.getStringTime(0L));
        this.seekbar.setProgress(0.0f);
        this.seekbar.setMax((float) Long.parseLong(arrayList.get(i).getDuration()));
    }


    public void changeBottomSheetButton(boolean z) {
        if (z) {
            this.playImage.setImageResource(R.drawable.pause);
            this.play_pause.setImageResource(R.drawable.pause);
            return;
        }
        this.playImage.setImageResource(R.drawable.play);
        this.play_pause.setImageResource(R.drawable.play);
    }


    public void changeBottomSheetData(MPMSongModel mPMSongModel) {
        StorageUtil storageUtil = new StorageUtil(this);
        this.song_title.setText(mPMSongModel.getTitle());
        this.song_artist.setText(mPMSongModel.getArtist());
        this.song_title_bottom_sheet.setText(mPMSongModel.getTitle());
        this.song_artist_bottom_sheet.setText(mPMSongModel.getArtist());
        MediaPlayer mediaPlayer = this.mediaPlayer;
        if (mediaPlayer != null) {
            this.current_time.setText(Util.getStringTime(mediaPlayer.getCurrentPosition()));
        } else {
            this.current_time.setText(Util.getStringTime(0L));
        }
        this.total_time.setText(Util.getStringTime(Long.parseLong(mPMSongModel.getDuration())));
        if (this.serviceBound) {
            MPMMediaPlayerService mPMMediaPlayerService = this.player;
            if (MPMMediaPlayerService.isMediaPlaying()) {
                this.playImage.setImageResource(R.drawable.pause);
            } else {
                this.playImage.setImageResource(R.drawable.play);
            }
        }
        byte[] bArr = null;
        try {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(mPMSongModel.getData());
            bArr = mediaMetadataRetriever.getEmbeddedPicture();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bArr != null) {
            this.imageView.setImageBitmap(BitmapFactory.decodeByteArray(bArr, 0, bArr.length));
        } else {
            Picasso.with(this).load(ContentUris.withAppendedId(Util.sArtworkUri, Long.parseLong(mPMSongModel.getAlbumid()))).placeholder(R.drawable.logo).error(R.drawable.logo).noFade().into(this.imageView);
        }
        this.song_title.setSelected(true);
        if (storageUtil.isFav(mPMSongModel)) {
            this.favImage.setImageResource(R.drawable.fav_selected);
            this.image2.setImageResource(R.drawable.ic_favorite___fill);
            return;
        }
        this.favImage.setImageResource(R.drawable.favourite);
        this.image2.setImageResource(R.drawable.ic_favorite);
    }

    @Override
    public void onSaveInstanceState(Bundle bundle, PersistableBundle persistableBundle) {
        super.onSaveInstanceState(bundle, persistableBundle);
        bundle.putBoolean("serviceStatus", this.serviceBound);
    }

    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        this.serviceBound = bundle.getBoolean("serviceStatus");
    }

    private void addToQueue(ArrayList<MPMSongModel> arrayList, int i) {
        if (!this.serviceBound) {
            StorageUtil storageUtil = new StorageUtil(this);
            this.songModels = storageUtil.loadAudio();
            if (this.songModels == null) {
                this.songModels = new ArrayList<>();
            }
            ArrayList<MPMSongModel> arrayList2 = this.songModels;
            int size = arrayList2 == null ? 0 : arrayList2.size();
            this.songModels.addAll(arrayList);
            storageUtil.storeAudio(this.songModels);
            storageUtil.storeAudioIndex(size);
            MPMStatusAdapter mPMStatusAdapter = this.dataAdapter;
            if (mPMStatusAdapter == null) {
                this.dataAdapter = new MPMStatusAdapter(this, this.songModels, size, this);
            } else {
                mPMStatusAdapter.updateData(this.songModels);
            }
            this.viewPager2.setAdapter(this.dataAdapter);
            this.viewPager2.setCurrentItem(this.songModels.isEmpty() ? 0 : (1073741823 - (1073741823 % this.songModels.size())) + size, false);
            Intent intent = new Intent(this, MPMMediaPlayerService.class);
            if (Build.VERSION.SDK_INT >= 26) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
            this.adapter.setDataSet(this.songModels);
            this.adapter.notifyDataSetChanged();
            bindService(intent, this.serviceConnection, BIND_AUTO_CREATE);
        } else {
            StorageUtil storageUtil2 = new StorageUtil(getApplicationContext());
            this.songModels = storageUtil2.loadAudio();
            if (this.songModels == null) {
                this.songModels = new ArrayList<>();
            }
            ArrayList<MPMSongModel> arrayList3 = this.songModels;
            int size2 = arrayList3 == null ? 0 : arrayList3.size();
            this.songModels.addAll(arrayList);
            storageUtil2.storeAudio(this.songModels);
            storageUtil2.storeAudioIndex(size2);
            MPMStatusAdapter mPMStatusAdapter2 = this.dataAdapter;
            if (mPMStatusAdapter2 == null) {
                this.dataAdapter = new MPMStatusAdapter(this, this.songModels, size2, this);
            } else {
                mPMStatusAdapter2.updateData(this.songModels);
            }
            this.adapter.setDataSet(this.songModels);
            this.adapter.notifyDataSetChanged();
            this.viewPager2.setAdapter(this.dataAdapter);
            this.viewPager2.setCurrentItem(this.songModels.isEmpty() ? 0 : (1073741823 - (1073741823 % this.songModels.size())) + size2, false);
            sendBroadcast(new Intent(Broadcast_PLAY_NEW_AUDIO));
        }
        showBottomSheet();
    }


    public void playSongs(ArrayList<MPMSongModel> arrayList) {
        int i;
        int i2;
        if (!this.serviceBound) {
            StorageUtil storageUtil = new StorageUtil(this);
            this.songModels = new ArrayList<>(arrayList);
            storageUtil.storeAudio(this.songModels);
            storageUtil.storeAudioIndex(0);
            storageUtil.storeSuffledSongs(this.songModels);
            storageUtil.storeCurrent(this.songModels.get(0));
            MPMStatusAdapter mPMStatusAdapter = this.dataAdapter;
            if (mPMStatusAdapter == null) {
                this.dataAdapter = new MPMStatusAdapter(this, this.songModels, this.index, this);
            } else {
                mPMStatusAdapter.updateData(this.songModels);
            }
            this.viewPager2.setAdapter(this.dataAdapter);
            ViewPager2 viewPager2 = this.viewPager2;
            if (this.songModels.isEmpty()) {
                i2 = 0;
            } else {
                i2 = this.index + (1073741823 - (1073741823 % this.songModels.size()));
            }
            viewPager2.setCurrentItem(i2, false);
            Intent intent = new Intent(this, MPMMediaPlayerService.class);
            if (Build.VERSION.SDK_INT >= 26) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
            this.adapter.setDataSet(this.songModels);
            this.adapter.notifyDataSetChanged();
            bindService(intent, this.serviceConnection, BIND_AUTO_CREATE);
        } else {
            StorageUtil storageUtil2 = new StorageUtil(getApplicationContext());
            this.songModels = new ArrayList<>(arrayList);
            storageUtil2.storeAudio(this.songModels);
            storageUtil2.storeAudioIndex(0);
            storageUtil2.storeSuffledSongs(this.songModels);
            storageUtil2.storeCurrent(this.songModels.get(0));
            MPMStatusAdapter mPMStatusAdapter2 = this.dataAdapter;
            if (mPMStatusAdapter2 == null) {
                this.dataAdapter = new MPMStatusAdapter(this, this.songModels, this.index, this);
            } else {
                mPMStatusAdapter2.updateData(this.songModels);
            }
            this.adapter.setDataSet(this.songModels);
            this.adapter.notifyDataSetChanged();
            this.viewPager2.setAdapter(this.dataAdapter);
            ViewPager2 viewPager22 = this.viewPager2;
            if (this.songModels.isEmpty()) {
                i = 0;
            } else {
                i = this.index + (1073741823 - (1073741823 % this.songModels.size()));
            }
            viewPager22.setCurrentItem(i, false);
            sendBroadcast(new Intent(Broadcast_PLAY_NEW_AUDIO));
        }
        showBottomSheet();
    }

    private void showBottomSheet() {
        this.mBottomSheetBehaviour.setPeekHeight(Util.dpToPx(this, 80));
        this.mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void playSingleSong(MPMSongModel mPMSongModel) {
        if (!this.serviceBound) {
            StorageUtil storageUtil = new StorageUtil(this);
            this.songModels = storageUtil.loadAudio();
            if (this.songModels == null) {
                this.songModels = new ArrayList<>();
            }
            ArrayList<MPMSongModel> arrayList = this.songModels;
            int size = arrayList == null ? 0 : arrayList.size();
            this.songModels.add(mPMSongModel);
            storageUtil.storeAudio(this.songModels);
            storageUtil.storeAudioIndex(size);
            MPMStatusAdapter mPMStatusAdapter = this.dataAdapter;
            if (mPMStatusAdapter == null) {
                this.dataAdapter = new MPMStatusAdapter(this, this.songModels, size, this);
            } else {
                mPMStatusAdapter.updateData(this.songModels);
            }
            this.adapter.setDataSet(this.songModels);
            this.adapter.notifyDataSetChanged();
            this.viewPager2.setAdapter(this.dataAdapter);
            this.viewPager2.setCurrentItem(this.songModels.isEmpty() ? 0 : (1073741823 - (1073741823 % this.songModels.size())) + size, false);
            Intent intent = new Intent(this, MPMMediaPlayerService.class);
            if (Build.VERSION.SDK_INT >= 26) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
            bindService(intent, this.serviceConnection, BIND_AUTO_CREATE);
        } else {
            StorageUtil storageUtil2 = new StorageUtil(getApplicationContext());
            this.songModels = storageUtil2.loadAudio();
            if (this.songModels == null) {
                this.songModels = new ArrayList<>();
            }
            ArrayList<MPMSongModel> arrayList2 = this.songModels;
            int size2 = arrayList2 == null ? 0 : arrayList2.size();
            this.songModels.add(mPMSongModel);
            storageUtil2.storeAudio(this.songModels);
            storageUtil2.storeAudioIndex(size2);
            MPMStatusAdapter mPMStatusAdapter2 = this.dataAdapter;
            if (mPMStatusAdapter2 == null) {
                this.dataAdapter = new MPMStatusAdapter(this, this.songModels, size2, this);
            } else {
                mPMStatusAdapter2.updateData(this.songModels);
            }
            this.adapter.setDataSet(this.songModels);
            this.adapter.notifyDataSetChanged();
            this.viewPager2.setAdapter(this.dataAdapter);
            this.viewPager2.setCurrentItem(this.songModels.isEmpty() ? 0 : (1073741823 - (1073741823 % this.songModels.size())) + size2, false);
            sendBroadcast(new Intent(Broadcast_PLAY_NEW_AUDIO));
        }
        showBottomSheet();
    }

    @Override
    public void onSeeMoreClicked(String str, MPMSongModel mPMSongModel, boolean z) {
        if (z) {
            this.bottomNavigationView.setSelectedItemId(R.id.favourite);
            return;
        }
        this.albumSongsFragment = MPMAlbumSongsFragment.getInstance(str, mPMSongModel, false);
        changeFragment(this.albumSongsFragment);
    }


    public void changeFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.container, fragment).commit();
    }

    @Override
    public void onPlayAll(ArrayList<MPMSongModel> arrayList) {
        playSongs(arrayList);
    }

    @Override
    public void onAddtoQueue(ArrayList<MPMSongModel> arrayList) {
        addToQueue(arrayList, 0);
    }

    @Override
    public void OnSingleSongClicked(MPMSongModel mPMSongModel) {
        playSingleSong(mPMSongModel);
    }

    @Override
    public void onPlayNextClicked(MPMSongModel mPMSongModel) {
        int i;
        StorageUtil storageUtil = new StorageUtil(this);
        boolean z = this.sharedPreferences.getBoolean(Util.SHUFFLE, false);
        ArrayList<MPMSongModel> loadAudio = storageUtil.loadAudio();
        ArrayList<MPMSongModel> suffledSongs = storageUtil.getSuffledSongs();
        MPMSongModel current = storageUtil.getCurrent();
        int i2 = 0;
        while (true) {
            if (i2 >= suffledSongs.size()) {
                i = 0;
                break;
            } else if (suffledSongs.get(i2).getData().equals(current.getData())) {
                i = i2 + 1;
                break;
            } else {
                i2++;
            }
        }
        ArrayList<MPMSongModel> arrayList = new ArrayList<>();
        for (int i3 = 0; i3 < i; i3++) {
            arrayList.add(suffledSongs.get(i3));
        }
        arrayList.add(mPMSongModel);
        for (int i4 = i; i4 < suffledSongs.size(); i4++) {
            arrayList.add(suffledSongs.get(i4));
        }
        storageUtil.storeSuffledSongs(arrayList);
        if (z) {
            this.songModels.clear();
            this.songModels.addAll(arrayList);
        }
        int i5 = 0;
        while (true) {
            if (i5 >= loadAudio.size()) {
                break;
            } else if (loadAudio.get(i5).getData().equals(current.getData())) {
                i = i5 + 1;
                break;
            } else {
                i5++;
            }
        }
        arrayList.clear();
        for (int i6 = 0; i6 < i; i6++) {
            arrayList.add(loadAudio.get(i6));
        }
        arrayList.add(mPMSongModel);
        for (int i7 = i; i7 < loadAudio.size(); i7++) {
            arrayList.add(loadAudio.get(i7));
        }
        storageUtil.storeAudio(arrayList);
        if (!z) {
            this.songModels.clear();
            this.songModels.addAll(arrayList);
        }
        if (this.serviceBound) {
            if (z) {
                this.player.setDatas(storageUtil.getSuffledSongs(), storageUtil.loadAudioIndex());
            } else {
                this.player.setDatas(storageUtil.loadAudio(), storageUtil.loadAudioIndex());
            }
        }
        MPMStatusAdapter mPMStatusAdapter = this.dataAdapter;
        if (mPMStatusAdapter == null) {
            this.dataAdapter = new MPMStatusAdapter(this, this.songModels, this.index, this);
        } else {
            mPMStatusAdapter.updateData(this.songModels);
        }
        this.viewPager2.setAdapter(this.dataAdapter);
        this.viewPager2.setCurrentItem(this.songModels.isEmpty() ? 0 : (1073741823 - (1073741823 % this.songModels.size())) + i, false);
        this.adapter.setDataSet(this.songModels);
        this.adapter.notifyDataSetChanged();
    }

    @Override
    public void onAddtoQueue(MPMSongModel mPMSongModel) {
        int i;
        StorageUtil storageUtil = new StorageUtil(this);
        ArrayList<MPMSongModel> loadAudio = storageUtil.loadAudio();
        ArrayList<MPMSongModel> suffledSongs = storageUtil.getSuffledSongs();
        boolean z = this.sharedPreferences.getBoolean(Util.SHUFFLE, false);
        loadAudio.add(mPMSongModel);
        suffledSongs.add(mPMSongModel);
        storageUtil.storeAudio(loadAudio);
        storageUtil.storeSuffledSongs(suffledSongs);
        if (this.serviceBound) {
            if (z) {
                this.player.setDatas(suffledSongs, storageUtil.loadAudioIndex());
            } else {
                this.player.setDatas(loadAudio, storageUtil.loadAudioIndex());
            }
        }
        if (z) {
            this.songModels.clear();
            this.songModels.addAll(suffledSongs);
        } else {
            this.songModels.clear();
            this.songModels.addAll(loadAudio);
        }
        MPMStatusAdapter mPMStatusAdapter = this.dataAdapter;
        if (mPMStatusAdapter == null) {
            this.dataAdapter = new MPMStatusAdapter(this, this.songModels, this.index, this);
        } else {
            mPMStatusAdapter.updateData(this.songModels);
        }
        this.viewPager2.setAdapter(this.dataAdapter);
        ViewPager2 viewPager2 = this.viewPager2;
        if (this.songModels.isEmpty()) {
            i = 0;
        } else {
            i = this.index + (1073741823 - (1073741823 % this.songModels.size()));
        }
        viewPager2.setCurrentItem(i, false);
        this.adapter.setDataSet(this.songModels);
        this.adapter.notifyDataSetChanged();
    }

    @Override
    public void onMoreClicked(final MPMSongModel mPMSongModel) {
        final StorageUtil storageUtil = new StorageUtil(this);
        CharSequence[] charSequenceArr = {"Favorite", "Share", "Add to Playlist", "Play Next", "Add to Queue"};
        int[] iArr = {R.drawable.ic_favorite, R.drawable.share_bottom_sheer, R.drawable.add_to_playlist_circle, R.drawable.ic_play_next, R.drawable.ic_add_to_queue};
        int[] iArr2 = {R.drawable.ic_favorite___fill, R.drawable.share_bottom_sheer, R.drawable.add_to_playlist_circle, R.drawable.ic_play_next, R.drawable.ic_add_to_queue};
        final boolean isFav = storageUtil.isFav(mPMSongModel);
        BottomSheet.Builder builder = new BottomSheet.Builder(this);
        builder.setTitle("Options");
        builder.setContentType(BottomSheet.GRID);
        if (!isFav) {
            iArr2 = iArr;
        }
        builder.setItems(charSequenceArr, iArr2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        if (MPMMainActivity.this.songModel != null) {
                            if (mPMSongModel.getData().equals(MPMMainActivity.this.songModel.getData())) {
                                MPMMainActivity.this.favImage.performClick();
                            } else if (isFav) {
                                storageUtil.removeFav(mPMSongModel);
                            } else {
                                storageUtil.storeFav(mPMSongModel);
                            }
                        } else if (isFav) {
                            storageUtil.removeFav(mPMSongModel);
                        } else {
                            storageUtil.storeFav(mPMSongModel);
                        }
                        if (MPMMainActivity.this.getSupportFragmentManager().findFragmentById(R.id.container) instanceof MPMFavSongsFragment) {
                            MPMMainActivity.this.favFragment.updateData();
                            return;
                        }
                        return;
                    case 1:
                        Util.shareSong(MPMMainActivity.this, mPMSongModel);
                        return;
                    case 2:
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(mPMSongModel);
                        MPMMainActivity.this.changeFragment(MPMPlaylistFragment.getInstance(false, arrayList));
                        return;
                    case 3:
                        MPMMainActivity.this.onPlayNextClicked(mPMSongModel);
                        return;
                    case 4:
                        MPMMainActivity.this.onAddtoQueue(mPMSongModel);
                        return;
                    default:
                        return;
                }
            }
        });
        builder.setWindowDimming(166);
        builder.show();
    }

    @Override
    public void OnAlbumClicked(MPMAlbumModel mPMAlbumModel) {
        this.albumSongsFragment = MPMAlbumSongsFragment.getInstance(mPMAlbumModel.getAlbumName(), mPMAlbumModel.getAlbum_id(), true);
        changeFragment(this.albumSongsFragment);
    }

    @Override
    public void onBackPressed() {
        if (this.multiSelect) {
            if (getSupportFragmentManager().findFragmentById(R.id.container) instanceof MPMAlbumSongsFragment) {
                this.albumSongsFragment.ClearAll();
            } else if (getSupportFragmentManager().findFragmentById(R.id.container) instanceof MPMPlaylistSongsFragment) {
                this.playlistSongFragment.ClearAll();
            }
        } else if (this.bottom_sheet_dialog_behavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            this.bottom_sheet_dialog_behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else if (this.queue_bottom_sheet_behaviour.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            this.queue_bottom_sheet_behaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else if (this.mBottomSheetBehaviour.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
            this.mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            SharedPreferences sharedPreferences = getSharedPreferences("app_rater", 0);
            int i = sharedPreferences.getInt("total_launch_count", 1);
            int i2 = sharedPreferences.getInt("never_count", 1);
            int i3 = sharedPreferences.getInt("rate_count", 1);
            Long valueOf = Long.valueOf(sharedPreferences.getLong("first_launch_date_time", 0L));
            Long valueOf2 = Long.valueOf(sharedPreferences.getLong("launch_date_time", 0L));
            if (valueOf.longValue() == 0) {
                AppRater.app_launched(this, R.layout.rate_us, R.id.never, R.id.remindme, R.id.rate_now);
            } else if (System.currentTimeMillis() < valueOf2.longValue() + 86400000 || i > 5 || i2 > 2 || i3 > 2) {
                moveTaskToBack(true);
            } else {
                AppRater.app_launched(this, R.layout.rate_us, R.id.never, R.id.remindme, R.id.rate_now);
            }
        } else if (getSupportFragmentManager().findFragmentById(R.id.container) instanceof MPMHomeFragment) {
            SharedPreferences sharedPreferences2 = getSharedPreferences("app_rater", 0);
            int i4 = sharedPreferences2.getInt("total_launch_count", 1);
            int i5 = sharedPreferences2.getInt("never_count", 1);
            int i6 = sharedPreferences2.getInt("rate_count", 1);
            Long valueOf3 = Long.valueOf(sharedPreferences2.getLong("first_launch_date_time", 0L));
            Long valueOf4 = Long.valueOf(sharedPreferences2.getLong("launch_date_time", 0L));
            if (valueOf3.longValue() == 0) {
                AppRater.app_launched(this, R.layout.rate_us, R.id.never, R.id.remindme, R.id.rate_now);
            } else if (System.currentTimeMillis() < valueOf4.longValue() + 86400000 || i4 > 5 || i5 > 2 || i6 > 2) {
                moveTaskToBack(true);
            } else {
                AppRater.app_launched(this, R.layout.rate_us, R.id.never, R.id.remindme, R.id.rate_now);
            }
        } else {
            super.onBackPressed();
            if (getSupportFragmentManager().findFragmentById(R.id.container) instanceof MPMAlbumSongsFragment) {
                if (((MPMAlbumSongsFragment) getSupportFragmentManager().findFragmentById(R.id.container)).getFromLibrary()) {
                    if (this.bottomNavigationView.getSelectedItemId() != R.id.library) {
                        this.fromUserTouch = false;
                        this.bottomNavigationView.setSelectedItemId(R.id.library);
                    }
                } else if (this.bottomNavigationView.getSelectedItemId() != R.id.home) {
                    this.fromUserTouch = false;
                    this.bottomNavigationView.setSelectedItemId(R.id.home);
                }
            } else if ((getSupportFragmentManager().findFragmentById(R.id.container) instanceof MPMPlaylistFragment) || (getSupportFragmentManager().findFragmentById(R.id.container) instanceof MPMPlaylistCreatorFragment) || (getSupportFragmentManager().findFragmentById(R.id.container) instanceof MPMPlaylistSongsFragment)) {
                if (this.bottomNavigationView.getSelectedItemId() != R.id.playlist) {
                    this.fromUserTouch = false;
                    this.bottomNavigationView.setSelectedItemId(R.id.playlist);
                }
            } else if (getSupportFragmentManager().findFragmentById(R.id.container) instanceof MPMFavSongsFragment) {
                if (this.bottomNavigationView.getSelectedItemId() != R.id.favourite) {
                    this.fromUserTouch = false;
                    this.bottomNavigationView.setSelectedItemId(R.id.favourite);
                }
            } else if (getSupportFragmentManager().findFragmentById(R.id.container) instanceof MPMHomeFragment) {
                if (this.bottomNavigationView.getSelectedItemId() != R.id.home) {
                    this.fromUserTouch = false;
                    this.bottomNavigationView.setSelectedItemId(R.id.home);
                }
            } else if ((getSupportFragmentManager().findFragmentById(R.id.container) instanceof MPMLibraryFragment) && this.bottomNavigationView.getSelectedItemId() != R.id.library) {
                this.fromUserTouch = false;
                this.bottomNavigationView.setSelectedItemId(R.id.library);
            }
        }
    }


    @Override

    public void onDestroy() {
        super.onDestroy();
        if (this.serviceBound) {
            unbindService(this.serviceConnection);
            this.player.stopSelf();
            this.serviceBound = false;
            this.player = null;
        }
        if (MPMApp.equalizerBound) {
            getApplicationContext().unbindService(MPMApp.equalizerServiceConnection);
            MPMApp.equalizerBound = false;
        }
    }

    @Override
    public void onItemClick(View view, int i) {
        Log.e("itemClick", "hello");
        new StorageUtil(this).storeAudioIndex(i);
        Log.e("itemClick", i + "");
        if (!this.serviceBound) {
            Log.e("itemClick", "hello 1");
            Intent intent = new Intent(this, MPMMediaPlayerService.class);
            if (Build.VERSION.SDK_INT >= 26) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
            bindService(intent, this.serviceConnection, BIND_AUTO_CREATE);
        } else {
            Log.e("itemClick", "hello 2");
            sendBroadcast(new Intent(Broadcast_PLAY_NEW_AUDIO));
        }
        this.updateViewPager = true;
    }

    @Override
    public void onPlayPauseClicked() {
        this.playImage.performClick();
    }

    @Override
    public void onCreatePlaylistClicked(boolean z, @Nullable final ArrayList<MPMSongModel> arrayList) {
        if (z) {
            changeFragment(new MPMPlaylistCreatorFragment());
            return;
        }
        this.dialog = new MPMPlaylistNameDialog(this, "", new MPMPlaylistDialogListener() {
            @Override
            public void onSaveClicked(String str) {
                MPMDatabaseHelper mPMDatabaseHelper = MPMMainActivity.this.databaseHelper;
                int size = arrayList.size();
                ArrayList arrayList2 = arrayList;
                mPMDatabaseHelper.createPlaylist(new MPMPlaylistModel(-1, str, size, arrayList2, ((MPMSongModel) arrayList2.get(0)).getData(), ((MPMSongModel) arrayList.get(0)).getAlbumid()));
                MPMMainActivity.this.dialog.dismiss();
                MPMMainActivity.this.onBackPressed();
                Toast.makeText(MPMMainActivity.this, "Song(s) added to playlist!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelClicked() {
                MPMMainActivity.this.dialog.dismiss();
            }
        });
        this.dialog.show();
    }

    @Override
    public void onPlaylistClicked(MPMPlaylistModel mPMPlaylistModel, boolean z, ArrayList<MPMSongModel> arrayList) {
        if (z) {
            this.playlistSongFragment = MPMPlaylistSongsFragment.getInstance(mPMPlaylistModel);
            changeFragment(this.playlistSongFragment);
            return;
        }
        ArrayList<MPMSongModel> songModels = mPMPlaylistModel.getSongModels();
        songModels.addAll(arrayList);
        mPMPlaylistModel.setSongModels(songModels);
        mPMPlaylistModel.setNo_of_songs(songModels.size());
        this.databaseHelper.editPlaylist(mPMPlaylistModel);
        onBackPressed();
        Toast.makeText(this, "Song(s) added to playlist!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditPlaylist(MPMPlaylistModel mPMPlaylistModel) {
        changeFragment(MPMPlaylistCreatorFragment.getInstance(mPMPlaylistModel));
    }

    @Override
    public void onPlaylistCreated() {
        changeFragment(MPMPlaylistFragment.getInstance(true, null));
    }

    @Override
    public void multiSelect(boolean z) {
        this.multiSelect = z;
        if (!z) {
            this.bottomNavigationView.setVisibility(View.VISIBLE);
            this.multipleSelect.setVisibility(View.GONE);
            return;
        }
        this.multiSelected.clear();
        this.bottomNavigationView.setVisibility(View.INVISIBLE);
        this.multipleSelect.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemSelected(MPMSongModel mPMSongModel) {
        Log.e("added", mPMSongModel.getData());
        this.multiSelected.add(mPMSongModel);
        this.isMultiSelectedFav = this.storageUtil.isFav(this.multiSelected);
        if (this.isMultiSelectedFav) {
            this.fav_multi_select_image.setImageResource(R.drawable.fav_multi_select_pressed);
        } else {
            this.fav_multi_select_image.setImageResource(R.drawable.fav_multi_select);
        }
    }

    @Override
    public void onItemUnselected(MPMSongModel mPMSongModel) {
        this.multiSelected.remove(mPMSongModel);
        this.isMultiSelectedFav = this.storageUtil.isFav(this.multiSelected);
        if (this.isMultiSelectedFav) {
            this.fav_multi_select_image.setImageResource(R.drawable.fav_multi_select_pressed);
        } else {
            this.fav_multi_select_image.setImageResource(R.drawable.fav_multi_select);
        }
    }

    @Override
    public void onFavRemoved(MPMSongModel mPMSongModel) {
        if (mPMSongModel.getData().equals(this.songModel.getData())) {
            this.favImage.setImageResource(R.drawable.favourite);
            this.image2.setImageResource(R.drawable.ic_favorite);
        }
    }


    public class HardButtonReceiver extends BroadcastReceiver {
        public HardButtonReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("TestApp", "Button press received");
            int keyCode = ((KeyEvent) intent.getParcelableExtra("android.intent.extra.KEY_EVENT")).getKeyCode();
            if (keyCode == 87) {
                Log.e("TestApp", "Next Pressed");
            } else if (keyCode == 88) {
                Log.e("TestApp", "Previous pressed");
            } else if (keyCode == 79) {
                Log.e("TestApp", "Head Set Hook pressed");
            }
        }
    }
}
