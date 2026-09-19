// FILE: consumer-app/app/src/main/java/com/mobilewallet/consumer/ConsumerApplication.java
package com.mobilewallet.consumer;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.FirebaseApp;
import com.mobilewallet.consumer.utils.Constants;
import com.mobilewallet.consumer.utils.PreferenceManager;
import com.mobilewallet.consumer.utils.TokenManager;

public class ConsumerApplication extends Application {

    private static final String TAG = "ConsumerApplication";
    private static ConsumerApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        Log.d("STARTUP", "========== APP STARTING ==========");
        Log.d("STARTUP", "Build: " + BuildConfig.BUILD_TYPE);
        Log.d("STARTUP", "Base URL: " + BuildConfig.BASE_URL);

        // ✅ Firebase — wrap in try/catch
        try {
            FirebaseApp.initializeApp(this);
            Log.d(TAG, "✅ Firebase initialized");
        } catch (Throwable t) {
            Log.e(TAG, "Firebase init failed", t);
        }

        // ✅ Preferences — safe
        try {
            PreferenceManager.init(this);
            Log.d(TAG, "✅ PreferenceManager initialized");
        } catch (Throwable t) {
            Log.e(TAG, "PreferenceManager init failed", t);
        }

        // ✅ TokenManager — initialize ASYNC (non-blocking)
        TokenManager.getInstance().initializeAsync(() -> {
            Log.d(TAG, "✅ TokenManager initialized");
        });

        // ✅ Light mode
        try {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } catch (Throwable ignored) {}

        // ✅ Notification channels
        try {
            createNotificationChannels();
            Log.d(TAG, "✅ Notification channels created");
        } catch (Throwable t) {
            Log.e(TAG, "Notification channel creation failed", t);
        }

        Log.d(TAG, "✅ ConsumerApplication initialized successfully");
    }

    public static ConsumerApplication getInstance() {
        return instance;
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = getSystemService(NotificationManager.class);

            NotificationChannel payments = new NotificationChannel(
                    Constants.CHANNEL_PAYMENTS,
                    getString(R.string.channel_payments),
                    NotificationManager.IMPORTANCE_HIGH
            );
            payments.setDescription(getString(R.string.channel_payments_desc));
            manager.createNotificationChannel(payments);

            NotificationChannel security = new NotificationChannel(
                    Constants.CHANNEL_SECURITY,
                    getString(R.string.channel_security),
                    NotificationManager.IMPORTANCE_HIGH
            );
            security.setDescription(getString(R.string.channel_security_desc));
            manager.createNotificationChannel(security);

            NotificationChannel general = new NotificationChannel(
                    Constants.CHANNEL_GENERAL,
                    getString(R.string.channel_general),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            general.setDescription(getString(R.string.channel_general_desc));
            manager.createNotificationChannel(general);
        }
    }
}




/*package com.mobilewallet.consumer;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.FirebaseApp;
import com.mobilewallet.consumer.utils.Constants;
import com.mobilewallet.consumer.utils.PreferenceManager;

public class ConsumerApplication extends Application {

    private static final String TAG = "ConsumerApplication";
    private static ConsumerApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Log build info in debug builds
        if (BuildConfig.ENABLE_LOGGING) {
            Log.i(TAG, "═══════════════════════════════════════");
            Log.i(TAG, "  " + BuildConfig.APP_NAME + " v" + BuildConfig.APP_VERSION);
            Log.i(TAG, "  Environment : " + BuildConfig.ENVIRONMENT);
            Log.i(TAG, "  Base URL    : " + BuildConfig.BASE_URL);
            Log.i(TAG, "  Build Type  : " + BuildConfig.BUILD_TYPE);
            Log.i(TAG, "  Debug Build : " + BuildConfig.DEBUG);
            Log.i(TAG, "═══════════════════════════════════════");
        }

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Initialize PreferenceManager
        PreferenceManager.init(this);

        // Force light mode for consistent fintech UI
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Create notification channels
        createNotificationChannels();

    }

    public static ConsumerApplication getInstance() {
        return instance;
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = getSystemService(NotificationManager.class);

            // Payment notifications
            NotificationChannel paymentChannel = new NotificationChannel(
                    Constants.CHANNEL_PAYMENTS,
                    getString(R.string.channel_payments),
                    NotificationManager.IMPORTANCE_HIGH
            );
            paymentChannel.setDescription(getString(R.string.channel_payments_desc));
            paymentChannel.enableVibration(true);
            paymentChannel.enableLights(true);
            manager.createNotificationChannel(paymentChannel);

            // Security notifications
            NotificationChannel securityChannel = new NotificationChannel(
                    Constants.CHANNEL_SECURITY,
                    getString(R.string.channel_security),
                    NotificationManager.IMPORTANCE_HIGH
            );
            securityChannel.setDescription(getString(R.string.channel_security_desc));
            securityChannel.enableVibration(true);
            manager.createNotificationChannel(securityChannel);

            // General notifications
            NotificationChannel generalChannel = new NotificationChannel(
                    Constants.CHANNEL_GENERAL,
                    getString(R.string.channel_general),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            generalChannel.setDescription(getString(R.string.channel_general_desc));
            manager.createNotificationChannel(generalChannel);
        }
    }
}*/