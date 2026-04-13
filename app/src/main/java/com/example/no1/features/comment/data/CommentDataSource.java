package com.example.no1.features.comment.data;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.no1.features.comment.models.Comment;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CommentDataSource {
    private static final String PREF_NAME = "comment_prefs";
    private static final String KEY_COMMENTS = "comments_list";

    private SharedPreferences sharedPreferences;
    private Gson gson;
    private static CommentDataSource instance;

    private CommentDataSource(Context context) {
        sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized CommentDataSource getInstance(Context context) {
        if (instance == null) {
            instance = new CommentDataSource(context);
        }
        return instance;
    }

    public void saveComments(List<Comment> comments) {
        try {
            String json = gson.toJson(comments);
            sharedPreferences.edit().putString(KEY_COMMENTS, json).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Comment> loadComments() {
        try {
            String json = sharedPreferences.getString(KEY_COMMENTS, "");
            if (json == null || json.isEmpty()) {
                return new ArrayList<>();
            }
            Type type = new TypeToken<List<Comment>>(){}.getType();
            List<Comment> comments = gson.fromJson(json, type);
            return comments != null ? comments : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Comment> getCommentsByPostId(String postId) {
        List<Comment> allComments = loadComments();
        List<Comment> topLevelComments = new ArrayList<>();

        for (Comment comment : allComments) {
            if (comment.getPostId().equals(postId) && comment.isTopLevel()) {
                topLevelComments.add(comment);
            }
        }

        for (Comment topComment : topLevelComments) {
            List<Comment> replies = new ArrayList<>();
            for (Comment comment : allComments) {
                if (comment.getParentId() != null && comment.getParentId().equals(topComment.getId())) {
                    replies.add(comment);
                }
            }
            topComment.setReplies(replies);
        }

        return topLevelComments;
    }

    public void addComment(Comment comment) {
        List<Comment> comments = loadComments();
        comments.add(0, comment);
        saveComments(comments);
    }

    public boolean deleteComment(String commentId, String userId) {
        List<Comment> comments = loadComments();
        for (int i = 0; i < comments.size(); i++) {
            if (comments.get(i).getId().equals(commentId)) {
                if (comments.get(i).getAuthorId().equals(userId)) {
                    comments.remove(i);
                    saveComments(comments);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public Comment getCommentById(String commentId) {
        List<Comment> comments = loadComments();
        for (Comment comment : comments) {
            if (comment.getId().equals(commentId)) {
                return comment;
            }
        }
        return null;
    }

    public List<Comment> getRepliesByParentId(String parentId) {
        List<Comment> allComments = loadComments();
        List<Comment> replies = new ArrayList<>();
        for (Comment comment : allComments) {
            if (parentId.equals(comment.getParentId())) {
                replies.add(comment);
            }
        }
        return replies;
    }
}