package com.example.no1.features.comment.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.no1.R;
import com.example.no1.common.utils.UserSessionManager;
import com.example.no1.features.comment.adapters.CommentAdapter;
import com.example.no1.features.comment.models.Comment;
import com.example.no1.features.comment.viewmodels.CommentViewModel;

public class CommentFragment extends Fragment {

    private CommentViewModel viewModel;
    private UserSessionManager sessionManager;

    private RecyclerView recyclerView;
    private CommentAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyText;
    private EditText commentInput;
    private Button submitButton;
    private LinearLayout replyBar;
    private TextView replyHint;
    private TextView cancelReplyButton;

    private String postId;

    public static CommentFragment newInstance(String postId) {
        CommentFragment fragment = new CommentFragment();
        Bundle args = new Bundle();
        args.putString("post_id", postId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postId = getArguments().getString("post_id");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);

        sessionManager = UserSessionManager.getInstance(requireContext());

        initViews(view);
        setupViewModel();
        setupObservers();
        setupListeners();

        if (postId != null) {
            viewModel.setPostId(postId);
        }

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.commentRecyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyText = view.findViewById(R.id.emptyText);
        commentInput = view.findViewById(R.id.commentInput);
        submitButton = view.findViewById(R.id.submitButton);
        replyBar = view.findViewById(R.id.replyBar);
        replyHint = view.findViewById(R.id.replyHint);
        cancelReplyButton = view.findViewById(R.id.cancelReplyButton);

        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(CommentViewModel.class);
    }

    private void setupObservers() {
        viewModel.getComments().observe(getViewLifecycleOwner(), comments -> {
            if (comments == null || comments.isEmpty()) {
                if (emptyText != null) emptyText.setVisibility(View.VISIBLE);
                if (recyclerView != null) recyclerView.setVisibility(View.GONE);
            } else {
                if (emptyText != null) emptyText.setVisibility(View.GONE);
                if (recyclerView != null) recyclerView.setVisibility(View.VISIBLE);

                String currentUserId = sessionManager.isLoggedIn() ?
                        sessionManager.getUsername() : "";
                boolean isAdmin = sessionManager.isAdmin();

                if (adapter == null) {
                    adapter = new CommentAdapter(comments, postId, currentUserId,
                            comment -> {
                                if (sessionManager.isLoggedIn()) {
                                    viewModel.setReplyingTo(comment);
                                    commentInput.requestFocus();
                                    InputMethodManager imm = (InputMethodManager)
                                            requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                                    imm.showSoftInput(commentInput, InputMethodManager.SHOW_IMPLICIT);
                                } else {
                                    Toast.makeText(getContext(), "登录后才能回复哦~", Toast.LENGTH_SHORT).show();
                                }
                            },
                            (comment, position) -> {
                                showDeleteConfirmDialog(comment, position);
                            }
                    );
                    adapter.setAdmin(isAdmin);
                    if (recyclerView != null) recyclerView.setAdapter(adapter);
                } else {
                    adapter.updateComments(comments);
                    adapter.updateCurrentUserId(currentUserId);
                    adapter.setAdmin(isAdmin);
                }
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (progressBar != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getContentError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty() && commentInput != null) {
                commentInput.setError(error);
            }
        });

        viewModel.getIsReplying().observe(getViewLifecycleOwner(), isReplying -> {
            if (replyBar != null) {
                replyBar.setVisibility(isReplying ? View.VISIBLE : View.GONE);
            }
            if (isReplying && viewModel.getReplyingTo().getValue() != null) {
                Comment replying = viewModel.getReplyingTo().getValue();
                replyHint.setText("正在回复 @" + replying.getAuthor());
            }
        });
    }

    private void setupListeners() {
        submitButton.setOnClickListener(v -> {
            if (sessionManager.isLoggedIn()) {
                String content = commentInput.getText().toString().trim();
                if (content.isEmpty()) {
                    Toast.makeText(getContext(), "请输入内容", Toast.LENGTH_SHORT).show();
                    return;
                }
                viewModel.setCommentContent(content);
                viewModel.addComment(
                        sessionManager.getUsername(),
                        sessionManager.getDisplayName()
                );
                commentInput.setText("");

                InputMethodManager imm = (InputMethodManager)
                        requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(commentInput.getWindowToken(), 0);
            } else {
                Toast.makeText(getContext(), "登录后才能评论哦~", Toast.LENGTH_SHORT).show();
            }
        });

        cancelReplyButton.setOnClickListener(v -> {
            viewModel.cancelReply();
        });
    }

    private void showDeleteConfirmDialog(Comment comment, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("确认删除")
                .setMessage("确定要删除这条评论吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    String currentUserId = sessionManager.getUsername();
                    boolean isAdmin = sessionManager.isAdmin();
                    viewModel.deleteComment(comment.getId(), currentUserId, isAdmin);
                    Toast.makeText(getContext(), "评论已删除", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    public void refreshComments() {
        if (viewModel != null && postId != null) {
            viewModel.loadComments();
        }
    }
}