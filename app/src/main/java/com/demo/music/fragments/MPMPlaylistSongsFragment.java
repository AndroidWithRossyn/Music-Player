package com.demo.music.fragments;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
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
import com.demo.music.interfaces.MPMMultiSelectListener;
import com.demo.music.interfaces.MPMOnPlayClicked;
import com.demo.music.interfaces.MPMOnSingleSongClicked;
import com.demo.music.model.MPMPlaylistModel;
import com.demo.music.model.MPMSongModel;
import com.demo.music.utils.StorageUtil;
import com.demo.music.utils.Util;
import java.util.ArrayList;


public class MPMPlaylistSongsFragment extends Fragment {
    public static String Arg1 = "Song";
    Activity activity;
    MPMAlbumSongListAdapter adapter;
    View addToPlaylist;
    View addToQueue;
    AppBarLayout appBarLayout;
    ImageView collapsingToolbarImageView;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Context context;
    private MPMSongModel firstSong;
    String folderName;
    byte[] img;
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
    StorageUtil storageUtil;
    Toolbar toolbar;
    boolean toolbarCollapsed;
    ArrayList<MPMSongModel> songs = new ArrayList<>();
    boolean isMultiSelect = false;

    public static MPMPlaylistSongsFragment getInstance(MPMPlaylistModel mPMPlaylistModel) {
        MPMPlaylistSongsFragment mPMPlaylistSongsFragment = new MPMPlaylistSongsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Arg1, mPMPlaylistModel);
        mPMPlaylistSongsFragment.setArguments(bundle);
        return mPMPlaylistSongsFragment;
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
        View inflate = layoutInflater.inflate(R.layout.fragment_playlisst_songs, viewGroup, false);
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
        this.addToPlaylist = inflate.findViewById(R.id.addToPlaylist);
        setHasOptionsMenu(true);
        return inflate;
    }

    @Override 
    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);
        if (getArguments() != null) {
            this.playlist = (MPMPlaylistModel) getArguments().getParcelable(Arg1);
            this.folderName = this.playlist.getName();
        }
        this.context = getContext();
        this.activity = getActivity();
        this.storageUtil = new StorageUtil(this.context);
        Typeface font = ResourcesCompat.getFont(this.context, R.font.bahnschrift);
        this.collapsingToolbarLayout.setCollapsedTitleTypeface(font);
        this.collapsingToolbarLayout.setExpandedTitleTypeface(font);
        this.share.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view2) {
                Util.shareMultipleSongs(MPMPlaylistSongsFragment.this.context, MPMPlaylistSongsFragment.this.songs);
            }
        });
        this.addToQueue.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view2) {
                MPMPlaylistSongsFragment.this.listener.onAddtoQueue(MPMPlaylistSongsFragment.this.songs);
            }
        });
        this.addToPlaylist.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view2) {
                MPMPlaylistSongsFragment.this.listener.onEditPlaylist(MPMPlaylistSongsFragment.this.playlist);
            }
        });
        this.searchView.setVoiceSearch(false);
        this.searchView.setCursorDrawable(R.drawable.custom_cursor);
        this.searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() { 
            @Override 
            public void onSearchViewShown() {
                Log.e("searchView", "helloo");
                MPMPlaylistSongsFragment mPMPlaylistSongsFragment = MPMPlaylistSongsFragment.this;
                mPMPlaylistSongsFragment.shownSearchView = true;
                if (mPMPlaylistSongsFragment.toolbarCollapsed) {
                    MPMPlaylistSongsFragment.this.collapsingToolbarLayout.setTitle(" ");
                }
            }

            @Override 
            public void onSearchViewClosed() {
                Log.e("searchView", "helloo123");
                MPMPlaylistSongsFragment mPMPlaylistSongsFragment = MPMPlaylistSongsFragment.this;
                mPMPlaylistSongsFragment.shownSearchView = false;
                mPMPlaylistSongsFragment.collapsingToolbarLayout.setTitle(MPMPlaylistSongsFragment.this.folderName);
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
                    MPMPlaylistSongsFragment mPMPlaylistSongsFragment = MPMPlaylistSongsFragment.this;
                    mPMPlaylistSongsFragment.toolbarCollapsed = true;
                    if (mPMPlaylistSongsFragment.shownSearchView) {
                        MPMPlaylistSongsFragment.this.collapsingToolbarLayout.setTitle(" ");
                    }
                    this.isShow = true;
                } else if (this.isShow) {
                    MPMPlaylistSongsFragment.this.collapsingToolbarLayout.setTitle(MPMPlaylistSongsFragment.this.folderName);
                    MPMPlaylistSongsFragment.this.toolbarCollapsed = false;
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
                MPMPlaylistSongsFragment.this.adapter.filter(str);
                return false;
            }
        });
        this.playAll.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view2) {
                MPMPlaylistSongsFragment.this.listener.onPlayAll(MPMPlaylistSongsFragment.this.songs);
            }
        });
        ((AppCompatActivity) this.activity).setSupportActionBar(this.toolbar);
        ActionBar supportActionBar = ((AppCompatActivity) this.activity).getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setHomeAsUpIndicator(R.drawable.ic_back_1);
        this.collapsingToolbarLayout.setTitle(this.folderName);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this.context));
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
                MPMPlaylistSongsFragment.this.listener2.multiSelect(z);
                MPMPlaylistSongsFragment mPMPlaylistSongsFragment = MPMPlaylistSongsFragment.this;
                mPMPlaylistSongsFragment.isMultiSelect = z;
                mPMPlaylistSongsFragment.appBarLayout.setExpanded(!z);
                MPMPlaylistSongsFragment.this.recyclerView.setNestedScrollingEnabled(!z);
                MPMPlaylistSongsFragment.this.activity.invalidateOptionsMenu();
            }

            @Override 
            public void onItemSelected(MPMSongModel mPMSongModel) {
                MPMPlaylistSongsFragment.this.listener2.onItemSelected(mPMSongModel);
            }

            @Override 
            public void onItemUnselected(MPMSongModel mPMSongModel) {
                MPMPlaylistSongsFragment.this.listener2.onItemUnselected(mPMSongModel);
            }
        });
        this.recyclerView.setAdapter(this.adapter);
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

    public void ClearAll() {
        this.adapter.setLongPressed(false);
    }
}
