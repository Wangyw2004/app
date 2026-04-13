package com.example.no1.main.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.example.no1.R;
import com.example.no1.common.utils.UserSessionManager;
import com.example.no1.features.auth.views.LoginActivity;
import com.example.no1.features.counter.views.CounterFragment;
import com.example.no1.features.post.views.PostListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private UserSessionManager sessionManager;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("功能列表");
        }

        sessionManager = UserSessionManager.getInstance(this);

        initViews();
        setupBottomNavigation();

        if (savedInstanceState == null) {
            loadFragment(new CounterFragment());
        }
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_counter) {
                loadFragment(new CounterFragment());
                return true;
            } else if (itemId == R.id.nav_posts) {
                loadFragment(new PostListFragment());
                return true;
            }else if (itemId == R.id.nav_profile) {
                showProfileInfo();
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    private void showProfileInfo() {
        if (sessionManager.isLoggedIn()) {
            new AlertDialog.Builder(this)
                    .setTitle("用户信息")
                    .setMessage("用户名：" + sessionManager.getUsername() + "\n" +
                            "昵称：" + sessionManager.getDisplayName())
                    .setPositiveButton("确定", null)
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("请先登录")
                    .setPositiveButton("去登录", (dialog, which) -> {
                        Intent intent = new Intent(this, LoginActivity.class);
                        startActivity(intent);
                    })
                    .setNegativeButton("取消", null)
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem loginItem = menu.findItem(R.id.action_login);
        MenuItem logoutItem = menu.findItem(R.id.action_logout);
        MenuItem userInfoItem = menu.findItem(R.id.action_user_info);

        if (sessionManager.isLoggedIn()) {
            loginItem.setVisible(false);
            logoutItem.setVisible(true);
            userInfoItem.setVisible(true);
            userInfoItem.setTitle(sessionManager.getDisplayName());
        } else {
            loginItem.setVisible(true);
            logoutItem.setVisible(false);
            userInfoItem.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_login) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.action_logout) {
            new AlertDialog.Builder(this)
                    .setTitle("确认退出")
                    .setMessage("确定要退出登录吗？")
                    .setPositiveButton("确定", (dialog, which) -> {
                        sessionManager.logout();
                        Toast.makeText(this, "已退出登录，当前为游客模式", Toast.LENGTH_SHORT).show();
                        recreate();
                    })
                    .setNegativeButton("取消", null)
                    .show();
            return true;
        } else if (id == R.id.action_user_info) {
            showProfileInfo();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }
}