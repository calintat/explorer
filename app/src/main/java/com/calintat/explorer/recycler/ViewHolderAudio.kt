package com.calintat.explorer.recycler

import android.content.Context
import android.media.MediaMetadataRetriever
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.calintat.explorer.R

import java.io.File

import com.calintat.explorer.utils.FileUtils.getAlbum
import com.calintat.explorer.utils.FileUtils.getArtist
import com.calintat.explorer.utils.FileUtils.getName
import com.calintat.explorer.utils.FileUtils.getTitle
import com.github.calintat.getBoolean

internal class ViewHolderAudio
//----------------------------------------------------------------------------------------------

(context: Context, listener: OnItemClickListener, view: View) : ViewHolder(context, listener, view) {

    private var title: TextView? = null

    private var artist: TextView? = null

    private var album: TextView? = null

    //----------------------------------------------------------------------------------------------

    override fun loadIcon() {

        image = itemView.findViewById(R.id.list_item_image) as ImageView
    }

    override fun loadName() {

        title = itemView.findViewById(R.id.list_item_title) as TextView
    }

    override fun loadInfo() {

        artist = itemView.findViewById(R.id.list_item_artist) as TextView

        album = itemView.findViewById(R.id.list_item_album) as TextView
    }

    override fun bindIcon(file: File, selected: Boolean?) {

        try {

            val retriever = MediaMetadataRetriever()

            retriever.setDataSource(file.path)

            Glide.with(context).load(retriever.embeddedPicture).into(image)
        }
        catch (e: Exception) {

            image!!.setImageResource(R.drawable.ic_audio)
        }

    }

    override fun bindName(file: File) {

        val extension = context.getBoolean("pref_extension", true)

        val string = getTitle(file)

        title!!.text = if (string != null && string.isEmpty()) string else if (extension) getName(file) else file.name
    }

    override fun bindInfo(file: File) {

        artist!!.text = getArtist(file)

        album!!.text = getAlbum(file)
    }
}