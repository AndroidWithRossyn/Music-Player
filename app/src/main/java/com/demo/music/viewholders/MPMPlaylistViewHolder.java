package com.demo.music.viewholders;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.music.R;
import com.squareup.picasso.Picasso;

import com.demo.music.model.MPMSongModel;
import com.demo.music.utils.Util;


public class MPMPlaylistViewHolder extends RecyclerView.ViewHolder {
    TextView artist;
    ImageView imageView;
    TextView index;
    View more;
    View parent;
    TextView title;

    public MPMPlaylistViewHolder(@NonNull View view) {
        super(view);
        this.index = (TextView) view.findViewById(R.id.index);
        this.imageView = (ImageView) view.findViewById(R.id.imageView);
        this.title = (TextView) view.findViewById(R.id.song_title);
        this.artist = (TextView) view.findViewById(R.id.artist);
        this.more = view.findViewById(R.id.more);
        this.parent = view.findViewById(R.id.parent);
    }

    public static void bind(Context context, MPMPlaylistViewHolder mPMPlaylistViewHolder, MPMSongModel mPMSongModel) {
        StringBuilder sb;
        int adapterPosition = mPMPlaylistViewHolder.getAdapterPosition();
        mPMPlaylistViewHolder.parent.setPadding(Util.dpToPx(context, 8), Util.dpToPx(context, 8), Util.dpToPx(context, 8), Util.dpToPx(context, 8));
        mPMPlaylistViewHolder.index.setVisibility(View.GONE);
        mPMPlaylistViewHolder.more.setVisibility(View.GONE);
        TextView textView = mPMPlaylistViewHolder.index;
        if (adapterPosition < 9) {
            sb = new StringBuilder();
            sb.append("0");
            sb.append(adapterPosition + 1);
        } else {
            sb = new StringBuilder();
            sb.append(adapterPosition + 1);
            sb.append("");
        }
        textView.setText(sb.toString());
        mPMPlaylistViewHolder.title.setText(mPMSongModel.getTitle());
        mPMPlaylistViewHolder.artist.setText(mPMSongModel.getArtist());
        byte[] bArr = null;
        try {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(mPMSongModel.getData());
            bArr = mediaMetadataRetriever.getEmbeddedPicture();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bArr != null) {
            mPMPlaylistViewHolder.imageView.setImageBitmap(BitmapFactory.decodeByteArray(bArr, 0, bArr.length));
            return;
        }
        Picasso.with(context).load(ContentUris.withAppendedId(Util.sArtworkUri, Long.parseLong(mPMSongModel.getAlbumid()))).placeholder(R.drawable.logo).error(R.drawable.logo).noFade().into(mPMPlaylistViewHolder.imageView);
    }
}
