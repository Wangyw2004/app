package com.example.no1.features.post.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.no1.features.post.models.Post;
import com.example.no1.features.post.repository.PostRepository;
import java.util.List;

public class PostListViewModel extends AndroidViewModel {

    private PostRepository repository;

    public PostListViewModel(Application application) {
        super(application);
        repository = PostRepository.getInstance(application);
    }

    public LiveData<List<Post>> getPosts() {
        return repository.getPosts();
    }

    public LiveData<Boolean> getIsLoading() {
        return repository.getIsLoading();
    }

    public LiveData<String> getErrorMessage() {
        return repository.getErrorMessage();
    }

    public void loadPosts() {
        repository.loadPosts();
    }

    public void toggleLike(Post post) {
        repository.toggleLike(post.getId(), post.isLiked());
    }

    public void deletePost(String postId, String currentUserId, boolean isAdmin) {
        repository.deletePost(postId, currentUserId, isAdmin);
    }
}