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
        repository.loadReplies(currentCommentId);

        repository.getReplies().observeForever(repliesList -> {
            if (repliesList != null) {
                replies.setValue(repliesList);
                isLoading.setValue(false);
            }
        });
    }

    public void replyToComment(String content, String authorId, String authorName,
                               String parentId, String replyToId, String replyToName) {
        String currentPostId = postId.getValue();
        if (currentPostId == null) return;

        repository.replyToComment(currentPostId, content, authorId, authorName,
                parentId, replyToId, replyToName);

        new android.os.Handler().postDelayed(() -> {
            loadReplies();
        }, 500);
    }

    public void replyToReply(String content, String authorId, String authorName,
                             String parentId, String replyToId, String replyToName) {
        String currentPostId = postId.getValue();
        if (currentPostId == null) return;

        repository.replyToReply(currentPostId, content, authorId, authorName,
                parentId, replyToId, replyToName);

        new android.os.Handler().postDelayed(() -> {
            loadReplies();
        }, 500);
    }

    public void deleteReply(String replyId, String userId, boolean isAdmin) {
        repository.deleteComment(replyId, userId, isAdmin);
        new android.os.Handler().postDelayed(() -> {
            loadReplies();
        }, 500);
    }
}