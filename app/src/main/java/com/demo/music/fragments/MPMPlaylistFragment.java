package com.demo.music.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.music.AdAdmob;
import com.demo.music.R;


import com.demo.music.adapter.MPMPlaylistAdapter;
import com.demo.music.database.MPMDatabaseHelper;
import com.demo.music.dialog.MPMPlaylistNameDialog;
import com.demo.music.interfaces.MPMPlaylistCallbacks;
import com.demo.music.interfaces.MPMPlaylistDialogListener;
import com.demo.music.interfaces.MPMSinglePlaylistCallback;
import com.demo.music.model.MPMPlaylistModel;
import com.demo.music.model.MPMSongModel;

import java.util.ArrayList;


public class MPMPlaylistFragment extends Fragment {
    public static String Arg1 = "showIcons";
    public static String Arg2 = "song";
    MPMPlaylistAdapter adapter;
    Context context;
    View create_playlist;
    MPMDatabaseHelper databaseHelper;
    MPMPlaylistNameDialog dialog;
    MPMPlaylistCallbacks listener;
    View not_found;
    RecyclerView recyclerView;
    boolean showIcons;
    ArrayList<MPMPlaylistModel> playlists = new ArrayList<>();
    ArrayList<MPMSongModel> songs = new ArrayList<>();

    public static MPMPlaylistFragment getInstance(boolean z, ArrayList<MPMSongModel> arrayList) {
        MPMPlaylistFragment mPMPlaylistFragment = new MPMPlaylistFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Arg1, z);
        bundle.putParcelableArrayList(Arg2, arrayList);
        mPMPlaylistFragment.setArguments(bundle);
        return mPMPlaylistFragment;
    }

    @Override 
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MPMPlaylistCallbacks) {
            this.listener = (MPMPlaylistCallbacks) context;
            return;
        }
        throw new RuntimeException("Attach MPMPlaylistCallbacks listener to MPMMainActivity");
    }

    @Override 
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    @Override 
    @Nullable
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_playlist, viewGroup, false);
        this.recyclerView = (RecyclerView) inflate.findViewById(R.id.recyclerView);
        this.create_playlist = inflate.findViewById(R.id.create_playlist);
        this.not_found = inflate.findViewById(R.id.not_found);


        AdAdmob adAdmob = new AdAdmob(getActivity());
        adAdmob.BannerAd((RelativeLayout) inflate.findViewById(R.id.adview), getActivity());

        return inflate;
    }

    @Override 
    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.context = getContext();
        this.databaseHelper = new MPMDatabaseHelper(this.context);
        if (getArguments() != null) {
            this.showIcons = getArguments().getBoolean(Arg1);
            this.songs = getArguments().getParcelableArrayList(Arg2);
        }
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this.context));
        new GetData().execute(new Void[0]);
        this.create_playlist.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view2) {
                MPMPlaylistFragment.this.listener.onCreatePlaylistClicked(MPMPlaylistFragment.this.showIcons, MPMPlaylistFragment.this.songs);
            }
        });
    }

    
    private class GetData extends AsyncTask<Void, Void, Void> {
        private GetData() {
        }

        @Override 
        protected void onPreExecute() {
            super.onPreExecute();
        }

        
        public Void doInBackground(Void... voidArr) {
            MPMPlaylistFragment mPMPlaylistFragment = MPMPlaylistFragment.this;
            mPMPlaylistFragment.playlists = mPMPlaylistFragment.databaseHelper.getPlaylists();
            return null;
        }

        
        public void onPostExecute(Void r9) {
            super.onPostExecute(r9);
            if (MPMPlaylistFragment.this.playlists.isEmpty()) {
                MPMPlaylistFragment.this.not_found.setVisibility(View.VISIBLE);
                MPMPlaylistFragment.this.recyclerView.setVisibility(View.GONE);
                return;
            }
            MPMPlaylistFragment.this.not_found.setVisibility(View.GONE);
            MPMPlaylistFragment.this.recyclerView.setVisibility(View.VISIBLE);
            MPMPlaylistFragment mPMPlaylistFragment = MPMPlaylistFragment.this;
            mPMPlaylistFragment.adapter = new MPMPlaylistAdapter(mPMPlaylistFragment.context, MPMPlaylistFragment.this.showIcons, MPMPlaylistFragment.this.songs, MPMPlaylistFragment.this.playlists, MPMPlaylistFragment.this.listener, new MPMSinglePlaylistCallback() { 
                @Override 
                public void onPlaylistEditClicked(int i, final MPMPlaylistModel mPMPlaylistModel) {
                    MPMPlaylistFragment.this.dialog = new MPMPlaylistNameDialog(MPMPlaylistFragment.this.context, mPMPlaylistModel.getName(), new MPMPlaylistDialogListener() { 
                        @Override 
                        public void onSaveClicked(String str) {
                            mPMPlaylistModel.setName(str);
                            MPMPlaylistFragment.this.databaseHelper.editPlaylist(mPMPlaylistModel);
                            MPMPlaylistFragment.this.listener.onPlaylistCreated();
                            MPMPlaylistFragment.this.dialog.dismiss();
                        }

                        @Override 
                        public void onCancelClicked() {
                            MPMPlaylistFragment.this.dialog.dismiss();
                        }
                    });
                    MPMPlaylistFragment.this.dialog.show();
                }

                @Override 
                public void onPlaylistDeleteClicked(final int i, final MPMPlaylistModel mPMPlaylistModel) {
                    new AlertDialog.Builder(MPMPlaylistFragment.this.context).setMessage("Do you want to delete this playlist?").setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() { 
                        @Override 
                        public void onClick(DialogInterface dialogInterface, int i2) {
                            MPMPlaylistFragment.this.databaseHelper.deletePlaylist(mPMPlaylistModel);
                            MPMPlaylistFragment.this.playlists.remove(i);
                            MPMPlaylistFragment.this.adapter.notifyItemRemoved(i);
                            if (MPMPlaylistFragment.this.playlists.isEmpty()) {
                                MPMPlaylistFragment.this.recyclerView.setVisibility(View.GONE);
                                MPMPlaylistFragment.this.not_found.setVisibility(View.VISIBLE);
                            }
                            dialogInterface.dismiss();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() { 
                        @Override 
                        public void onClick(DialogInterface dialogInterface, int i2) {
                            dialogInterface.dismiss();
                        }
                    }).show();
                }
            });
            MPMPlaylistFragment.this.recyclerView.setAdapter(MPMPlaylistFragment.this.adapter);
        }
    }
}
