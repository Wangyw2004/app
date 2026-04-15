package com.example.no1.features.admin.views;

import android.os.Bundle;
import android.text.TextUtils;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.no1.R;
import com.example.no1.common.utils.UserSessionManager;
import com.example.no1.data.local.UserDataSource;
import com.example.no1.features.auth.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserManagementFragment extends Fragment {

    private UserDataSource userDataSource;
    private UserSessionManager sessionManager;

    private RecyclerView recyclerView;
    private UserManagementAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyText;
    private FloatingActionButton fabSearch;

    private List<User> allUsers = new ArrayList<>();
    private List<User> filteredUsers = new ArrayList<>();
    private boolean isSearchMode = false;

    private static final String TAG = "UserManagementFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_management, container, false);

        sessionManager = UserSessionManager.getInstance(requireContext());

        // 检查是否为管理员
        if (!sessionManager.isAdmin()) {
            Toast.makeText(getContext(), "无权限访问", Toast.LENGTH_SHORT).show();
            requireActivity().finish();
            return view;
        }

        userDataSource = UserDataSource.getInstance(requireContext());

        initViews(view);
        setupToolbar(view);
        setupListeners();
        loadUsers();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyText = view.findViewById(R.id.emptyText);
        fabSearch = view.findViewById(R.id.fabSearch);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    // ✅ 修复：将 toolbar 作为参数传入
    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            AppCompatActivity activity = (AppCompatActivity) requireActivity();
            activity.setSupportActionBar(toolbar);
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                activity.getSupportActionBar().setTitle("用户管理");
            }
            toolbar.setNavigationOnClickListener(v -> requireActivity().finish());
        }
    }

    private void setupListeners() {
        fabSearch.setOnClickListener(v -> showSearchDialog());
    }

    private void loadUsers() {
        progressBar.setVisibility(View.VISIBLE);

        allUsers = userDataSource.loadUsers();
        filteredUsers = new ArrayList<>(allUsers);

        updateUI();

        progressBar.setVisibility(View.GONE);
    }

    private void updateUI() {
        if (filteredUsers == null || filteredUsers.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            if (adapter == null) {
                adapter = new UserManagementAdapter(filteredUsers,
                        this::showResetPasswordDialog,
                        this::showDeleteUserDialog);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.updateUsers(filteredUsers);
            }
        }
    }

    private void showSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("搜索用户");

        EditText input = new EditText(requireContext());
        input.setHint("输入用户名");
        input.setPadding(48, 32, 48, 32);
        builder.setView(input);

        builder.setPositiveButton("搜索", (dialog, which) -> {
            String keyword = input.getText().toString().trim();
            searchUsers(keyword);
        });
        builder.setNegativeButton("取消", (dialog, which) -> {
            if (isSearchMode) {
                clearSearch();
            }
        });
        builder.setNeutralButton("清除", (dialog, which) -> clearSearch());
        builder.show();
    }

    private void searchUsers(String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            clearSearch();
            return;
        }

        isSearchMode = true;
        filteredUsers.clear();

        for (User user : allUsers) {
            if (user.getUsername().toLowerCase().contains(keyword.toLowerCase())) {
                filteredUsers.add(user);
            }
        }

        updateUI();

        if (filteredUsers.isEmpty()) {
            Toast.makeText(getContext(), "未找到匹配的用户", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearSearch() {
        isSearchMode = false;
        filteredUsers = new ArrayList<>(allUsers);
        updateUI();
        Toast.makeText(getContext(), "已清除搜索", Toast.LENGTH_SHORT).show();
    }

    private void showResetPasswordDialog(User user) {
        // 不能重置自己的密码
        if (user.getUsername().equals(sessionManager.getUsername())) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("提示")
                    .setMessage("不能重置自己的密码，请使用修改密码功能")
                    .setPositiveButton("确定", null)
                    .show();
            return;
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("重置密码")
                .setMessage("确定要将用户 \"" + user.getUsername() + "\" 的密码重置为 \"123456\" 吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    boolean success = userDataSource.resetPassword(user.getUsername());
                    if (success) {
                        Toast.makeText(getContext(), "密码已重置为 123456", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "重置失败", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void showDeleteUserDialog(User user) {
        // 不能删除自己
        if (user.getUsername().equals(sessionManager.getUsername())) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("提示")
                    .setMessage("不能删除自己的账号")
                    .setPositiveButton("确定", null)
                    .show();
            return;
        }

        // 不能删除管理员
        if ("admin".equals(user.getRole())) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("提示")
                    .setMessage("不能删除管理员账号")
                    .setPositiveButton("确定", null)
                    .show();
            return;
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("删除账号")
                .setMessage("⚠️ 警告：此操作不可恢复！\n\n确定要删除用户 \"" + user.getUsername() + "\" 吗？\n该用户的所有数据将被清除。")
                .setPositiveButton("确定", (dialog, which) -> {
                    boolean success = userDataSource.deleteUser(user.getUsername());
                    if (success) {
                        Toast.makeText(getContext(), "用户已删除", Toast.LENGTH_SHORT).show();
                        loadUsers();
                    } else {
                        Toast.makeText(getContext(), "删除失败", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUsers();
    }
}

// ==================== 适配器类 ====================

class UserManagementAdapter extends RecyclerView.Adapter<UserManagementAdapter.ViewHolder> {

    private List<User> users;
    private OnResetPasswordListener resetListener;
    private OnDeleteUserListener deleteListener;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public interface OnResetPasswordListener {
        void onReset(User user);
    }

    public interface OnDeleteUserListener {
        void onDelete(User user);
    }

    public UserManagementAdapter(List<User> users, OnResetPasswordListener resetListener,
                                 OnDeleteUserListener deleteListener) {
        this.users = users;
        this.resetListener = resetListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_management, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users == null ? 0 : users.size();
    }

    public void updateUsers(List<User> newUsers) {
        this.users = newUsers;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUsername;
        private TextView tvRole;
        private TextView tvEmail;
        private TextView tvCreateTime;
        private TextView btnResetPassword;
        private TextView btnDeleteUser;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvRole = itemView.findViewById(R.id.tvRole);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvCreateTime = itemView.findViewById(R.id.tvCreateTime);
            btnResetPassword = itemView.findViewById(R.id.btnResetPassword);
            btnDeleteUser = itemView.findViewById(R.id.btnDeleteUser);
        }

        void bind(User user) {
            tvUsername.setText(user.getUsername());

            if ("admin".equals(user.getRole())) {
                tvRole.setText("管理员");
                // ✅ 修复：使用 androidx.core.content.ContextCompat
                tvRole.setBackgroundColor(androidx.core.content.ContextCompat.getColor(
                        itemView.getContext(), android.R.color.holo_orange_dark));
            } else {
                tvRole.setText("普通用户");
                tvRole.setBackgroundColor(androidx.core.content.ContextCompat.getColor(
                        itemView.getContext(), android.R.color.holo_blue_dark));
            }

            tvEmail.setText(user.getEmail() != null && !user.getEmail().isEmpty() ?
                    user.getEmail() : "未设置邮箱");
            tvCreateTime.setText("注册时间：" + sdf.format(user.getCreateTime()));

            btnResetPassword.setOnClickListener(v -> {
                if (resetListener != null) resetListener.onReset(user);
            });

            btnDeleteUser.setOnClickListener(v -> {
                if (deleteListener != null) deleteListener.onDelete(user);
            });
        }
    }
}