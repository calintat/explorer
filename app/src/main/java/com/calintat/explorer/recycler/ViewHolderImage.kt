package com.calintat.explorer.recycler

import android.content.Context
import android.graphics.Bitmap
import android.support.v4.content.ContextCompat
import android.support.v7.graphics.Palette
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.calintat.explorer.R
import com.calintat.explorer.utils.FileUtils

import java.io.File

import com.calintat.explorer.utils.FileUtils.getColorResource
import com.calintat.explorer.utils.FileUtils.getName
import com.github.calintat.getBoolean

internal class ViewHolderImage(context: Context, listener: OnItemClickListener, view: View) : ViewHolder(context, listener, view) {

    private var name: TextView? = null

    private var date: TextView? = null

    override fun loadIcon() {

        image = itemView.findViewById(R.id.list_item_image) as ImageView
    }

    override fun loadName() {

        name = itemView.findViewById(R.id.list_item_name) as TextView
    }

    override fun loadInfo() {

        date = itemView.findViewById(R.id.list_item_date) as TextView
    }

    override fun bindIcon(file: File, selected: Boolean?) {

        val color = ContextCompat.getColor(context, getColorResource(file))

        Glide.with(context).load(file).asBitmap().fitCenter().into(object : BitmapImageViewTarget(image) {

            override fun onResourceReady(resource: Bitmap, animation: GlideAnimation<in Bitmap>?) {

                this.view.setImageBitmap(resource)

                name!!.setBackgroundColor(Palette.from(resource).generate().getMutedColor(color))
            }
        })
    }

    override fun bindName(file: File) {

        val extension = context.getBoolean("pref_extension", true)

        name!!.text = if (extension) getName(file) else file.name
    }

    override fun bindInfo(file: File) {

        if (date == null) return

        date!!.text = FileUtils.getLastModified(file)
    }
}