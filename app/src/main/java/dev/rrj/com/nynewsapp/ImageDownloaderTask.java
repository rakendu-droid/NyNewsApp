package dev.rrj.com.nynewsapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import org.apache.http.HttpStatus;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by rakendu on 11/05/15.
 */
public class ImageDownloaderTask extends AsyncTask<String,Void,Bitmap> {

    private final WeakReference<ImageView> imageViewReference;
    android.support.v4.util.LruCache<String,Bitmap> imageCache;
    String url;

    public ImageDownloaderTask(ImageView imageView, android.support.v4.util.LruCache<String,Bitmap> imageCache) {
        imageViewReference = new WeakReference<ImageView>(imageView);
        this.imageCache = imageCache;
    }
    @Override
    protected Bitmap doInBackground(String... params) {

        String url = params[0];
        this.url = url;
        if(url.length()>1)
            return downloadBitmap(url);
        else
            return null;


    }


    protected void onPostExecute(Bitmap bitmap) {

        if (isCancelled()) {
            bitmap = null;
        }

        if (imageViewReference != null) {
            ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                if (bitmap != null) {
                    imageCache.put(url,bitmap);

                    imageView.setImageBitmap(bitmap);
                } else {
                    Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.multicolor);
                    imageView.setImageDrawable(placeholder);
                }
            }
        }

    }

    private Bitmap downloadBitmap(String url) {

        HttpURLConnection urlConnection = null;
        if(url!=null) {
            try {
                URL uri = new URL(url);
                urlConnection = (HttpURLConnection) uri.openConnection();
                int statusCode = urlConnection.getResponseCode();
                if (statusCode != HttpStatus.SC_OK) {
                    return null;
                }

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                }
            } catch (Exception e) {
                urlConnection.disconnect();

            } finally {
                try {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
                catch(Exception e)
                {
                    Log.d("LOG","Exception  "+e);
                }
            }
        }

        return null;
    }
}
