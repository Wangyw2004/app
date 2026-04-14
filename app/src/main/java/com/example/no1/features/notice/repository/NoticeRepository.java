package com.example.no1.features.notice.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.no1.features.notice.data.NoticeDataSource;
import com.example.no1.features.notice.models.Notice;
import java.util.List;
import java.util.UUID;

public class NoticeRepository {
    private static NoticeRepository instance;
    private NoticeDataSource dataSource;
    private MutableLiveData<List<Notice>> noticesLiveData;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<String> errorMessage;

    private NoticeRepository(Context context) {
        dataSource = NoticeDataSource.getInstance(context);
        noticesLiveData = new MutableLiveData<>();
        isLoading = new MutableLiveData<>(false);
        errorMessage = new MutableLiveData<>();
        loadNotices();
    }

    public static synchronized NoticeRepository getInstance(Context context) {
        if (instance == null) {
            instance = new NoticeRepository(context);
        }
        return instance;
    }

    public LiveData<List<Notice>> getNotices() {
        return noticesLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadNotices() {
        isLoading.setValue(true);
        List<Notice> notices = dataSource.loadNotices();
        noticesLiveData.setValue(notices);
        isLoading.setValue(false);
    }

    public void publishNotice(String title, String content, String authorId, String authorName) {
        if (title == null || title.trim().isEmpty()) {
            errorMessage.setValue("标题不能为空");
            return;
        }
        if (content == null || content.trim().isEmpty()) {
            errorMessage.setValue("内容不能为空");
            return;
        }

        String noticeId = UUID.randomUUID().toString();
        Notice notice = new Notice(noticeId, title, content, authorName, authorId);
        dataSource.addNotice(notice);
        loadNotices();
        errorMessage.setValue(null);
    }

    public void updateNotice(String noticeId, String title, String content) {
        Notice notice = dataSource.getNoticeById(noticeId);
        if (notice != null) {
            notice.setTitle(title);
            notice.setContent(content);
            notice.setUpdateTime(new java.util.Date());
            dataSource.updateNotice(notice);
            loadNotices();
        }
    }

    public void deleteNotice(String noticeId) {
        boolean success = dataSource.deleteNotice(noticeId);
        if (success) {
            loadNotices();
        } else {
            errorMessage.setValue("删除失败");
        }
    }

    public Notice getNoticeById(String id) {
        return dataSource.getNoticeById(id);
    }

    public void incrementViewCount(String noticeId) {
        Notice notice = dataSource.getNoticeById(noticeId);
        if (notice != null) {
            notice.incrementViewCount();
            dataSource.updateNotice(notice);
        }
    }
}