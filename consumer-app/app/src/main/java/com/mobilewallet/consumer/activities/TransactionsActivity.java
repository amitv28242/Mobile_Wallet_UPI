package com.mobilewallet.consumer.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.mobilewallet.consumer.R;
import com.mobilewallet.consumer.models.Transaction;
import com.mobilewallet.consumer.utils.Constants;

public class TransactionsActivity extends AppCompatActivity {

    private ImageView ivIcon;
    private View btnBack, btnShare;
    private TextView tvTitle, tvAmount, tvStatus, tvReference, tvDate, tvDescription,
            tvFrom, tvTo, tvBalanceBefore, tvBalanceAfter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);

        Transaction transaction = (Transaction) getIntent()
                .getSerializableExtra(Constants.EXTRA_TRANSACTION);

        initViews();
        displayTransaction(transaction);
        setupListeners();
    }

    private void initViews() {
        ivIcon = findViewById(R.id.ivIcon);
        btnBack = findViewById(R.id.btnBack);
        btnShare = findViewById(R.id.btnShare);
        tvTitle = findViewById(R.id.tvTitle);
        tvAmount = findViewById(R.id.tvAmount);
        tvStatus = findViewById(R.id.tvStatus);
        tvReference = findViewById(R.id.tvReference);
        tvDate = findViewById(R.id.tvDate);
        tvDescription = findViewById(R.id.tvDescription);
        tvFrom = findViewById(R.id.tvFrom);
        tvTo = findViewById(R.id.tvTo);
        tvBalanceBefore = findViewById(R.id.tvBalanceBefore);
        tvBalanceAfter = findViewById(R.id.tvBalanceAfter);
    }

    private void displayTransaction(Transaction transaction) {
        if (transaction == null) return;

        boolean isCredit = transaction.isCredit();

        tvTitle.setText(isCredit ? "Money Received" : "Money Sent");
        tvAmount.setText(transaction.getFormattedAmount());
        tvAmount.setTextColor(ContextCompat.getColor(this,
                isCredit ? R.color.success : R.color.error));

        tvStatus.setText(transaction.getStatus());
        tvReference.setText(transaction.getReference());
        tvDate.setText(transaction.getCreatedAt());
        tvDescription.setText(transaction.getDescription() != null ?
                transaction.getDescription() : "—");
        tvBalanceBefore.setText("₹" + transaction.getBalanceBefore());
        tvBalanceAfter.setText("₹" + transaction.getBalanceAfter());

        ivIcon.setImageResource(isCredit ?
                R.drawable.ic_arrow_down : R.drawable.ic_arrow_up);
        ivIcon.setColorFilter(ContextCompat.getColor(this,
                isCredit ? R.color.success : R.color.error));
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnShare.setOnClickListener(v -> shareTransaction());
    }

    private void shareTransaction() {
        android.content.Intent share = new android.content.Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(android.content.Intent.EXTRA_TEXT,
                "Transaction: " + tvReference.getText() + "\nAmount: " + tvAmount.getText());
        startActivity(android.content.Intent.createChooser(share, "Share"));
    }
}