package com.mobilewallet.consumer.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mobilewallet.consumer.api.ApiClient;
import com.mobilewallet.consumer.api.ApiInterface;
import com.mobilewallet.consumer.api.ApiResponse;
import com.mobilewallet.consumer.models.BankAccount;
import com.mobilewallet.consumer.models.Card;
import com.mobilewallet.consumer.models.Wallet;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository class - central data access point for the app
 */
public class AppRepository {

    private static final String TAG = "AppRepository";
    private static AppRepository instance;

    private final ApiInterface api;

    private AppRepository(Context context) {
        this.api = ApiClient.getApiInterface(context);
    }

    public static synchronized AppRepository getInstance(Context context) {
        if (instance == null) {
            instance = new AppRepository(context.getApplicationContext());
        }
        return instance;
    }

    // Wallet
    public LiveData<Wallet> getWallet() {
        MutableLiveData<Wallet> data = new MutableLiveData<>();
        api.getWallet().enqueue(new Callback<ApiResponse<Wallet>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Wallet>> call,
                                   @NonNull Response<ApiResponse<Wallet>> response) {
                if (response.isSuccessful() && response.body() != null &&
                        response.body().isSuccess()) {
                    data.setValue(response.body().getData());
                }
            }
            @Override
            public void onFailure(@NonNull Call<ApiResponse<Wallet>> call, @NonNull Throwable t) {
                Log.e(TAG, "Get wallet failed", t);
            }
        });
        return data;
    }

    // Cards
    public LiveData<List<Card>> getCards() {
        MutableLiveData<List<Card>> data = new MutableLiveData<>();
        api.getCards().enqueue(new Callback<ApiResponse<List<Card>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Card>>> call,
                                   @NonNull Response<ApiResponse<List<Card>>> response) {
                if (response.isSuccessful() && response.body() != null &&
                        response.body().isSuccess()) {
                    data.setValue(response.body().getData());
                }
            }
            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Card>>> call, @NonNull Throwable t) {
                Log.e(TAG, "Get cards failed", t);
            }
        });
        return data;
    }

    // Bank Accounts
    public LiveData<List<BankAccount>> getBankAccounts() {
        MutableLiveData<List<BankAccount>> data = new MutableLiveData<>();
        api.getBankAccounts().enqueue(new Callback<ApiResponse<List<BankAccount>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<BankAccount>>> call,
                                   @NonNull Response<ApiResponse<List<BankAccount>>> response) {
                if (response.isSuccessful() && response.body() != null &&
                        response.body().isSuccess()) {
                    data.setValue(response.body().getData());
                }
            }
            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<BankAccount>>> call,
                                  @NonNull Throwable t) {
                Log.e(TAG, "Get bank accounts failed", t);
            }
        });
        return data;
    }

    // Notifications
    public LiveData<java.util.Map<String, Object>> getNotifications(int page, int size, boolean unreadOnly) {
        MutableLiveData<java.util.Map<String, Object>> data = new MutableLiveData<>();
        api.getNotifications(page, size, unreadOnly)
                .enqueue(new Callback<ApiResponse<java.util.Map<String, Object>>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<java.util.Map<String, Object>>> call,
                                           @NonNull Response<ApiResponse<java.util.Map<String, Object>>> response) {
                        if (response.isSuccessful() && response.body() != null &&
                                response.body().isSuccess()) {
                            data.setValue(response.body().getData());
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<java.util.Map<String, Object>>> call,
                                          @NonNull Throwable t) {
                        Log.e(TAG, "Get notifications failed", t);
                    }
                });
        return data;
    }
}