package com.calintat.explorer.recycler;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.calintat.explorer.R;

import java.io.File;

import static com.calintat.explorer.utils.FileUtils.getAlbum;
import static com.calintat.explorer.utils.FileUtils.getArtist;
import static com.calintat.explorer.utils.FileUtils.getName;
import static com.calintat.explorer.utils.FileUtils.getTitle;
import static com.calintat.explorer.utils.PreferenceUtils.getBoolean;

final class ViewHolderAudio extends ViewHolder {

    private TextView title;

    private TextView artist;

    private TextView album;

    //----------------------------------------------------------------------------------------------

    ViewHolderAudio(Context context, OnItemClickListener listener, View view) {

        super(context, listener, view);
    }

    //----------------------------------------------------------------------------------------------

    @Override
    protected void loadIcon() {

        image = (ImageView) itemView.findViewById(R.id.list_item_image);
    }

    @Override
    protected void loadName() {

        title = (TextView) itemView.findViewById(R.id.list_item_title);
    }

    @Override
    protected void loadInfo() {

        artist = (TextView) itemView.findViewById(R.id.list_item_artist);

        album = (TextView) itemView.findViewById(R.id.list_item_album);
    }

    @Override
    protected void bindIcon(File file, Boolean selected) {

        try {

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();

            retriever.setDataSource(file.getPath());

            Glide.with(context).load(retriever.getEmbeddedPicture()).into(image);
        }
        catch (Exception e) {

            image.setImageResource(R.drawable.ic_audio);
        }
    }

    @Override
    protected void bindName(File file) {

        boolean extension = getBoolean(context, "pref_extension", true);

        String string = getTitle(file);

        title.setText(string != null && string.isEmpty() ? string : (extension ? getName(file) : file.getName()));
    }

    @Override
    protected void bindInfo(File file) {

        artist.setText(getArtist(file));

        album.setText(getAlbum(file));
    }
}