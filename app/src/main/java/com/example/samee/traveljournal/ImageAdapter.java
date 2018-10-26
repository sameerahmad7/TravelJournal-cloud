package com.example.samee.traveljournal;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    public List<Bitmap> bitmaps;

   public ImageAdapter(Context mContext,List <Bitmap> bitmaps)
    {
        this.mContext=mContext;
        this.bitmaps=bitmaps;
    }

    @Override
    public int getCount() {
        return bitmaps.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(350, 450));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //          imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageBitmap(bitmaps.get(position));
        return imageView;
    }
}
