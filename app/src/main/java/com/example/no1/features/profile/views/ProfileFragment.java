package com.example.no1.features.profile.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.example.no1.R;
import com.example.no1.common.utils.UserSessionManager;
import com.example.no1.data.local.UserDataSource;
import com.example.no1.features.admin.views.UserManagementActivity;
import com.example.no1.features.auth.views.LoginActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Calendar;

public class ProfileFragment extends Fragment {

    private UserSessionManager sessionManager;
    private UserDataSource userDataSource;

    private TextView userRoleText;
    private TextInputEditText nicknameEdit;
    private TextInputEditText genderEdit;
    private TextInputEditText birthYearEdit;
    private TextInputEditText emailEdit;
    private TextInputLayout nicknameLayout;
    private TextInputLayout genderLayout;
    private TextInputLayout birthYearLayout;
    private TextInputLayout emailLayout;
    private Button userManagementButton;
    private Button editInfoButton;
    private Button saveInfoButton;
    private Button cancelEditButton;
    private Button resetPasswordButton;
    private Button changePasswordButton;
    private Button logoutButton;
    private Button gotoLoginButton;
    private ProgressBar progressBar;
    private View cardUserManagement;  // ✅ 添加卡片引用

    private boolean isEditing = false;
    private String[] genderOptions = {"保密", "男", "女"};
    private int[] birthYearOptions;

    private static final String TAG = "ProfileFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        android.util.Log.d(TAG, "onCreateView called");

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sessionManager = UserSessionManager.getInstance(requireContext());
        userDataSource = UserDataSource.getInstance(requireContext());
        android.util.Log.d(TAG, "onCreateView - isLoggedIn: " + sessionManager.isLoggedIn() + ", role: " + sessionManager.getRole());

        // 先初始化所有 View
        initViews(view);

        // 初始化出生年份选项（1900-当前年）
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        birthYearOptions = new int[currentYear - 1900 + 1];
        for (int i = 0; i <= currentYear - 1900; i++) {
            birthYearOptions[i] = 1900 + i;
        }

        setupListeners();
        updateUI();

