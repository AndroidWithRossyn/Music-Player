package com.demo.music.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.music.AdAdmob;
import com.demo.music.R;


import com.demo.music.adapter.MPMLibraryAdapter;
import com.demo.music.interfaces.MPMOnAlbumClicked;
import com.demo.music.model.MPMAlbumModel;
import java.util.ArrayList;


public class MPMLibraryFragment extends Fragment {
    MPMLibraryAdapter adapter;
    Context context;
    MPMOnAlbumClicked listener;
    View not_found;
    ProgressBar progressbar;
    RecyclerView recyclerView;
    ArrayList<MPMAlbumModel> albumList = new ArrayList<>();
    ArrayList<String> albumId = new ArrayList<>();

    @Override 
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MPMOnAlbumClicked) {
            this.listener = (MPMOnAlbumClicked) context;
            return;
        }
        throw new RuntimeException("Attach listener");
    }

    @Override 
    @Nullable
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_album, viewGroup, false);
        this.recyclerView = (RecyclerView) inflate.findViewById(R.id.recyclerView);
        this.not_found = inflate.findViewById(R.id.not_found);
        this.progressbar = (ProgressBar) inflate.findViewById(R.id.progressbar);


        AdAdmob adAdmob = new AdAdmob(getActivity());
        adAdmob.BannerAd((RelativeLayout) inflate.findViewById(R.id.adview), getActivity());






        return inflate;
    }

    @Override 
    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.context = getContext();
        this.recyclerView.setLayoutManager(new GridLayoutManager(this.context, 3));
        new GetData().execute(new Void[0]);
    }

    
    private class GetData extends AsyncTask<Void, Void, Void> {
        private GetData() {
        }

        @Override 
        protected void onPreExecute() {
            super.onPreExecute();
            MPMLibraryFragment.this.progressbar.setVisibility(View.VISIBLE);
        }

        
        public Void doInBackground(Void... voidArr) {
            Cursor query = MPMLibraryFragment.this.context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"album_id", "album", "artist"}, null, null, "date_modified DESC");
            if (!query.moveToFirst()) {
                return null;
            }
            do {
                String string = query.getString(query.getColumnIndex("album_id"));
                MPMAlbumModel mPMAlbumModel = new MPMAlbumModel(query.getString(query.getColumnIndex("album")), string, query.getString(query.getColumnIndex("artist")));
                if (!MPMLibraryFragment.this.albumId.contains(string)) {
                    MPMLibraryFragment.this.albumId.add(string);
                    MPMLibraryFragment.this.albumList.add(mPMAlbumModel);
                }
            } while (query.moveToNext());
            return null;
        }

        
        public void onPostExecute(Void r6) {
            super.onPostExecute( r6);
            if (MPMLibraryFragment.this.albumList.isEmpty()) {
                MPMLibraryFragment.this.not_found.setVisibility(View.VISIBLE);
                MPMLibraryFragment.this.recyclerView.setVisibility(View.GONE);
            } else {
                MPMLibraryFragment.this.not_found.setVisibility(View.GONE);
                MPMLibraryFragment.this.recyclerView.setVisibility(View.VISIBLE);
                MPMLibraryFragment mPMLibraryFragment = MPMLibraryFragment.this;
                mPMLibraryFragment.adapter = new MPMLibraryAdapter(mPMLibraryFragment.context, MPMLibraryFragment.this.albumList, MPMLibraryFragment.this.listener);
                MPMLibraryFragment.this.recyclerView.setAdapter(MPMLibraryFragment.this.adapter);
            }
            MPMLibraryFragment.this.progressbar.setVisibility(View.GONE);
        }
    }
}
