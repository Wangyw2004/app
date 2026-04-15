package com.example.no1.features.featured.views;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.example.no1.R;
import com.example.no1.common.utils.UserSessionManager;
import com.example.no1.features.featured.models.FeaturedPost;
import com.example.no1.features.featured.viewmodels.FeaturedViewModel;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class FeaturedDetailActivity extends AppCompatActivity {

    private FeaturedViewModel viewModel;
    private UserSessionManager sessionManager;

    private TextView tvTitle;
    private TextView tvAuthor;
    private TextView tvTime;
    private ImageView ivCover;  // 添加封面ImageView
    private LinearLayout contentContainer;
    private MenuItem deleteMenuItem;

    private String featuredId;
    private FeaturedPost currentPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_featured_detail);

        sessionManager = UserSessionManager.getInstance(this);
        featuredId = getIntent().getStringExtra("featured_id");

        if (featuredId == null) {
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupViewModel();
        loadData();
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvTitle);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvTime = findViewById(R.id.tvTime);
        ivCover = findViewById(R.id.ivCover);  // 初始化封面ImageView
        contentContainer = findViewById(R.id.contentContainer);
    }

    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("精品详情");
        }
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(FeaturedViewModel.class);
    }

    private void loadData() {
        currentPost = viewModel.getFeaturedById(featuredId);
        if (currentPost == null) {
            Toast.makeText(this, "内容不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 增加阅读量
        viewModel.incrementViewCount(featuredId);

        displayPost();
    }

    private void displayPost() {
        if (currentPost == null) return;

        tvTitle.setText(currentPost.getTitle());
        tvAuthor.setText(currentPost.getAuthor());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        tvTime.setText(sdf.format(currentPost.getPublishTime() != null ?
                currentPost.getPublishTime() : currentPost.getCreateTime()));

        // 修复：加载封面图片
        if (currentPost.getCoverImage() != null && !currentPost.getCoverImage().isEmpty()) {
            File coverFile = new File(currentPost.getCoverImage());
            if (coverFile.exists()) {
                Glide.with(this)
                        .load(coverFile)
                        .centerCrop()
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_foreground)
                        .into(ivCover);
            } else {
                ivCover.setImageResource(R.drawable.ic_launcher_foreground);
            }
        } else {
            ivCover.setImageResource(R.drawable.ic_launcher_foreground);
        }

        // 清空并重新显示图文内容
        contentContainer.removeAllViews();

        // 显示图文内容
        List<FeaturedPost.ContentBlock> blocks = currentPost.getContent();
        if (blocks != null) {
            for (FeaturedPost.ContentBlock block : blocks) {
                if ("text".equals(block.getType())) {
                    TextView textView = new TextView(this);
                    textView.setText(block.getContent());
                    textView.setTextSize(16);
                    textView.setLineSpacing(4, 1.2f);
                    textView.setPadding(0, 16, 0, 16);
                    contentContainer.addView(textView);
                } else if ("image".equals(block.getType())) {
                    ImageView imageView = new ImageView(this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 16, 0, 16);
                    imageView.setLayoutParams(params);
                    imageView.setAdjustViewBounds(true);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    String imagePath = block.getContent();
                    if (imagePath != null) {
                        File imageFile = new File(imagePath);
                        if (imageFile.exists()) {
                            Glide.with(this)
                                    .load(imageFile)
                                    .placeholder(R.drawable.ic_launcher_foreground)
                                    .error(R.drawable.ic_launcher_foreground)
                                    .into(imageView);
                        } else {
                            imageView.setImageResource(R.drawable.ic_launcher_foreground);
                        }
                    } else {
                        imageView.setImageResource(R.drawable.ic_launcher_foreground);
                    }
                    contentContainer.addView(imageView);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_featured_detail, menu);
        deleteMenuItem = menu.findItem(R.id.action_delete);

        if (currentPost != null && sessionManager != null && sessionManager.isAdmin()) {
            deleteMenuItem.setVisible(true);
        } else if (deleteMenuItem != null) {
            deleteMenuItem.setVisible(false);
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
        if (currentPost == null) return;

        new AlertDialog.Builder(this)
                .setTitle("确认删除")
                .setMessage("确定要删除《" + currentPost.getTitle() + "》吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    if (viewModel != null) {
                        viewModel.deleteFeatured(featuredId, true, sessionManager.getUsername());
                        Toast.makeText(this, "已删除", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (contentContainer != null) {
            contentContainer.removeAllViews();
        }
    }
}