package com.example.no1.features.featured.views;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.example.no1.R;
import com.example.no1.common.utils.UserSessionManager;
import com.example.no1.features.featured.models.FeaturedPost;
import com.example.no1.features.featured.viewmodels.FeaturedViewModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SubmitFeaturedActivity extends AppCompatActivity {

    private FeaturedViewModel viewModel;
    private UserSessionManager sessionManager;

    private EditText editTitle;
    private ImageView ivCover;
    private Button btnSelectCover;
    private LinearLayout contentContainer;
    private Button btnAddText;
    private Button btnAddImage;
    private Button btnSubmit;
    private ProgressBar progressBar;

    private String coverImagePath;
    private List<FeaturedPost.ContentBlock> contentBlocks = new ArrayList<>();
    private List<Uri> tempImageUris = new ArrayList<>();

    private String editingId;  // 编辑模式下的ID
    private boolean isEditMode = false;

    private final ActivityResultLauncher<Intent> coverPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        coverImagePath = saveImageToLocal(uri, "cover");
                        if (coverImagePath != null) {
                            Glide.with(this).load(new File(coverImagePath)).into(ivCover);
                        }
                    }
                }
            }
    );

    // 修改 imagePickerLauncher
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    if (result.getData().getClipData() != null) {
                        // 多选
                        int count = result.getData().getClipData().getItemCount();
                        for (int i = 0; i < count; i++) {
                            Uri uri = result.getData().getClipData().getItemAt(i).getUri();
                            addImageBlock(uri);
                        }
                    } else if (result.getData().getData() != null) {
                        // 单选
                        addImageBlock(result.getData().getData());
                    }
                }
            }
    );

    // 修改 openImagePicker 方法
    private void openImagePicker(boolean isCover) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (!isCover) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);  // 支持多选
        }
        if (isCover) {
            coverPickerLauncher.launch(intent);
        } else {
            imagePickerLauncher.launch(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = UserSessionManager.getInstance(this);

        // 游客无法发布精品帖
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_submit_featured);  // 只调用一次

        // 检查是否是编辑模式
        editingId = getIntent().getStringExtra("featured_id");
        isEditMode = editingId != null;

        initViews();
        setupToolbar();
        setupViewModel();
        setupListeners();

        if (isEditMode) {
            loadExistingData();
        }
    }

    private void initViews() {
        editTitle = findViewById(R.id.editTitle);
        ivCover = findViewById(R.id.ivCover);
        btnSelectCover = findViewById(R.id.btnSelectCover);
        contentContainer = findViewById(R.id.contentContainer);
        btnAddText = findViewById(R.id.btnAddText);
        btnAddImage = findViewById(R.id.btnAddImage);
        btnSubmit = findViewById(R.id.btnSubmit);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(isEditMode ? "编辑申请" : "提交精品申请");
        }
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(FeaturedViewModel.class);
    }

    private void setupListeners() {
        btnSelectCover.setOnClickListener(v -> openImagePicker(true));
        btnAddText.setOnClickListener(v -> addTextBlock());
        btnAddImage.setOnClickListener(v -> openImagePicker(false));
        btnSubmit.setOnClickListener(v -> submitApplication());
    }

    private void loadExistingData() {
        FeaturedPost post = viewModel.getFeaturedById(editingId);
        if (post != null) {
            editTitle.setText(post.getTitle());
            coverImagePath = post.getCoverImage();
            if (coverImagePath != null && new File(coverImagePath).exists()) {
                Glide.with(this).load(new File(coverImagePath)).into(ivCover);
            }

            if (post.getContent() != null) {
                contentBlocks = post.getContent();
                refreshContentContainer();
            }
        }
    }


    private String saveImageToLocal(Uri uri, String prefix) {
        try {
            ContentResolver resolver = getContentResolver();
            InputStream inputStream = resolver.openInputStream(uri);

            File dir = new File(getFilesDir(), "featured_images");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = prefix + "_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
            File destFile = new File(dir, fileName);

            FileOutputStream outputStream = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();

            return destFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void addTextBlock() {
        View textBlock = getLayoutInflater().inflate(R.layout.item_featured_text, contentContainer, false);
        EditText editText = textBlock.findViewById(R.id.editText);
        ImageView btnDelete = textBlock.findViewById(R.id.btnDelete);

        int position = contentContainer.getChildCount();
        if (position < contentBlocks.size()) {
            editText.setText(contentBlocks.get(position).getContent());
        }

        btnDelete.setOnClickListener(v -> {
            contentContainer.removeView(textBlock);
            if (position < contentBlocks.size()) {
                contentBlocks.remove(position);
            }
        });

        contentContainer.addView(textBlock);

        if (position >= contentBlocks.size()) {
            contentBlocks.add(new FeaturedPost.ContentBlock("text", ""));
        }

        editText.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (position < contentBlocks.size()) {
                    contentBlocks.get(position).setContent(s.toString());
                }
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private void addImageBlock(Uri uri) {
        String imagePath = saveImageToLocal(uri, "content");
        if (imagePath == null) {
            Toast.makeText(this, "图片保存失败", Toast.LENGTH_SHORT).show();
            return;
        }

        View imageBlock = getLayoutInflater().inflate(R.layout.item_featured_image, contentContainer, false);
        ImageView ivImage = imageBlock.findViewById(R.id.ivImage);
        ImageView btnDelete = imageBlock.findViewById(R.id.btnDelete);

        Glide.with(this).load(new File(imagePath)).into(ivImage);

        int position = contentContainer.getChildCount();

        btnDelete.setOnClickListener(v -> {
            contentContainer.removeView(imageBlock);
            if (position < contentBlocks.size()) {
                contentBlocks.remove(position);
            }
        });

        contentContainer.addView(imageBlock);

        contentBlocks.add(new FeaturedPost.ContentBlock("image", imagePath));
    }

    private void refreshContentContainer() {
        contentContainer.removeAllViews();
        for (int i = 0; i < contentBlocks.size(); i++) {
            FeaturedPost.ContentBlock block = contentBlocks.get(i);
            if ("text".equals(block.getType())) {
                View textBlock = getLayoutInflater().inflate(R.layout.item_featured_text, contentContainer, false);
                EditText editText = textBlock.findViewById(R.id.editText);
                ImageView btnDelete = textBlock.findViewById(R.id.btnDelete);
                final int position = i;

                editText.setText(block.getContent());
                btnDelete.setOnClickListener(v -> {
                    contentContainer.removeView(textBlock);
                    contentBlocks.remove(position);
                });
                editText.addTextChangedListener(new android.text.TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (position < contentBlocks.size()) {
                            contentBlocks.get(position).setContent(s.toString());
                        }
                    }
                    @Override public void afterTextChanged(android.text.Editable s) {}
                });
                contentContainer.addView(textBlock);
            } else if ("image".equals(block.getType())) {
                View imageBlock = getLayoutInflater().inflate(R.layout.item_featured_image, contentContainer, false);
                ImageView ivImage = imageBlock.findViewById(R.id.ivImage);
                ImageView btnDelete = imageBlock.findViewById(R.id.btnDelete);
                final int position = i;

                String imagePath = block.getContent();
                if (new File(imagePath).exists()) {
                    Glide.with(this).load(new File(imagePath)).into(ivImage);
                }
                btnDelete.setOnClickListener(v -> {
                    contentContainer.removeView(imageBlock);
                    contentBlocks.remove(position);
                });
                contentContainer.addView(imageBlock);
            }
        }
    }

    private void submitApplication() {
        String title = editTitle.getText().toString().trim();

        if (title.isEmpty()) {
            editTitle.setError("请填写标题");
            return;
        }
        if (coverImagePath == null || coverImagePath.isEmpty()) {
            Toast.makeText(this, "请上传封面图片", Toast.LENGTH_SHORT).show();
            return;
        }
        if (contentBlocks.isEmpty()) {
            Toast.makeText(this, "请添加内容", Toast.LENGTH_SHORT).show();
            return;
        }

        // 检查是否有空文本块
        for (FeaturedPost.ContentBlock block : contentBlocks) {
            if ("text".equals(block.getType()) && (block.getContent() == null || block.getContent().trim().isEmpty())) {
                Toast.makeText(this, "请填写所有文本内容", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSubmit.setEnabled(false);

        if (isEditMode) {
            viewModel.updateApplication(editingId, title, coverImagePath, contentBlocks);
        } else {
            viewModel.submitApplication(title, coverImagePath, contentBlocks,
                    sessionManager.getUsername(), sessionManager.getDisplayName());
        }

        viewModel.getErrorMessage().observe(this, error -> {
            progressBar.setVisibility(View.GONE);
            btnSubmit.setEnabled(true);
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, isEditMode ? "修改已提交，等待审核" : "申请已提交，等待审核", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}