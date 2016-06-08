package com.smartmaster.newslist.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

import com.smartmaster.newslist.R;
import com.smartmaster.newslist.adapter.NewsAdapter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * 1.使用 AsyncTask（异步任务类）实现异步加载任务
 * 2.使用 LruCache 实现缓存
 * 3.ListView的滚动高效优化（ListView滚动时不加载，停止滚动时再加载）--符合用户使用习惯
 */
public class ImageLoader {

    //ListView的滑动优化
    private ListView mListView;
    private Set<ImageAsyncTask> mTask;  //管理AsyncTask中的Task

    //加载从可见项的第一项到最后一项序列中的所有图片
    public void loadImage(int start, int end) {
        for (int i = start; i < end; i++) {
            String url = NewsAdapter.URLS[i];
            //从缓存中取出对应的图片
            Bitmap bitmap = getBitmapFromCache(url);
            if (bitmap == null) {
                //如果缓存中没有图片，就必须从网络下载
                ImageAsyncTask task = new ImageAsyncTask(url);
                task.execute(url);
                mTask.add(task);
            } else {
                //如果缓存中已经有图片了，直接使用缓存中的图片
                ImageView imageView = (ImageView) mListView.findViewWithTag(url);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    //停止所有加载任务
    public void cancelAllTask() {
        if (mTask != null) {
            for (ImageAsyncTask task : mTask) {
                task.cancel(false);
            }
        }
    }


    //使用LruCache缓存
    private LruCache<String, Bitmap> mCache;

    public ImageLoader(ListView listView) {
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
        //ListView的滑动优化
        mListView = listView;
        mTask = new HashSet<>();
    }

    //从缓存中获取数据
    private Bitmap getBitmapFromCache(String url) {
        return mCache.get(url);
    }

    //增加到缓存
    private void addBitmapToCache(String url, Bitmap bitmap) {
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
//            new ImageAsyncTask(url).execute(url);
            imageView.setImageResource(R.mipmap.ic_launcher);
        } else {
            //缓存中有就直接使用缓存中的图片
            imageView.setImageBitmap(bitmap);
        }
    }

    //异步任务类
    class ImageAsyncTask extends AsyncTask<String, Void, Bitmap> {

        //        private ImageView mImageView;
        private String mUrl;

        public ImageAsyncTask(String url) {
//            mImageView = imageView;
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
//            if (mImageView.getTag().equals(mUrl)) {
//                mImageView.setImageBitmap(bitmap);
//            }
            ImageView imageView = (ImageView) mListView.findViewWithTag(mUrl);
            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
            mTask.remove(this);
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
