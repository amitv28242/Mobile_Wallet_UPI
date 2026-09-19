package com.mobilewallet.consumer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.mobilewallet.consumer.ConsumerApplication;
import java.util.concurrent.atomic.AtomicBoolean;

public class TokenManager {

    private static final String TAG = "TokenManager";
    private static final String PREF_NAME = "secure_wallet_prefs";
    private static final String FALLBACK_PREF_NAME = "wallet_prefs_fallback";

    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_IS_GUEST = "is_guest";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_LAST_LOGIN = "last_login";

    private static volatile TokenManager instance;
    private static final Object LOCK = new Object();

    private SharedPreferences securePrefs;
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    private TokenManager() {
        // Do NOT initialize here — it blocks main thread
    }

    public static TokenManager getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new TokenManager();
                }
            }
        }
        return instance;
    }

    /**
     * ✅ Initialize on a background thread. Call this from Application.onCreate
     * using a background executor.
     */
    public void initializeAsync(Runnable onDone) {
        if (initialized.get()) {
            if (onDone != null) onDone.run();
            return;
        }

        new Thread(() -> {
            try {
                initInternal();
            } catch (Throwable t) {
                Log.e(TAG, "TokenManager init failed — using fallback", t);
                initFallback();
            } finally {
                initialized.set(true);
                if (onDone != null) {
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(onDone);
                }
            }
        }, "TokenManager-Init").start();
    }

    private void initInternal() {
        Context ctx = ConsumerApplication.getInstance();

        try {
            MasterKey masterKey = new MasterKey.Builder(ctx)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            securePrefs = EncryptedSharedPreferences.create(
                    ctx,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
            Log.d(TAG, "EncryptedSharedPreferences OK");
        } catch (Throwable t) {
            Log.e(TAG, "EncryptedSharedPreferences failed", t);
            initFallback();
        }
    }

    private void initFallback() {
        Context ctx = ConsumerApplication.getInstance();
        securePrefs = ctx.getSharedPreferences(FALLBACK_PREF_NAME, Context.MODE_PRIVATE);
        Log.w(TAG, "Using fallback SharedPreferences");
    }

    private SharedPreferences prefs() {
        if (securePrefs == null) {
            // Lazy fallback — safe, won't block
            initFallback();
        }
        return securePrefs;
    }

    // =========================================================
    // Public API — each call is fast and safe
    // =========================================================

    public void saveTokens(String accessToken, String refreshToken) {
        try {
            prefs().edit()
                    .putString(KEY_ACCESS_TOKEN, accessToken)
                    .putString(KEY_REFRESH_TOKEN, refreshToken)
                    .putLong(KEY_LAST_LOGIN, System.currentTimeMillis())
                    .apply();
        } catch (Throwable t) {
            Log.e(TAG, "saveTokens failed", t);
        }
    }

    public void saveUserInfo(Long userId, String role, String name,
                             String email, boolean isGuest) {
        try {
            prefs().edit()
                    .putLong(KEY_USER_ID, userId != null ? userId : -1L)
                    .putString(KEY_USER_ROLE, role)
                    .putString(KEY_USER_NAME, name)
                    .putString(KEY_USER_EMAIL, email)
                    .putBoolean(KEY_IS_GUEST, isGuest)
                    .apply();
        } catch (Throwable t) {
            Log.e(TAG, "saveUserInfo failed", t);
        }
    }

    public String getAccessToken() {
        try { return prefs().getString(KEY_ACCESS_TOKEN, null); }
        catch (Throwable t) { return null; }
    }

    public String getRefreshToken() {
        try { return prefs().getString(KEY_REFRESH_TOKEN, null); }
        catch (Throwable t) { return null; }
    }

    public Long getUserId() {
        try {
            long id = prefs().getLong(KEY_USER_ID, -1L);
            return id == -1L ? null : id;
        } catch (Throwable t) { return null; }
    }

    public String getUserRole() {
        try { return prefs().getString(KEY_USER_ROLE, null); }
        catch (Throwable t) { return null; }
    }

    public String getUserName() {
        try { return prefs().getString(KEY_USER_NAME, null); }
        catch (Throwable t) { return null; }
    }

    public String getUserEmail() {
        try { return prefs().getString(KEY_USER_EMAIL, null); }
        catch (Throwable t) { return null; }
    }

    public boolean isGuest() {
        try { return prefs().getBoolean(KEY_IS_GUEST, false); }
        catch (Throwable t) { return false; }
    }

    public boolean isLoggedIn() {
        String token = getAccessToken();
        return token != null && !token.isEmpty();
    }

    public void clearTokens() {
        try { prefs().edit().clear().apply(); }
        catch (Throwable t) { Log.e(TAG, "clearTokens failed", t); }
    }

    public void logout() {
        clearTokens();
    }
}







