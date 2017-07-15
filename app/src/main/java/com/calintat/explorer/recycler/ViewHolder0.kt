package com.calintat.explorer.recycler

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.calintat.explorer.R

import java.io.File

import com.calintat.explorer.utils.FileUtils.getColorResource
import com.calintat.explorer.utils.FileUtils.getImageResource
import com.calintat.explorer.utils.FileUtils.getLastModified
import com.calintat.explorer.utils.FileUtils.getName
import com.calintat.explorer.utils.FileUtils.getSize
import com.github.calintat.getBoolean
import java.security.AccessController.getContext

internal class ViewHolder0(context: Context, listener: OnItemClickListener, view: View) : ViewHolder(context, listener, view) {

    private var name: TextView? = null

    private var date: TextView? = null

    private var size: TextView? = null

    override fun loadIcon() {

        image = itemView.findViewById(R.id.list_item_image) as ImageView
    }

    override fun loadName() {

        name = itemView.findViewById(R.id.list_item_name) as TextView
    }

    override fun loadInfo() {

        date = itemView.findViewById(R.id.list_item_date) as TextView

        size = itemView.findViewById(R.id.list_item_size) as TextView
    }

    override fun bindIcon(file: File, selected: Boolean?) {

        if (context.getBoolean("pref_icon", true)) {

            image!!.setOnClickListener(onActionClickListener)

            image!!.setOnLongClickListener(onActionLongClickListener)

            if (selected!!) {

                val color = ContextCompat.getColor(context, R.color.misc_file)

                image!!.background = getBackground(color)

                val drawable = ContextCompat.getDrawable(context, R.drawable.ic_selected)

                DrawableCompat.setTint(drawable, Color.rgb(255, 255, 255))

                image!!.setImageDrawable(drawable)
            } else {

                val color = ContextCompat.getColor(context, getColorResource(file))

                image!!.background = getBackground(color)

                val drawable = ContextCompat.getDrawable(context, getImageResource(file))

                DrawableCompat.setTint(drawable, Color.rgb(255, 255, 255))

                image!!.setImageDrawable(drawable)
            }
        } else {

            val color = ContextCompat.getColor(context, getColorResource(file))

            image!!.setBackground(null)

            val drawable = ContextCompat.getDrawable(context, getImageResource(file))

            DrawableCompat.setTint(drawable, color)

            image!!.setImageDrawable(drawable)
        }
    }

    override fun bindName(file: File) {

        val extension = context.getBoolean("pref_extension", true)

        name!!.text = if (extension) getName(file) else file.name
    }

    override fun bindInfo(file: File) {

        date!!.text = getLastModified(file)

        size!!.text = getSize(context, file)

        setVisibility(date!!, context.getBoolean("pref_date", true))

        setVisibility(size!!, context.getBoolean("pref_size", false))
    }

    private fun getBackground(color: Int): ShapeDrawable {

        val shapeDrawable = ShapeDrawable(OvalShape())

        val size = context.resources.getDimension(R.dimen.avatar_size)

        shapeDrawable.intrinsicWidth = size.toInt()

        shapeDrawable.intrinsicHeight = size.toInt()

        shapeDrawable.paint.color = color

        return shapeDrawable
    }
}