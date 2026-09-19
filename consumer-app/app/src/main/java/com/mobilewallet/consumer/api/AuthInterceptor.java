package com.mobilewallet.consumer.api;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.mobilewallet.consumer.activities.LoginActivity;
import com.mobilewallet.consumer.utils.TokenManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private static final String TAG = "AuthInterceptor";
    private final Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        if (response.code() == 401) {
            Log.w(TAG, "401 Unauthorized - clearing tokens and redirecting to login");

            TokenManager.getInstance().clearTokens();

            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }

        return response;
    }
}