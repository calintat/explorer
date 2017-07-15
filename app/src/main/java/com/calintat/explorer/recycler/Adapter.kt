package com.calintat.explorer.recycler

import android.content.Context
import android.support.v7.util.SortedList
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.calintat.explorer.R

import java.io.File
import java.util.ArrayList

class Adapter(private val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    private val items: SortedList<File>

    private val selectedItems: SparseBooleanArray

    private val callback: Callback

    private var itemLayout: Int? = null

    private var spanCount: Int? = null

    private var onItemClickListener: OnItemClickListener? = null

    private var onItemSelectedListener: OnItemSelectedListener? = null

    init {

        this.callback = Callback(context, this)

        this.items = SortedList(File::class.java, callback)

        this.selectedItems = SparseBooleanArray()
    }

    //----------------------------------------------------------------------------------------------

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder? {

        val itemView = LayoutInflater.from(context).inflate(itemLayout!!, parent, false)

        when (itemLayout) {

            R.layout.list_item_0 -> return ViewHolder0(context, onItemClickListener!!, itemView)

            R.layout.list_item_1 -> return ViewHolderAudio(context, onItemClickListener!!, itemView)

            R.layout.list_item_2 -> return ViewHolderImage(context, onItemClickListener!!, itemView)

            R.layout.list_item_3 -> return ViewHolderVideo(context, onItemClickListener!!, itemView)

            else -> return null
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {

        recyclerView!!.layoutManager = GridLayoutManager(context, spanCount!!)

        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.setData(get(position), getSelected(position))
    }

    override fun getItemCount(): Int {

        return items.size()
    }

    //----------------------------------------------------------------------------------------------

    fun setItemLayout(itemLayout: Int) {

        this.itemLayout = itemLayout
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {

        this.onItemClickListener = onItemClickListener
    }

    fun setOnItemSelectedListener(onItemSelectedListener: OnItemSelectedListener) {

        this.onItemSelectedListener = onItemSelectedListener
    }

    fun setSpanCount(spanCount: Int) {

        this.spanCount = spanCount
    }

    //----------------------------------------------------------------------------------------------

    fun add(file: File) {

        items.add(file)
    }

    fun addAll(vararg files: File) {

        items.addAll(*files)
    }

    fun addAll(files: Collection<File>) {

        items.addAll(files)
    }

    fun clear() {

        while (items.size() > 0) items.removeItemAt(items.size() - 1)
    }

    fun refresh() {

        for (i in 0..itemCount - 1) {

            notifyItemChanged(i)
        }
    }

    fun removeAll(files: Collection<File>) {

        for (file in files) items.remove(file)
    }

    fun updateItemAt(index: Int, file: File) {

        items.updateItemAt(index, file)
    }

    //----------------------------------------------------------------------------------------------

    fun clearSelection() {

        val selectedPositions = selectedPositions

        selectedItems.clear()

        for (i in selectedPositions) notifyItemChanged(i)

        onItemSelectedListener!!.onItemSelected()
    }

    fun update(criteria: Int) {

        if (callback.update(criteria)) {

            val list = getItems()

            clear()

            addAll(list)
        }
    }

    fun select(positions: ArrayList<Int>) {

        selectedItems.clear()

        for (i in positions) {

            selectedItems.append(i, true)

            notifyItemChanged(i)
        }

        onItemSelectedListener!!.onItemSelected()
    }

    fun toggle(position: Int) {

        if (getSelected(position))
            selectedItems.delete(position)
        else
            selectedItems.append(position, true)

        notifyItemChanged(position)

        onItemSelectedListener!!.onItemSelected()
    }

    //----------------------------------------------------------------------------------------------

    fun anySelected(): Boolean {

        return selectedItems.size() > 0
    }

    private fun getSelected(position: Int): Boolean {

        return selectedItems.get(position)
    }

    //----------------------------------------------------------------------------------------------

    val selectedItemCount: Int
        get() = selectedItems.size()

    fun indexOf(file: File): Int {

        return items.indexOf(file)
    }

    //----------------------------------------------------------------------------------------------

    fun getSelectedItems(): ArrayList<File> {

        val list = ArrayList<File>()

        for (i in 0..itemCount - 1) {

            if (getSelected(i)) list.add(get(i))
        }

        return list
    }

    private fun getItems(): ArrayList<File> {

        val list = ArrayList<File>()

        for (i in 0..itemCount - 1) {

            list.add(get(i))
        }

        return list
    }

    val selectedPositions: ArrayList<Int>
        get() {

            val list = ArrayList<Int>()

            for (i in 0..itemCount - 1) {

                if (getSelected(i)) list.add(i)
            }

            return list
        }

    operator fun get(index: Int): File {

        return items.get(index)
    }
}