package com.example.snamer;

import android.app.Activity;
import android.content.Intent;
import android.widget.LinearLayout;

public class BottomNavHelper {

    public static void setup(Activity activity,
                             int selectedNavId,
                             LinearLayout navHome,
                             LinearLayout navTimer,
                             LinearLayout navHistory,
                             LinearLayout navReward,
                             LinearLayout navProfile) {

        // set selected background (warna)
        navHome.setSelected(selectedNavId == R.id.navHome);
        navTimer.setSelected(selectedNavId == R.id.navTimer);
        navHistory.setSelected(selectedNavId == R.id.navHistory);
        navReward.setSelected(selectedNavId == R.id.navReward);
        navProfile.setSelected(selectedNavId == R.id.navProfile);

        navHome.setOnClickListener(v -> go(activity, MainActivity.class, selectedNavId, R.id.navHome));
        navTimer.setOnClickListener(v -> go(activity, StopwatchActivity.class, selectedNavId, R.id.navTimer));
        navHistory.setOnClickListener(v -> go(activity, BagActivity.class, selectedNavId, R.id.navHistory));
        navReward.setOnClickListener(v -> go(activity, ShopActivity.class, selectedNavId, R.id.navReward));
        navProfile.setOnClickListener(v -> go(activity, ProfileActivity.class, selectedNavId, R.id.navProfile));
    }

    private static void go(Activity activity, Class<?> target, int selectedNavId, int clickedId) {
        if (selectedNavId == clickedId) return; // sudah di halaman ini

        Intent i = new Intent(activity, target);
        activity.startActivity(i);

        // biar transisi halus & tidak numpuk animasi default
        activity.overridePendingTransition(0, 0);

        // supaya back tidak balik ke tab-tab sebelumnya bertumpuk:
        activity.finish();
    }
}
