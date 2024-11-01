package com.demo.music.fragments;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.demo.music.R;
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeAdapter;
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeRecyclerView;
import com.ernestoyaquello.dragdropswiperecyclerview.listener.OnItemSwipeListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import com.demo.music.adapter.MPMFavAdapter;
import com.demo.music.interfaces.MPMOnFavRemoved;
import com.demo.music.interfaces.MPMOnPlayClicked;
import com.demo.music.interfaces.MPMOnSingleSongClicked;
import com.demo.music.model.MPMSongModel;
import com.demo.music.utils.StorageUtil;
import com.demo.music.utils.Util;

import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;


public class MPMFavSongsFragment extends Fragment {
    public static String Arg1 = "Song";
    Activity activity;
    MPMFavAdapter adapter;
    View addToPlaylist;
    View addToQueue;
    AppBarLayout appBarLayout;
    ImageView collapsingToolbarImageView;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Context context;
    private MPMSongModel firstSong;
    String folderName;
    byte[] img;
    View lay_1;
    MPMOnPlayClicked listener;
    MPMOnSingleSongClicked listener1;
    MPMOnFavRemoved listener2;
    View not_found;
    View playAll;
    ProgressBar progressBar;
    DragDropSwipeRecyclerView recyclerView;
    MaterialSearchView searchView;
    View share;
    boolean shownSearchView;
    ArrayList<MPMSongModel> songs = new ArrayList<>();
    StorageUtil storageUtil;
    Toolbar toolbar;
    boolean toolbarCollapsed;

