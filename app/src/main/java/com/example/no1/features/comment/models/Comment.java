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
    private String parentId;
    private String replyTo;
    private String replyToId;
    private List<Comment> replies;

    public Comment() {
        this.replies = new ArrayList<>();
    }

    public Comment(String id, String postId, String content, String author, String authorId) {
        this.id = id;
        this.postId = postId;
        this.content = content;
        this.author = author;
        this.authorId = authorId;
        this.createTime = new Date();
        this.replies = new ArrayList<>();
        this.parentId = null;
        this.replyTo = null;
        this.replyToId = null;
    }

    public Comment(String id, String postId, String content, String author, String authorId,
                   String parentId, String replyTo, String replyToId) {
        this.id = id;
        this.postId = postId;
        this.content = content;
        this.author = author;
        this.authorId = authorId;
        this.createTime = new Date();
        this.parentId = parentId;
        this.replyTo = replyTo;
        this.replyToId = replyToId;
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

    public String getReplyTo() { return replyTo; }
    public void setReplyTo(String replyTo) { this.replyTo = replyTo; }

    public String getReplyToId() { return replyToId; }
    public void setReplyToId(String replyToId) { this.replyToId = replyToId; }

    public List<Comment> getReplies() { return replies; }
    public void setReplies(List<Comment> replies) { this.replies = replies; }

    public void addReply(Comment reply) {
        if (this.replies == null) {
            this.replies = new ArrayList<>();
        }
        this.replies.add(reply);
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

    public boolean isTopLevel() {
        return parentId == null || parentId.isEmpty();
    }

    public boolean isAuthor(String userId) {
        return authorId != null && authorId.equals(userId);
    }

    public String getDisplayContent() {
        if (replyTo != null && !replyTo.isEmpty()) {
            return "@" + replyTo + " " + content;
        }
        return content;
    }
}