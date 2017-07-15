package com.calintat.explorer.recycler

interface OnItemClickListener {

    fun onItemClick(position: Int)

    fun onItemLongClick(position: Int): Boolean
}