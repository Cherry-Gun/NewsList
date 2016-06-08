package com.smartmaster.newslist.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 1.使用 AsyncTask（异步任务类）实现异步加载任务
 * 2.使用 LruCache 实现缓存
 */
public class ImageLoader {

    //使用LruCache缓存
    private LruCache<String, Bitmap> mCache;

    public ImageLoader() {
        //我们不可能将全部内存用于LruCache，所以我们计算出一部分内存空间用于缓存
        //先计算出全部内存,再取需要的缓存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 4;
        mCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    //从缓存中获取数据
    public Bitmap getBitmapFromCache(String url) {
        return mCache.get(url);
    }

    //增加到缓存
    public void addBitmapToCache(String url, Bitmap bitmap) {
        if (getBitmapFromCache(url) == null) {
            mCache.put(url, bitmap);
        }
    }



    //使用异步任务类的方式显示图片
    public void showImageByAsyncTask(ImageView imageView, String url) {
//        new ImageAsyncTask(imageView, url).execute(url);  //使用AsyncTask
        //使用LruCache，从缓存中取出图片，缓存中没有再下载
        Bitmap bitmap = getBitmapFromCache(url);
        if (bitmap == null) {
            //缓存中没有就异步加载
            new ImageAsyncTask(imageView, url).execute(url);
        } else {
            //缓存中有就直接使用缓存中的图片
            imageView.setImageBitmap(bitmap);
        }
    }

    //异步任务类
    class ImageAsyncTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView mImageView;
        private String mUrl;

        public ImageAsyncTask(ImageView imageView, String url) {
            mImageView = imageView;
            mUrl = url;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
//            return getImageFromUrl(params[0]);
            //使用LruCache将已下载的图片增加到缓存中
            String url = params[0];
            Bitmap bitmap = getImageFromUrl(url);
            if (bitmap != null) {
                addBitmapToCache(url, bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (mImageView.getTag().equals(mUrl)) {
                mImageView.setImageBitmap(bitmap);
            }
        }
    }

    //用于从一个url来获取一个bitmap
    private Bitmap getImageFromUrl(String urlString) {
        Bitmap bitmap;
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(connection.getInputStream());
            bitmap = BitmapFactory.decodeStream(in);
            connection.disconnect();
            in.close();
            return bitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
