package com.example.no1.features.comment.models;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class Comment {
    private String id;
    private String postId;
    private String content;
    private String author;
    private String authorId;
    private Date createTime;
    private String parentId;      // 父评论ID（一级评论parentId=null）
    private String replyToId;     // 回复的用户ID（null表示回复一级评论）
    private String replyToName;   // 回复的用户名（null表示不加@）
    private List<Comment> replies;

    public Comment() {
        this.replies = new ArrayList<>();
    }

    // 一级评论（回复帖子）
    public Comment(String id, String postId, String content, String author, String authorId) {
        this.id = id;
        this.postId = postId;
        this.content = content;
        this.author = author;
        this.authorId = authorId;
        this.createTime = new Date();
        this.parentId = null;
        this.replyToId = null;
        this.replyToName = null;
        this.replies = new ArrayList<>();
    }

    // 二级评论（回复一级评论或回复二级评论）
    public Comment(String id, String postId, String content, String author, String authorId,
                   String parentId, String replyToId, String replyToName) {
        this.id = id;
        this.postId = postId;
        this.content = content;
        this.author = author;
        this.authorId = authorId;
        this.createTime = new Date();
        this.parentId = parentId;
        this.replyToId = replyToId;
        this.replyToName = replyToName;
        this.replies = new ArrayList<>();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }

    public String getReplyToId() { return replyToId; }
    public void setReplyToId(String replyToId) { this.replyToId = replyToId; }

    public String getReplyToName() { return replyToName; }
    public void setReplyToName(String replyToName) { this.replyToName = replyToName; }

    public List<Comment> getReplies() { return replies; }
    public void setReplies(List<Comment> replies) { this.replies = replies; }

    public void addReply(Comment reply) {
        if (this.replies == null) {
            this.replies = new ArrayList<>();
        }
        this.replies.add(reply);
    }

    public boolean isTopLevel() {
        return parentId == null;
    }

    public boolean isAuthor(String userId) {
        return authorId != null && authorId.equals(userId);
    }

    public int getReplyCount() {
        return replies != null ? replies.size() : 0;
    }

    public Comment getLatestReply() {
        if (replies != null && !replies.isEmpty()) {
            return replies.get(replies.size() - 1);
        }
        return null;
    }

    public String getDisplayContent() {
        if (replyToName != null && !replyToName.isEmpty()) {
            return "@" + replyToName + " " + content;
        }
        return content;
    }
}