package com.example.no1.features.post.views;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.no1.R;
import com.example.no1.common.utils.UserSessionManager;
import com.example.no1.features.post.models.Post;
import com.example.no1.features.post.viewmodels.PostListViewModel;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {

    private PostListViewModel viewModel;
    private UserSessionManager sessionManager;

    private TextView titleText;
    private TextView contentText;
    private TextView authorText;
    private TextView timeText;
    private TextView likeCountText;

    private String postId;
    private Post currentPost;
    private MenuItem deleteMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        sessionManager = UserSessionManager.getInstance(this);
        postId = getIntent().getStringExtra("post_id");

        if (postId == null) {
            Toast.makeText(this, "帖子不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupViewModel();
        loadPostDetail();
    }

    private void initViews() {
        titleText = findViewById(R.id.detailTitle);
        contentText = findViewById(R.id.detailContent);
        authorText = findViewById(R.id.detailAuthor);
        timeText = findViewById(R.id.detailTime);
        likeCountText = findViewById(R.id.detailLikeCount);
    }

    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("帖子详情");
        }
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(PostListViewModel.class);
    }

    private void loadPostDetail() {
        viewModel.getPosts().observe(this, posts -> {
            if (posts != null) {
                for (Post post : posts) {
                    if (post.getId().equals(postId)) {
                        currentPost = post;
                        displayPost(post);
                        updateDeleteButtonVisibility(post);
                        return;
                    }
                }
                Toast.makeText(this, "帖子不存在", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        viewModel.loadPosts();
    }

    private void displayPost(Post post) {
        titleText.setText(post.getTitle());
        contentText.setText(post.getContent());
        authorText.setText(post.getAuthor());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        timeText.setText(sdf.format(post.getCreateTime()));

        likeCountText.setText("点赞 " + post.getLikeCount());
    }

    private void updateDeleteButtonVisibility(Post post) {
        String currentUserId = sessionManager.isLoggedIn() ? sessionManager.getUsername() : "";

        if (deleteMenuItem != null) {
            boolean isAuthor = post.isAuthor(currentUserId);
            deleteMenuItem.setVisible(isAuthor);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post_detail, menu);
        deleteMenuItem = menu.findItem(R.id.action_delete);

        if (currentPost != null) {
            updateDeleteButtonVisibility(currentPost);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_delete) {
            showDeleteConfirmDialog();
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("确认删除")
                .setMessage("确定要删除《" + currentPost.getTitle() + "》吗？\n删除后无法恢复。")
                .setPositiveButton("删除", (dialog, which) -> {
                    deletePost();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void deletePost() {
        String currentUserId = sessionManager.getUsername();
        viewModel.deletePost(postId, currentUserId);

        // 观察删除结果
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "帖子已删除", Toast.LENGTH_SHORT).show();
                finish(); // 返回列表页
            }
        });

        viewModel.loadPosts();
    }
}