package com.example.morgan.lasertang;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by nastya on 23.05.16.
 */
class ImageCache {
    private DiskLruCache mDiskLruCache;
    private final Object mDiskCacheLock = new Object();
    private boolean mDiskCacheStarting = true;
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
    private static final String DISK_CACHE_SUBDIR = "avatars";
    private Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.PNG;
    private int mCompressQuality = 70;
    private static final int APP_VERSION = 1;
    private static final int VALUE_COUNT = 1;

    class InitCacheTask extends AsyncTask<File, Void, Void> {
        @Override
        protected Void doInBackground(File... params) {
            synchronized (mDiskCacheLock) {
                File cacheDir = params[0];
                try {
                    mDiskLruCache = DiskLruCache.open(cacheDir, 1, DISK_CACHE_SIZE, DISK_CACHE_SIZE);
                } catch (IOException e) {
                    return null;
                }
                mDiskCacheStarting = false;
                mDiskCacheLock.notifyAll();
            }
            return null;
        }
    }
    public void addBitmapToCache(String key, Bitmap bitmap) throws IOException{
        // Also add to disk cache
        synchronized (mDiskCacheLock) {
            if (mDiskLruCache != null && mDiskLruCache.get(key) == null) {
                writeBitmapToFile(bitmap, mDiskLruCache.edit(key));
                mDiskLruCache.flush();
                mDiskLruCache.edit(key).commit();
            }
        }
    }


    public static File getDiskCacheDir(Context context) {
        final String cachePath =
                //Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                // !Environment.isExternalStorageRemovable() ? getExternalCacheDir(context).getPath() :
                        context.getCacheDir().getPath();

        return new File(cachePath + File.separator + DISK_CACHE_SUBDIR );
    }

    public void initCacheDir(Context context) {
        File cacheDir = getDiskCacheDir(context);
        (new InitCacheTask()).execute(cacheDir);

    }

    private boolean writeBitmapToFile( Bitmap bitmap, DiskLruCache.Editor editor )
            throws IOException {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream( editor.newOutputStream( 0 ));
            return bitmap.compress( mCompressFormat, mCompressQuality, out );
        } finally {
            if ( out != null ) {
                out.close();
            }
        }
    }
    public Bitmap getBitmapFromDiskCache( String key ) {

        Bitmap bitmap = null;
        DiskLruCache.Snapshot snapshot = null;
        if (mDiskLruCache == null)  {
            return null;
        }
        synchronized (mDiskCacheLock) {
            try {

                snapshot = mDiskLruCache.get(key);
                if (snapshot == null) {
                    return null;
                }
                final InputStream in = snapshot.getInputStream(0);
                if (in != null) {
                    final BufferedInputStream buffIn =
                            new BufferedInputStream(in);
                    bitmap = BitmapFactory.decodeStream(buffIn);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (snapshot != null) {
                    snapshot.close();
                }
            }
        }
        return bitmap;

    }
}

