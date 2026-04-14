package com.example.no1.features.notice.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.no1.features.notice.models.Notice;
import com.example.no1.features.notice.repository.NoticeRepository;
import java.util.List;

public class NoticeViewModel extends AndroidViewModel {

    private NoticeRepository repository;
    private LiveData<List<Notice>> notices;
    private LiveData<Boolean> isLoading;
    private LiveData<String> errorMessage;

    public NoticeViewModel(Application application) {
        super(application);
        repository = NoticeRepository.getInstance(application);
        notices = repository.getNotices();
        isLoading = repository.getIsLoading();
        errorMessage = repository.getErrorMessage();
    }

    public LiveData<List<Notice>> getNotices() {
        return notices;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadNotices() {
        repository.loadNotices();
    }

    public void publishNotice(String title, String content, String authorId, String authorName) {
        repository.publishNotice(title, content, authorId, authorName);
    }

    public void updateNotice(String noticeId, String title, String content) {
        repository.updateNotice(noticeId, title, content);
    }

    public void deleteNotice(String noticeId) {
        repository.deleteNotice(noticeId);
    }

    public Notice getNoticeById(String id) {
        return repository.getNoticeById(id);
    }

    public void incrementViewCount(String noticeId) {
        repository.incrementViewCount(noticeId);
    }
}