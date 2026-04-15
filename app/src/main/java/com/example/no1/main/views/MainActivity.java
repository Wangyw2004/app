package com.example.no1.main.views;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.example.no1.R;
import com.example.no1.common.utils.UserSessionManager;
import com.example.no1.data.remote.ApiService;
import com.example.no1.features.auth.views.LoginActivity;
import com.example.no1.features.notice.views.NoticeFragment;
import com.example.no1.features.post.views.PostTabFragment;
import com.example.no1.features.profile.views.ProfileFragment;
import com.example.no1.features.service.views.AdminServiceFragment;
import com.example.no1.features.service.views.ServiceFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private UserSessionManager sessionManager;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApiService.init(this);
        setContentView(R.layout.activity_main);

        sessionManager = UserSessionManager.getInstance(this);

        android.util.Log.d(TAG, "onCreate - isLoggedIn: " + sessionManager.isLoggedIn() +
                ", role: " + sessionManager.getRole());

        // ✅ 游客和登录用户都可以进入 MainActivity
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("No1");
        }

        initViews();
        setupBottomNavigation();

        if (savedInstanceState == null) {
            loadFragment(new NoticeFragment());
            bottomNavigationView.setSelectedItemId(R.id.nav_notice);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        android.util.Log.d(TAG, "onNewIntent called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        android.util.Log.d(TAG, "onResume - isLoggedIn: " + sessionManager.isLoggedIn() +
                ", role: " + sessionManager.getRole());
        // ✅ 不需要任何跳转逻辑，让 Fragment 自己处理
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_notice) {
                loadFragment(new NoticeFragment());
                return true;
            } else if (itemId == R.id.nav_posts) {
                loadFragment(new PostTabFragment());
                return true;
            } else if (itemId == R.id.nav_service) {
                if (sessionManager.isAdmin()) {
                    loadFragment(new AdminServiceFragment());
                } else {
                    loadFragment(new ServiceFragment());
                }
                return true;
            } else if (itemId == R.id.nav_profile) {
                loadFragment(new ProfileFragment());
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
}