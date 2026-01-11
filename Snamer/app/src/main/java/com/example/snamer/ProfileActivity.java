package com.example.snamer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private LinearLayout layoutLoggedOut, layoutLoggedIn;
    private Button btnGoLogin, btnGoRegister, btnLogout;

    private EditText edtName, edtEmail, edtPhone;

    // navbar
    private LinearLayout navHome, navTimer, navHistory, navReward, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        layoutLoggedOut = findViewById(R.id.layoutLoggedOut);
        layoutLoggedIn = findViewById(R.id.layoutLoggedIn);

        btnGoLogin = findViewById(R.id.btnGoLogin);
        btnGoRegister = findViewById(R.id.btnGoRegister);
        btnLogout = findViewById(R.id.btnLogout);

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);

        navHome = findViewById(R.id.navHome);
        navTimer = findViewById(R.id.navTimer);
        navHistory = findViewById(R.id.navHistory);
        navReward = findViewById(R.id.navReward);
        navProfile = findViewById(R.id.navProfile);

        initBottomNav();

        btnGoLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            overridePendingTransition(0,0);
        });

        btnGoRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            overridePendingTransition(0,0);
        });

        btnLogout.setOnClickListener(v -> {
            UserSession.logout(this);
            render();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UserSession.isLoggedIn(this)) {
            startActivity(new Intent(this, ProfileLoggedInActivity.class));
            overridePendingTransition(0,0);
            finish();
        }
    }

    private void render() {
        boolean logged = UserSession.isLoggedIn(this);
        layoutLoggedOut.setVisibility(logged ? android.view.View.GONE : android.view.View.VISIBLE);
        layoutLoggedIn.setVisibility(logged ? android.view.View.VISIBLE : android.view.View.GONE);

        if (logged) {
            edtName.setText(UserSession.getName(this));
            edtEmail.setText(UserSession.getEmail(this));
            edtPhone.setText(UserSession.getPhone(this));
        }
    }

    private void initBottomNav() {
        setSelectedTab(navProfile);

        navHome.setOnClickListener(v -> go(MainActivity.class));
        navTimer.setOnClickListener(v -> go(StopwatchActivity.class));
        navHistory.setOnClickListener(v -> go(BagActivity.class));
        navReward.setOnClickListener(v -> go(ShopActivity.class));
        navProfile.setOnClickListener(v -> setSelectedTab(navProfile));
    }

    private void setSelectedTab(LinearLayout selected) {
        navHome.setSelected(navHome == selected);
        navTimer.setSelected(navTimer == selected);
        navHistory.setSelected(navHistory == selected);
        navReward.setSelected(navReward == selected);
        navProfile.setSelected(navProfile == selected);
    }

    private void go(Class<?> target) {
        if (getClass().equals(target)) return;
        startActivity(new Intent(this, target));
        overridePendingTransition(0, 0);
        finish();
    }
}
