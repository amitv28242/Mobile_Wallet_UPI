package com.mobilewallet.consumer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.mobilewallet.consumer.R;
import com.mobilewallet.consumer.activities.LoginActivity;
import com.mobilewallet.consumer.activities.SettingsActivity;
import com.mobilewallet.consumer.utils.TokenManager;

public class ProfileFragment extends Fragment {

    private MaterialCardView cvAvatar;
    private TextView tvUserName, tvUserEmail, tvUserPhone, tvUserRole;
    private View itemEditProfile, itemChangePassword, itemCards, itemBankAccounts,
            itemNotifications, itemSettings, itemHelp, itemAbout, itemLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        loadUserData();
        setupListeners();
    }

    private void initViews(View view) {
        cvAvatar = view.findViewById(R.id.cvAvatar);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        tvUserPhone = view.findViewById(R.id.tvUserPhone);
        tvUserRole = view.findViewById(R.id.tvUserRole);

        itemEditProfile = view.findViewById(R.id.itemEditProfile);
        itemChangePassword = view.findViewById(R.id.itemChangePassword);
        itemCards = view.findViewById(R.id.itemCards);
        itemBankAccounts = view.findViewById(R.id.itemBankAccounts);
        itemNotifications = view.findViewById(R.id.itemNotifications);
        itemSettings = view.findViewById(R.id.itemSettings);
        itemHelp = view.findViewById(R.id.itemHelp);
        itemAbout = view.findViewById(R.id.itemAbout);
        itemLogout = view.findViewById(R.id.itemLogout);
    }

    private void loadUserData() {
        TokenManager tm = TokenManager.getInstance();
        tvUserName.setText(tm.getUserName() != null ? tm.getUserName() : "User");
        tvUserEmail.setText(tm.getUserEmail() != null ? tm.getUserEmail() : "");
        tvUserRole.setText(tm.getUserRole() != null ?
                tm.getUserRole().replace("ROLE_", "") : "CONSUMER");
    }

    private void setupListeners() {
        itemSettings.setOnClickListener(v ->
                startActivity(new Intent(getContext(), SettingsActivity.class)));

        itemAbout.setOnClickListener(v -> new AlertDialog.Builder(requireContext())
                .setTitle("About Mobile Wallet")
                .setMessage("Version 1.0.0\n\nSecure digital wallet for seamless payments.")
                .setPositiveButton("OK", null)
                .show());

        itemLogout.setOnClickListener(v -> showLogoutDialog());

        itemEditProfile.setOnClickListener(v -> {
            // Open edit profile
        });

        itemChangePassword.setOnClickListener(v -> {
            // Open change password
        });

        itemCards.setOnClickListener(v -> {
            // Open cards
        });

        itemBankAccounts.setOnClickListener(v -> {
            // Open bank accounts
        });

        itemNotifications.setOnClickListener(v -> {
            // Open notification settings
        });

        itemHelp.setOnClickListener(v -> {
            // Open help
        });
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (d, w) -> {
                    TokenManager.getInstance().logout();
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}