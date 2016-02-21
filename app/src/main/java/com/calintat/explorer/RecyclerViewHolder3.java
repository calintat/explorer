package com.calintat.explorer;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;

import static com.calintat.explorer.FileUtils.*;

public final class RecyclerViewHolder3 extends RecyclerViewHolder
{
    private TextView name;

    private TextView duration;

    RecyclerViewHolder3(Context context,RecyclerOnItemClickListener listener,View view)
    {
        super(context,listener,view);
    }

    @Override
    protected void loadIcon()
    {
        image=(ImageView)itemView.findViewById(R.id.list_item_image);
    }

    @Override
    protected void loadName()
    {
        name=(TextView)itemView.findViewById(R.id.list_item_name);
    }

    @Override
    protected void loadInfo()
    {
        duration=(TextView)itemView.findViewById(R.id.list_item_duration);
    }

    @Override
    protected void bindIcon(File file,Boolean selected)
    {
        Glide.with(context).load(file).into(image);
    }

    @Override
    protected void bindName(File file)
    {
        boolean extension=PreferenceUtils.getBoolean(context,"pref_extension",true);

        name.setText(extension ? getName(file) : file.getName());
    }

    @Override
    protected void bindInfo(File file)
    {
        duration.setText(FileUtils.getDuration(file));
    }
}
