package com.mobilewallet.consumer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.mobilewallet.consumer.R;
import com.mobilewallet.consumer.models.Wallet;
import com.mobilewallet.consumer.viewmodels.WalletViewModel;

public class WalletActivity extends AppCompatActivity {

    private View btnBack;
    private ImageView btnToggleBalance, ivVisibility;
    private TextView tvBalance, tvWalletNumber, tvStatus, tvCurrency;
    private MaterialButton btnAddMoney, btnSendMoney, btnWithdraw;
    private ProgressBar progressBar;

    private WalletViewModel viewModel;
    private boolean isBalanceVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        initViews();
        initViewModel();
        setupListeners();
        observeViewModel();
        viewModel.loadWallet();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnToggleBalance = findViewById(R.id.btnToggleBalance);
        tvBalance = findViewById(R.id.tvBalance);
        tvWalletNumber = findViewById(R.id.tvWalletNumber);
        tvStatus = findViewById(R.id.tvStatus);
        tvCurrency = findViewById(R.id.tvCurrency);
        btnAddMoney = findViewById(R.id.btnAddMoney);
        btnSendMoney = findViewById(R.id.btnSendMoney);
        btnWithdraw = findViewById(R.id.btnWithdraw);
        progressBar = findViewById(R.id.progressBar);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(WalletViewModel.class);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnToggleBalance.setOnClickListener(v -> toggleBalance());
        btnAddMoney.setOnClickListener(v -> {
            // Show add money dialog
        });
        btnSendMoney.setOnClickListener(v -> {
            startActivity(new Intent(this, QRScannerActivity.class));
        });
        btnWithdraw.setOnClickListener(v -> {
            // Show withdraw dialog
        });
    }

    private void observeViewModel() {
        viewModel.getWalletLiveData().observe(this, wallet -> {
            if (wallet != null) displayWallet(wallet);
        });

        viewModel.getLoadingLiveData().observe(this, isLoading ->
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE));
    }

    private void displayWallet(Wallet wallet) {
        tvWalletNumber.setText(wallet.getWalletNumber());
        tvStatus.setText(wallet.getStatus());
        tvCurrency.setText(wallet.getCurrency());

        if (isBalanceVisible) {
            tvBalance.setText(wallet.getFormattedBalance());
        } else {
            tvBalance.setText("₹••••••");
        }
    }

    private void toggleBalance() {
        isBalanceVisible = !isBalanceVisible;
        Wallet wallet = viewModel.getWalletLiveData().getValue();
        if (wallet != null) displayWallet(wallet);
    }
}