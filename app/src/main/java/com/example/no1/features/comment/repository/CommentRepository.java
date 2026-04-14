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
    private MutableLiveData<List<Comment>> repliesLiveData;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<String> errorMessage;

    private CommentRepository(Context context) {
        dataSource = CommentDataSource.getInstance(context);
        commentsLiveData = new MutableLiveData<>();
        repliesLiveData = new MutableLiveData<>();
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

    public LiveData<List<Comment>> getReplies() {
        return repliesLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadCommentTree(String postId) {
        isLoading.setValue(true);
        try {
            List<Comment> comments = dataSource.getCommentTreeByPostId(postId);
            commentsLiveData.setValue(comments);
        } catch (Exception e) {
            errorMessage.setValue("加载评论失败: " + e.getMessage());
        } finally {
            isLoading.setValue(false);
        }
    }

    public void loadReplies(String parentId) {
        isLoading.setValue(true);
        try {
            List<Comment> replies = dataSource.getRepliesByParentId(parentId);
            repliesLiveData.setValue(replies);
        } catch (Exception e) {
            errorMessage.setValue("加载回复失败: " + e.getMessage());
        } finally {
            isLoading.setValue(false);
        }
    }

    public void addPostComment(String postId, String content, String authorId, String authorName) {
        if (content == null || content.trim().isEmpty()) {
            errorMessage.setValue("内容不能为空");
            return;
        }
        try {
            String commentId = UUID.randomUUID().toString();
            Comment comment = new Comment(commentId, postId, content, authorName, authorId);
            dataSource.addComment(comment);
            loadCommentTree(postId);
        } catch (Exception e) {
            errorMessage.setValue("发布失败: " + e.getMessage());
        }
    }

    public void replyToComment(String postId, String content, String authorId, String authorName,
                               String parentId, String replyToId, String replyToName) {
        if (content == null || content.trim().isEmpty()) {
            errorMessage.setValue("内容不能为空");
            return;
        }
        try {
            String commentId = UUID.randomUUID().toString();
            Comment reply = new Comment(commentId, postId, content, authorName, authorId,
                    parentId, replyToId, replyToName);
            dataSource.addComment(reply);
            loadCommentTree(postId);
            loadReplies(parentId);
        } catch (Exception e) {
            errorMessage.setValue("回复失败: " + e.getMessage());
        }
    }

    public void replyToReply(String postId, String content, String authorId, String authorName,
                             String parentId, String replyToId, String replyToName) {
        if (content == null || content.trim().isEmpty()) {
            errorMessage.setValue("内容不能为空");
            return;
        }
        try {
            String commentId = UUID.randomUUID().toString();
            Comment reply = new Comment(commentId, postId, content, authorName, authorId,
                    parentId, replyToId, replyToName);
            dataSource.addComment(reply);
            loadCommentTree(postId);
            loadReplies(parentId);
        } catch (Exception e) {
            errorMessage.setValue("回复失败: " + e.getMessage());
        }
    }

    /**
     * 删除评论
     * @param commentId 评论ID
     * @param userId 当前用户ID
     * @param isAdmin 是否是管理员
     */
    public void deleteComment(String commentId, String userId, boolean isAdmin) {
        Comment comment = dataSource.getCommentById(commentId);
        if (comment == null) {
            errorMessage.setValue("评论不存在");
            return;
        }

        if (isAdmin || comment.getAuthorId().equals(userId)) {
            boolean success = dataSource.deleteComment(commentId, userId);
            if (success) {
                loadCommentTree(comment.getPostId());
                if (comment.getParentId() != null) {
                    loadReplies(comment.getParentId());
                }
            } else {
                errorMessage.setValue("删除失败");
            }
        } else {
            errorMessage.setValue("无权删除此评论");
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

    public interface OnCommentLoadedListener {
        void onSuccess(Comment comment);
        void onError(String error);
    }
}