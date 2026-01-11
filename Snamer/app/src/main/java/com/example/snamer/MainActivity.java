package com.example.snamer;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "snamer_timer_channel";
    private static final int NOTIF_ID = 100;
    private static final int REQ_POST_NOTIF = 200;

    private TextView txtMotivation;
    private TextView txtTimer;
    private ImageView imgSnake;
    private Button btnStart;

    private LinearLayout navHome, navTimer, navHistory, navReward, navProfile;

    private CountDownTimer countDownTimer;
    private boolean isRunning = false;
    private long totalMillis = 0L;
    private long remainingMillis = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initBottomNav();
        createNotificationChannel();

        // Default: 0 menit
        setNewTime(0, 0);

        setupListeners();
    }

    private void initViews() {
        txtMotivation = findViewById(R.id.txtMotivation);
        txtTimer = findViewById(R.id.txtTimer);
        imgSnake = findViewById(R.id.imgSnake);
        btnStart = findViewById(R.id.btnStart);

        navHome = findViewById(R.id.navHome);
        navTimer = findViewById(R.id.navTimer);
        navHistory = findViewById(R.id.navHistory);
        navReward = findViewById(R.id.navReward);
        navProfile = findViewById(R.id.navProfile);
    }

    private void initBottomNav() {
        // Default tab: Home
        setSelectedTab(navHome);

        navHome.setOnClickListener(v -> {
            // sudah di halaman Home (MainActivity)
            setSelectedTab(navHome);
        });

        navTimer.setOnClickListener(v -> {
            setSelectedTab(navTimer);
            Intent i = new Intent(MainActivity.this, StopwatchActivity.class);
            startActivity(i);
            overridePendingTransition(0, 0);
            finish();
        });

        navHistory.setOnClickListener(v -> {
            setSelectedTab(navHistory);
            Intent i = new Intent(MainActivity.this, BagActivity.class);
            startActivity(i);
            overridePendingTransition(0, 0);
            finish();
        });

        navReward.setOnClickListener(v -> {
            setSelectedTab(navReward);
            Intent i = new Intent(MainActivity.this, ShopActivity.class);
            startActivity(i);
            overridePendingTransition(0, 0);
            finish();
        });

        navProfile.setOnClickListener(v -> {
            setSelectedTab(navProfile);
            Intent i = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(i);
            overridePendingTransition(0, 0);
            finish();
        });
    }


    private void setSelectedTab(LinearLayout selected) {
        navHome.setSelected(navHome == selected);
        navTimer.setSelected(navTimer == selected);
        navHistory.setSelected(navHistory == selected);
        navReward.setSelected(navReward == selected);
        navProfile.setSelected(navProfile == selected);
    }

    private void setupListeners() {
        // Tap angka untuk mengubah waktu
        txtTimer.setOnClickListener(v -> showSetTimeDialog());

        // Tombol Mulai / Berhenti
        btnStart.setOnClickListener(v -> {
            if (!isRunning) {
                if (remainingMillis <= 0) {
                    Toast.makeText(this, "Setel waktu dulu", Toast.LENGTH_SHORT).show();
                    return;
                }
                startTimer();
            } else {
                stopTimer();
            }
        });
    }

    private void showSetTimeDialog() {
        if (isRunning) {
            Toast.makeText(this, "Hentikan timer dulu", Toast.LENGTH_SHORT).show();
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_set_time, null);

        EditText edtMinutes = dialogView.findViewById(R.id.edtMinutes);
        EditText edtSeconds = dialogView.findViewById(R.id.edtSeconds);

        // Prefill dengan waktu saat ini
        long totalSec = remainingMillis / 1000;
        long curMin = totalSec / 60;
        long curSec = totalSec % 60;
        if (curMin > 0) edtMinutes.setText(String.valueOf(curMin));
        if (curSec > 0) edtSeconds.setText(String.valueOf(curSec));

        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Simpan", (dialog, which) -> {
                    int m = 0;
                    int s = 0;
                    try {
                        String mStr = edtMinutes.getText().toString().trim();
                        String sStr = edtSeconds.getText().toString().trim();
                        if (!mStr.isEmpty()) m = Integer.parseInt(mStr);
                        if (!sStr.isEmpty()) s = Integer.parseInt(sStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Input tidak valid", Toast.LENGTH_SHORT).show();
                    }

                    if (m < 0) m = 0;
                    if (s < 0) s = 0;
                    if (s > 59) s = 59;

                    setNewTime(m, s);
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void setNewTime(int minutes, int seconds) {
        totalMillis = (minutes * 60L + seconds) * 1000L;
        remainingMillis = totalMillis;
        updateTimerDisplay();
    }

    private void startTimer() {
        btnStart.setText("Berhenti");
        isRunning = true;

        countDownTimer = new CountDownTimer(remainingMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingMillis = millisUntilFinished;
                updateTimerDisplay();
            }

            @Override
            public void onFinish() {
                remainingMillis = 0;
                updateTimerDisplay();
                isRunning = false;
                btnStart.setText("Mulai");
                showFinishedNotification();
            }
        }.start();
    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        isRunning = false;
        btnStart.setText("Mulai");
    }

    private void updateTimerDisplay() {
        long totalSec = remainingMillis / 1000;
        long minutes = totalSec / 60;
        long seconds = totalSec % 60;

        String text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        txtTimer.setText(text);

        // Rotasi ular sinkron dengan progress
        float rotation = 0f;
        if (totalMillis > 0) {
            float progress = (float) (totalMillis - remainingMillis) / (float) totalMillis;
            rotation = 360f * progress;  // 0 -> 360 derajat
        }
        imgSnake.setRotation(rotation);
    }

    // ------------------ NOTIFIKASI ------------------ //

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Timer SNAMER";
            String description = "Notifikasi selesai waktu timer";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void showFinishedNotification() {
        // Android 13+ butuh izin POST_NOTIFICATIONS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Minta izin sekali, kalau tidak diberi, skip saja
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQ_POST_NOTIF);
                return;
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.taskbar_timer)  // pakai icon apa saja
                .setContentTitle("SNAMER")
                .setContentText("Waktu kamu sudah selesai!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIF_ID, builder.build());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_POST_NOTIF) {
            // Tidak perlu aksi khusus; notifikasi akan muncul pada timer berikutnya
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
