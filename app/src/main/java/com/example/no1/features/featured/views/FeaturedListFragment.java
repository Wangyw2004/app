package com.example.no1.features.featured.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.no1.R;
import com.example.no1.features.featured.models.FeaturedPost;
import com.example.no1.features.featured.viewmodels.FeaturedViewModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class FeaturedListFragment extends Fragment {

    private FeaturedViewModel viewModel;
    private RecyclerView recyclerView;
    private FeaturedAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyText;

    // 将接口移到 Fragment 类中
    public interface OnFeaturedItemClickListener {
        void onClick(FeaturedPost post);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_featured_list, container, false);

        initViews(view);
        setupViewModel();
        setupObservers();

        viewModel.loadPublishedFeatured();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyText = view.findViewById(R.id.emptyText);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(FeaturedViewModel.class);
    }

    private void setupObservers() {
        viewModel.getPublishedFeatured().observe(getViewLifecycleOwner(), posts -> {
            if (posts == null || posts.isEmpty()) {
                emptyText.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyText.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                if (adapter == null) {
                    adapter = new FeaturedAdapter(posts, post -> {
                        Intent intent = new Intent(getActivity(), FeaturedDetailActivity.class);
                        intent.putExtra("featured_id", post.getId());
                        startActivity(intent);
                    });
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.updatePosts(posts);
                }
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.loadPublishedFeatured();
        }
    }
    // 适配器 - 接口已移到外部
    class FeaturedAdapter extends RecyclerView.Adapter<FeaturedAdapter.ViewHolder> {
        private List<FeaturedPost> posts;
        private OnFeaturedItemClickListener listener;

        FeaturedAdapter(List<FeaturedPost> posts, OnFeaturedItemClickListener listener) {
            this.posts = posts;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_featured_post, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            FeaturedPost post = posts.get(position);
            holder.bind(post);
        }

        @Override
        public int getItemCount() {
            return posts == null ? 0 : posts.size();
        }

        void updatePosts(List<FeaturedPost> newPosts) {
            this.posts = newPosts;
            notifyDataSetChanged();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivCover;
            TextView tvTitle;
            TextView tvTime;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                ivCover = itemView.findViewById(R.id.ivCover);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvTime = itemView.findViewById(R.id.tvTime);
            }

            void bind(FeaturedPost post) {
                tvTitle.setText(post.getTitle());

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                tvTime.setText(sdf.format(post.getPublishTime() != null ?
                        post.getPublishTime() : post.getCreateTime()));

                if (post.getCoverImage() != null && !post.getCoverImage().isEmpty()) {
                    File coverFile = new File(post.getCoverImage());
                    if (coverFile.exists()) {
                        Glide.with(itemView.getContext())
                                .load(coverFile)
                                .centerCrop()
                                .placeholder(R.drawable.ic_launcher_foreground)  // 添加占位图
                                .into(ivCover);
                    } else {
                        ivCover.setImageResource(R.drawable.ic_launcher_foreground);
                    }
                } else {
                    ivCover.setImageResource(R.drawable.ic_launcher_foreground);
                }

                itemView.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onClick(post);
                    }
                });
            }
        }
    }
}