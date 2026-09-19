// FILE: consumer-app/app/src/main/java/com/mobilewallet/consumer/api/ApiClient.java
package com.mobilewallet.consumer.api;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mobilewallet.consumer.BuildConfig;
import com.mobilewallet.consumer.utils.TokenManager;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String TAG = "ApiClient";

    private static volatile Retrofit retrofit = null;
    private static volatile ApiInterface apiInterface = null;
    private static final Object LOCK = new Object();

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            synchronized (LOCK) {
                if (retrofit == null) {
                    Log.d(TAG, "Building Retrofit…");
                    long t0 = System.currentTimeMillis();

                    Gson gson = new GsonBuilder().setLenient().create();

                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor(
                            msg -> Log.d(TAG, msg));
                    logging.setLevel(BuildConfig.ENABLE_LOGGING
                            ? HttpLoggingInterceptor.Level.BODY
                            : HttpLoggingInterceptor.Level.NONE);

                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(30, TimeUnit.SECONDS)
                            .addInterceptor(chain -> {
                                Request orig = chain.request();
                                Request.Builder rb = orig.newBuilder()
                                        .header("Accept", "application/json")
                                        .header("Content-Type", "application/json");
                                String token = TokenManager.getInstance().getAccessToken();
                                if (token != null && !token.isEmpty()) {
                                    rb.header("Authorization", "Bearer " + token);
                                }
                                return chain.proceed(rb.build());
                            })
                            .addInterceptor(logging)
                            .retryOnConnectionFailure(true)
                            .build();

                    retrofit = new Retrofit.Builder()
                            .baseUrl(BuildConfig.BASE_URL)
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();

                    Log.d(TAG, "Retrofit built in "
                            + (System.currentTimeMillis() - t0) + "ms");
                }
            }
        }
        return retrofit;
    }

    public static ApiInterface getApiInterface(Context context) {
        if (apiInterface == null) {
            synchronized (LOCK) {
                if (apiInterface == null) {
                    apiInterface = getClient(context).create(ApiInterface.class);
                }
            }
        }
        return apiInterface;
    }
}

/*
package com.mobilewallet.consumer.api;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mobilewallet.consumer.BuildConfig;
import com.mobilewallet.consumer.utils.TokenManager;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String TAG = "ApiClient";
    private static final String BASE_URL = BuildConfig.BASE_URL;
    private static final long TIMEOUT_SECONDS = 30;

    private static Retrofit retrofit = null;
    private static ApiInterface apiInterface = null;

    // In ApiClient.java — Validate URL before using
    private static void validateBaseUrl() {
        if (BuildConfig.BASE_URL == null || BuildConfig.BASE_URL.isEmpty()) {
            throw new IllegalStateException(
                    "BASE_URL is not configured. Check build.gradle buildConfigField."
            );
        }
        if (!BuildConfig.BASE_URL.endsWith("/")) {
            throw new IllegalStateException(
                    "BASE_URL must end with '/'. Current: " + BuildConfig.BASE_URL);
        }
    }

    public static synchronized Retrofit getClient(Context context) {
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message ->
                    Log.d(TAG, message));
            loggingInterceptor.setLevel(BuildConfig.DEBUG ?
                    HttpLoggingInterceptor.Level.BODY :
                    HttpLoggingInterceptor.Level.NONE);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Accept", "application/json")
                                .header("Content-Type", "application/json");

                        String token = TokenManager.getInstance().getAccessToken();
                        if (token != null && !token.isEmpty()) {
                            requestBuilder.header("Authorization", "Bearer " + token);
                        }

                        return chain.proceed(requestBuilder.build());
                    })
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(new AuthInterceptor(context))
                    .retryOnConnectionFailure(true)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    public static synchronized ApiInterface getApiInterface(Context context) {
        if (apiInterface == null) {
            apiInterface = getClient(context).create(ApiInterface.class);
        }
        return apiInterface;
    }
}*/