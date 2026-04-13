package com.example.no1.features.post.data;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.no1.features.post.models.Post;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PostDataSource {
    private static final String PREF_NAME = "post_prefs";
    private static final String KEY_POSTS = "posts_list";

    private SharedPreferences sharedPreferences;
    private Gson gson;
    private static PostDataSource instance;

    private PostDataSource(Context context) {
        sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized PostDataSource getInstance(Context context) {
        if (instance == null) {
            instance = new PostDataSource(context);
        }
        return instance;
    }

    public void savePosts(List<Post> posts) {
        String json = gson.toJson(posts);
        sharedPreferences.edit().putString(KEY_POSTS, json).apply();
    }

    public List<Post> loadPosts() {
        String json = sharedPreferences.getString(KEY_POSTS, "");
        if (json.isEmpty()) {
            return getMockPosts();
        }
        Type type = new TypeToken<List<Post>>(){}.getType();
        return gson.fromJson(json, type);
    }

    private List<Post> getMockPosts() {
        List<Post> posts = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Post post = new Post(
                    String.valueOf(i),
                    "示例帖子 " + i,
                    "这是帖子 " + i + " 的示例内容。欢迎使用发帖功能！",
                    "系统用户",
                    "system"
            );
            post.setLikeCount((int)(Math.random() * 50));
            posts.add(post);
        }
        return posts;
    }

    public void addPost(Post post) {
        List<Post> posts = loadPosts();
        posts.add(0, post);
        savePosts(posts);
    }
    public boolean deletePost(String postId) {
        List<Post> posts = loadPosts();
        for (int i = 0; i < posts.size(); i++) {
            if (posts.get(i).getId().equals(postId)) {
                posts.remove(i);
                savePosts(posts);
                return true;
            }
        }
        return false;
    }
}