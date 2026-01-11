package com.example.snamer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BagActivity extends AppCompatActivity {

    private TextView txtEmpty;
    private RecyclerView rvBag;

    // navbar
    private LinearLayout navHome, navTimer, navHistory, navReward, navProfile;

    private ArrayList<BagItem> items;
    private BagGridAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bag);

        txtEmpty = findViewById(R.id.txtEmpty);
        rvBag = findViewById(R.id.rvBag);

        navHome = findViewById(R.id.navHome);
        navTimer = findViewById(R.id.navTimer);
        navHistory = findViewById(R.id.navHistory);
        navReward = findViewById(R.id.navReward);
        navProfile = findViewById(R.id.navProfile);

        // grid 6 kolom (sesuaikan: 5/6 sesuai selera)
        rvBag.setLayoutManager(new GridLayoutManager(this, 6));
        rvBag.setHasFixedSize(true);

        initBottomNav();
        loadAndRender();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // supaya kalau habis add dari shop, bag langsung update
        loadAndRender();
    }

    private void loadAndRender() {
        items = BagRepository.load(this);

        if (items == null || items.isEmpty()) {
            txtEmpty.setVisibility(View.VISIBLE);
            rvBag.setVisibility(View.GONE);
        } else {
            txtEmpty.setVisibility(View.GONE);
            rvBag.setVisibility(View.VISIBLE);

            if (adapter == null) {
                adapter = new BagGridAdapter(items);
                rvBag.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void initBottomNav() {
        setSelectedTab(navHistory); // Bag aktif

        navHome.setOnClickListener(v -> go(MainActivity.class));
        navTimer.setOnClickListener(v -> go(StopwatchActivity.class));
        navHistory.setOnClickListener(v -> setSelectedTab(navHistory)); // sudah di sini
        navReward.setOnClickListener(v -> go(ShopActivity.class));
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
