package com.calintat.explorer.recycler

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.calintat.explorer.R
import com.calintat.explorer.utils.FileUtils

import java.io.File

import com.calintat.explorer.utils.FileUtils.getName
import com.github.calintat.getBoolean

internal class ViewHolderVideo(context: Context, listener: OnItemClickListener, view: View) : ViewHolder(context, listener, view) {

    private var name: TextView? = null

    private var duration: TextView? = null

    override fun loadIcon() {

        image = itemView.findViewById(R.id.list_item_image) as ImageView
    }

    override fun loadName() {

        name = itemView.findViewById(R.id.list_item_name) as TextView
    }

    override fun loadInfo() {

        duration = itemView.findViewById(R.id.list_item_duration) as TextView
    }

    override fun bindIcon(file: File, selected: Boolean?) {

        Glide.with(context).load(file).into(image)
    }

    override fun bindName(file: File) {

        val extension = context.getBoolean("pref_extension", true)

        name!!.text = if (extension) getName(file) else file.name
    }

    override fun bindInfo(file: File) {

        duration!!.text = FileUtils.getDuration(file)
    }
}