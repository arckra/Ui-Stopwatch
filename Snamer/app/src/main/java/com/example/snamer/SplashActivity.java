package com.example.snamer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SplashActivity extends AppCompatActivity {

    private static final int REQ_LOCATION = 1001;

    private ImageView imgLogo;
    private TextView txtAppName;
    private TextView txtLocationStatus;
    private LinearLayout layoutLocationChip;
    private TextView txtLocationValue;

    private LocationManager locationManager;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initViews();
        playSequence();
    }

    private void initViews() {
        imgLogo = findViewById(R.id.imgLogo);
        txtAppName = findViewById(R.id.txtAppName);
        txtLocationStatus = findViewById(R.id.txtLocationStatus);
        layoutLocationChip = findViewById(R.id.layoutLocationChip);
        txtLocationValue = findViewById(R.id.txtLocationValue);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    private void playSequence() {
        // 1. Halaman kosong (sudah otomatis saat start)
        // 2. Tampilkan logo
        handler.postDelayed(() -> fadeIn(imgLogo), 700);

        // 3. Tampilkan judul
        handler.postDelayed(() -> fadeIn(txtAppName), 1300);

        // 4. Tampilkan teks lokasi + chip, lalu mulai deteksi lokasi
        handler.postDelayed(() -> {
            fadeIn(txtLocationStatus);
            fadeIn(layoutLocationChip);
            startLocationFlow();
        }, 1900);
    }

    private void fadeIn(View v) {
        v.setVisibility(View.VISIBLE);
        AlphaAnimation anim = new AlphaAnimation(0f, 1f);
        anim.setDuration(400);
        v.startAnimation(anim);
    }

    // ----------------- LOKASI ----------------- //

    private void startLocationFlow() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Minta izin runtime
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQ_LOCATION
            );
        } else {
            requestLocation();
        }
    }

    private void requestLocation() {
        txtLocationStatus.setText("Mendeteksi Lokasi Anda");
        txtLocationValue.setText("Mencari Lokasi...");

        boolean isGpsOn = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetOn = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGpsOn && !isNetOn) {
            // Provider mati, tampilkan pesan dan lanjut ke main
            txtLocationValue.setText("Lokasi tidak aktif");
            goToMainWithDelay();
            return;
        }

        try {
            String provider = isNetOn ? LocationManager.NETWORK_PROVIDER : LocationManager.GPS_PROVIDER;

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            // Coba gunakan last known location dulu agar cepat
            Location last = locationManager.getLastKnownLocation(provider);
            if (last != null) {
                handleLocation(last);
            } else {
                // Kalau belum ada, request update satu kali
                locationManager.requestSingleUpdate(provider, new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        handleLocation(location);
                    }

                    @Override public void onStatusChanged(String provider, int status, Bundle extras) { }
                    @Override public void onProviderEnabled(@NonNull String provider) { }
                    @Override public void onProviderDisabled(@NonNull String provider) { }
                }, null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            txtLocationValue.setText("Gagal mendeteksi lokasi");
            goToMainWithDelay();
        }
    }

    private void handleLocation(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();

        // Reverse geocoding: lat/lon -> Kota, Provinsi (tanpa API eksternal)
        String displayAddress = String.format(Locale.getDefault(), "%.4f, %.4f", lat, lon);

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> list = geocoder.getFromLocation(lat, lon, 1);
            if (list != null && !list.isEmpty()) {
                Address a = list.get(0);
                String city = a.getLocality();
                String admin = a.getAdminArea();
                if (city != null && admin != null) {
                    displayAddress = city + ", " + admin;   // contoh: "Bekasi, Jawa Barat"
                } else if (a.getSubAdminArea() != null) {
                    displayAddress = a.getSubAdminArea();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        txtLocationStatus.setText("Selamat Datang");
        txtLocationValue.setText(displayAddress);

        // Setelah lokasi tampil sebentar, masuk ke halaman utama
        goToMainWithDelay();
    }

    private void goToMainWithDelay() {
        handler.postDelayed(() -> {
            Intent i = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }, 1500);
    }

    // Hasil permintaan permission lokasi
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQ_LOCATION) {
            boolean granted = true;
            for (int r : grantResults) {
                if (r != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                    break;
                }
            }
            if (granted) {
                requestLocation();
            } else {
                txtLocationValue.setText("Izin lokasi ditolak");
                goToMainWithDelay();
            }
        }
    }
}
