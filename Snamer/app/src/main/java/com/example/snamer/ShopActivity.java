package com.example.snamer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.UUID;

public class ShopActivity extends AppCompatActivity {

    private LinearLayout navHome, navTimer, navHistory, navReward, navProfile;
    private RecyclerView rvShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        navHome = findViewById(R.id.navHome);
        navTimer = findViewById(R.id.navTimer);
        navHistory = findViewById(R.id.navHistory);
        navReward = findViewById(R.id.navReward);
        navProfile = findViewById(R.id.navProfile);

        rvShop = findViewById(R.id.rvShop);

        initBottomNav();

        // Grid 2 kolom (seperti gambar)
        rvShop.setLayoutManager(new GridLayoutManager(this, 2));

        // data dummy (nanti Anda ganti sesuai kebutuhan)
        ArrayList<ShopItem> data = new ArrayList<>();
        data.add(new ShopItem(UUID.randomUUID().toString(), "Judul", "Deskripsi"));
        data.add(new ShopItem(UUID.randomUUID().toString(), "Judul", "Deskripsi"));
        data.add(new ShopItem(UUID.randomUUID().toString(), "Judul", "Deskripsi"));
        data.add(new ShopItem(UUID.randomUUID().toString(), "Judul", "Deskripsi"));
        data.add(new ShopItem(UUID.randomUUID().toString(), "Judul", "Deskripsi"));
        data.add(new ShopItem(UUID.randomUUID().toString(), "Judul", "Deskripsi"));
        data.add(new ShopItem(UUID.randomUUID().toString(), "Judul", "Deskripsi"));
        data.add(new ShopItem(UUID.randomUUID().toString(), "Judul", "Deskripsi"));

        ShopAdapter adapter = new ShopAdapter(data, item -> {
            // klik item => masuk Bag
            BagRepository.add(this, new BagItem(item.id, item.title));
            Toast.makeText(this, "Ditambahkan ke Tas", Toast.LENGTH_SHORT).show();
        });

        rvShop.setAdapter(adapter);
    }

    private void initBottomNav() {
        setSelectedTab(navReward); // Shop aktif

        navHome.setOnClickListener(v -> go(MainActivity.class));
        navTimer.setOnClickListener(v -> go(StopwatchActivity.class));
        navHistory.setOnClickListener(v -> go(BagActivity.class));
        navReward.setOnClickListener(v -> setSelectedTab(navReward)); // sudah di sini
        navProfile.setOnClickListener(v -> go(ProfileActivity.class));
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
