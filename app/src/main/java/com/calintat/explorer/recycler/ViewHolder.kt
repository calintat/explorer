package com.calintat.explorer.recycler

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView

import java.io.File

abstract class ViewHolder
//----------------------------------------------------------------------------------------------

internal constructor(internal val context: Context, listener: OnItemClickListener, view: View) : RecyclerView.ViewHolder(view) {

    internal var image: ImageView? = null

    internal lateinit var onActionClickListener: (View) -> Unit

    internal lateinit var onActionLongClickListener: (View) -> Boolean

    private var onClickListener: ((View) -> Unit)? = null

    private var onLongClickListener: ((View) -> Boolean)? = null

    init {

        setClickListener(listener)

        loadIcon()

        loadName()

        loadInfo()
    }

    //----------------------------------------------------------------------------------------------

    protected abstract fun loadIcon()

    protected abstract fun loadName()

    protected abstract fun loadInfo()

    protected abstract fun bindIcon(file: File, selected: Boolean?)

    protected abstract fun bindName(file: File)

    protected abstract fun bindInfo(file: File)

    //----------------------------------------------------------------------------------------------

    private fun setClickListener(listener: OnItemClickListener) {

        this.onActionClickListener = { v -> listener.onItemLongClick(adapterPosition) }

        this.onActionLongClickListener = { v -> listener.onItemLongClick(adapterPosition) }

        this.onClickListener = { v -> listener.onItemClick(adapterPosition) }

        this.onLongClickListener = { v -> listener.onItemLongClick(adapterPosition) }
    }

    internal fun setData(file: File, selected: Boolean?) {

        itemView.setOnClickListener(onClickListener)

        itemView.setOnLongClickListener(onLongClickListener)

        itemView.isSelected = selected!!

        bindIcon(file, selected)

        bindName(file)

        bindInfo(file)
    }

    internal fun setVisibility(view: View, visibility: Boolean) {

        view.visibility = if (visibility) View.VISIBLE else View.GONE
    }
}