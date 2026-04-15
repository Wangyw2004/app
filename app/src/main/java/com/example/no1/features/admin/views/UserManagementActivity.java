package com.example.no1.features.admin.views;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.no1.R;

public class UserManagementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragmentContainer, new UserManagementFragment())
                    .commit();
        }
    }
}