package com.example.snamer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class StopwatchActivity extends AppCompatActivity {

    private TextView txtStopwatch;
    private Button btnStart, btnPause, btnStop;
    private ImageButton btnLap;
    private LinearLayout groupRunning;
    private RecyclerView lapList;

    // NAVBAR
    private LinearLayout navHome, navTimer, navHistory, navReward, navProfile;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean running = false;
    private boolean paused = false;

    private long startTimeMs = 0L;
    private long elapsedBeforePause = 0L;

    private final ArrayList<String> laps = new ArrayList<>();
    private LapAdapter lapAdapter;

    private final Runnable tick = new Runnable() {
        @Override
        public void run() {
            if (running && !paused) {
                long now = SystemClock.elapsedRealtime();
                long elapsed = (now - startTimeMs) + elapsedBeforePause;
                txtStopwatch.setText(formatTime(elapsed));
                handler.postDelayed(this, 10);
            } else {
                handler.removeCallbacks(this);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

        bindViews();
        setupRecycler();
        setInitialUI();

        initBottomNav(); // <-- TAMBAHAN NAVBAR

        btnStart.setOnClickListener(v -> start());
        btnPause.setOnClickListener(v -> togglePause());
        btnStop.setOnClickListener(v -> stop());
        btnLap.setOnClickListener(v -> addLap());
    }

    private void bindViews() {
        txtStopwatch = findViewById(R.id.txtStopwatch);
        btnStart = findViewById(R.id.btnStart);
        btnPause = findViewById(R.id.btnPause);
        btnStop = findViewById(R.id.btnStop);
        btnLap = findViewById(R.id.btnLap);
        groupRunning = findViewById(R.id.groupRunning);
        lapList = findViewById(R.id.lapList);

        // NAVBAR IDs (harus ada di XML)
        navHome = findViewById(R.id.navHome);
        navTimer = findViewById(R.id.navTimer);
        navHistory = findViewById(R.id.navHistory);
        navReward = findViewById(R.id.navReward);
        navProfile = findViewById(R.id.navProfile);
    }

    private void initBottomNav() {
        // Stopwatch = tab Timer aktif
        setSelectedTab(navTimer);

        navHome.setOnClickListener(v -> {
            goTo(MainActivity.class);
        });

        navTimer.setOnClickListener(v -> {
            // sudah di halaman ini
            setSelectedTab(navTimer);
        });

        navHistory.setOnClickListener(v -> {
            goTo(BagActivity.class);
        });

        navReward.setOnClickListener(v -> {
            goTo(ShopActivity.class);
        });

        navProfile.setOnClickListener(v -> {
            goTo(ProfileActivity.class);
        });
    }

    private void setSelectedTab(LinearLayout selected) {
        navHome.setSelected(navHome == selected);
        navTimer.setSelected(navTimer == selected);
        navHistory.setSelected(navHistory == selected);
        navReward.setSelected(navReward == selected);
        navProfile.setSelected(navProfile == selected);
    }

    private void goTo(Class<?> target) {
        // Hindari pindah kalau target sama
        if (getClass().equals(target)) return;

        Intent i = new Intent(StopwatchActivity.this, target);
        startActivity(i);
        overridePendingTransition(0, 0);
        finish();
    }

    private void setupRecycler() {
        lapAdapter = new LapAdapter(laps);
        lapList.setLayoutManager(new LinearLayoutManager(this));
        lapList.setAdapter(lapAdapter);
    }

    private void setInitialUI() {
        txtStopwatch.setText("00:00.00");
        btnStart.setVisibility(View.VISIBLE);
        groupRunning.setVisibility(View.GONE);
    }

    private void start() {
        running = true;
        paused = false;
        elapsedBeforePause = 0L;
        startTimeMs = SystemClock.elapsedRealtime();

        btnStart.setVisibility(View.GONE);
        groupRunning.setVisibility(View.VISIBLE);
        btnPause.setText("Jeda");

        handler.post(tick);
    }

    private void togglePause() {
        if (!running) return;

        if (!paused) {
            paused = true;
            long now = SystemClock.elapsedRealtime();
            elapsedBeforePause += (now - startTimeMs);
            btnPause.setText("Lanjut");
        } else {
            paused = false;
            startTimeMs = SystemClock.elapsedRealtime();
            btnPause.setText("Jeda");
            handler.post(tick);
        }
    }

    private void stop() {
        if (!running) return;

        running = false;
        paused = false;
        handler.removeCallbacks(tick);

        elapsedBeforePause = 0L;
        startTimeMs = 0L;
        txtStopwatch.setText("00:00.00");

        laps.clear();
        lapAdapter.notifyDataSetChanged();

        btnStart.setVisibility(View.VISIBLE);
        groupRunning.setVisibility(View.GONE);
    }

    private void addLap() {
        if (!running) return;

        long displayed = getCurrentElapsed();
        String lap = formatTime(displayed);

        laps.add(0, lap);
        lapAdapter.notifyItemInserted(0);
        lapList.scrollToPosition(0);
    }

    private long getCurrentElapsed() {
        if (!running) return 0L;
        if (paused) return elapsedBeforePause;

        long now = SystemClock.elapsedRealtime();
        return (now - startTimeMs) + elapsedBeforePause;
    }

    private String formatTime(long ms) {
        long totalCentis = ms / 10;
        long centis = totalCentis % 100;
        long totalSeconds = totalCentis / 100;
        long seconds = totalSeconds % 60;
        long minutes = totalSeconds / 60;

        return String.format(Locale.getDefault(), "%02d:%02d.%02d", minutes, seconds, centis);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(tick);
    }
}
