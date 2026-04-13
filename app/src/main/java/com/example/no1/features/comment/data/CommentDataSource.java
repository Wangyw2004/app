package com.example.no1.features.comment.data;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.no1.features.comment.models.Comment;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // 获取帖子的评论树
    public List<Comment> getCommentTreeByPostId(String postId) {
        List<Comment> allComments = loadComments();
        List<Comment> topLevelComments = new ArrayList<>();
        Map<String, Comment> commentMap = new HashMap<>();

        // 收集该帖子的所有评论
        List<Comment> postComments = new ArrayList<>();
        for (Comment comment : allComments) {
            if (comment.getPostId().equals(postId)) {
                postComments.add(comment);
                commentMap.put(comment.getId(), comment);
            }
        }

        // 分离一级评论
        for (Comment comment : postComments) {
            if (comment.isTopLevel()) {
                topLevelComments.add(comment);
            }
        }

        // 为每个一级评论添加二级评论（按时间正序）
        for (Comment topComment : topLevelComments) {
            List<Comment> replies = new ArrayList<>();
            for (Comment comment : postComments) {
                if (topComment.getId().equals(comment.getParentId())) {
                    replies.add(comment);
                }
            }
            replies.sort((a, b) -> a.getCreateTime().compareTo(b.getCreateTime()));
            topComment.setReplies(replies);
        }

        // 一级评论按时间倒序
        topLevelComments.sort((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()));

        return topLevelComments;
    }

    // 获取某个一级评论的所有二级评论
    public List<Comment> getRepliesByParentId(String parentId) {
        List<Comment> allComments = loadComments();
        List<Comment> replies = new ArrayList<>();
        for (Comment comment : allComments) {
            if (parentId.equals(comment.getParentId())) {
                replies.add(comment);
            }
        }
        replies.sort((a, b) -> a.getCreateTime().compareTo(b.getCreateTime()));
        return replies;
    }

    public void addComment(Comment comment) {
        List<Comment> comments = loadComments();
        comments.add(comment);
        saveComments(comments);
    }

    public boolean deleteComment(String commentId, String userId) {
        List<Comment> comments = loadComments();
        List<Comment> toRemove = new ArrayList<>();

        for (Comment comment : comments) {
            if (comment.getId().equals(commentId)) {
                if (comment.getAuthorId().equals(userId)) {
                    toRemove.add(comment);
                    for (Comment other : comments) {
                        if (commentId.equals(other.getParentId())) {
                            toRemove.add(other);
                        }
                    }
                    break;
                } else {
                    return false;
                }
            }
        }

        comments.removeAll(toRemove);
        saveComments(comments);
        return true;
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
}