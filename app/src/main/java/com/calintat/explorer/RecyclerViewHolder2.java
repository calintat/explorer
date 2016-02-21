package com.calintat.explorer;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.io.File;

import static com.calintat.explorer.FileUtils.*;

public final class RecyclerViewHolder2 extends RecyclerViewHolder
{
    private TextView name;

    private TextView date;

    RecyclerViewHolder2(Context context,RecyclerOnItemClickListener listener,View view)
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
        date=(TextView)itemView.findViewById(R.id.list_item_date);
    }

    @Override
    protected void bindIcon(File file,Boolean selected)
    {
        final int color=ContextCompat.getColor(context,getColorResource(file));

        Glide.with(context).load(file).asBitmap().fitCenter().into(new BitmapImageViewTarget(image)
        {
            @Override
            public void onResourceReady(Bitmap resource,GlideAnimation<? super Bitmap> animation)
            {
                this.view.setImageBitmap(resource);

                name.setBackgroundColor(Palette.from(resource).generate().getMutedColor(color));
            }
        });
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
        if(date==null) return;

        date.setText(FileUtils.getLastModified(file));
    }

}
