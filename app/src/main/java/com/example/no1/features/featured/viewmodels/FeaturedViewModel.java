package com.example.no1.features.featured.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.no1.features.featured.models.FeaturedPost;
import com.example.no1.features.featured.repository.FeaturedRepository;
import java.util.List;

public class FeaturedViewModel extends AndroidViewModel {

    private FeaturedRepository repository;
    private LiveData<List<FeaturedPost>> publishedFeatured;
    private LiveData<List<FeaturedPost>> pendingFeatured;
    private LiveData<List<FeaturedPost>> userApplications;
    private LiveData<Boolean> isLoading;
    private LiveData<String> errorMessage;

    public FeaturedViewModel(Application application) {
        super(application);
        repository = FeaturedRepository.getInstance(application);
        publishedFeatured = repository.getPublishedFeatured();
        pendingFeatured = repository.getPendingFeatured();
        userApplications = repository.getUserApplications();
        isLoading = repository.getIsLoading();
        errorMessage = repository.getErrorMessage();
    }

    public LiveData<List<FeaturedPost>> getPublishedFeatured() {
        return publishedFeatured;
    }

    public LiveData<List<FeaturedPost>> getPendingFeatured() {
        return pendingFeatured;
    }

    public LiveData<List<FeaturedPost>> getUserApplications() {
        return userApplications;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadPublishedFeatured() {
        repository.loadPublishedFeatured();
    }

    public void loadPendingFeatured() {
        repository.loadPendingFeatured();
    }

    public void loadUserApplications(String userId) {
        repository.loadUserApplications(userId);
    }

    public void submitApplication(String title, String coverImage,
                                  List<FeaturedPost.ContentBlock> content,
                                  String authorId, String authorName) {
        repository.submitApplication(title, coverImage, content, authorId, authorName);
    }

    public void approveApplication(String postId, String adminName) {
        repository.approveApplication(postId, adminName);
    }

    public void rejectApplication(String postId, String reason, String adminName) {
        repository.rejectApplication(postId, reason, adminName);
    }

    public void updateApplication(String postId, String title, String coverImage,
                                  List<FeaturedPost.ContentBlock> content) {
        repository.updateApplication(postId, title, coverImage, content);
    }

    public void deleteFeatured(String postId, boolean isAdmin, String userId) {
        repository.deleteFeatured(postId, isAdmin, userId);
    }

    public FeaturedPost getFeaturedById(String id) {
        return repository.getFeaturedById(id);
    }

    public void incrementViewCount(String postId) {
        repository.incrementViewCount(postId);
    }
}