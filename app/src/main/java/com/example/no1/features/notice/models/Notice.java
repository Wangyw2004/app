package com.example.no1.features.notice.models;

import java.util.Date;

public class Notice {
    private String id;
    private String title;
    private String content;
    private String author;
    private String authorId;
    private Date createTime;
    private Date updateTime;
    private int viewCount;

    public Notice() {}

    public Notice(String id, String title, String content, String author, String authorId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.authorId = authorId;
        this.createTime = new Date();
        this.updateTime = new Date();
        this.viewCount = 0;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }

    public void incrementViewCount() {
        this.viewCount++;
    }
}