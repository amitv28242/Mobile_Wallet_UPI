// FILE: consumer-app/app/src/main/java/com/mobilewallet/consumer/activities/LoginActivity.java
package com.mobilewallet.consumer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mobilewallet.consumer.R;
import com.mobilewallet.consumer.utils.NetworkUtils;
import com.mobilewallet.consumer.viewmodels.AuthViewModel;

import java.util.UUID;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LOGIN";

    private TextInputLayout tilUsername, tilPassword;
    private TextInputEditText etUsername, etPassword;
    private MaterialButton btnLogin, btnGuestLogin;
    private TextView tvForgotPassword, tvRegister;
    private ProgressBar progressBar;

    private AuthViewModel authViewModel;
    private boolean viewModelReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        long t0 = System.currentTimeMillis();
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate START");

        // ✅ STEP 1: Set content view FIRST — shows UI immediately
        setContentView(R.layout.activity_login);
        Log.d(TAG, "setContentView OK (+" + (System.currentTimeMillis() - t0) + "ms)");

        // ✅ STEP 2: Find views — fast, no I/O
        initViews();
        Log.d(TAG, "initViews OK (+" + (System.currentTimeMillis() - t0) + "ms)");

        // ✅ STEP 3: Setup listeners — no network, no disk
        setupListeners();

        // ✅ STEP 4: Defer ViewModel + Retrofit init to NEXT frame
        //          This lets the UI paint FIRST
        findViewById(android.R.id.content).post(() -> {
            Log.d(TAG, "post() running — initializing ViewModel (+"
                    + (System.currentTimeMillis() - t0) + "ms)");
            initViewModelAsync(t0);
        });

        Log.d(TAG, "onCreate END (+" + (System.currentTimeMillis() - t0) + "ms)");
    }

    private void initViews() {
        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGuestLogin = findViewById(R.id.btnGuestLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvRegister = findViewById(R.id.tvRegister);
        progressBar = findViewById(R.id.progressBar);

        if (progressBar != null) progressBar.setVisibility(View.GONE);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> {
            if (!viewModelReady) {
                Toast.makeText(this, "Please wait…", Toast.LENGTH_SHORT).show();
                return;
            }
            attemptLogin();
        });

        btnGuestLogin.setOnClickListener(v -> {
            if (!viewModelReady) {
                Toast.makeText(this, "Please wait…", Toast.LENGTH_SHORT).show();
                return;
            }
            continueAsGuest();
        });

        tvForgotPassword.setOnClickListener(v ->
                Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show());

        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    /**
     * ✅ Initialize ViewModel + Retrofit on a BACKGROUND thread.
     *    AuthViewModel uses lazy ApiClient — but ApiClient builds Retrofit
     *    in the constructor which triggers OkHttp class loading (slow).
     */
    private void initViewModelAsync(long t0) {
        new Thread(() -> {
            try {
                long t1 = System.currentTimeMillis();

                // Force Retrofit + OkHttp to initialize on background thread
                com.mobilewallet.consumer.api.ApiClient.getApiInterface(getApplicationContext());

                Log.d(TAG, "ApiClient init OK (+"
                        + (System.currentTimeMillis() - t1) + "ms on bg thread)");
            } catch (Throwable t) {
                Log.e(TAG, "ApiClient init failed", t);
            }

            // ✅ Now create the ViewModel on main thread (it's cheap after ApiClient is warm)
            runOnUiThread(() -> {
                try {
                    authViewModel = new androidx.lifecycle.ViewModelProvider(
                            LoginActivity.this).get(AuthViewModel.class);
                    viewModelReady = true;
                    observeViewModel();
                    Log.d(TAG, "ViewModel ready (+"
                            + (System.currentTimeMillis() - t0) + "ms total)");
                } catch (Throwable t) {
                    Log.e(TAG, "ViewModel init failed", t);
                    Toast.makeText(LoginActivity.this,
                            "Init failed: " + t.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });
        }, "Login-Init").start();
    }

    private void attemptLogin() {
        if (authViewModel == null) return;

        if (tilUsername != null) tilUsername.setError(null);
        if (tilPassword != null) tilPassword.setError(null);

        String username = etUsername.getText() != null
                ? etUsername.getText().toString().trim() : "";
        String password = etPassword.getText() != null
                ? etPassword.getText().toString() : "";

        if (TextUtils.isEmpty(username)) {
            if (tilUsername != null) tilUsername.setError(getString(R.string.field_required));
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            if (tilPassword != null) tilPassword.setError(getString(R.string.password_too_short));
            return;
        }

        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
            return;
        }

        authViewModel.login(username, password);
    }

    private void continueAsGuest() {
        // Get Android ID (unique per app install on device)
        String deviceId = android.provider.Settings.Secure.getString(
                getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID
        );

        // Fallback if null (rare)
        if (deviceId == null || deviceId.isEmpty()) {
            deviceId = UUID.randomUUID().toString();
        }

        String deviceName = android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL;

        authViewModel.guestLogin(deviceId, deviceName);
    }

    private void observeViewModel() {
        if (authViewModel == null) return;

        authViewModel.getLoginResult().observe(this, authResponse -> {
            if (authResponse != null && authResponse.isSuccess()) {
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                navigateToMain();
            }
        });

        authViewModel.getGuestLoginResult().observe(this, guestResponse -> {
            if (guestResponse != null && guestResponse.isSuccess()) {
                Toast.makeText(this, "Logged in as Guest", Toast.LENGTH_SHORT).show();
                navigateToMain();
            }
        });

        authViewModel.getErrorLiveData().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });

        authViewModel.getLoadingLiveData().observe(this, isLoading -> {
            if (progressBar != null) {
                progressBar.setVisibility(Boolean.TRUE.equals(isLoading) ? View.VISIBLE : View.GONE);
            }
            btnLogin.setEnabled(!Boolean.TRUE.equals(isLoading));
            btnGuestLogin.setEnabled(!Boolean.TRUE.equals(isLoading));
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}



/*package com.mobilewallet.consumer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mobilewallet.consumer.R;
import com.mobilewallet.consumer.utils.NetworkUtils;
import com.mobilewallet.consumer.viewmodels.AuthViewModel;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilUsername, tilPassword;
    private TextInputEditText etUsername, etPassword;
    private MaterialButton btnLogin, btnGuestLogin;
    private TextView tvForgotPassword, tvRegister;
    private ProgressBar progressBar;

    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        initViewModel();
        setupListeners();
        observeViewModel();
    }

    private void initViews() {
        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGuestLogin = findViewById(R.id.btnGuestLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvRegister = findViewById(R.id.tvRegister);
        progressBar = findViewById(R.id.progressBar);
    }

    private void initViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());

        btnGuestLogin.setOnClickListener(v -> continueAsGuest());

        tvForgotPassword.setOnClickListener(v -> {
            Toast.makeText(this, "Forgot password feature coming soon", Toast.LENGTH_SHORT).show();
        });

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void attemptLogin() {
        // Clear previous errors
        tilUsername.setError(null);
        tilPassword.setError(null);

        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";

        // Validate
        if (TextUtils.isEmpty(username)) {
            tilUsername.setError(getString(R.string.field_required));
            etUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError(getString(R.string.field_required));
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            tilPassword.setError(getString(R.string.password_too_short));
            etPassword.requestFocus();
            return;
        }

        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
            return;
        }

        // Perform login
        authViewModel.login(username, password);
    }

    private void continueAsGuest() {
        // Generate a device ID for guest login
        String deviceId = android.provider.Settings.Secure.getString(
                getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID
        );

        authViewModel.guestLogin(deviceId, android.os.Build.MODEL);
    }

    private void observeViewModel() {
        authViewModel.getLoginResult().observe(this, authResponse -> {
            if (authResponse != null && authResponse.isSuccess()) {
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                navigateToMain();
            }
        });

        authViewModel.getGuestLoginResult().observe(this, guestResponse -> {
            if (guestResponse != null && guestResponse.isSuccess()) {
                Toast.makeText(this, "Logged in as Guest", Toast.LENGTH_SHORT).show();
                navigateToMain();
            }
        });

        authViewModel.getErrorLiveData().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });

        // ✅ FIX: Null-safe ProgressBar handling
        authViewModel.getLoadingLiveData().observe(this, isLoading -> {
            if (progressBar != null) {
                progressBar.setVisibility(
                        Boolean.TRUE.equals(isLoading) ? View.VISIBLE : View.GONE
                );
            }
            if (btnLogin != null) {
                btnLogin.setEnabled(!Boolean.TRUE.equals(isLoading));
            }
            if (btnGuestLogin != null) {
                btnGuestLogin.setEnabled(!Boolean.TRUE.equals(isLoading));
            }
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}*/