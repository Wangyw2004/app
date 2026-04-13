package com.example.no1.features.comment.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.no1.features.comment.models.Comment;
import com.example.no1.features.comment.repository.CommentRepository;
import java.util.List;

public class CommentViewModel extends AndroidViewModel {

    private CommentRepository repository;
    private MutableLiveData<String> postId = new MutableLiveData<>();
    private MutableLiveData<String> commentContent = new MutableLiveData<>("");
    private MutableLiveData<String> contentError = new MutableLiveData<>();
    private MutableLiveData<Comment> replyingTo = new MutableLiveData<>(null);
    private MutableLiveData<Boolean> isReplying = new MutableLiveData<>(false);
    private MutableLiveData<String> replyParentId = new MutableLiveData<>();
    private MutableLiveData<String> replyToId = new MutableLiveData<>();
    private MutableLiveData<String> replyToName = new MutableLiveData<>();

    public CommentViewModel(Application application) {
        super(application);
        repository = CommentRepository.getInstance(application);
    }

    public LiveData<List<Comment>> getComments() {
        return repository.getComments();
    }

    public LiveData<Boolean> getIsLoading() {
        return repository.getIsLoading();
    }

    public LiveData<String> getErrorMessage() {
        return repository.getErrorMessage();
    }

    public LiveData<String> getCommentContent() {
        return commentContent;
    }

    public LiveData<String> getContentError() {
        return contentError;
    }

    public LiveData<Comment> getReplyingTo() {
        return replyingTo;
    }

    public LiveData<Boolean> getIsReplying() {
        return isReplying;
    }

    public void setPostId(String id) {
        postId.setValue(id);
        loadComments();
    }

    public void setCommentContent(String content) {
        commentContent.setValue(content);
        if (content != null && content.trim().isEmpty()) {
            contentError.setValue("请输入内容");
        } else if (content != null && content.length() > 200) {
            contentError.setValue("内容不能超过200个字符");
        } else {
            contentError.setValue(null);
        }
    }

    public void setReplyingTo(Comment comment) {
        replyingTo.setValue(comment);
        isReplying.setValue(comment != null);
        if (comment != null) {
            replyParentId.setValue(comment.getId());
            replyToId.setValue(comment.getAuthorId());
            replyToName.setValue(null);  // 回复一级评论不加@
            commentContent.setValue("");
        }
    }

    public void setReplyToReply(Comment reply) {
        replyingTo.setValue(reply);
        isReplying.setValue(true);
        replyParentId.setValue(reply.getParentId());  // parentId是一级评论的ID
        replyToId.setValue(reply.getAuthorId());
        replyToName.setValue(reply.getAuthor());      // 回复二级评论需要@
        commentContent.setValue("");
    }

    public void cancelReply() {
        replyingTo.setValue(null);
        isReplying.setValue(false);
        replyParentId.setValue(null);
        replyToId.setValue(null);
        replyToName.setValue(null);
        commentContent.setValue("");
        contentError.setValue(null);
    }

    public void loadComments() {
        String currentPostId = postId.getValue();
        if (currentPostId != null && !currentPostId.isEmpty()) {
            repository.loadCommentTree(currentPostId);
        }
    }

    public void addComment(String authorId, String authorName) {
        String currentPostId = postId.getValue();
        String currentContent = commentContent.getValue();

        if (currentPostId == null || currentPostId.isEmpty()) {
            return;
        }
        if (currentContent == null || currentContent.trim().isEmpty()) {
            contentError.setValue("请输入内容");
            return;
        }
        if (currentContent.length() > 200) {
            contentError.setValue("内容不能超过200个字符");
            return;
        }

        Comment replying = replyingTo.getValue();

        if (replying != null) {
            // 回复评论
            String parentId = replyParentId.getValue();
            String toId = replyToId.getValue();
            String toName = replyToName.getValue();

            repository.replyToComment(currentPostId, currentContent, authorId, authorName,
                    parentId, toId, toName);
            cancelReply();
        } else {
            // 发布新评论
            repository.addPostComment(currentPostId, currentContent, authorId, authorName);
        }

        commentContent.setValue("");
        contentError.setValue(null);
    }

    public void deleteComment(String commentId, String userId) {
        repository.deleteComment(commentId, userId);
    }
}