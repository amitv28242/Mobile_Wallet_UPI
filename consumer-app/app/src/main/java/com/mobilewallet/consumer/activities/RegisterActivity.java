package com.mobilewallet.consumer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mobilewallet.consumer.R;
import com.mobilewallet.consumer.models.RegisterRequest;
import com.mobilewallet.consumer.utils.Constants;
import com.mobilewallet.consumer.viewmodels.AuthViewModel;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etEmail, etPhone, etPassword, etConfirmPassword,
            etFirstName, etLastName;
    private MaterialButton btnRegister;
    private TextView tvLogin;
    private View btnBack;
    private ProgressBar progressBar;

    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        initViewModel();
        setupListeners();
        observeViewModel();
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnRegister.setOnClickListener(v -> attemptRegister());
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void attemptRegister() {
        String username = getText(etUsername);
        String email = getText(etEmail);
        String phone = getText(etPhone);
        String password = getText(etPassword);
        String confirmPassword = getText(etConfirmPassword);
        String firstName = getText(etFirstName);
        String lastName = getText(etLastName);

        // Validation
        if (TextUtils.isEmpty(username) || username.length() < 3) {
            etUsername.setError("Username must be at least 3 characters");
            return;
        }
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter valid email");
            return;
        }
        if (TextUtils.isEmpty(phone) || phone.length() < 10) {
            etPhone.setError("Enter valid phone number");
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 8) {
            etPassword.setError("Password must be at least 8 characters");
            return;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            return;
        }
        if (TextUtils.isEmpty(firstName)) {
            etFirstName.setError("First name required");
            return;
        }
        if (TextUtils.isEmpty(lastName)) {
            etLastName.setError("Last name required");
            return;
        }

        RegisterRequest request = new RegisterRequest(username, email, phone, password,
                firstName, lastName);
        viewModel.register(request);
    }

    private String getText(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    private void observeViewModel() {
        viewModel.getRegisterResult().observe(this, authResponse -> {
            if (authResponse != null && authResponse.isSuccess()) {
                Toast.makeText(this, "Registration successful! Please verify OTP",
                        Toast.LENGTH_LONG).show();

                Intent intent = new Intent(this, OTPActivity.class);
                intent.putExtra("identifier", getText(etPhone));
                intent.putExtra("purpose", Constants.OTP_PURPOSE_REGISTRATION);
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
            btnRegister.setEnabled(!isLoading);
        });
    }
}