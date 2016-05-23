package com.example.morgan.lasertang;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by nastya on 02.05.16.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    private final String CACHED_FILENAME = "avatar";

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        ImageCache cache = new ImageCache();
        Bitmap mIcon =  cache.getBitmapFromDiskCache(CACHED_FILENAME);

        if (mIcon == null) {
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
        }
        try {
            cache.addBitmapToCache(CACHED_FILENAME, mIcon);
        } catch (IOException e) {
            //do nothing;
        }
        return mIcon;
    }

    protected void onPostExecute(Bitmap result) {
        if (bmImage == null) { return; }
        bmImage.setImageBitmap(result);
    }
}