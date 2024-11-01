package com.demo.music.fragments;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.demo.music.AdAdmob;

import com.demo.music.R;
import com.yalantis.multiselection.lib.MultiSelect;
import com.yalantis.multiselection.lib.MultiSelectBuilder;

import com.demo.music.adapter.MPMLeftAdapter;
import com.demo.music.adapter.MPMRightAdapter;
import com.demo.music.database.MPMDatabaseHelper;
import com.demo.music.dialog.MPMPlaylistNameDialog;
import com.demo.music.interfaces.MPMCallback;
import com.demo.music.interfaces.MPMPlaylistCallbacks;
import com.demo.music.interfaces.MPMPlaylistDialogListener;
import com.demo.music.model.MPMPlaylistModel;
import com.demo.music.model.MPMSongModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;


public class MPMPlaylistCreatorFragment extends Fragment {
    public static String Arg1 = "object";
    public static String Arg2 = "isupdate";
    Activity activity;
    MultiSelectBuilder<MPMSongModel> builder;
    Context context;
    MPMDatabaseHelper databaseHelper;
    MPMPlaylistNameDialog dialog;
    View done;
    boolean isupdate;
    MPMLeftAdapter leftAdapter;
    MPMPlaylistCallbacks listener;
    private MultiSelect<MPMSongModel> mMultiSelect;
    MPMPlaylistModel model;
    View not_found;
    View playlist_container;
    MPMRightAdapter rightAdapter;
    Toolbar toolbar;
    ArrayList<MPMSongModel> songs = new ArrayList<>();
    ArrayList<MPMSongModel> playlistSongs = new ArrayList<>();

