package com.example.no1.features.post.models;
import java.util.Date;
public class Post {
    private String id;
    private String title;
    private String content;
    private String author;
    private String authorId;
    private Date createTime;
    private int likeCount;
    private int commentCount;
    private boolean isLiked;

    public Post() {}

    public Post(String id, String title, String content, String author, String authorId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.authorId = authorId;
        this.createTime = new Date();
        this.likeCount = 0;
        this.commentCount = 0;
        this.isLiked = false;
    }
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

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public int getCommentCount() { return commentCount; }
    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }

    public boolean isLiked() { return isLiked; }
    public void setLiked(boolean liked) { isLiked = liked; }
    // 判断当前用户是否是作者
    public boolean isAuthor(String userId) {
        return authorId != null && authorId.equals(userId);
    }



}
