// FILE: consumer-app/app/src/main/java/com/mobilewallet/consumer/utils/CrashHandler.java
package com.mobilewallet.consumer.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Process;
import android.util.Log;

import androidx.annotation.NonNull;

import com.mobilewallet.consumer.activities.LoginActivity;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Global uncaught exception handler.
 * Logs crash, cleans up state, and restarts at LoginActivity.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "CrashHandler";

    private final Context context;
    private final Thread.UncaughtExceptionHandler defaultHandler;

    public CrashHandler(Context context) {
        this.context = context.getApplicationContext();
        this.defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    public static void install(Context context) {
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(context));
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        try {
            // ============ LOG CRASH ============
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            String stackTrace = sw.toString();

            String timestamp = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date());

            Log.e(TAG, "═══════════════════════════════════════════");
            Log.e(TAG, "CRASH at " + timestamp);
            Log.e(TAG, "Device: " + Build.MANUFACTURER + " " + Build.MODEL);
            Log.e(TAG, "Android: " + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")");
            Log.e(TAG, "Thread: " + thread.getName());
            Log.e(TAG, "Stack trace:");
            Log.e(TAG, stackTrace);
            Log.e(TAG, "═══════════════════════════════════════════");

            // ============ CLEAN STATE ============
            try {
                TokenManager.getInstance().clearTokens();
            } catch (Exception ignored) {}

            // ============ RESTART AT LOGIN ============
            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);

        } catch (Exception e) {
            Log.e(TAG, "CrashHandler failed", e);
        } finally {
            // Kill process cleanly
            Process.killProcess(Process.myPid());
            System.exit(10);
        }
    }
}