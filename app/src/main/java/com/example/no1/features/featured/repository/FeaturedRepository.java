package com.example.no1.features.featured.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.no1.features.featured.data.FeaturedDataSource;
import com.example.no1.features.featured.models.FeaturedPost;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class FeaturedRepository {
    private static FeaturedRepository instance;
    private FeaturedDataSource dataSource;
    private MutableLiveData<List<FeaturedPost>> publishedLiveData;
    private MutableLiveData<List<FeaturedPost>> pendingLiveData;
    private MutableLiveData<List<FeaturedPost>> userApplicationsLiveData;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<String> errorMessage;

    private FeaturedRepository(Context context) {
        dataSource = FeaturedDataSource.getInstance(context);
        publishedLiveData = new MutableLiveData<>();
        pendingLiveData = new MutableLiveData<>();
        userApplicationsLiveData = new MutableLiveData<>();
        isLoading = new MutableLiveData<>(false);
        errorMessage = new MutableLiveData<>();
    }

    public static synchronized FeaturedRepository getInstance(Context context) {
        if (instance == null) {
            instance = new FeaturedRepository(context);
        }
        return instance;
    }

    public LiveData<List<FeaturedPost>> getPublishedFeatured() {
        return publishedLiveData;
    }

    public LiveData<List<FeaturedPost>> getPendingFeatured() {
        return pendingLiveData;
    }

    public LiveData<List<FeaturedPost>> getUserApplications() {
        return userApplicationsLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadPublishedFeatured() {
        isLoading.setValue(true);
        List<FeaturedPost> posts = dataSource.getPublishedFeatured();
        publishedLiveData.setValue(posts);
        isLoading.setValue(false);
    }

    public void loadPendingFeatured() {
        isLoading.setValue(true);
        List<FeaturedPost> posts = dataSource.getPendingFeatured();
        pendingLiveData.setValue(posts);
        isLoading.setValue(false);
    }

    public void loadUserApplications(String userId) {
        isLoading.setValue(true);
        List<FeaturedPost> posts = dataSource.getFeaturedByUserId(userId);
        userApplicationsLiveData.setValue(posts);
        isLoading.setValue(false);
    }

    public void submitApplication(String title, String coverImage,
                                  List<FeaturedPost.ContentBlock> content,
                                  String authorId, String authorName) {
        if (title == null || title.trim().isEmpty()) {
            errorMessage.setValue("标题不能为空");
            return;
        }
        if (coverImage == null || coverImage.trim().isEmpty()) {
            errorMessage.setValue("请上传封面图片");
            return;
        }
        if (content == null || content.isEmpty()) {
            errorMessage.setValue("内容不能为空");
            return;
        }

        String id = UUID.randomUUID().toString();
        FeaturedPost post = new FeaturedPost();
        post.setId(id);
        post.setTitle(title);
        post.setCoverImage(coverImage);
        post.setContent(content);
        post.setAuthor(authorName);
        post.setAuthorId(authorId);
        post.setCreateTime(new Date());
        post.setStatus("pending");
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setViewCount(0);

        dataSource.addFeatured(post);
        errorMessage.setValue(null);
    }

    public void approveApplication(String postId, String adminName) {
        FeaturedPost post = dataSource.getFeaturedById(postId);
        if (post != null) {
            post.setStatus("published");
            post.setPublishTime(new Date());
            dataSource.updateFeatured(post);
            loadPublishedFeatured();
            loadPendingFeatured();
        }
    }

    public void rejectApplication(String postId, String reason, String adminName) {
        FeaturedPost post = dataSource.getFeaturedById(postId);
        if (post != null) {
            post.setStatus("rejected");
            post.setRejectReason(reason);
            dataSource.updateFeatured(post);
            loadPendingFeatured();
            // 刷新用户申请列表
            if (post.getAuthorId() != null) {
                loadUserApplications(post.getAuthorId());
            }
        }
    }

    public void updateApplication(String postId, String title, String coverImage,
                                  List<FeaturedPost.ContentBlock> content) {
        FeaturedPost post = dataSource.getFeaturedById(postId);
        if (post != null) {
            post.setTitle(title);
            post.setCoverImage(coverImage);
            post.setContent(content);
            post.setStatus("pending");
            post.setRejectReason(null);
            dataSource.updateFeatured(post);
            if (post.getAuthorId() != null) {
                loadUserApplications(post.getAuthorId());
            }
            loadPendingFeatured();
        }
    }

    public void deleteFeatured(String postId, boolean isAdmin, String userId) {
        FeaturedPost post = dataSource.getFeaturedById(postId);
        if (post != null) {
            if (isAdmin || post.getAuthorId().equals(userId)) {
                dataSource.deleteFeatured(postId);
                loadPublishedFeatured();
                loadPendingFeatured();
                if (post.getAuthorId() != null) {
                    loadUserApplications(post.getAuthorId());
                }
            } else {
                errorMessage.setValue("无权删除");
            }
        }
    }

    public FeaturedPost getFeaturedById(String id) {
        return dataSource.getFeaturedById(id);
    }

    public void incrementViewCount(String postId) {
        FeaturedPost post = dataSource.getFeaturedById(postId);
        if (post != null) {
            post.setViewCount(post.getViewCount() + 1);
            dataSource.updateFeatured(post);
        }
    }
}