/*package com.mobilewallet.consumer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.mobilewallet.consumer.ConsumerApplication;

public class TokenManager {

    private static final String TAG = "TokenManager";
    private static final String PREF_NAME = "secure_wallet_prefs";
    private static final String FALLBACK_PREF_NAME = "wallet_prefs_fallback";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_IS_GUEST = "is_guest";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_LAST_LOGIN = "last_login";

    private static TokenManager instance;
    private SharedPreferences securePrefs;

    private TokenManager() {
        Context context = ConsumerApplication.getInstance();

        // ✅ Try EncryptedSharedPreferences first
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            securePrefs = EncryptedSharedPreferences.create(
                    context,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
            Log.d(TAG, "EncryptedSharedPreferences initialized");
        } catch (Exception e) {
            // ✅ FALLBACK to regular SharedPreferences — app still works
            Log.e(TAG, "EncryptedSharedPreferences failed — using fallback", e);
            securePrefs = context.getSharedPreferences(
                    FALLBACK_PREF_NAME, Context.MODE_PRIVATE);
        }
    }

    public static synchronized TokenManager getInstance() {
        if (instance == null) {
            instance = new TokenManager();
        }
        return instance;
    }

    public void saveTokens(String accessToken, String refreshToken) {
        try {
            securePrefs.edit()
                    .putString(KEY_ACCESS_TOKEN, accessToken)
                    .putString(KEY_REFRESH_TOKEN, refreshToken)
                    .putLong(KEY_LAST_LOGIN, System.currentTimeMillis())
                    .apply();
        } catch (Exception e) {
            Log.e(TAG, "saveTokens failed", e);
        }
    }

    public void saveUserInfo(Long userId, String role, String name,
                             String email, boolean isGuest) {
        try {
            securePrefs.edit()
                    .putLong(KEY_USER_ID, userId != null ? userId : -1)
                    .putString(KEY_USER_ROLE, role)
                    .putString(KEY_USER_NAME, name)
                    .putString(KEY_USER_EMAIL, email)
                    .putBoolean(KEY_IS_GUEST, isGuest)
                    .apply();
        } catch (Exception e) {
            Log.e(TAG, "saveUserInfo failed", e);
        }
    }

    public String getAccessToken() {
        try {
            return securePrefs.getString(KEY_ACCESS_TOKEN, null);
        } catch (Exception e) {
            return null;
        }
    }

    public String getRefreshToken() {
        try {
            return securePrefs.getString(KEY_REFRESH_TOKEN, null);
        } catch (Exception e) {
            return null;
        }
    }

    public Long getUserId() {
        try {
            long id = securePrefs.getLong(KEY_USER_ID, -1);
            return id == -1 ? null : id;
        } catch (Exception e) {
            return null;
        }
    }

    public String getUserRole() {
        try { return securePrefs.getString(KEY_USER_ROLE, null); }
        catch (Exception e) { return null; }
    }

    public String getUserName() {
        try { return securePrefs.getString(KEY_USER_NAME, null); }
        catch (Exception e) { return null; }
    }

    public String getUserEmail() {
        try { return securePrefs.getString(KEY_USER_EMAIL, null); }
        catch (Exception e) { return null; }
    }

    public boolean isGuest() {
        try { return securePrefs.getBoolean(KEY_IS_GUEST, false); }
        catch (Exception e) { return false; }
    }

    public boolean isLoggedIn() {
        String token = getAccessToken();
        return token != null && !token.isEmpty();
    }

    public void clearTokens() {
        try { securePrefs.edit().clear().apply(); }
        catch (Exception e) { Log.e(TAG, "clearTokens failed", e); }
    }

    public void logout() {
        clearTokens();
    }
}*/