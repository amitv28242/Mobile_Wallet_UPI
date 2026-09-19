package com.mobilewallet.consumer.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.mobilewallet.consumer.R;
import com.mobilewallet.consumer.utils.Constants;
import com.mobilewallet.consumer.viewmodels.QRViewModel;

import java.math.BigDecimal;

public class QRGeneratorActivity extends AppCompatActivity {

    private ImageView ivQRCode;
    private View btnBack, btnShare;
    private TextView tvAmount, tvTimer, tvUserName, tvWalletNumber, tvInstruction;
    private MaterialButton btnRegenerate;
    private ProgressBar progressBar;

    private QRViewModel viewModel;
    private CountDownTimer qrTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_generator);

        String amountStr = getIntent().getStringExtra("amount");
        String description = getIntent().getStringExtra("description");
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr != null ? amountStr : "0");
        } catch (Exception e) {
            amount = BigDecimal.ZERO;
        }

        initViews();
        initViewModel();
        setupListeners();
        observeViewModel();

        viewModel.generateQR(amount, description);
    }

    private void initViews() {
        ivQRCode = findViewById(R.id.ivQRCode);
        btnBack = findViewById(R.id.btnBack);
        btnShare = findViewById(R.id.btnShare);
        tvAmount = findViewById(R.id.tvAmount);
        tvTimer = findViewById(R.id.tvTimer);
        tvUserName = findViewById(R.id.tvUserName);
        tvWalletNumber = findViewById(R.id.tvWalletNumber);
        tvInstruction = findViewById(R.id.tvInstruction);
        btnRegenerate = findViewById(R.id.btnRegenerate);
        progressBar = findViewById(R.id.progressBar);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(QRViewModel.class);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnShare.setOnClickListener(v -> shareQR());
        btnRegenerate.setOnClickListener(v -> regenerateQR());
    }

    private void observeViewModel() {
        viewModel.getGeneratedQRLiveData().observe(this, qr -> {
            if (qr != null) {
                tvAmount.setText("₹" + (qr.getAmount() != null ? qr.getAmount().toPlainString() : "0"));
                tvUserName.setText(qr.getUserFullName());
                tvWalletNumber.setText("Wallet: " + qr.getUserId());
                startQRTimer();
            }
        });

        viewModel.getQrBitmapLiveData().observe(this, bitmap -> {
            if (bitmap != null) {
                ivQRCode.setImageBitmap(bitmap);
                ivQRCode.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getErrorLiveData().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getLoadingLiveData().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }

    private void startQRTimer() {
        if (qrTimer != null) qrTimer.cancel();

        qrTimer = new CountDownTimer(Constants.QR_EXPIRY_SECONDS * 1000L, 1000) {
            @Override
            public void onTick(long msUntilFinished) {
                long seconds = msUntilFinished / 1000;
                tvTimer.setText("Expires in " + seconds + "s");
            }

            @Override
            public void onFinish() {
                tvTimer.setText("QR Expired");
                ivQRCode.setAlpha(0.3f);
                btnRegenerate.setVisibility(View.VISIBLE);
                tvInstruction.setText("This QR code has expired. Please regenerate.");
            }
        }.start();
    }

    private void regenerateQR() {
        ivQRCode.setAlpha(1f);
        btnRegenerate.setVisibility(View.GONE);
        tvInstruction.setText(R.string.show_this_qr);
        viewModel.generateQR(BigDecimal.ZERO, "Payment");
    }

    private void shareQR() {
        Bitmap bitmap = viewModel.getQrBitmapLiveData().getValue();
        if (bitmap == null) {
            Toast.makeText(this, "QR not ready", Toast.LENGTH_SHORT).show();
        }
        // Share bitmap
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (qrTimer != null) qrTimer.cancel();
    }
}