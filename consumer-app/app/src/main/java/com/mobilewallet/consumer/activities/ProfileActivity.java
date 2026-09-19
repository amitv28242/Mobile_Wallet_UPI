package com.mobilewallet.consumer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.mobilewallet.consumer.R;
import com.mobilewallet.consumer.utils.TokenManager;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivAvatar;
    private View btnBack;
    private TextView tvUserName, tvUserEmail, tvUserPhone, tvUserRole;
    private View itemEditProfile, itemChangePassword, itemNotifications, itemSettings,
            itemHelp, itemAbout, itemPrivacy, itemTerms, itemLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        loadUserData();
        setupListeners();
    }

    private void initViews() {
        ivAvatar = findViewById(R.id.ivAvatar);
        btnBack = findViewById(R.id.btnBack);
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvUserPhone = findViewById(R.id.tvUserPhone);
        tvUserRole = findViewById(R.id.tvUserRole);

        itemEditProfile = findViewById(R.id.itemEditProfile);
        itemChangePassword = findViewById(R.id.itemChangePassword);
        itemNotifications = findViewById(R.id.itemNotifications);
        itemSettings = findViewById(R.id.itemSettings);
        itemHelp = findViewById(R.id.itemHelp);
        itemAbout = findViewById(R.id.itemAbout);
        itemPrivacy = findViewById(R.id.itemPrivacy);
        itemTerms = findViewById(R.id.itemTerms);
        itemLogout = findViewById(R.id.itemLogout);
    }

    private void loadUserData() {
        String name = TokenManager.getInstance().getUserName();
        String email = TokenManager.getInstance().getUserEmail();
        String role = TokenManager.getInstance().getUserRole();

        tvUserName.setText(name != null ? name : "User");
        tvUserEmail.setText(email != null ? email : "user@example.com");
        tvUserRole.setText(role != null ? role.replace("ROLE_", "") : "CONSUMER");
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        itemEditProfile.setOnClickListener(v -> {
            // Navigate to Edit Profile
        });

        itemChangePassword.setOnClickListener(v -> {
            // Navigate to Change Password
        });

        itemNotifications.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
        });

        itemSettings.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
        });

        itemHelp.setOnClickListener(v -> {
            // Open help
        });

        itemAbout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("About Mobile Wallet")
                    .setMessage("Version 1.0.0\n\nA secure digital wallet for seamless payments.")
                    .setPositiveButton("OK", null)
                    .show();
        });

        itemPrivacy.setOnClickListener(v -> {
            // Open privacy policy
        });

        itemTerms.setOnClickListener(v -> {
            // Open terms
        });

        itemLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (d, w) -> {
                    TokenManager.getInstance().logout();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
}