package com.example.no1.features.comment.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.no1.features.comment.data.CommentDataSource;
import com.example.no1.features.comment.models.Comment;
import java.util.List;
import java.util.UUID;

public class CommentRepository {
    private static CommentRepository instance;
    private CommentDataSource dataSource;
    private MutableLiveData<List<Comment>> commentsLiveData;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<String> errorMessage;

    private CommentRepository(Context context) {
        dataSource = CommentDataSource.getInstance(context);
        commentsLiveData = new MutableLiveData<>();
        isLoading = new MutableLiveData<>(false);
        errorMessage = new MutableLiveData<>();
    }

    public static synchronized CommentRepository getInstance(Context context) {
        if (instance == null) {
            instance = new CommentRepository(context);
        }
        return instance;
    }

    public LiveData<List<Comment>> getComments() {
        return commentsLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadCommentsByPostId(String postId) {
        isLoading.setValue(true);
        try {
            List<Comment> comments = dataSource.getCommentsByPostId(postId);
            commentsLiveData.setValue(comments);
        } catch (Exception e) {
            errorMessage.setValue("加载评论失败: " + e.getMessage());
        } finally {
            isLoading.setValue(false);
        }
    }

    public void addComment(String postId, String content, String authorId, String authorName) {
        if (content == null || content.trim().isEmpty()) {
            errorMessage.setValue("评论内容不能为空");
            return;
        }

        try {
            String commentId = UUID.randomUUID().toString();
            Comment newComment = new Comment(commentId, postId, content, authorName, authorId);
            dataSource.addComment(newComment);
            loadCommentsByPostId(postId);
        } catch (Exception e) {
            errorMessage.setValue("发布评论失败: " + e.getMessage());
        }
    }

    public void addReply(String postId, String content, String authorId, String authorName,
                         String parentId, String replyTo, String replyToId) {
        if (content == null || content.trim().isEmpty()) {
            errorMessage.setValue("回复内容不能为空");
            return;
        }

        try {
            String commentId = UUID.randomUUID().toString();
            Comment reply = new Comment(commentId, postId, content, authorName, authorId,
                    parentId, replyTo, replyToId);
            dataSource.addComment(reply);
            loadCommentsByPostId(postId);
        } catch (Exception e) {
            errorMessage.setValue("回复失败: " + e.getMessage());
        }
    }

    public void deleteComment(String commentId, String userId) {
        boolean success = dataSource.deleteComment(commentId, userId);
        if (success) {
            // 刷新当前显示的评论列表
            List<Comment> currentComments = commentsLiveData.getValue();
            if (currentComments != null && !currentComments.isEmpty()) {
                loadCommentsByPostId(currentComments.get(0).getPostId());
            }
        } else {
            errorMessage.setValue("删除失败：无权删除或评论不存在");
        }
    }

    public void getCommentById(String commentId, OnCommentLoadedListener listener) {
        Comment comment = dataSource.getCommentById(commentId);
        if (comment != null) {
            listener.onSuccess(comment);
        } else {
            listener.onError("评论不存在");
        }
    }

    public void getRepliesByParentId(String parentId, OnRepliesLoadedListener listener) {
        List<Comment> replies = dataSource.getRepliesByParentId(parentId);
        listener.onSuccess(replies);
    }

    public interface OnCommentLoadedListener {
        void onSuccess(Comment comment);
        void onError(String error);
    }

    public interface OnRepliesLoadedListener {
        void onSuccess(List<Comment> replies);
        void onError(String error);
    }
}