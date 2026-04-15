package com.example.no1.features.featured.data;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.no1.features.featured.models.FeaturedPost;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FeaturedDataSource {
    private static final String PREF_NAME = "featured_prefs";
    private static final String KEY_FEATURED = "featured_list";

    private SharedPreferences sharedPreferences;
    private Gson gson;
    private static FeaturedDataSource instance;

    private FeaturedDataSource(Context context) {
        sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized FeaturedDataSource getInstance(Context context) {
        if (instance == null) {
            instance = new FeaturedDataSource(context);
        }
        return instance;
    }

    public void saveFeatured(List<FeaturedPost> posts) {
        String json = gson.toJson(posts);
        sharedPreferences.edit().putString(KEY_FEATURED, json).apply();
    }

    public List<FeaturedPost> loadFeatured() {
        String json = sharedPreferences.getString(KEY_FEATURED, "");
        if (json.isEmpty()) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<FeaturedPost>>(){}.getType();
        List<FeaturedPost> posts = gson.fromJson(json, type);
        return posts != null ? posts : new ArrayList<>();
    }

    public void addFeatured(FeaturedPost post) {
        List<FeaturedPost> posts = loadFeatured();
        posts.add(0, post);
        saveFeatured(posts);
    }

    public void updateFeatured(FeaturedPost post) {
        List<FeaturedPost> posts = loadFeatured();
        for (int i = 0; i < posts.size(); i++) {
            if (posts.get(i).getId().equals(post.getId())) {
                posts.set(i, post);
                break;
            }
        }
        saveFeatured(posts);
    }

    public boolean deleteFeatured(String postId) {
        List<FeaturedPost> posts = loadFeatured();
        for (int i = 0; i < posts.size(); i++) {
            if (posts.get(i).getId().equals(postId)) {
                posts.remove(i);
                saveFeatured(posts);
                return true;
            }
        }
        return false;
    }

    public FeaturedPost getFeaturedById(String id) {
        List<FeaturedPost> posts = loadFeatured();
        for (FeaturedPost post : posts) {
            if (post.getId().equals(id)) {
                return post;
            }
        }
        return null;
    }

    public List<FeaturedPost> getFeaturedByUserId(String userId) {
        List<FeaturedPost> all = loadFeatured();
        List<FeaturedPost> result = new ArrayList<>();
        for (FeaturedPost post : all) {
            if (post.getAuthorId().equals(userId)) {
                result.add(post);
            }
        }
        return result;
    }

    public List<FeaturedPost> getPublishedFeatured() {
        List<FeaturedPost> all = loadFeatured();
        List<FeaturedPost> result = new ArrayList<>();
        for (FeaturedPost post : all) {
            if ("published".equals(post.getStatus())) {
                result.add(post);
            }
        }
        return result;
    }

    public List<FeaturedPost> getPendingFeatured() {
        List<FeaturedPost> all = loadFeatured();
        List<FeaturedPost> result = new ArrayList<>();
        for (FeaturedPost post : all) {
            if ("pending".equals(post.getStatus())) {
                result.add(post);
            }
        }
        return result;
    }
}