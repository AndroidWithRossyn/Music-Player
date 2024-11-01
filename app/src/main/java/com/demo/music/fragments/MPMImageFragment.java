package com.demo.music.fragments;

import android.content.ContentUris;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestOptions;
import com.demo.music.R;

import com.demo.music.model.MPMSongModel;
import com.demo.music.utils.Util;


public class MPMImageFragment extends Fragment {
    public static final String Arg1 = "song";
    Context context;
    ImageView image;
    byte[] img;

    public static MPMImageFragment newInstance(MPMSongModel mPMSongModel) {
        MPMImageFragment mPMImageFragment = new MPMImageFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Arg1, mPMSongModel);
        mPMImageFragment.setArguments(bundle);
        return mPMImageFragment;
    }

    @Override 
    @Nullable
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.item_image, viewGroup, false);
        this.image = (ImageView) inflate.findViewById(R.id.image);
        return inflate;
    }

    @Override 
    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);
        MPMSongModel mPMSongModel = (MPMSongModel) getArguments().getParcelable(Arg1);
        this.context = getContext();
        try {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(mPMSongModel.getData());
            this.img = mediaMetadataRetriever.getEmbeddedPicture();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (this.img != null) {
            Glide.with(this.context).load(this.img).dontAnimate().apply((BaseRequestOptions<?>) new RequestOptions().dontTransform()).error(R.drawable.logo).into(this.image);
            return;
        }
        Glide.with(this.context).load(ContentUris.withAppendedId(Util.sArtworkUri, Long.parseLong(mPMSongModel.getAlbumid()))).dontAnimate().apply((BaseRequestOptions<?>) new RequestOptions().dontTransform()).error(R.drawable.logo).into(this.image);
    }
}
