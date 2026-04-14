package com.example.no1.features.comment.views;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.no1.R;
import com.example.no1.common.utils.UserSessionManager;
import com.example.no1.features.comment.adapters.CommentReplyAdapter;
import com.example.no1.features.comment.models.Comment;
import com.example.no1.features.comment.viewmodels.CommentDetailViewModel;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CommentDetailActivity extends AppCompatActivity {

    private CommentDetailViewModel viewModel;
    private UserSessionManager sessionManager;

    private TextView authorText;
    private TextView timeText;
    private TextView contentText;
    private RecyclerView replyRecyclerView;
    private CommentReplyAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyText;
    private EditText replyInput;
    private Button submitButton;
    private View replyBar;
    private TextView replyHint;
    private TextView cancelReplyButton;

    private String postId;
    private String commentId;
    private Comment originalComment;
    private Comment replyingToReply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_detail);

        sessionManager = UserSessionManager.getInstance(this);

        postId = getIntent().getStringExtra("post_id");
        commentId = getIntent().getStringExtra("comment_id");

        if (postId == null || commentId == null) {
            Toast.makeText(this, "参数错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupViewModel();
        setupObservers();
        setupListeners();

        viewModel.setPostId(postId);
        viewModel.setCommentId(commentId);
        viewModel.loadOriginalComment();
        viewModel.loadReplies();
    }

    private void initViews() {
        authorText = findViewById(R.id.commentAuthor);
        timeText = findViewById(R.id.commentTime);
        contentText = findViewById(R.id.commentContent);
        replyRecyclerView = findViewById(R.id.replyRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyText = findViewById(R.id.emptyText);
        replyInput = findViewById(R.id.replyInput);
        submitButton = findViewById(R.id.submitButton);
        replyBar = findViewById(R.id.replyBar);
        replyHint = findViewById(R.id.replyHint);
        cancelReplyButton = findViewById(R.id.cancelReplyButton);

        if (replyRecyclerView != null) {
            replyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("回复详情");
        }
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(CommentDetailViewModel.class);
    }

    private void setupObservers() {
        viewModel.getOriginalComment().observe(this, comment -> {
            if (comment != null) {
                originalComment = comment;
                displayOriginalComment(comment);
            }
        });

        viewModel.getReplies().observe(this, replies -> {
            if (replies == null || replies.isEmpty()) {
                emptyText.setVisibility(View.VISIBLE);
                replyRecyclerView.setVisibility(View.GONE);
            } else {
                emptyText.setVisibility(View.GONE);
                replyRecyclerView.setVisibility(View.VISIBLE);

                String currentUserId = sessionManager.isLoggedIn() ?
                        sessionManager.getUsername() : "";
                boolean isAdmin = sessionManager.isAdmin();

                if (adapter == null) {
                    adapter = new CommentReplyAdapter(replies, currentUserId,
                            (reply, position) -> {
                                if (sessionManager.isLoggedIn()) {
                                    replyingToReply = reply;
                                    replyBar.setVisibility(View.VISIBLE);
                                    replyHint.setText("正在回复 @" + reply.getAuthor());
                                    replyInput.requestFocus();
                                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                    imm.showSoftInput(replyInput, InputMethodManager.SHOW_IMPLICIT);
                                } else {
                                    Toast.makeText(this, "登录后才能回复哦~", Toast.LENGTH_SHORT).show();
                                }
                            },
                            (reply, position) -> {
                                showDeleteConfirmDialog(reply, position);
                            }
                    );
                    adapter.setAdmin(isAdmin);
                    replyRecyclerView.setAdapter(adapter);
                } else {
                    adapter.updateReplies(replies);
                    adapter.updateCurrentUserId(currentUserId);
                    adapter.setAdmin(isAdmin);
                }
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayOriginalComment(Comment comment) {
        authorText.setText(comment.getAuthor());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        timeText.setText(sdf.format(comment.getCreateTime()));
        contentText.setText(comment.getContent());
    }

    private void setupListeners() {
        submitButton.setOnClickListener(v -> {
            if (sessionManager.isLoggedIn()) {
                String content = replyInput.getText().toString().trim();
                if (content.isEmpty()) {
                    Toast.makeText(this, "请输入回复内容", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (replyingToReply != null) {
                    viewModel.replyToReply(
                            content,
                            sessionManager.getUsername(),
                            sessionManager.getDisplayName(),
                            replyingToReply.getParentId(),
                            replyingToReply.getAuthorId(),
                            replyingToReply.getAuthor()
                    );
                    replyBar.setVisibility(View.GONE);
                    replyingToReply = null;
                } else {
                    viewModel.replyToComment(
                            content,
                            sessionManager.getUsername(),
                            sessionManager.getDisplayName(),
                            commentId,
                            originalComment.getAuthorId(),
                            null
                    );
                }
                replyInput.setText("");

                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(replyInput.getWindowToken(), 0);
            } else {
                Toast.makeText(this, "登录后才能回复哦~", Toast.LENGTH_SHORT).show();
            }
        });

        cancelReplyButton.setOnClickListener(v -> {
            replyingToReply = null;
            replyBar.setVisibility(View.GONE);
            replyInput.setText("");
        });
    }

    private void showDeleteConfirmDialog(Comment reply, int position) {
        new AlertDialog.Builder(this)
                .setTitle("确认删除")
                .setMessage("确定要删除这条回复吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    String currentUserId = sessionManager.getUsername();
                    boolean isAdmin = sessionManager.isAdmin();
                    viewModel.deleteReply(reply.getId(), currentUserId, isAdmin);
                    Toast.makeText(this, "回复已删除", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}