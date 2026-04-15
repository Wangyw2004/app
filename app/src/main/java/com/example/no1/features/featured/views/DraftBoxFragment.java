package com.example.no1.features.featured.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.example.no1.features.featured.models.FeaturedPost;
import com.example.no1.features.featured.viewmodels.FeaturedViewModel;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class DraftBoxFragment extends Fragment {

    private FeaturedViewModel viewModel;
    private UserSessionManager sessionManager;

    private RecyclerView recyclerView;
    private DraftAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyText;

    // 将接口移到 Fragment 类中（不在 Adapter 内部）
    public interface OnApproveListener {
        void onApprove(FeaturedPost post);
    }
    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null && sessionManager != null && sessionManager.isAdmin()) {
            viewModel.loadPendingFeatured();
        }
    }
    public interface OnRejectListener {
        void onReject(FeaturedPost post);
    }

    public interface OnViewListener {
        void onView(FeaturedPost post);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_draft_box, container, false);

        sessionManager = UserSessionManager.getInstance(requireContext());

        initViews(view);
        setupViewModel();
        setupObservers();

        if (sessionManager.isAdmin()) {
            viewModel.loadPendingFeatured();
        }

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
        viewModel.getPendingFeatured().observe(getViewLifecycleOwner(), posts -> {
            if (posts == null || posts.isEmpty()) {
                emptyText.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyText.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                if (adapter == null) {
                    adapter = new DraftAdapter(posts,
                            this::showApproveConfirmDialog,
                            this::showRejectDialog,
                            post -> {
                                Intent intent = new Intent(getActivity(), FeaturedDetailActivity.class);
                                intent.putExtra("featured_id", post.getId());
                                startActivity(intent);
                            }
                    );
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.updatePosts(posts);
                }
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showApproveConfirmDialog(FeaturedPost post) {
        new AlertDialog.Builder(requireContext())
                .setTitle("确认通过")
                .setMessage("确定要通过《" + post.getTitle() + "》的申请吗？")
                .setPositiveButton("通过", (dialog, which) -> {
                    viewModel.approveApplication(post.getId(), sessionManager.getDisplayName());
                    Toast.makeText(getContext(), "已通过", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void showRejectDialog(FeaturedPost post) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("拒绝申请");

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_reject_reason, null);
        EditText editReason = view.findViewById(R.id.editReason);

        builder.setView(view);
        builder.setPositiveButton("确认拒绝", (dialog, which) -> {
            String reason = editReason.getText().toString().trim();
            if (reason.isEmpty()) {
                reason = "内容不符合精品要求";
            }
            viewModel.rejectApplication(post.getId(), reason, sessionManager.getDisplayName());
            Toast.makeText(getContext(), "已拒绝", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    // 适配器 - 使用外部定义的接口
    class DraftAdapter extends RecyclerView.Adapter<DraftAdapter.ViewHolder> {
        private List<FeaturedPost> posts;
        private OnApproveListener approveListener;
        private OnRejectListener rejectListener;
        private OnViewListener viewListener;

        DraftAdapter(List<FeaturedPost> posts, OnApproveListener approveListener,
                     OnRejectListener rejectListener, OnViewListener viewListener) {
            this.posts = posts;
            this.approveListener = approveListener;
            this.rejectListener = rejectListener;
            this.viewListener = viewListener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_draft, parent, false);
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
            TextView tvAuthor, tvTitle, tvTime, btnApprove, btnReject, btnView;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvAuthor = itemView.findViewById(R.id.tvAuthor);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvTime = itemView.findViewById(R.id.tvTime);
                btnApprove = itemView.findViewById(R.id.btnApprove);
                btnReject = itemView.findViewById(R.id.btnReject);
                btnView = itemView.findViewById(R.id.btnView);
            }

            void bind(FeaturedPost post) {
                tvAuthor.setText("申请人：" + post.getAuthor());
                tvTitle.setText(post.getTitle());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                tvTime.setText("提交时间：" + sdf.format(post.getCreateTime()));

                btnApprove.setOnClickListener(v -> {
                    if (approveListener != null) approveListener.onApprove(post);
                });
                btnReject.setOnClickListener(v -> {
                    if (rejectListener != null) rejectListener.onReject(post);
                });
                btnView.setOnClickListener(v -> {
                    if (viewListener != null) viewListener.onView(post);
                });
            }
        }
    }
}