    public static MPMPlaylistCreatorFragment getInstance(MPMPlaylistModel mPMPlaylistModel) {
        MPMPlaylistCreatorFragment mPMPlaylistCreatorFragment = new MPMPlaylistCreatorFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Arg1, mPMPlaylistModel);
        bundle.putBoolean(Arg2, true);
        mPMPlaylistCreatorFragment.setArguments(bundle);
        return mPMPlaylistCreatorFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MPMPlaylistCallbacks) {
            this.listener = (MPMPlaylistCallbacks) context;
            return;
        }
        throw new RuntimeException("Attach MPMPlaylistCallbacks listener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_playlist_creator, viewGroup, false);
        this.done = inflate.findViewById(R.id.done);
        this.not_found = inflate.findViewById(R.id.not_found);
        this.playlist_container = inflate.findViewById(R.id.playlist_container);
        this.toolbar = (Toolbar) inflate.findViewById(R.id.toolbar);
        setHasOptionsMenu(true);

        AdAdmob adAdmob = new AdAdmob(getActivity());
        adAdmob.BannerAd((RelativeLayout) inflate.findViewById(R.id.adview), getActivity());

        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);
        if (getArguments() != null) {
            this.isupdate = getArguments().getBoolean(Arg2, false);
            if (this.isupdate) {
                this.model = (MPMPlaylistModel) getArguments().getParcelable(Arg1);
                this.playlistSongs.addAll(this.model.getSongModels());
            }
        }
        this.context = getContext();
        this.activity = getActivity();
        this.databaseHelper = new MPMDatabaseHelper(this.context);
        ((AppCompatActivity) this.activity).setSupportActionBar(this.toolbar);
        ActionBar supportActionBar = ((AppCompatActivity) this.activity).getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setHomeAsUpIndicator(R.drawable.ic_back_1);
        supportActionBar.setDisplayShowTitleEnabled(false);
        this.builder = new MultiSelectBuilder(MPMSongModel.class).withContext(this.context).mountOn((ViewGroup) view.findViewById(R.id.playlist_container)).withSidebarWidth(72.0f);
        setUpAdapters();
        this.done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {
                final ArrayList<MPMSongModel> arrayList = new ArrayList<>((Collection) Objects.requireNonNull(MPMPlaylistCreatorFragment.this.mMultiSelect.getSelectedItems()));
                if (arrayList.isEmpty()) {
                    Toast.makeText(MPMPlaylistCreatorFragment.this.context, "Select Songs", Toast.LENGTH_SHORT).show();
                } else if (MPMPlaylistCreatorFragment.this.isupdate) {
                    Log.e("songssize", arrayList.size() + "");
                    MPMPlaylistCreatorFragment.this.model.setSongModels(arrayList);
                    MPMPlaylistCreatorFragment.this.model.setNo_of_songs(arrayList.size());
                    MPMPlaylistCreatorFragment.this.databaseHelper.editPlaylist(MPMPlaylistCreatorFragment.this.model);
                    MPMPlaylistCreatorFragment.this.listener.onPlaylistCreated();
                } else {
                    MPMPlaylistCreatorFragment mPMPlaylistCreatorFragment = MPMPlaylistCreatorFragment.this;
                    mPMPlaylistCreatorFragment.dialog = new MPMPlaylistNameDialog(mPMPlaylistCreatorFragment.context, "", new MPMPlaylistDialogListener() {
                        @Override
                        public void onSaveClicked(String str) {
                            MPMDatabaseHelper mPMDatabaseHelper = MPMPlaylistCreatorFragment.this.databaseHelper;
                            int size = arrayList.size();
                            ArrayList arrayList2 = arrayList;
                            mPMDatabaseHelper.createPlaylist(new MPMPlaylistModel(-1, str, size, arrayList2, ((MPMSongModel) arrayList2.get(0)).getData(), ((MPMSongModel) arrayList.get(0)).getAlbumid()));
                            MPMPlaylistCreatorFragment.this.listener.onPlaylistCreated();
                            MPMPlaylistCreatorFragment.this.dialog.dismiss();
                        }

                        @Override
                        public void onCancelClicked() {
                            MPMPlaylistCreatorFragment.this.dialog.dismiss();
                        }
                    });
                    MPMPlaylistCreatorFragment.this.dialog.show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            this.activity.onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void setUpAdapters() {
        this.leftAdapter = new MPMLeftAdapter(this.context, new MPMCallback() {
            @Override
            public final void onClick(int i) {
                MPMPlaylistCreatorFragment.this.mMultiSelect.select(i);
            }
        });
        this.rightAdapter = new MPMRightAdapter(this.context, new MPMCallback() {
            @Override
            public final void onClick(int i) {
                MPMPlaylistCreatorFragment.this.mMultiSelect.deselect(i);
            }
        });
        new GetData().execute(new Void[0]);
    }


    public class GetData extends AsyncTask<Void, Void, Void> {
        private GetData() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        public Void doInBackground(Void... voidArr) {
            boolean z;
            Cursor query = MPMPlaylistCreatorFragment.this.context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"title", "_data", "_display_name", "duration", "album", "album_id", "year", "artist", "_size"}, null, null, "title");
            if (!query.moveToFirst()) {
                return null;
            }
            do {
                String string = query.getString(query.getColumnIndex("title"));
                String string2 = query.getString(query.getColumnIndex("_data"));
                String string3 = query.getString(query.getColumnIndex("_display_name"));
                String string4 = query.getString(query.getColumnIndex("duration"));
                String string5 = query.getString(query.getColumnIndex("album_id"));
                String string6 = query.getString(query.getColumnIndex("album"));
                String string7 = query.getString(query.getColumnIndex("year"));
                String string8 = query.getString(query.getColumnIndex("artist"));
                String string9 = query.getString(query.getColumnIndex("_size"));
                if (Long.parseLong(string9) > 25000) {
                    MPMSongModel mPMSongModel = new MPMSongModel(string, string2, string3, string4, string6, string5, string7, string8, Long.parseLong(string9));
                    int i = 0;
                    while (true) {
                        if (i >= MPMPlaylistCreatorFragment.this.playlistSongs.size()) {
                            z = false;
                            break;
                        } else if (MPMPlaylistCreatorFragment.this.playlistSongs.get(i).getData().equals(string2)) {
                            z = true;
                            break;
                        } else {
                            i++;
                        }
                    }
                    if (z) {
                        MPMPlaylistCreatorFragment.this.rightAdapter.add(mPMSongModel, false);
                    } else {
                        MPMPlaylistCreatorFragment.this.leftAdapter.add(mPMSongModel, false);
                    }
                    MPMPlaylistCreatorFragment.this.songs.add(mPMSongModel);
                }
            } while (query.moveToNext());
            return null;
        }


        public void onPostExecute(Void r3) {
            super.onPostExecute(r3);
            if (MPMPlaylistCreatorFragment.this.songs.isEmpty()) {
                MPMPlaylistCreatorFragment.this.playlist_container.setVisibility(View.GONE);
                MPMPlaylistCreatorFragment.this.not_found.setVisibility(View.VISIBLE);
                return;
            }
            MPMPlaylistCreatorFragment.this.playlist_container.setVisibility(View.VISIBLE);
            MPMPlaylistCreatorFragment.this.not_found.setVisibility(View.GONE);
            MPMPlaylistCreatorFragment.this.leftAdapter.notifyDataSetChanged();
            MPMPlaylistCreatorFragment.this.builder.withLeftAdapter(MPMPlaylistCreatorFragment.this.leftAdapter).withRightAdapter(MPMPlaylistCreatorFragment.this.rightAdapter);
            MPMPlaylistCreatorFragment mPMPlaylistCreatorFragment = MPMPlaylistCreatorFragment.this;
            mPMPlaylistCreatorFragment.mMultiSelect = mPMPlaylistCreatorFragment.builder.build();
        }
    }
}
