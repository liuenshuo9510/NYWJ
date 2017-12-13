package com.nanyue.app.nywj.bean;

import java.text.SimpleDateFormat;

/**
 * Created by 87710 on 2017/12/11.
 */

public class CourseListBean {
    /**
     * id : 461f9630f4074db99c39f37d532af85a
     * title : test
     * image : /userfiles/1/_thumbs/images/cms/article/2017/12/20171211183801_eebe497a-16.jpg
     * hits : 0
     * attach : /userfiles/1/files/cms/article/2017/12/20171205124358_750bd82e-8c.mp4
     * publishDate : 1512995741000
     * categoryId : 7
     */

    private String id;
    private String title;
    private String image;
    private int hits;
    private String attach;
    private long publishDate;
    private int categoryId;

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

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getPublishDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return simpleDateFormat.format(publishDate);
    }

    public void setPublishDate(long publishDate) {
        this.publishDate = publishDate;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
