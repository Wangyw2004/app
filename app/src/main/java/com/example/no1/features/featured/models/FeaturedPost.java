package com.example.no1.features.featured.models;

import java.util.Date;
import java.util.List;

public class FeaturedPost {
    private String id;
    private String title;
    private String coverImage;
    private List<ContentBlock> content;
    private String author;
    private String authorId;
    private Date createTime;
    private Date publishTime;
    private String status;  // pending, published, rejected
    private String rejectReason;
    private int likeCount;
    private int commentCount;
    private int viewCount;

    public FeaturedPost() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }

    public List<ContentBlock> getContent() { return content; }
    public void setContent(List<ContentBlock> content) { this.content = content; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getPublishTime() { return publishTime; }
    public void setPublishTime(Date publishTime) { this.publishTime = publishTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public int getCommentCount() { return commentCount; }
    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }

    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }

    public String getStatusText() {
        switch (status) {
            case "pending": return "审核中";
            case "published": return "已通过";
            case "rejected": return "已拒绝";
            default: return "未知";
        }
    }

    public int getStatusColor() {
        switch (status) {
            case "pending": return android.R.color.holo_orange_dark;
            case "published": return android.R.color.holo_green_dark;
            case "rejected": return android.R.color.holo_red_dark;
            default: return android.R.color.darker_gray;
        }
    }

    public static class ContentBlock {
        private String type;  // "text" or "image"
        private String content;

        public ContentBlock(String type, String content) {
            this.type = type;
            this.content = content;
        }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
}