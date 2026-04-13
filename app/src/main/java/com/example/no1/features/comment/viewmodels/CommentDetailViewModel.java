package com.example.no1.features.comment.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.no1.features.comment.models.Comment;
import com.example.no1.features.comment.repository.CommentRepository;
import java.util.List;

public class CommentDetailViewModel extends AndroidViewModel {

    private CommentRepository repository;
    private MutableLiveData<String> postId = new MutableLiveData<>();
    private MutableLiveData<String> commentId = new MutableLiveData<>();
    private MutableLiveData<Comment> originalComment = new MutableLiveData<>();
    private MutableLiveData<List<Comment>> replies = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public CommentDetailViewModel(Application application) {
        super(application);
        repository = CommentRepository.getInstance(application);
    }

    public LiveData<Comment> getOriginalComment() {
        return originalComment;
    }

    public LiveData<List<Comment>> getReplies() {
        return replies;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void setPostId(String id) {
        postId.setValue(id);
    }

    public void setCommentId(String id) {
        commentId.setValue(id);
    }

    public void loadOriginalComment() {
        String currentCommentId = commentId.getValue();
        if (currentCommentId == null) return;

        isLoading.setValue(true);
        repository.getCommentById(currentCommentId, new CommentRepository.OnCommentLoadedListener() {
            @Override
            public void onSuccess(Comment comment) {
                originalComment.setValue(comment);
                isLoading.setValue(false);
            }

            @Override
            public void onError(String error) {
                errorMessage.setValue(error);
                isLoading.setValue(false);
            }
        });
    }

    public void loadReplies() {
        String currentCommentId = commentId.getValue();
        if (currentCommentId == null) return;

        isLoading.setValue(true);
        repository.getRepliesByParentId(currentCommentId, new CommentRepository.OnRepliesLoadedListener() {
            @Override
            public void onSuccess(List<Comment> repliesList) {
                replies.setValue(repliesList);
                isLoading.setValue(false);
            }

            @Override
            public void onError(String error) {
                errorMessage.setValue(error);
                isLoading.setValue(false);
            }
        });
    }

    public void addReply(String content, String authorId, String authorName,
                         String parentId, String replyTo, String replyToId) {
        String currentPostId = postId.getValue();
        if (currentPostId == null) return;

        if (content == null || content.trim().isEmpty()) {
            errorMessage.setValue("回复内容不能为空");
            return;
        }

        repository.addReply(currentPostId, content, authorId, authorName,
                parentId, replyTo, replyToId);

        // 刷新回复列表
        loadReplies();
    }

    public void deleteReply(String replyId, String userId) {
        repository.deleteComment(replyId, userId);
        loadReplies();
    }
}