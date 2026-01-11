package com.example.snamer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileLoggedInActivity extends AppCompatActivity {

    private EditText edtName, edtEmail, edtPhone;
    private Button btnLogout;

    private LinearLayout navHome, navTimer, navHistory, navReward, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Guard: kalau belum login, arahkan ke Profile (pilih login/register)
        if (!UserSession.isLoggedIn(this)) {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_profile_logged_in);

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        btnLogout = findViewById(R.id.btnLogout);

        navHome = findViewById(R.id.navHome);
        navTimer = findViewById(R.id.navTimer);
        navHistory = findViewById(R.id.navHistory);
        navReward = findViewById(R.id.navReward);
        navProfile = findViewById(R.id.navProfile);

        // isi data
        edtName.setText(UserSession.getName(this));
        edtEmail.setText(UserSession.getEmail(this));
        edtPhone.setText(UserSession.getPhone(this));

        initBottomNav();

        btnLogout.setOnClickListener(v -> {
            // logout = hapus data (sesuai permintaan Anda)
            UserSession.logout(this);

            // balik ke Profile awal (pilih login/register)
            startActivity(new Intent(this, ProfileActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
    }

    private void initBottomNav() {
        setSelectedTab(navProfile);

        navHome.setOnClickListener(v -> go(MainActivity.class));
        navTimer.setOnClickListener(v -> go(StopwatchActivity.class));
        navHistory.setOnClickListener(v -> go(BagActivity.class));
        navReward.setOnClickListener(v -> go(ShopActivity.class));
        navProfile.setOnClickListener(v -> setSelectedTab(navProfile)); // sudah di sini
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
