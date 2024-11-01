package com.demo.music.fragments;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.music.AdAdmob;
import com.demo.music.R;


import com.demo.music.adapter.MPMFolderAdapter;
import com.demo.music.database.MPMDatabaseHelper;
import com.demo.music.interfaces.MPMOnSingleSongClicked;
import com.demo.music.interfaces.MPMonSeeMoreClicked;
import com.demo.music.model.MPMFolderModel;
import com.demo.music.model.MPMSongModel;
import com.demo.music.utils.StorageUtil;

import java.io.File;
import java.util.ArrayList;


public class MPMHomeFragment extends Fragment {
    Activity activity;
    MPMFolderAdapter adapter;
    Context context;
    MPMonSeeMoreClicked listener;
    MPMOnSingleSongClicked listener1;
    View more;
    View not_found;
    ProgressBar progressbar;
    RecyclerView recyclerView;
    StorageUtil storageUtil;
    ArrayList<MPMFolderModel> folderList = new ArrayList<>();
    ArrayList<File> filesList = new ArrayList<>();
    boolean isFavAdded = false;

    @Override 
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MPMonSeeMoreClicked) {
            this.listener = (MPMonSeeMoreClicked) context;
            if (context instanceof MPMOnSingleSongClicked) {
                this.listener1 = (MPMOnSingleSongClicked) context;
                return;
            }
            throw new RuntimeException("Attach listener");
        }
        throw new RuntimeException("Attach listener");
    }

    @Override 
    @Nullable
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_home, viewGroup, false);
        this.recyclerView = (RecyclerView) inflate.findViewById(R.id.recyclerView);
        this.more = inflate.findViewById(R.id.more);
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
        this.activity = getActivity();
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this.context));
        this.storageUtil = new StorageUtil(this.context);
        this.more.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view2) {
                PopupMenu popupMenu = new PopupMenu(MPMHomeFragment.this.context, view2);
                MenuInflater menuInflater = popupMenu.getMenuInflater();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() { 
                    @Override 
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int itemId = menuItem.getItemId();
                        if (itemId == R.id.privacy) {

                            startActivity(new Intent("android.intent.action.VIEW", Uri.parse(getResources().getString(R.string.privacypolicy))));

                            return false;
                        } else if (itemId == R.id.rate) {
                            String packageName = MPMHomeFragment.this.context.getPackageName();
                            try {
                                MPMHomeFragment.this.context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + packageName)));
                                return false;
                            } catch (ActivityNotFoundException unused) {
                                MPMHomeFragment.this.context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)));
                                return false;
                            }
                        } else if (itemId != R.id.share) {
                            return false;
                        } else {
                            try {
                                Intent intent = new Intent("android.intent.action.SEND");
                                intent.setType("text/plain");
                                intent.putExtra("android.intent.extra.SUBJECT", MPMHomeFragment.this.context.getResources().getString(R.string.app_name));
                                intent.putExtra("android.intent.extra.TEXT", "\nLet me recommend you this application\n\nhttps://play.google.com/store/apps/details?id=" + getActivity().getPackageName() + "\n\n");
                                MPMHomeFragment.this.context.startActivity(Intent.createChooser(intent, "choose one"));
                                return false;
                            } catch (Exception e) {
                                e.toString();
                                return false;
                            }
                        }
                    }
                });
                menuInflater.inflate(R.menu.menu_for_main_screen, popupMenu.getMenu());
                popupMenu.show();
            }
        });
        new getData().execute(new Void[0]);
    }

    public void updateFav(ArrayList<MPMSongModel> arrayList) {
        if (this.folderList.isEmpty()) {
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add(new MPMFolderModel("Favorite", "Favorite", arrayList));
            arrayList2.addAll(this.folderList);
            this.folderList.clear();
            this.folderList.addAll(arrayList2);
            this.adapter.notifyDataSetChanged();
        } else if (!arrayList.isEmpty()) {
            this.adapter.setFavAdded(true);
            if (this.folderList.get(0).getFolderName().equals("Favorite")) {
                this.folderList.set(0, new MPMFolderModel("Favorite", "Favorite", arrayList));
                this.adapter.notifyItemChanged(0);
                return;
            }
            ArrayList arrayList3 = new ArrayList();
            arrayList3.add(new MPMFolderModel("Favorite", "Favorite", arrayList));
            arrayList3.addAll(this.folderList);
            this.folderList.clear();
            this.folderList.addAll(arrayList3);
            this.adapter.notifyDataSetChanged();
        } else {
            this.adapter.setFavAdded(false);
            if (this.folderList.get(0).getFolderName().equals("Favorite")) {
                this.folderList.remove(0);
                this.adapter.notifyDataSetChanged();
            }
        }
    }

    
    private class getData extends AsyncTask<Void, Void, Void> {
        private getData() {
        }

        @Override 
        protected void onPreExecute() {
            super.onPreExecute();
            if (!MPMHomeFragment.this.folderList.isEmpty()) {
                MPMHomeFragment.this.folderList.clear();
            }
            ArrayList<MPMSongModel> fav = MPMHomeFragment.this.storageUtil.getFav();
            MPMHomeFragment.this.progressbar.setVisibility(View.VISIBLE);
            if (!fav.isEmpty()) {
                MPMHomeFragment mPMHomeFragment = MPMHomeFragment.this;
                mPMHomeFragment.isFavAdded = true;
                mPMHomeFragment.folderList.add(new MPMFolderModel("Favorite", "Favorite", fav));
            }
        }


        public Void doInBackground(Void... voidArr) {
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            char c = 0;
            try {
                Cursor query = MPMHomeFragment.this.context.getContentResolver().query(uri, new String[]{"bucket_id", "bucket_display_name", "_data"}, "1) GROUP BY 1,(2", (String[]) null, "date_modified DESC");
                if (query.moveToFirst()) {
                    while (true) {
                        String string = query.getString(query.getColumnIndex("bucket_display_name"));
                        String name = new File(new File(query.getString(query.getColumnIndex("_data"))).getParent()).getName();
                        ContentResolver contentResolver = MPMHomeFragment.this.context.getContentResolver();
                        String[] strArr = new String[1];
                        strArr[c] = "%" + name + "%";
                        Cursor query2 = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"title", "_data", "_display_name", "duration", "album_id", "album", "year", "artist", "_size", "date_modified"}, "_data like ? ", strArr, "date_modified DESC");
                        ArrayList arrayList = new ArrayList();
                        if (query2.moveToFirst()) {
                            int i = 0;
                            while (true) {
                                String string2 = query2.getString(query2.getColumnIndex("title"));
                                String str = string2;
                                MPMSongModel mPMSongModel2 = new MPMSongModel(str, query2.getString(query2.getColumnIndex("_data")), query2.getString(query2.getColumnIndex("_display_name")), query2.getString(query2.getColumnIndex("duration")), query2.getString(query2.getColumnIndex("album")), query2.getString(query2.getColumnIndex("album_id")), query2.getString(query2.getColumnIndex("year")), query2.getString(query2.getColumnIndex("artist")), Long.parseLong(query2.getString(query2.getColumnIndex("_size"))));
                                arrayList.add(mPMSongModel2);
                                Log.e(MPMDatabaseHelper.PLAYLIST_TABLE_SONGS, string2);
                                i++;
                                if (!query2.moveToNext()) {
                                    break;
                                } else if (i >= 6) {
                                    break;
                                }
                            }
                        }
                        MPMHomeFragment.this.folderList.add(new MPMFolderModel(name, string, arrayList));
                        if (!query.moveToNext()) {
                            break;
                        }
                        c = 0;
                    }
                }
            } catch (Exception e) {
                Log.e("exception", e.getMessage());
                Uri uri2 = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                Cursor query3 = MPMHomeFragment.this.context.getContentResolver().query(uri2, new String[]{"_data"}, (String) null, (String[]) null, "date_modified DESC");
                if (query3.moveToFirst()) {
                    do {
                        File file = new File(new File(query3.getString(query3.getColumnIndex("_data"))).getParent());
                        if (!MPMHomeFragment.this.filesList.contains(file)) {
                            MPMHomeFragment.this.filesList.add(file);
                        }
                    } while (query3.moveToNext());
                }
                String[] strArr2 = {"title", "_data", "_display_name", "duration", "album_id", "album", "year", "artist", "_size", "date_modified"};
                for (int i2 = 0; i2 < MPMHomeFragment.this.filesList.size(); i2++) {
                    ContentResolver contentResolver2 = MPMHomeFragment.this.context.getContentResolver();
                    Cursor query4 = contentResolver2.query(uri2, strArr2, "_data like ? ", new String[]{"%" + MPMHomeFragment.this.filesList.get(i2).getName() + "%"}, "date_modified DESC");
                    ArrayList arrayList2 = new ArrayList();
                    if (query4.moveToFirst()) {
                        int i3 = 0;
                        while (true) {
                            String string3 = query4.getString(query4.getColumnIndex("title"));
                            String str2 = string3;
                            MPMSongModel mPMSongModel4 = new MPMSongModel(str2, query4.getString(query4.getColumnIndex("_data")), query4.getString(query4.getColumnIndex("_display_name")), query4.getString(query4.getColumnIndex("duration")), query4.getString(query4.getColumnIndex("album")), query4.getString(query4.getColumnIndex("album_id")), query4.getString(query4.getColumnIndex("year")), query4.getString(query4.getColumnIndex("artist")), Long.parseLong(query4.getString(query4.getColumnIndex("_size"))));
                            arrayList2.add(mPMSongModel4);
                            Log.e(MPMDatabaseHelper.PLAYLIST_TABLE_SONGS, string3);
                            i3++;
                            if (!query4.moveToNext() || i3 >= 6) {
                                break;
                            }
                        }
                    }
                    MPMHomeFragment.this.folderList.add(new MPMFolderModel(MPMHomeFragment.this.filesList.get(i2).getName(), MPMHomeFragment.this.filesList.get(i2).getName(), arrayList2));
                }
            }
            Log.e("folderList", MPMHomeFragment.this.folderList.size() + "");
            return null;
        }


        
        public void onPostExecute(Void r9) {
            super.onPostExecute(r9);
            Log.e("done", "done");
            if (MPMHomeFragment.this.folderList.isEmpty()) {
                MPMHomeFragment.this.not_found.setVisibility(View.VISIBLE);
                MPMHomeFragment.this.recyclerView.setVisibility(View.GONE);
            } else {
                MPMHomeFragment.this.not_found.setVisibility(View.GONE);
                MPMHomeFragment.this.recyclerView.setVisibility(View.VISIBLE);
                MPMHomeFragment mPMHomeFragment = MPMHomeFragment.this;
                mPMHomeFragment.adapter = new MPMFolderAdapter(mPMHomeFragment.context, MPMHomeFragment.this.folderList, MPMHomeFragment.this.listener, MPMHomeFragment.this.listener1, MPMHomeFragment.this.isFavAdded);
                MPMHomeFragment.this.recyclerView.setAdapter(MPMHomeFragment.this.adapter);
            }
            MPMHomeFragment.this.progressbar.setVisibility(View.GONE);
        }
    }
}
