package com.example.no1.features.post.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.no1.R;
import com.example.no1.common.utils.UserSessionManager;
import com.example.no1.features.post.adapters.PostAdapter;
import com.example.no1.features.post.models.Post;
import com.example.no1.features.post.viewmodels.PostListViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class PostListFragment extends Fragment {

    private PostListViewModel viewModel;
    private UserSessionManager sessionManager;
    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyText;
    private FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_list, container, false);

        sessionManager = UserSessionManager.getInstance(requireContext());

        initViews(view);
        setupViewModel();
        setupObservers();
        setupListeners();

        viewModel.loadPosts();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyText = view.findViewById(R.id.emptyText);
        fab = view.findViewById(R.id.fab);

        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(PostListViewModel.class);
    }

    private void setupObservers() {
        viewModel.getPosts().observe(getViewLifecycleOwner(), posts -> {
            if (posts == null || posts.isEmpty()) {
                if (emptyText != null) emptyText.setVisibility(View.VISIBLE);
                if (recyclerView != null) recyclerView.setVisibility(View.GONE);
            } else {
                if (emptyText != null) emptyText.setVisibility(View.GONE);
                if (recyclerView != null) recyclerView.setVisibility(View.VISIBLE);

                if (adapter == null) {
                    adapter = new PostAdapter(posts,
                            post -> {
                                // 点击查看详情
                                Intent intent = new Intent(getContext(), PostDetailActivity.class);
                                intent.putExtra("post_id", post.getId());
                                startActivity(intent);
                            },
                            (post, position) -> {
                                // 点赞
                                if (sessionManager.isLoggedIn()) {
                                    viewModel.toggleLike(post);
                                } else {
                                    Toast.makeText(getContext(), "登录后才能点赞哦~", Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                    if (recyclerView != null) recyclerView.setAdapter(adapter);
                } else {
                    adapter.updatePosts(posts);
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
    }

    private void setupListeners() {
        if (fab != null) {
            fab.setOnClickListener(v -> {
                if (sessionManager.isLoggedIn()) {
                    Intent intent = new Intent(getContext(), CreatePostActivity.class);
                    intent.putExtra("username", sessionManager.getDisplayName());
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "登录后才能发布帖子哦~", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.loadPosts();
        }
    }
}