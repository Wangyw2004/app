package com.example.no1.features.post.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.no1.features.post.data.PostDataSource;
import com.example.no1.features.post.models.Post;
import java.util.List;
import java.util.UUID;

public class PostRepository {
    private static PostRepository instance;
    private PostDataSource dataSource;
    private MutableLiveData<List<Post>> postsLiveData;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<String> errorMessage;

    private PostRepository(Context context) {
        dataSource = PostDataSource.getInstance(context);
        postsLiveData = new MutableLiveData<>();
        isLoading = new MutableLiveData<>(false);
        errorMessage = new MutableLiveData<>();
        loadPosts();
    }

    public static synchronized PostRepository getInstance(Context context) {
        if (instance == null) {
            instance = new PostRepository(context);
        }
        return instance;
    }

    public LiveData<List<Post>> getPosts() {
        return postsLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadPosts() {
        isLoading.setValue(true);
        List<Post> posts = dataSource.loadPosts();
        postsLiveData.setValue(posts);
        isLoading.setValue(false);
    }

    public void createPost(String title, String content, String authorId, String authorName, List<String> images) {
        if (title == null || title.trim().isEmpty()) {
            errorMessage.setValue("标题不能为空");
            return;
        }
        if (content == null || content.trim().isEmpty()) {
            errorMessage.setValue("内容不能为空");
            return;
        }
        if (title.length() > 50) {
            errorMessage.setValue("标题不能超过50个字符");
            return;
        }

        String postId = UUID.randomUUID().toString();
        Post newPost = new Post(postId, title, content, authorName, authorId);
        newPost.setImages(images);  // 保存图片
        dataSource.addPost(newPost);
        loadPosts();
    }

    public void toggleLike(String postId, boolean isLiked) {
        List<Post> posts = postsLiveData.getValue();
        if (posts != null) {
            for (Post post : posts) {
                if (post.getId().equals(postId)) {
                    if (isLiked) {
                        post.setLikeCount(post.getLikeCount() - 1);
                    } else {
                        post.setLikeCount(post.getLikeCount() + 1);
                    }
                    post.setLiked(!isLiked);
                    break;
                }
            }
            postsLiveData.setValue(posts);
            dataSource.savePosts(posts);
        }
    }
    // 增加帖子评论数
    public void incrementCommentCount(String postId) {
        List<Post> posts = postsLiveData.getValue();
        if (posts != null) {
            for (Post post : posts) {
                if (post.getId().equals(postId)) {
                    post.incrementCommentCount();
                    break;
                }
            }
            postsLiveData.setValue(posts);
            dataSource.savePosts(posts);
        }
    }

    // 减少帖子评论数
    public void decrementCommentCount(String postId) {
        List<Post> posts = postsLiveData.getValue();
        if (posts != null) {
            for (Post post : posts) {
                if (post.getId().equals(postId)) {
                    post.decrementCommentCount();
                    break;
                }
            }
            postsLiveData.setValue(posts);
            dataSource.savePosts(posts);
        }
    }
    /**
     * 删除帖子
     * @param postId 帖子ID
     * @param currentUserId 当前用户ID
     * @param isAdmin 是否是管理员
     */
    public void deletePost(String postId, String currentUserId, boolean isAdmin) {
        List<Post> posts = postsLiveData.getValue();
        if (posts != null) {
            for (Post post : posts) {
                if (post.getId().equals(postId)) {
                    if (isAdmin || post.getAuthorId().equals(currentUserId)) {
                        boolean success = dataSource.deletePost(postId);
                        if (success) {
                            loadPosts();
                        } else {
                            errorMessage.setValue("删除失败");
                        }
                        return;
                    } else {
                        errorMessage.setValue("只能删除自己发布的帖子");
                        return;
                    }
                }
            }
        }
    }
}