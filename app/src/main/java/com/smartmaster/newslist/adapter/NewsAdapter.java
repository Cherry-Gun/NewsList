package com.smartmaster.newslist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartmaster.newslist.R;
import com.smartmaster.newslist.model.News;
import com.smartmaster.newslist.util.ImageLoaderWithThread;

import java.util.List;

/**
 * 将网络请求的json数据通过适配器添加到ListView中
 */
public class NewsAdapter extends BaseAdapter{

    private List<News> mList;
    private LayoutInflater mInflater;

    public NewsAdapter(Context context, List<News> list) {
        this.mList = list;
        mInflater = LayoutInflater.from(context);
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
        viewHolder.picture.setImageResource(R.mipmap.ic_launcher); //TODO 先使用系统默认图片
        new ImageLoaderWithThread().showImageByThread(viewHolder.picture, mList.get(position).getNewsPicUrl());
        viewHolder.title.setText(mList.get(position).getNewsTitle());
        viewHolder.content.setText(mList.get(position).getNewsContent());
        return convertView;
    }

    class ViewHolder {
        ImageView picture;
        TextView title;
        TextView content;
    }

}
