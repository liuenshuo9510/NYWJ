package com.nanyue.app.nywj.okhttp.bean;

import java.text.SimpleDateFormat;

/**
 * Created by 87710 on 2017/12/12.
 */

public class NewsDetailBean {
    /**
     * title : 云南武警：淬火加钢练意志 野外拉练励精兵
     * content : <body></body>
     * hits : 4
     * publishDate : 1512785098000
     * author : 云南总队
     * categroyId : 4
     * categroyName : 我的故事
     * copyfrom : 中国军网
     */

    private String title;
    private String content;
    private int hits;
    private long publishDate;
    private String author;
    private int categroyId;
    private String categroyName;
    private String copyfrom;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public String getPublishDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return simpleDateFormat.format(publishDate);
    }

    public void setPublishDate(long publishDate) {
        this.publishDate = publishDate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getCategroyId() {
        return categroyId;
    }

    public void setCategroyId(int categroyId) {
        this.categroyId = categroyId;
    }

    public String getCategroyName() {
        return categroyName;
    }

    public void setCategroyName(String categroyName) {
        this.categroyName = categroyName;
    }

    public String getCopyfrom() {
        return copyfrom;
    }

    public void setCopyfrom(String copyfrom) {
        this.copyfrom = copyfrom;
    }
}
