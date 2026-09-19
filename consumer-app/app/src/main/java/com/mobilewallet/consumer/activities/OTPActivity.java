package com.mobilewallet.consumer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.mobilewallet.consumer.R;
import com.mobilewallet.consumer.utils.Constants;
import com.mobilewallet.consumer.viewmodels.AuthViewModel;

public class OTPActivity extends AppCompatActivity {

    private EditText[] otpInputs;
    private MaterialButton btnVerify;
    private TextView tvResendOTP, tvTimer, tvPhoneNumber;
    private ProgressBar progressBar;
    private View btnBack;

    private AuthViewModel authViewModel;
    private String identifier;
    private String purpose;
    private CountDownTimer resendTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        identifier = getIntent().getStringExtra("identifier");
        purpose = getIntent().getStringExtra("purpose");
        if (purpose == null) purpose = Constants.OTP_PURPOSE_REGISTRATION;

        initViews();
        initViewModel();
        setupListeners();
        observeViewModel();
        startResendTimer();
    }

    private void initViews() {
        otpInputs = new EditText[]{
                findViewById(R.id.etOtp1),
                findViewById(R.id.etOtp2),
                findViewById(R.id.etOtp3),
                findViewById(R.id.etOtp4),
                findViewById(R.id.etOtp5),
                findViewById(R.id.etOtp6)
        };

        btnVerify = findViewById(R.id.btnVerify);
        tvResendOTP = findViewById(R.id.tvResendOTP);
        tvTimer = findViewById(R.id.tvTimer);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        progressBar = findViewById(R.id.progressBar);
        btnBack = findViewById(R.id.btnBack);

        if (identifier != null) {
            tvPhoneNumber.setText("OTP sent to " + identifier);
        }

        setupOtpInputs();
    }

    private void setupOtpInputs() {
        for (int i = 0; i < otpInputs.length; i++) {
            final int index = i;
            otpInputs[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < otpInputs.length - 1) {
                        otpInputs[index + 1].requestFocus();
                    } else if (s.length() == 0 && index > 0) {
                        otpInputs[index - 1].requestFocus();
                    }

                    if (isOtpComplete()) {
                        attemptVerify();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void initViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    private void setupListeners() {
        btnVerify.setOnClickListener(v -> attemptVerify());
        tvResendOTP.setOnClickListener(v -> resendOTP());
        btnBack.setOnClickListener(v -> finish());
    }

    private void attemptVerify() {
        String otp = getOtpValue();
        if (otp.length() != 6) {
            Toast.makeText(this, "Please enter complete OTP", Toast.LENGTH_SHORT).show();
            return;
        }
        authViewModel.verifyOTP(identifier, purpose, otp);
    }

    private void resendOTP() {
        if (!tvResendOTP.isEnabled()) return;
        authViewModel.resendOTP(identifier, purpose);
        startResendTimer();
        Toast.makeText(this, "OTP resent successfully", Toast.LENGTH_SHORT).show();
    }

    private String getOtpValue() {
        StringBuilder sb = new StringBuilder();
        for (EditText et : otpInputs) {
            sb.append(et.getText().toString());
        }
        return sb.toString();
    }

    private boolean isOtpComplete() {
        for (EditText et : otpInputs) {
            if (et.getText().toString().isEmpty()) return false;
        }
        return true;
    }

    private void startResendTimer() {
        tvResendOTP.setEnabled(false);
        tvResendOTP.setAlpha(0.5f);

        if (resendTimer != null) resendTimer.cancel();

        resendTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText("Resend in " + (millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                tvTimer.setText("");
                tvResendOTP.setEnabled(true);
                tvResendOTP.setAlpha(1f);
            }
        }.start();
    }

    private void observeViewModel() {
        authViewModel.getOtpVerified().observe(this, verified -> {
            if (verified != null && verified) {
                Toast.makeText(this, "OTP verified successfully", Toast.LENGTH_SHORT).show();

                if (Constants.OTP_PURPOSE_REGISTRATION.equals(purpose)) {
                    // After registration verification, go to log in
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    // For other purposes, go to main
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                finish();
            }
        });

        authViewModel.getErrorLiveData().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                clearOtpInputs();
            }
        });

        authViewModel.getLoadingLiveData().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            btnVerify.setEnabled(!isLoading);
        });
    }

    private void clearOtpInputs() {
        for (EditText et : otpInputs) {
            et.setText("");
        }
        otpInputs[0].requestFocus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (resendTimer != null) resendTimer.cancel();
    }
}