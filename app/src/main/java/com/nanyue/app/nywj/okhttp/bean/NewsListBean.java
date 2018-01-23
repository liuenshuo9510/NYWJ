package com.nanyue.app.nywj.okhttp.bean;

import java.text.SimpleDateFormat;

/**
 * Created by 87710 on 2017/12/11.
 */

public class NewsListBean {
    /**
     * id : 7cd8ea166b8547e4ba92deaabbcb33aa
     * title : 海军
     * image : /userfiles/1/_thumbs/images/cms/article/2017/12/20171211183801_eebe497a-16.jpg
     * hits : 0
     * publishDate : 1511797003000
     * categoryId : 2
     */

    private String id;
    private String title;
    private String image;
    private String attach;
    private int hits;
    private long publishDate;
    private String categoryId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }
}
