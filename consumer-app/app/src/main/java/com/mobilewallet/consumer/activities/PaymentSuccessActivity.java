package com.mobilewallet.consumer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.mobilewallet.consumer.R;
import com.mobilewallet.consumer.models.Payment;
import com.mobilewallet.consumer.utils.Constants;

public class PaymentSuccessActivity extends AppCompatActivity {

    private ImageView ivSuccess;
    private TextView tvTitle, tvAmount, tvPayeeName, tvReferenceId, tvDate, tvStatus;
    private MaterialButton btnDone, btnShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        Payment payment = (Payment) getIntent().getSerializableExtra(Constants.EXTRA_PAYMENT);

        initViews();
        displayPaymentDetails(payment);
        setupListeners();
        animateSuccess();
    }

    private void initViews() {
        ivSuccess = findViewById(R.id.ivSuccess);
        tvTitle = findViewById(R.id.tvTitle);
        tvAmount = findViewById(R.id.tvAmount);
        tvPayeeName = findViewById(R.id.tvPayeeName);
        tvReferenceId = findViewById(R.id.tvReferenceId);
        tvDate = findViewById(R.id.tvDate);
        tvStatus = findViewById(R.id.tvStatus);
        btnDone = findViewById(R.id.btnDone);
        btnShare = findViewById(R.id.btnShare);
    }

    private void displayPaymentDetails(Payment payment) {
        if (payment == null) return;

        boolean success = payment.isSuccess();
        tvTitle.setText(success ? "Payment Successful!" : "Payment Failed");
        tvAmount.setText("₹" + (payment.getAmount() != null ? payment.getAmount().toPlainString() : "0"));

        if (payment.getPayee() != null) {
            tvPayeeName.setText("To: " + payment.getPayee().getFullName());
        }

        tvReferenceId.setText(payment.getReferenceId() != null ? payment.getReferenceId() : "—");
        tvDate.setText(payment.getCreatedAt() != null ? payment.getCreatedAt() : "—");
        tvStatus.setText(payment.getStatus() != null ? payment.getStatus() : "—");

        if (!success) {
            ivSuccess.setImageResource(R.drawable.ic_error_circle);
            ivSuccess.setColorFilter(getResources().getColor(R.color.error, getTheme()));
        }
    }

    private void setupListeners() {
        btnDone.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        btnShare.setOnClickListener(v -> shareReceipt());
    }

    private void shareReceipt() {
        // Share receipt via WhatsApp/Email
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Payment successful: " + tvAmount.getText());
        startActivity(Intent.createChooser(shareIntent, "Share Receipt"));
    }

    private void animateSuccess() {
        ivSuccess.setScaleX(0f);
        ivSuccess.setScaleY(0f);
        ivSuccess.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(500)
                .setInterpolator(new android.view.animation.OvershootInterpolator())
                .start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}