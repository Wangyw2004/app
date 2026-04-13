package com.example.no1.features.post.models;

public class CreatePostRequest {
    private String title;
    private String content;
    private String authorId;

    public CreatePostRequest(String title, String content, String authorId) {
        this.title = title;
        this.content = content;
        this.authorId = authorId;
    }

    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getAuthorId() { return authorId; }
}
