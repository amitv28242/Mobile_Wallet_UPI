package com.mobilewallet.consumer.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.mobilewallet.consumer.R;
import com.mobilewallet.consumer.utils.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {

    private View btnBack;
    private MaterialSwitch switchNotifications, switchBiometric, switchBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initViews();
        loadPreferences();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        switchNotifications = findViewById(R.id.switchNotifications);
        switchBiometric = findViewById(R.id.switchBiometric);
        switchBalance = findViewById(R.id.switchBalance);
    }

    private void loadPreferences() {
        PreferenceManager prefs = PreferenceManager.getInstance();
        if (prefs == null) return;

        switchNotifications.setChecked(prefs.isNotificationsEnabled());
        switchBiometric.setChecked(prefs.isBiometricEnabled());
        switchBalance.setChecked(prefs.isBalanceVisible());
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        switchNotifications.setOnCheckedChangeListener((btn, checked) ->
                PreferenceManager.getInstance().setNotificationsEnabled(checked));

        switchBiometric.setOnCheckedChangeListener((btn, checked) ->
                PreferenceManager.getInstance().setBiometricEnabled(checked));

        switchBalance.setOnCheckedChangeListener((btn, checked) ->
                PreferenceManager.getInstance().setBalanceVisible(checked));
    }
}