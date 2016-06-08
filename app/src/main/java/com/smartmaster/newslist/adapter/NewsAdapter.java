package com.smartmaster.newslist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.smartmaster.newslist.R;
import com.smartmaster.newslist.model.News;
import com.smartmaster.newslist.util.ImageLoader;

import java.util.List;

/**
 * 1.将网络请求的json数据通过适配器添加到ListView中
 * 2.高效优化ListView --> 符合用户使用习惯,提高加载效率
 */
public class NewsAdapter extends BaseAdapter implements AbsListView.OnScrollListener {

    private List<News> mList;
    private LayoutInflater mInflater;

    //ListView的高效优化
    private int mStart, mEnd;  //滚动后可见项的第一项和最后一项
    private ImageLoader mImageLoader; //图片加载缓存类
    public static String[] URLS;  //当前所有图片的URL地址
    private boolean isFirstStart;  //初始化程序的预加载

    public NewsAdapter(Context context, List<News> list, ListView listView) {
        this.mList = list;
        mInflater = LayoutInflater.from(context);
        //ListView的高效优化
        listView.setOnScrollListener(this);
        mImageLoader = new ImageLoader(listView);
        URLS = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            URLS[i] = list.get(i).getNewsPicUrl();
        }
        isFirstStart = true;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_news, null);
            viewHolder.picture = (ImageView) convertView.findViewById(R.id.item_picture);
            viewHolder.title = (TextView) convertView.findViewById(R.id.item_title);
            viewHolder.content = (TextView) convertView.findViewById(R.id.item_content);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.picture.setImageResource(R.mipmap.ic_launcher); //先使用系统默认图片
        String url = mList.get(position).getNewsPicUrl();
        viewHolder.picture.setTag(url);
//        new ImageLoaderWithThread().showImageByThread(viewHolder.picture, url);  //多线程的方法
//        new ImageLoaderWithAsyncTask().showImageByAsyncTask(viewHolder.picture, url);  //使用AsyncTask方法
//        new ImageLoader().showImageByAsyncTask(viewHolder.picture, url);  //使用AsyncTask方法　+　LruCache缓存类
        mImageLoader.showImageByAsyncTask(viewHolder.picture, url);  //使用AsyncTask方法　+　LruCache缓存类 + ListView滑动优化
        viewHolder.title.setText(mList.get(position).getNewsTitle());
        viewHolder.content.setText(mList.get(position).getNewsContent());
        return convertView;
    }

    class ViewHolder {
        ImageView picture;
        TextView title;
        TextView content;
    }

    /**
     * 判断当前ListView滚动的状态:
     * 1.如果处于滚动过程中，那么我们取消掉所有的正在下载的Task（任务）
     * 2.当ListView滚动完毕之后，我们再根据当前ListView所显示的第一项和最后一项，去加载这之间所有的项目，
     * 也就是说:在ListView停止滚动的时候加载所有的可见项
     */
    @Override //此方法在ListView滑动状态切换的时候才会调用,初始化的时候不被调用
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        /**
         *  scrollState --> 滚动状态的参数
         *  SCROLL_STATE_IDLE --> 表示停止状态的值
         */
        if (scrollState == SCROLL_STATE_IDLE) {  //如果ListView是停止状态（用户没有滑动屏幕）
            //加载可见项（可见项就是目前显示在屏幕上的这些项）
            mImageLoader.loadImage(mStart, mEnd);
        } else {
            //用户正在滑动，停止任务
            mImageLoader.cancelAllTask();
        }
    }

    @Override  //在整个滑动过程中都会调用，初始化的时候也会被调用
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        /**
         *  firstVisibleItem --> 可见项的第一项
         *  visibleItemCount --> 可见项的数量
         */
        mStart = firstVisibleItem;
        mEnd = firstVisibleItem + visibleItemCount;
        //第一次显示的时候调用
        if (isFirstStart && visibleItemCount > 0) {
            mImageLoader.loadImage(mStart, mEnd);
            isFirstStart = false;
        }
    }

}