    @Override 
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MPMOnPlayClicked) {
            this.listener = (MPMOnPlayClicked) context;
            if (context instanceof MPMOnSingleSongClicked) {
                this.listener1 = (MPMOnSingleSongClicked) context;
                if (context instanceof MPMOnFavRemoved) {
                    this.listener2 = (MPMOnFavRemoved) context;
                    return;
                }
                throw new RuntimeException("Attach listener MPMOnFavRemoved");
            }
            throw new RuntimeException("Attach listener");
        }
        throw new RuntimeException("MPMOnPlayClicked listener not attached");
    }

    @Override 
    @Nullable
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_fav_songs, viewGroup, false);
        this.collapsingToolbarLayout = (CollapsingToolbarLayout) inflate.findViewById(R.id.collapsing_toolbar_layout);
        this.collapsingToolbarImageView = (ImageView) inflate.findViewById(R.id.collapsing_toolbar_image_view);
        this.recyclerView = (DragDropSwipeRecyclerView) inflate.findViewById(R.id.collapsing_toolbar_recycler_view);
        this.toolbar = (Toolbar) inflate.findViewById(R.id.collapsing_toolbar);
        this.share = inflate.findViewById(R.id.share);
        this.addToQueue = inflate.findViewById(R.id.addToQueue);
        this.playAll = inflate.findViewById(R.id.playAll);
        this.searchView = (MaterialSearchView) inflate.findViewById(R.id.searchView);
        this.appBarLayout = (AppBarLayout) inflate.findViewById(R.id.appBarLayout);
        this.progressBar = (ProgressBar) inflate.findViewById(R.id.progressbar);
        this.addToPlaylist = inflate.findViewById(R.id.addToPlaylist);
        this.not_found = inflate.findViewById(R.id.not_found);
        this.lay_1 = inflate.findViewById(R.id.lay_1);
        setHasOptionsMenu(true);
        return inflate;
    }

    @Override 
    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.folderName = "Favorite";
        this.context = getContext();
        this.activity = getActivity();
        this.storageUtil = new StorageUtil(this.context);
        Typeface font = ResourcesCompat.getFont(this.context, R.font.bahnschrift);
        this.collapsingToolbarLayout.setCollapsedTitleTypeface(font);
        this.collapsingToolbarLayout.setExpandedTitleTypeface(font);
        if (!this.songs.isEmpty()) {
            this.songs.clear();
        }
        this.songs.addAll(this.storageUtil.getFav());
        if (this.songs.isEmpty()) {
            this.firstSong = null;
        } else {
            this.firstSong = this.songs.get(0);
        }
        this.share.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view2) {
                Util.shareMultipleSongs(MPMFavSongsFragment.this.context, MPMFavSongsFragment.this.songs);
            }
        });
        this.addToQueue.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view2) {
                MPMFavSongsFragment.this.listener.onAddtoQueue(MPMFavSongsFragment.this.songs);
            }
        });
        if (this.songs.isEmpty()) {
            this.not_found.setVisibility(View.VISIBLE);
            this.recyclerView.setVisibility(View.GONE);
            this.appBarLayout.setExpanded(false, false);
            this.lay_1.setVisibility(View.GONE);
        } else {
            this.not_found.setVisibility(View.GONE);
            this.recyclerView.setVisibility(View.VISIBLE);
            this.appBarLayout.setExpanded(true, false);
            this.lay_1.setVisibility(View.VISIBLE);
        }
        this.searchView.setVoiceSearch(false);
        this.searchView.setCursorDrawable(R.drawable.custom_cursor);
        this.searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() { 
            @Override 
            public void onSearchViewShown() {
                Log.e("searchView", "helloo");
                MPMFavSongsFragment mPMFavSongsFragment = MPMFavSongsFragment.this;
                mPMFavSongsFragment.shownSearchView = true;
                if (mPMFavSongsFragment.toolbarCollapsed) {
                    MPMFavSongsFragment.this.collapsingToolbarLayout.setTitle(" ");
                }
            }

            @Override 
            public void onSearchViewClosed() {
                Log.e("searchView", "helloo123");
                MPMFavSongsFragment mPMFavSongsFragment = MPMFavSongsFragment.this;
                mPMFavSongsFragment.shownSearchView = false;
                mPMFavSongsFragment.collapsingToolbarLayout.setTitle(MPMFavSongsFragment.this.folderName);
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
                    MPMFavSongsFragment mPMFavSongsFragment = MPMFavSongsFragment.this;
                    mPMFavSongsFragment.toolbarCollapsed = true;
                    if (mPMFavSongsFragment.shownSearchView) {
                        MPMFavSongsFragment.this.collapsingToolbarLayout.setTitle(" ");
                    }
                    this.isShow = true;
                } else if (this.isShow) {
                    MPMFavSongsFragment.this.collapsingToolbarLayout.setTitle(MPMFavSongsFragment.this.folderName);
                    MPMFavSongsFragment.this.toolbarCollapsed = false;
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
                MPMFavSongsFragment.this.adapter.filter(MPMFavSongsFragment.this.songs, str);
                return false;
            }
        });
        this.playAll.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view2) {
                MPMFavSongsFragment.this.listener.onPlayAll(MPMFavSongsFragment.this.songs);
            }
        });
        ((AppCompatActivity) this.activity).setSupportActionBar(this.toolbar);
        this.collapsingToolbarLayout.setTitle(this.folderName);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this.context));
        this.recyclerView.setOrientation(DragDropSwipeRecyclerView.ListOrientation.VERTICAL_LIST_WITH_VERTICAL_DRAGGING);

        this.recyclerView.setSwipeListener(new OnItemSwipeListener<MPMSongModel>() { 
            public boolean onItemSwiped(int i, @NotNull SwipeDirection swipeDirection, MPMSongModel mPMSongModel) {
                MPMFavSongsFragment.this.storageUtil.removeFav(mPMSongModel);
                MPMFavSongsFragment.this.listener2.onFavRemoved(mPMSongModel);
                Log.e("size", MPMFavSongsFragment.this.adapter.getDataSet().size() + "");
                if (MPMFavSongsFragment.this.adapter.getDataSet().size() == 1) {
                    MPMFavSongsFragment.this.not_found.setVisibility(View.VISIBLE);
                    MPMFavSongsFragment.this.recyclerView.setVisibility(View.GONE);
                    MPMFavSongsFragment.this.appBarLayout.setExpanded(false, false);
                    MPMFavSongsFragment.this.lay_1.setVisibility(View.GONE);
                } else {
                    MPMFavSongsFragment.this.not_found.setVisibility(View.GONE);
                    MPMFavSongsFragment.this.recyclerView.setVisibility(View.VISIBLE);
                    MPMFavSongsFragment.this.appBarLayout.setExpanded(true, false);
                    MPMFavSongsFragment.this.lay_1.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
        if (this.firstSong != null) {
            try {
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(this.firstSong.getData());
                this.img = mediaMetadataRetriever.getEmbeddedPicture();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (this.img != null) {
                Glide.with(this.context).load(this.img).thumbnail(0.5f).placeholder((int) R.drawable.logo).error(R.drawable.logo).into(this.collapsingToolbarImageView);
            } else {
                Glide.with(this.context).load(ContentUris.withAppendedId(Util.sArtworkUri, Long.parseLong(this.firstSong.getAlbumid()))).thumbnail(0.5f).placeholder((int) R.drawable.logo).error(R.drawable.logo).into(this.collapsingToolbarImageView);
            }
        }
        this.adapter = new MPMFavAdapter(this.context, this.songs, this.listener1);
        this.recyclerView.setAdapter((DragDropSwipeAdapter<?, ?>) this.adapter);
    }

    public void updateData() {
        if (this.storageUtil != null) {
            this.storageUtil = new StorageUtil(this.context);
            this.songs = this.storageUtil.getFav();
            this.adapter.setDataSet(this.songs);
            if (this.songs.isEmpty()) {
                this.not_found.setVisibility(View.VISIBLE);
                this.recyclerView.setVisibility(View.GONE);
                this.appBarLayout.setExpanded(false, false);
                this.lay_1.setVisibility(View.GONE);
                return;
            }
            this.not_found.setVisibility(View.GONE);
            this.recyclerView.setVisibility(View.VISIBLE);
            this.appBarLayout.setExpanded(true, false);
            this.lay_1.setVisibility(View.VISIBLE);
        }
    }
}
