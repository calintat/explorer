package com.calintat.explorer.recycler;

public interface RecyclerOnItemClickListener
{
    void onItemClick(int position);

    boolean onItemLongClick(int position);
}