package com.example.no1.features.post.views;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
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
import com.example.no1.features.post.viewmodels.CreatePostViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CreatePostActivity extends AppCompatActivity {

    private CreatePostViewModel viewModel;
    private UserSessionManager sessionManager;

    private TextInputEditText titleInput;
    private TextInputEditText contentInput;
    private TextInputLayout titleLayout;
    private TextInputLayout contentLayout;
    private Button submitButton;
    private ProgressBar progressBar;
    private LinearLayout imageContainer;

    private List<String> selectedImagePaths = new ArrayList<>();
    private List<Uri> selectedImageUris = new ArrayList<>();
    private static final int MAX_IMAGES = 9;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    handleImageResult(result.getData());
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        sessionManager = UserSessionManager.getInstance(this);

        initViews();
        setupToolbar();
        setupViewModel();
        setupObservers();
        setupListeners();
        refreshImageContainer();
    }

    private void initViews() {
        titleInput = findViewById(R.id.titleInput);
        contentInput = findViewById(R.id.contentInput);
        titleLayout = findViewById(R.id.titleLayout);
        contentLayout = findViewById(R.id.contentLayout);
        submitButton = findViewById(R.id.submitButton);
        progressBar = findViewById(R.id.progressBar);
        imageContainer = findViewById(R.id.imageContainer);
        android.util.Log.d("CreatePost", "imageContainer is " + (imageContainer != null ? "found" : "null"));
    }

    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("发布新帖");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(CreatePostViewModel.class);
    }

    private void setupObservers() {
        viewModel.getTitleError().observe(this, error -> {
            if (titleLayout != null) titleLayout.setError(error);
        });

        viewModel.getContentError().observe(this, error -> {
            if (contentLayout != null) contentLayout.setError(error);
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            submitButton.setEnabled(!isLoading);
            submitButton.setText(isLoading ? "发布中..." : "发布");
        });

        viewModel.getIsCreated().observe(this, isCreated -> {
            if (isCreated) {
                Toast.makeText(this, "发布成功！", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        titleInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setTitle(s.toString());
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        contentInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setContent(s.toString());
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        submitButton.setOnClickListener(v -> {
            String authorId = sessionManager.getUsername();
            String authorName = getIntent().getStringExtra("username");
            if (authorName == null) authorName = sessionManager.getDisplayName();

            viewModel.createPost(authorId, authorName, selectedImagePaths);
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        imagePickerLauncher.launch(intent);
    }

    private void handleImageResult(Intent data) {
        if (data.getClipData() != null) {
            int count = data.getClipData().getItemCount();
            int remaining = MAX_IMAGES - selectedImageUris.size();
            int toAdd = Math.min(count, remaining);

            for (int i = 0; i < toAdd; i++) {
                Uri imageUri = data.getClipData().getItemAt(i).getUri();
                addImage(imageUri);
            }

            if (count > remaining) {
                Toast.makeText(this, "最多只能选择" + MAX_IMAGES + "张图片", Toast.LENGTH_SHORT).show();
            }
        } else if (data.getData() != null) {
            addImage(data.getData());
        }

        refreshImageContainer();
    }

    private void addImage(Uri uri) {
        try {
            String imagePath = saveImageToLocal(uri);
            if (imagePath != null) {
                selectedImagePaths.add(imagePath);
                selectedImageUris.add(uri);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "图片处理失败", Toast.LENGTH_SHORT).show();
        }
    }

    private String saveImageToLocal(Uri uri) {
        try {
            ContentResolver resolver = getContentResolver();
            InputStream inputStream = resolver.openInputStream(uri);

            File dir = new File(getFilesDir(), "post_images");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = System.currentTimeMillis() + "_" + selectedImagePaths.size() + ".jpg";
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

    private void refreshImageContainer() {
        android.util.Log.d("CreatePost", "refreshImageContainer called, selectedImageUris size: " + selectedImageUris.size());

        if (imageContainer == null) {
            android.util.Log.e("CreatePost", "imageContainer is null!");
            return;
        }
        imageContainer.removeAllViews();

        for (int i = 0; i < selectedImageUris.size(); i++) {
            final int position = i;
            View imageItem = getLayoutInflater().inflate(R.layout.item_selected_image, imageContainer, false);
            ImageView imageView = imageItem.findViewById(R.id.imageView);
            ImageView deleteBtn = imageItem.findViewById(R.id.deleteBtn);

            Glide.with(this)
                    .load(selectedImageUris.get(i))
                    .centerCrop()
                    .into(imageView);

            deleteBtn.setOnClickListener(v -> {
                selectedImagePaths.remove(position);
                selectedImageUris.remove(position);
                refreshImageContainer();
            });

            imageContainer.addView(imageItem);
        }

        if (selectedImageUris.size() < MAX_IMAGES) {
            View addButton = getLayoutInflater().inflate(R.layout.item_add_image, imageContainer, false);
            addButton.setOnClickListener(v -> openImagePicker());
            imageContainer.addView(addButton);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}