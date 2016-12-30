package com.calintat.explorer.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;

import com.calintat.explorer.utils.FileUtils;
import com.calintat.explorer.utils.PreferenceUtils;

import java.io.File;

class Callback extends SortedListAdapterCallback<File> {

    private int criteria;

    Callback(Context context, RecyclerView.Adapter adapter) {

        super(adapter);

        this.criteria = PreferenceUtils.getInteger(context, "pref_sort", 0);
    }

    @Override
    public int compare(File file1, File file2) {

        boolean isDirectory1 = file1.isDirectory();

        boolean isDirectory2 = file2.isDirectory();

        if (isDirectory1 != isDirectory2) return isDirectory1 ? -1 : +1;

        switch (criteria) {

            case 0:
                return FileUtils.compareName(file1, file2);

            case 1:
                return FileUtils.compareDate(file1, file2);

            case 2:
                return FileUtils.compareSize(file1, file2);

            default:
                return 0;
        }
    }

    @Override
    public boolean areContentsTheSame(File oldItem, File newItem) {

        return oldItem.equals(newItem);
    }

    @Override
    public boolean areItemsTheSame(File item1, File item2) {

        return item1.equals(item2);
    }

    boolean update(int criteria) {

        if (criteria == this.criteria) return false;

        this.criteria = criteria;

        return true;
    }
}