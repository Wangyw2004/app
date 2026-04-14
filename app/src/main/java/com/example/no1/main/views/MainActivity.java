package com.example.no1.main.views;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.example.no1.R;
import com.example.no1.features.counter.views.CounterFragment;
import com.example.no1.features.post.views.PostListFragment;
import com.example.no1.features.profile.views.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("No1");
        }

        initViews();
        setupBottomNavigation();

        if (savedInstanceState == null) {
            loadFragment(new CounterFragment());
            bottomNavigationView.setSelectedItemId(R.id.nav_counter);
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