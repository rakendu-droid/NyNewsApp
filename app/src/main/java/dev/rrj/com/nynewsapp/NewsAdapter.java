package dev.rrj.com.nynewsapp;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by rakendu on 11/05/15.
 */
public class NewsAdapter extends BaseAdapter {

    private ArrayList dataList;
    private LayoutInflater inflator ;
    ViewHolder viewHolder;
    private LruCache<String,Bitmap> imageCache;

    @Override
    public int getCount() {
        return dataList.size();
    }

    public NewsAdapter(Context context,ArrayList dataList ){
        inflator = LayoutInflater.from(context);
        this.dataList = dataList;
        init(context);

    }
    public void init(Context context){

        final int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        final int cacheSize = 1024 * 1024 * memClass / 8;

        imageCache = new LruCache(cacheSize);


    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (long) position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null)
        {

            convertView = inflator.inflate(R.layout.list_row,null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView)convertView.findViewById(R.id.title);
            viewHolder.author = (TextView)convertView.findViewById(R.id.author);
            viewHolder.imageView = (ImageView)convertView.findViewById(R.id.image);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder)convertView.getTag();
        }


        NewsModel newsModel= (NewsModel) dataList.get(position);
        viewHolder.title.setText(newsModel.getTitle());

        viewHolder.author.setText(newsModel.getAuthor());

        if(viewHolder.imageView!=null) {
            if (newsModel.getImgUrl() != null) {
                final Bitmap bitmap = (Bitmap) imageCache.get(newsModel.getImgUrl());
                if (bitmap != null) {

                    viewHolder.imageView.setImageBitmap(bitmap);
                } else {


                    new ImageDownloaderTask(viewHolder.imageView, imageCache).execute(newsModel.getImgUrl());
                }
            }
            else
            {
                newsModel.setImgUrl("");
                new ImageDownloaderTask(viewHolder.imageView, imageCache).execute(newsModel.getImgUrl());
            }

        }


        return convertView;
    }
    public void change()
    {
        notifyDataSetChanged();
    }


    static class ViewHolder{
        TextView title;
        TextView author;
        ImageView imageView;

    }
}
