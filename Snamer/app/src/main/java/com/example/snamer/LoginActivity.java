package com.example.snamer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText edtPhone, edtPassword;
    private Button btnLogin;
    private TextView txtGoRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtGoRegister = findViewById(R.id.txtGoRegister);

        // Prefill nomor kalau akun pernah tersimpan
        if (UserSession.hasRegisteredAccount(this)) {
            edtPhone.setText(UserSession.getPhone(this));
        }

        btnLogin.setOnClickListener(v -> {
            String phone = edtPhone.getText().toString().trim();
            String pass = edtPassword.getText().toString().trim();

            if (phone.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Lengkapi data", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!UserSession.hasRegisteredAccount(this)) {
                Toast.makeText(this, "Belum ada akun, silakan daftar dulu", Toast.LENGTH_SHORT).show();
                return;
            }

            if (UserSession.checkLogin(this, phone, pass)) {
                UserSession.setLoggedIn(this, true);

                // LANGSUNG KE MAIN
                startActivity(new Intent(this, ProfileLoggedInActivity.class));
                overridePendingTransition(0,0);
                finish();
            } else {
                Toast.makeText(this, "Nomor HP atau password salah", Toast.LENGTH_SHORT).show();
            }
        });

        txtGoRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            overridePendingTransition(0,0);
            finish();
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (UserSession.isLoggedIn(this)) {
            startActivity(new Intent(this, ProfileLoggedInActivity.class));
            overridePendingTransition(0,0);
            finish();
        }
    }
}
