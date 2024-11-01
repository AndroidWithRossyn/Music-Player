package com.demo.music.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.demo.music.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import com.demo.music.adapter.MPMAlbumSongListAdapter;
import com.demo.music.database.MPMDatabaseHelper;
import com.demo.music.interfaces.MPMMultiSelectListener;
import com.demo.music.interfaces.MPMOnPlayClicked;
import com.demo.music.interfaces.MPMOnSingleSongClicked;
import com.demo.music.model.MPMPlaylistModel;
import com.demo.music.model.MPMSongModel;
import com.demo.music.utils.Util;

import java.util.ArrayList;


public class MPMAlbumSongsFragment extends Fragment {
    private static final String Arg1 = "folder_name";
    private static final String Arg2 = "firstSong";
    private static final String Arg3 = "fromLibrary";
    private static final String Arg4 = "isPlaylist";
    private static final String Arg5 = "playlist";
    Activity activity;
    MPMAlbumSongListAdapter adapter;
    View addToQueue;
    private String albumId;
    private String albumName;
    AppBarLayout appBarLayout;
    ImageView collapsingToolbarImageView;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Context context;
    private MPMSongModel firstSong;
    private String folderName;
    boolean fromLibrary;
    byte[] img;
    boolean isPlaylist;
    MPMOnPlayClicked listener;
    MPMOnSingleSongClicked listener1;
    MPMMultiSelectListener listener2;
    View playAll;
    MPMPlaylistModel playlist;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    MaterialSearchView searchView;
    View share;
    boolean shownSearchView;
    Toolbar toolbar;
    boolean toolbarCollapsed;
    ArrayList<MPMSongModel> songs = new ArrayList<>();
    boolean isMultiSelect = false;
    private boolean mAlreadyLoaded = false;

