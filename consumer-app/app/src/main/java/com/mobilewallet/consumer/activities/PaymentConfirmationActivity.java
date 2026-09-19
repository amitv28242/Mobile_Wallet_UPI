package com.mobilewallet.consumer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.mobilewallet.consumer.R;
import com.mobilewallet.consumer.utils.Constants;
import com.mobilewallet.consumer.viewmodels.PaymentViewModel;

import java.math.BigDecimal;

public class PaymentConfirmationActivity extends AppCompatActivity {

    private ImageView ivAvatar;
    private View btnBack;
    private TextView tvPayeeName, tvPayeeWallet, tvAmount, tvNote;
    private MaterialButton btnConfirm, btnCancel;
    private ProgressBar progressBar;

    private PaymentViewModel viewModel;
    private String payeeIdentifier;
    private BigDecimal amount;
    private String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_confirmation);

        payeeIdentifier = getIntent().getStringExtra("payee_identifier");
        String amountStr = getIntent().getStringExtra("amount");
        description = getIntent().getStringExtra("description");
        String payeeName = getIntent().getStringExtra("payee_name");
        String payeeWallet = getIntent().getStringExtra("payee_wallet");

        try {
            amount = new BigDecimal(amountStr != null ? amountStr : "0");
        } catch (NumberFormatException e) {
            amount = BigDecimal.ZERO;
        }

        initViews(payeeName, payeeWallet);
        initViewModel();
        setupListeners();
        observeViewModel();
    }

    private void initViews(String payeeName, String payeeWallet) {
        ivAvatar = findViewById(R.id.ivAvatar);
        btnBack = findViewById(R.id.btnBack);
        tvPayeeName = findViewById(R.id.tvPayeeName);
        tvPayeeWallet = findViewById(R.id.tvPayeeWallet);
        tvAmount = findViewById(R.id.tvAmount);
        tvNote = findViewById(R.id.tvNote);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);

        if (payeeName != null) tvPayeeName.setText(payeeName);
        if (payeeWallet != null) tvPayeeWallet.setText(payeeWallet);
        tvAmount.setText("₹" + amount.toPlainString());
        if (description != null && !description.isEmpty()) {
            tvNote.setText(description);
        }
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(PaymentViewModel.class);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> showCancelDialog());
        btnConfirm.setOnClickListener(v -> confirmPayment());
    }

    private void confirmPayment() {
        viewModel.initiatePayment(payeeIdentifier, amount, description);
    }

    private void showCancelDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Payment?")
                .setMessage("Are you sure you want to cancel this payment?")
                .setPositiveButton("Yes", (d, w) -> finish())
                .setNegativeButton("No", null)
                .show();
    }

    private void observeViewModel() {
        viewModel.getPaymentResult().observe(this, payment -> {
            if (payment != null) {
                Intent intent = new Intent(this, PaymentSuccessActivity.class);
                intent.putExtra(Constants.EXTRA_PAYMENT, payment);
                startActivity(intent);
                finish();
            }
        });

        viewModel.getErrorLiveData().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getLoadingLiveData().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            btnConfirm.setEnabled(!isLoading);
            btnCancel.setEnabled(!isLoading);
        });
    }
}