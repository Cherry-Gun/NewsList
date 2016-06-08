package com.smartmaster.newslist.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;

import com.smartmaster.newslist.R;
import com.smartmaster.newslist.model.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends Activity {

    private ListView mListView;
    //网络资源 --> 慕课网的json格式课程数据
    private static String IMOOC_URL = "http://www.imooc.com/api/teacher?type=4&num=30";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_news);
        mListView = (ListView) findViewById(R.id.lv_news);
        new NewsAsyncTask().execute(IMOOC_URL);  //启用异步任务类
    }

    //用来异步请求网络资源的异步任务类
    class NewsAsyncTask extends AsyncTask<String, Void, List<News>> {
        @Override
        protected List<News> doInBackground(String... params) {
            return getJSONData(params[0]);
        }
    }

    //获取JSON数据的方法,将url对应的JSON数据转化成我们所封装的News类
    private List<News> getJSONData(String url) {
        List<News> newsList = new ArrayList<>();
        try {
            //这句相当于url.openConnection().getInputStream();
            //可根据URL直接联网获取网络数据，返回值为InputStream
            String jsonString = readStream(new URL(url).openStream());
            JSONObject jsonObject;
            News news;
            jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                news = new News();
                news.setNewsPicUrl(jsonObject.getString("picSmall"));
                news.setNewsTitle(jsonObject.getString("name"));
                news.setNewsContent(jsonObject.getString("description"));
                newsList.add(news);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newsList;
    }

    //从InputStream解析网页返回的数据,读取网页返回的字符串--字节流转化为字符流
    private String readStream(InputStream in) {
        InputStreamReader reader;
        String result = "";
        try {
            String line;
            reader = new InputStreamReader(in, "utf-8");
            BufferedReader br = new BufferedReader(reader);
            while ((line = br.readLine()) != null) {
                result += line;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