    public static MPMAlbumSongsFragment getInstance(String str, MPMSongModel mPMSongModel, boolean z) {
        MPMAlbumSongsFragment mPMAlbumSongsFragment = new MPMAlbumSongsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Arg1, str);
        bundle.putParcelable(Arg2, mPMSongModel);
        bundle.putBoolean(Arg3, z);
        bundle.putBoolean(Arg4, false);
        mPMAlbumSongsFragment.setArguments(bundle);
        return mPMAlbumSongsFragment;
    }

    public static MPMAlbumSongsFragment getInstance(String str, String str2, boolean z) {
        MPMAlbumSongsFragment mPMAlbumSongsFragment = new MPMAlbumSongsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Arg1, str);
        bundle.putString(Arg2, str2);
        bundle.putBoolean(Arg3, z);
        bundle.putBoolean(Arg4, false);
        mPMAlbumSongsFragment.setArguments(bundle);
        return mPMAlbumSongsFragment;
    }

    public static MPMAlbumSongsFragment getInstance(MPMPlaylistModel mPMPlaylistModel, boolean z) {
        MPMAlbumSongsFragment mPMAlbumSongsFragment = new MPMAlbumSongsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Arg5, mPMPlaylistModel);
        bundle.putBoolean(Arg4, true);
        mPMAlbumSongsFragment.setArguments(bundle);
        return mPMAlbumSongsFragment;
    }

    @Override 
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MPMOnPlayClicked) {
            this.listener = (MPMOnPlayClicked) context;
            if (context instanceof MPMOnSingleSongClicked) {
                this.listener1 = (MPMOnSingleSongClicked) context;
                if (context instanceof MPMMultiSelectListener) {
                    this.listener2 = (MPMMultiSelectListener) context;
                    return;
                }
                throw new RuntimeException("Attach listener MPMMultiSelectListener");
            }
            throw new RuntimeException("Attach listener");
        }
        throw new RuntimeException("MPMOnPlayClicked listener not attached");
    }

    @Override 
    @Nullable
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_album_song, viewGroup, false);
        this.collapsingToolbarLayout = (CollapsingToolbarLayout) inflate.findViewById(R.id.collapsing_toolbar_layout);
        this.collapsingToolbarImageView = (ImageView) inflate.findViewById(R.id.collapsing_toolbar_image_view);
        this.recyclerView = (RecyclerView) inflate.findViewById(R.id.collapsing_toolbar_recycler_view);
        this.toolbar = (Toolbar) inflate.findViewById(R.id.collapsing_toolbar);
        this.share = inflate.findViewById(R.id.share);
        this.addToQueue = inflate.findViewById(R.id.addToQueue);
        this.playAll = inflate.findViewById(R.id.playAll);
        this.searchView = (MaterialSearchView) inflate.findViewById(R.id.searchView);
        this.appBarLayout = (AppBarLayout) inflate.findViewById(R.id.appBarLayout);
        this.progressBar = (ProgressBar) inflate.findViewById(R.id.progressbar);
        setHasOptionsMenu(true);
        return inflate;
    }

    @Override 
    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);
        if (!this.mAlreadyLoaded) {
            if (getArguments() != null) {
                this.isPlaylist = getArguments().getBoolean(Arg4);
                if (this.isPlaylist) {
                    this.playlist = (MPMPlaylistModel) getArguments().getParcelable(Arg5);
                    this.folderName = this.playlist.getName();
                } else {
                    this.fromLibrary = getArguments().getBoolean(Arg3);
                    if (this.fromLibrary) {
                        this.albumName = getArguments().getString(Arg1);
                        this.albumId = getArguments().getString(Arg2);
                        this.folderName = getArguments().getString(Arg1);
                    } else {
                        this.folderName = getArguments().getString(Arg1);
                        this.firstSong = (MPMSongModel) getArguments().getParcelable(Arg2);
                    }
                }
            }
            this.context = getContext();
            this.activity = getActivity();
            Typeface font = ResourcesCompat.getFont(this.context, R.font.bahnschrift);
            this.collapsingToolbarLayout.setCollapsedTitleTypeface(font);
            this.collapsingToolbarLayout.setExpandedTitleTypeface(font);
            this.share.setOnClickListener(new View.OnClickListener() { 
                @Override 
                public void onClick(View view2) {
                    Util.shareMultipleSongs(MPMAlbumSongsFragment.this.context, MPMAlbumSongsFragment.this.songs);
                }
            });
            this.addToQueue.setOnClickListener(new View.OnClickListener() { 
                @Override 
                public void onClick(View view2) {
                    MPMAlbumSongsFragment.this.listener.onAddtoQueue(MPMAlbumSongsFragment.this.songs);
                }
            });
            this.searchView.setVoiceSearch(false);
            this.searchView.setCursorDrawable(R.drawable.custom_cursor);
            this.searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() { 
                @Override
                
                public void onSearchViewShown() {
                    Log.e("searchView", "helloo");
                    MPMAlbumSongsFragment mPMAlbumSongsFragment = MPMAlbumSongsFragment.this;
                    mPMAlbumSongsFragment.shownSearchView = true;
                    if (mPMAlbumSongsFragment.toolbarCollapsed) {
                        MPMAlbumSongsFragment.this.collapsingToolbarLayout.setTitle(" ");
                    }
                }

                @Override
                
                public void onSearchViewClosed() {
                    Log.e("searchView", "helloo123");
                    MPMAlbumSongsFragment mPMAlbumSongsFragment = MPMAlbumSongsFragment.this;
                    mPMAlbumSongsFragment.shownSearchView = false;
                    mPMAlbumSongsFragment.collapsingToolbarLayout.setTitle(MPMAlbumSongsFragment.this.folderName);
                }
            });
            this.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() { 
                boolean isShow = true;
                int scrollRange = -1;

                @Override
                
                public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                    if (this.scrollRange == -1) {
                        this.scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (this.scrollRange + i == 0) {
                        MPMAlbumSongsFragment mPMAlbumSongsFragment = MPMAlbumSongsFragment.this;
                        mPMAlbumSongsFragment.toolbarCollapsed = true;
                        if (mPMAlbumSongsFragment.shownSearchView) {
                            MPMAlbumSongsFragment.this.collapsingToolbarLayout.setTitle(" ");
                        }
                        this.isShow = true;
                    } else if (this.isShow) {
                        MPMAlbumSongsFragment.this.collapsingToolbarLayout.setTitle(MPMAlbumSongsFragment.this.folderName);
                        MPMAlbumSongsFragment.this.toolbarCollapsed = false;
                        this.isShow = false;
                    }
                }
            });
            this.searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() { 
                @Override
                
                public boolean onQueryTextSubmit(String str) {
                    return false;
                }

                @Override
                
                public boolean onQueryTextChange(String str) {
                    MPMAlbumSongsFragment.this.adapter.filter(str);
                    return false;
                }
            });
            this.playAll.setOnClickListener(new View.OnClickListener() { 
                @Override 
                public void onClick(View view2) {
                    MPMAlbumSongsFragment.this.listener.onPlayAll(MPMAlbumSongsFragment.this.songs);
                }
            });
            ((AppCompatActivity) this.activity).setSupportActionBar(this.toolbar);
            ActionBar supportActionBar = ((AppCompatActivity) this.activity).getSupportActionBar();
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_back_1);
            this.collapsingToolbarLayout.setTitle(this.folderName);
            this.recyclerView.setLayoutManager(new LinearLayoutManager(this.context));
            if (this.isPlaylist) {
                try {
                    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                    mediaMetadataRetriever.setDataSource(this.playlist.getPath());
                    this.img = mediaMetadataRetriever.getEmbeddedPicture();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (this.img != null) {
                    Glide.with(this.context).load(this.img).thumbnail(0.5f).placeholder((int) R.drawable.logo).error(R.drawable.logo).into(this.collapsingToolbarImageView);
                } else {
                    Glide.with(this.context).load(ContentUris.withAppendedId(Util.sArtworkUri, Long.parseLong(this.playlist.getAlbumId()))).thumbnail(0.5f).placeholder((int) R.drawable.logo).error(R.drawable.logo).into(this.collapsingToolbarImageView);
                }
                if (!this.songs.isEmpty()) {
                    this.songs.clear();
                }
                this.songs.addAll(this.playlist.getSongModels());
                this.adapter = new MPMAlbumSongListAdapter(this.context, this.songs, this.listener1, new MPMMultiSelectListener() { 
                    @Override 
                    public void multiSelect(boolean z) {
                        MPMAlbumSongsFragment.this.listener2.multiSelect(z);
                        MPMAlbumSongsFragment mPMAlbumSongsFragment = MPMAlbumSongsFragment.this;
                        mPMAlbumSongsFragment.isMultiSelect = z;
                        mPMAlbumSongsFragment.appBarLayout.setExpanded(!z);
                        MPMAlbumSongsFragment.this.recyclerView.setNestedScrollingEnabled(!z);
                        MPMAlbumSongsFragment.this.activity.invalidateOptionsMenu();
                    }

                    @Override 
                    public void onItemSelected(MPMSongModel mPMSongModel) {
                        MPMAlbumSongsFragment.this.listener2.onItemSelected(mPMSongModel);
                    }

                    @Override 
                    public void onItemUnselected(MPMSongModel mPMSongModel) {
                        MPMAlbumSongsFragment.this.listener2.onItemUnselected(mPMSongModel);
                    }
                });
                this.recyclerView.setAdapter(this.adapter);
            } else if (this.fromLibrary) {
                Glide.with(this.context).load(ContentUris.withAppendedId(Util.sArtworkUri, Long.parseLong(this.albumId))).thumbnail(0.5f).placeholder((int) R.drawable.logo).error(R.drawable.logo).into(this.collapsingToolbarImageView);
                new GetDataForAlbum().execute(new Void[0]);
            } else {
                try {
                    MediaMetadataRetriever mediaMetadataRetriever2 = new MediaMetadataRetriever();
                    mediaMetadataRetriever2.setDataSource(this.firstSong.getData());
                    this.img = mediaMetadataRetriever2.getEmbeddedPicture();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                if (this.img != null) {
                    Glide.with(this.context).load(this.img).thumbnail(0.5f).placeholder((int) R.drawable.logo).error(R.drawable.logo).into(this.collapsingToolbarImageView);
                } else {
                    Glide.with(this.context).load(ContentUris.withAppendedId(Util.sArtworkUri, Long.parseLong(this.firstSong.getAlbumid()))).thumbnail(0.5f).placeholder((int) R.drawable.logo).error(R.drawable.logo).into(this.collapsingToolbarImageView);
                }
                new GetDataForFolder(this.folderName).execute(new Void[0]);
            }
        }
    }

    public boolean getFromLibrary() {
        return this.fromLibrary;
    }

    public void ClearAll() {
        this.adapter.setLongPressed(false);
    }

    @Override 
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_search, menu);
        MenuItem findItem = menu.findItem(R.id.action_search);
        this.searchView.setMenuItem(findItem);
        findItem.setVisible(!this.isMultiSelect);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override 
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            this.activity.onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    
    private class GetDataForFolder extends AsyncTask<Void, Void, Void> {
        String folderName;

        public GetDataForFolder(String str) {
            this.folderName = str;
        }

        @Override 
        protected void onPreExecute() {
            super.onPreExecute();
            MPMAlbumSongsFragment.this.progressBar.setVisibility(View.VISIBLE);
            Log.e("songsSize", MPMAlbumSongsFragment.this.songs.size() + "a");
        }

        
        public Void doInBackground(Void... voidArr) {
            ContentResolver contentResolver = MPMAlbumSongsFragment.this.context.getContentResolver();
            Cursor query = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"title", "_data", "_display_name", "duration", "album_id", "album", "year", "artist", "_size", "date_modified"}, "_data like ? ", new String[]{"%" + this.folderName + "%"}, "date_modified DESC");
            if (!MPMAlbumSongsFragment.this.songs.isEmpty()) {
                MPMAlbumSongsFragment.this.songs.clear();
            }
            Log.e("songsSize", MPMAlbumSongsFragment.this.songs.size() + "a");
            if (!query.moveToFirst()) {
                return null;
            }
            do {
                String string = query.getString(query.getColumnIndex("title"));
                MPMAlbumSongsFragment.this.songs.add(new MPMSongModel(string, query.getString(query.getColumnIndex("_data")), query.getString(query.getColumnIndex("_display_name")), query.getString(query.getColumnIndex("duration")), query.getString(query.getColumnIndex("album")), query.getString(query.getColumnIndex("album_id")), query.getString(query.getColumnIndex("year")), query.getString(query.getColumnIndex("artist")), Long.parseLong(query.getString(query.getColumnIndex("_size")))));
                Log.e(MPMDatabaseHelper.PLAYLIST_TABLE_SONGS, string + MPMAlbumSongsFragment.this.songs.size());
            } while (query.moveToNext());
            return null;
        }

        
        public void onPostExecute(Void r6) {
            super.onPostExecute(r6);
            Log.e("songsSize", MPMAlbumSongsFragment.this.songs.size() + "a");
            MPMAlbumSongsFragment mPMAlbumSongsFragment = MPMAlbumSongsFragment.this;
            mPMAlbumSongsFragment.adapter = new MPMAlbumSongListAdapter(mPMAlbumSongsFragment.context, MPMAlbumSongsFragment.this.songs, MPMAlbumSongsFragment.this.listener1, new MPMMultiSelectListener() { 
                @Override 
                public void multiSelect(boolean z) {
                    MPMAlbumSongsFragment.this.listener2.multiSelect(z);
                    MPMAlbumSongsFragment.this.isMultiSelect = z;
                    MPMAlbumSongsFragment.this.appBarLayout.setExpanded(!z);
                    MPMAlbumSongsFragment.this.recyclerView.setNestedScrollingEnabled(!z);
                    MPMAlbumSongsFragment.this.activity.invalidateOptionsMenu();
                }

                @Override 
                public void onItemSelected(MPMSongModel mPMSongModel) {
                    MPMAlbumSongsFragment.this.listener2.onItemSelected(mPMSongModel);
                }

                @Override 
                public void onItemUnselected(MPMSongModel mPMSongModel) {
                    MPMAlbumSongsFragment.this.listener2.onItemUnselected(mPMSongModel);
                }
            });
            MPMAlbumSongsFragment.this.recyclerView.setAdapter(MPMAlbumSongsFragment.this.adapter);
            MPMAlbumSongsFragment.this.progressBar.setVisibility(View.GONE);
        }
    }

    
    private class GetDataForAlbum extends AsyncTask<Void, Void, Void> {
        private GetDataForAlbum() {
        }

        @Override 
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("songsSize", MPMAlbumSongsFragment.this.songs.size() + "a");
            if (!MPMAlbumSongsFragment.this.songs.isEmpty()) {
                MPMAlbumSongsFragment.this.songs.clear();
            }
            Log.e("songsSize", MPMAlbumSongsFragment.this.songs.size() + "a");
            MPMAlbumSongsFragment.this.progressBar.setVisibility(View.VISIBLE);
        }

        
        public Void doInBackground(Void... voidArr) {
            ContentResolver contentResolver = MPMAlbumSongsFragment.this.context.getContentResolver();
            Cursor query = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"title", "_data", "_display_name", "duration", "album_id", "album", "year", "artist", "_size", "date_modified"}, "album like ? AND album_id like ?", new String[]{"%" + MPMAlbumSongsFragment.this.albumName + "%", "%" + MPMAlbumSongsFragment.this.albumId + "%"}, "date_modified DESC");
            if (!MPMAlbumSongsFragment.this.songs.isEmpty()) {
                MPMAlbumSongsFragment.this.songs.clear();
            }
            Log.e("songsSize", MPMAlbumSongsFragment.this.songs.size() + "a");
            if (!query.moveToFirst()) {
                return null;
            }
            do {
                String string = query.getString(query.getColumnIndex("title"));
                MPMAlbumSongsFragment.this.songs.add(new MPMSongModel(string, query.getString(query.getColumnIndex("_data")), query.getString(query.getColumnIndex("_display_name")), query.getString(query.getColumnIndex("duration")), query.getString(query.getColumnIndex("album")), query.getString(query.getColumnIndex("album_id")), query.getString(query.getColumnIndex("year")), query.getString(query.getColumnIndex("artist")), Long.parseLong(query.getString(query.getColumnIndex("_size")))));
                Log.e(MPMDatabaseHelper.PLAYLIST_TABLE_SONGS, string);
            } while (query.moveToNext());
            return null;
        }

        
        public void onPostExecute(Void r6) {
            super.onPostExecute(r6);
            Log.e("songsSize", MPMAlbumSongsFragment.this.songs.size() + "a");
            MPMAlbumSongsFragment mPMAlbumSongsFragment = MPMAlbumSongsFragment.this;
            mPMAlbumSongsFragment.adapter = new MPMAlbumSongListAdapter(mPMAlbumSongsFragment.context, MPMAlbumSongsFragment.this.songs, MPMAlbumSongsFragment.this.listener1, new MPMMultiSelectListener() { 
                @Override 
                public void multiSelect(boolean z) {
                    MPMAlbumSongsFragment.this.listener2.multiSelect(z);
                    MPMAlbumSongsFragment.this.isMultiSelect = z;
                    MPMAlbumSongsFragment.this.appBarLayout.setExpanded(!z);
                    MPMAlbumSongsFragment.this.recyclerView.setNestedScrollingEnabled(!z);
                    MPMAlbumSongsFragment.this.activity.invalidateOptionsMenu();
                }

                @Override 
                public void onItemSelected(MPMSongModel mPMSongModel) {
                    MPMAlbumSongsFragment.this.listener2.onItemSelected(mPMSongModel);
                }

                @Override 
                public void onItemUnselected(MPMSongModel mPMSongModel) {
                    MPMAlbumSongsFragment.this.listener2.onItemUnselected(mPMSongModel);
                }
            });
            MPMAlbumSongsFragment.this.recyclerView.setAdapter(MPMAlbumSongsFragment.this.adapter);
            MPMAlbumSongsFragment.this.progressBar.setVisibility(View.GONE);
        }
    }
}
