// FILE: consumer-app/app/src/main/java/com/mobilewallet/consumer/activities/SplashActivity.java
package com.mobilewallet.consumer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mobilewallet.consumer.BuildConfig;
import com.mobilewallet.consumer.R;
import com.mobilewallet.consumer.utils.TokenManager;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    private static final long SPLASH_DELAY = 1500; // shorter = less chance of ANR

    private CountDownTimer splashTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate START");

        try {
            setContentView(R.layout.activity_splash);
            Log.d(TAG, "setContentView OK");

            // Set version
            TextView tvVersion = findViewById(R.id.tvVersion);
            if (tvVersion != null) {
                tvVersion.setText("Version " + BuildConfig.APP_VERSION);
            }

            // ✅ Use CountDownTimer — safer than Handler
            splashTimer = new CountDownTimer(SPLASH_DELAY, 500) {
                @Override
                public void onTick(long millisUntilFinished) {
                    // no-op
                }

                @Override
                public void onFinish() {
                    Log.d(TAG, "Splash timer finished — navigating");
                    navigateNext();
                }
            };
            splashTimer.start();

        } catch (Throwable t) {
            Log.e(TAG, "Splash init failed", t);
            navigateNext(); // don't get stuck
        }
    }

    private void navigateNext() {
        if (isFinishing() || isDestroyed()) {
            Log.w(TAG, "Activity finishing — skip navigation");
            return;
        }

        try {
            boolean isLoggedIn = TokenManager.getInstance().isLoggedIn();
            Log.d(TAG, "isLoggedIn = " + isLoggedIn);

            Intent intent = isLoggedIn
                    ? new Intent(this, MainActivity.class)
                    : new Intent(this, LoginActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            Log.d(TAG, "Navigated to: " + intent.getComponent());

        } catch (Throwable t) {
            Log.e(TAG, "Navigation failed — fallback to Login", t);
            try {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } catch (Throwable ignored) {}
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (splashTimer != null) {
            splashTimer.cancel();
            splashTimer = null;
        }
    }
}


/*package com.mobilewallet.consumer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mobilewallet.consumer.R;
import com.mobilewallet.consumer.utils.TokenManager;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 2000;
    private LinearLayout logoContainer;
    private LinearLayout loadingContainer;
    private TextView tvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initViews();
        startAnimations();

        new Handler(Looper.getMainLooper()).postDelayed(this::navigateNext, SPLASH_DELAY);
    }

    private void initViews() {
        logoContainer = findViewById(R.id.logoContainer);
        loadingContainer = findViewById(R.id.loadingContainer);
        tvVersion = findViewById(R.id.tvVersion);
    }

    private void startAnimations() {
        // Fade in logo
        logoContainer.setAlpha(0f);
        logoContainer.animate()
                .alpha(1f)
                .setDuration(800)
                .start();

        // Fade in loading
        loadingContainer.setAlpha(0f);
        loadingContainer.animate()
                .alpha(1f)
                .setDuration(800)
                .setStartDelay(400)
                .start();

        // Fade in version
        tvVersion.setAlpha(0f);
        tvVersion.animate()
                .alpha(1f)
                .setDuration(600)
                .setStartDelay(600)
                .start();
    }

    private void navigateNext() {
        Intent intent;

        if (TokenManager.getInstance().isLoggedIn()) {
            // User is logged in, go to dashboard
            intent = new Intent(this, MainActivity.class);
        } else {
            // User not logged in, go to log in
            intent = new Intent(this, LoginActivity.class);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}*/