        return view;
    }

    private void gotoLoginPage() {
        android.util.Log.d(TAG, "gotoLoginPage called");

        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);

        getActivity().finishAffinity();
    }

    private void initViews(View view) {
        userRoleText = view.findViewById(R.id.userRoleText);
        nicknameEdit = view.findViewById(R.id.nicknameEdit);
        genderEdit = view.findViewById(R.id.genderEdit);
        birthYearEdit = view.findViewById(R.id.birthYearEdit);
        emailEdit = view.findViewById(R.id.emailEdit);
        nicknameLayout = view.findViewById(R.id.nicknameLayout);
        genderLayout = view.findViewById(R.id.genderLayout);
        birthYearLayout = view.findViewById(R.id.birthYearLayout);
        emailLayout = view.findViewById(R.id.emailLayout);

        editInfoButton = view.findViewById(R.id.editInfoButton);
        saveInfoButton = view.findViewById(R.id.saveInfoButton);
        cancelEditButton = view.findViewById(R.id.cancelEditButton);
        resetPasswordButton = view.findViewById(R.id.resetPasswordButton);
        changePasswordButton = view.findViewById(R.id.changePasswordButton);
        logoutButton = view.findViewById(R.id.logoutButton);
        gotoLoginButton = view.findViewById(R.id.gotoLoginButton);
        progressBar = view.findViewById(R.id.progressBar);
        userManagementButton = view.findViewById(R.id.userManagementButton);
        cardUserManagement = view.findViewById(R.id.cardUserManagement);  // ✅ 初始化卡片
    }

    private void setupListeners() {
        editInfoButton.setOnClickListener(v -> enterEditMode());
        saveInfoButton.setOnClickListener(v -> saveUserInfo());
        cancelEditButton.setOnClickListener(v -> exitEditMode());
        resetPasswordButton.setOnClickListener(v -> showResetPasswordDialog());
        changePasswordButton.setOnClickListener(v -> showChangePasswordDialog());
        logoutButton.setOnClickListener(v -> showLogoutConfirmDialog());
        gotoLoginButton.setOnClickListener(v -> gotoLoginPage());

        genderEdit.setOnClickListener(v -> showGenderPicker());
        genderEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) showGenderPicker();
        });

        birthYearEdit.setOnClickListener(v -> showBirthYearPicker());
        birthYearEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) showBirthYearPicker();
        });

        userManagementButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserManagementActivity.class);
            startActivity(intent);
        });
    }

    private void updateUI() {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }

        boolean isLoggedIn = sessionManager.isLoggedIn();
        String role = sessionManager.getRole();

        if (isLoggedIn) {
            // 登录状态（普通用户或管理员）
            if ("admin".equals(role)) {
                userRoleText.setText("管理员");
                // ✅ 管理员显示用户管理按钮
                if (userManagementButton != null) {
                    userManagementButton.setVisibility(View.VISIBLE);
                }
                if (cardUserManagement != null) {
                    cardUserManagement.setVisibility(View.VISIBLE);
                }
            } else {
                userRoleText.setText("普通用户");
                // 普通用户隐藏用户管理按钮
                if (userManagementButton != null) {
                    userManagementButton.setVisibility(View.GONE);
                }
                if (cardUserManagement != null) {
                    cardUserManagement.setVisibility(View.GONE);
                }
            }

            nicknameEdit.setText(sessionManager.getNickname());
            genderEdit.setText(sessionManager.getGender());
            birthYearEdit.setText(String.valueOf(sessionManager.getBirthYear()));
            emailEdit.setText(sessionManager.getEmail());

            editInfoButton.setVisibility(View.VISIBLE);
            resetPasswordButton.setVisibility(View.VISIBLE);
            changePasswordButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.VISIBLE);
            gotoLoginButton.setVisibility(View.GONE);
        } else {
            // 游客模式
            userRoleText.setText("游客");
            nicknameEdit.setText("");
            genderEdit.setText("");
            birthYearEdit.setText("");
            emailEdit.setText("");

            editInfoButton.setVisibility(View.GONE);
            resetPasswordButton.setVisibility(View.GONE);
            changePasswordButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.GONE);
            gotoLoginButton.setVisibility(View.VISIBLE);

            // 游客隐藏用户管理按钮
            if (userManagementButton != null) {
                userManagementButton.setVisibility(View.GONE);
            }
            if (cardUserManagement != null) {
                cardUserManagement.setVisibility(View.GONE);
            }
        }
        exitEditMode();
    }

    private void enterEditMode() {
        isEditing = true;
        nicknameEdit.setEnabled(true);
        genderEdit.setEnabled(true);
        birthYearEdit.setEnabled(true);
        emailEdit.setEnabled(true);
        editInfoButton.setVisibility(View.GONE);
        saveInfoButton.setVisibility(View.VISIBLE);
        cancelEditButton.setVisibility(View.VISIBLE);
    }

    private void exitEditMode() {
        isEditing = false;
        nicknameEdit.setEnabled(false);
        genderEdit.setEnabled(false);
        birthYearEdit.setEnabled(false);
        emailEdit.setEnabled(false);

        boolean isLoggedIn = sessionManager.isLoggedIn();
        editInfoButton.setVisibility(isLoggedIn ? View.VISIBLE : View.GONE);
        saveInfoButton.setVisibility(View.GONE);
        cancelEditButton.setVisibility(View.GONE);

        if (isLoggedIn) {
            nicknameEdit.setText(sessionManager.getNickname());
            genderEdit.setText(sessionManager.getGender());
            birthYearEdit.setText(String.valueOf(sessionManager.getBirthYear()));
            emailEdit.setText(sessionManager.getEmail());
        }
    }

    private void showGenderPicker() {
        new AlertDialog.Builder(requireContext())
                .setTitle("选择性别")
                .setItems(genderOptions, (dialog, which) -> {
                    genderEdit.setText(genderOptions[which]);
                })
                .show();
    }

    private void showBirthYearPicker() {
        String[] yearStrings = new String[birthYearOptions.length];
        for (int i = 0; i < birthYearOptions.length; i++) {
            yearStrings[i] = birthYearOptions[i] + "年";
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("选择出生年份")
                .setItems(yearStrings, (dialog, which) -> {
                    birthYearEdit.setText(String.valueOf(birthYearOptions[which]));
                })
                .show();
    }

    private void saveUserInfo() {
        String newNickname = nicknameEdit.getText().toString().trim();
        String newGender = genderEdit.getText().toString().trim();
        String newBirthYearStr = birthYearEdit.getText().toString().trim();
        String newEmail = emailEdit.getText().toString().trim();

        if (newNickname.isEmpty()) {
            nicknameLayout.setError("昵称不能为空");
            return;
        }

        int newBirthYear;
        try {
            newBirthYear = Integer.parseInt(newBirthYearStr);
            if (newBirthYear < 1900 || newBirthYear > Calendar.getInstance().get(Calendar.YEAR)) {
                birthYearLayout.setError("请输入有效的年份");
                return;
            }
        } catch (NumberFormatException e) {
            birthYearLayout.setError("请输入有效的年份");
            return;
        }

        if (!newEmail.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            emailLayout.setError("请输入有效的邮箱地址");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        new android.os.Handler().postDelayed(() -> {
            sessionManager.updateUserInfo(newNickname, newGender, newBirthYear, newEmail);
            Toast.makeText(getContext(), "保存成功", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            exitEditMode();
        }, 500);
    }

    private void showResetPasswordDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("重置密码")
                .setMessage("重置密码后，密码将变为默认值 '123456'，是否继续？")
                .setPositiveButton("确认", (dialog, which) -> {
                    progressBar.setVisibility(View.VISIBLE);
                    new android.os.Handler().postDelayed(() -> {
                        boolean success = userDataSource.resetPassword(sessionManager.getUsername());
                        progressBar.setVisibility(View.GONE);

                        if (success) {
                            Toast.makeText(getContext(), "密码已重置为 123456，请重新登录", Toast.LENGTH_SHORT).show();
                            performLogout();
                        } else {
                            Toast.makeText(getContext(), "重置失败", Toast.LENGTH_SHORT).show();
                        }
                    }, 500);
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("修改密码");

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_password, null);
        TextInputEditText oldPasswordInput = view.findViewById(R.id.oldPasswordInput);
        TextInputEditText newPasswordInput = view.findViewById(R.id.newPasswordInput);
        TextInputEditText confirmPasswordInput = view.findViewById(R.id.confirmPasswordInput);

        builder.setView(view);
        builder.setPositiveButton("确认", (dialog, which) -> {
            String oldPwd = oldPasswordInput.getText().toString().trim();
            String newPwd = newPasswordInput.getText().toString().trim();
            String confirmPwd = confirmPasswordInput.getText().toString().trim();

            if (oldPwd.isEmpty() || newPwd.isEmpty() || confirmPwd.isEmpty()) {
                Toast.makeText(getContext(), "请填写完整信息", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPwd.equals(confirmPwd)) {
                Toast.makeText(getContext(), "两次输入的新密码不一致", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPwd.length() < 6) {
                Toast.makeText(getContext(), "新密码长度至少6位", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            new android.os.Handler().postDelayed(() -> {
                boolean success = userDataSource.updatePassword(
                        sessionManager.getUsername(), oldPwd, newPwd
                );
                progressBar.setVisibility(View.GONE);

                if (success) {
                    Toast.makeText(getContext(), "密码修改成功，请重新登录", Toast.LENGTH_SHORT).show();
                    performLogout();
                } else {
                    Toast.makeText(getContext(), "原密码错误", Toast.LENGTH_SHORT).show();
                }
            }, 500);
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void performLogout() {
        android.util.Log.d(TAG, "performLogout called");

        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }

        sessionManager.logout();

        android.util.Log.d(TAG, "After logout - isLoggedIn: " + sessionManager.isLoggedIn() +
                ", role: " + sessionManager.getRole());

        updateUI();

        Toast.makeText(getContext(), "已退出登录，当前为游客模式", Toast.LENGTH_SHORT).show();
    }

    private void showLogoutConfirmDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("确认退出")
                .setMessage("确定要退出登录吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    performLogout();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        android.util.Log.d(TAG, "onResume - isLoggedIn: " + sessionManager.isLoggedIn() + ", role: " + sessionManager.getRole());
        updateUI();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}