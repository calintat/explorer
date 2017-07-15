package com.calintat.explorer.recycler

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.util.SortedListAdapterCallback

import com.calintat.explorer.utils.FileUtils
import com.github.calintat.getInt

import java.io.File

internal class Callback(context: Context, adapter: RecyclerView.Adapter<*>) : SortedListAdapterCallback<File>(adapter) {

    private var criteria: Int = 0

    init {

        this.criteria = context.getInt("pref_sort", 0)
    }

    override fun compare(file1: File, file2: File): Int {

        val isDirectory1 = file1.isDirectory

        val isDirectory2 = file2.isDirectory

        if (isDirectory1 != isDirectory2) return if (isDirectory1) -1 else +1

        when (criteria) {

            0 -> return FileUtils.compareName(file1, file2)

            1 -> return FileUtils.compareDate(file1, file2)

            2 -> return FileUtils.compareSize(file1, file2)

            else -> return 0
        }
    }

    override fun areContentsTheSame(oldItem: File, newItem: File): Boolean {

        return oldItem == newItem
    }

    override fun areItemsTheSame(item1: File, item2: File): Boolean {

        return item1 == item2
    }

    fun update(criteria: Int): Boolean {

        if (criteria == this.criteria) return false

        this.criteria = criteria

        return true
    }
}