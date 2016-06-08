package com.smartmaster.newslist.model;

/**
 * 封装网络资源的实体类
 */
public class News {

    private String newsPicUrl; //新闻图片的url地址
    private String newsTitle;  //新闻标题
    private String newsContent;//新闻内容

    public String getNewsPicUrl() {
        return newsPicUrl;
    }

    public void setNewsPicUrl(String newsPicUrl) {
        this.newsPicUrl = newsPicUrl;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsContent() {
        return newsContent;
    }

    public void setNewsContent(String newsContent) {
        this.newsContent = newsContent;
    }
}
