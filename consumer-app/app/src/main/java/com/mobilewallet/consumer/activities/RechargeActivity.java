package com.mobilewallet.consumer.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mobilewallet.consumer.R;
import com.mobilewallet.consumer.viewmodels.WalletViewModel;

import java.math.BigDecimal;

public class RechargeActivity extends AppCompatActivity {

    private AutoCompleteTextView spinnerOperator;
    private TextInputEditText etPhoneNumber, etAmount;
    private MaterialButton btnRecharge;
    private ProgressBar progressBar;
    private View btnBack;
    private TextView tvTitle;

    private WalletViewModel viewModel;
    private String serviceType;

    private static final String[] MOBILE_OPERATORS = {"Airtel", "Jio", "Vi", "BSNL", "MTNL"};
    private static final String[] DTH_OPERATORS = {"Tata Sky", "Airtel Digital TV", "Dish TV", "Videocon d2h"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);

        serviceType = getIntent().getStringExtra("service_type");
        if (serviceType == null) serviceType = "Mobile";

        initViews();
        initViewModel();
        setupOperators();
        setupListeners();
    }

    private void initViews() {
        spinnerOperator = findViewById(R.id.spinnerOperator);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etAmount = findViewById(R.id.etAmount);
        btnRecharge = findViewById(R.id.btnRecharge);
        progressBar = findViewById(R.id.progressBar);
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);

        tvTitle.setText(serviceType + " Recharge");
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(WalletViewModel.class);
    }

    private void setupOperators() {
        String[] operators = "DTH".equalsIgnoreCase(serviceType) ?
                DTH_OPERATORS : MOBILE_OPERATORS;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, operators);
        spinnerOperator.setAdapter(adapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnRecharge.setOnClickListener(v -> attemptRecharge());
    }

    private void attemptRecharge() {
        String operator = spinnerOperator.getText().toString().trim();
        String phone = etPhoneNumber.getText() != null ? etPhoneNumber.getText().toString().trim() : "";
        String amountStr = etAmount.getText() != null ? etAmount.getText().toString().trim() : "";

        if (TextUtils.isEmpty(operator)) {
            Toast.makeText(this, "Please select operator", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(phone) || phone.length() < 10) {
            Toast.makeText(this, "Enter valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(amountStr)) {
            Toast.makeText(this, "Enter amount", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            BigDecimal amount = new BigDecimal(amountStr);
            if (amount.compareTo(BigDecimal.ONE) < 0) {
                Toast.makeText(this, "Minimum amount is ₹1", Toast.LENGTH_SHORT).show();
                return;
            }
            // Process recharge
            Toast.makeText(this, "Recharge initiated", Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
        }
    }
}