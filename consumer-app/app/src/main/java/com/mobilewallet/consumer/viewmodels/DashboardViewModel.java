package com.mobilewallet.consumer.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mobilewallet.consumer.api.ApiClient;
import com.mobilewallet.consumer.api.ApiInterface;
import com.mobilewallet.consumer.api.ApiResponse;
import com.mobilewallet.consumer.models.Transaction;
import com.mobilewallet.consumer.models.Wallet;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardViewModel extends AndroidViewModel {

    private final ApiInterface api;

    private final MutableLiveData<Wallet> walletLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Transaction>> recentTransactions = new MutableLiveData<>();
    private final MutableLiveData<Long> unreadNotifications = new MutableLiveData<>(0L);
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>(false);

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        api = ApiClient.getApiInterface(application);
    }

    public LiveData<Wallet> getWalletLiveData() { return walletLiveData; }
    public LiveData<List<Transaction>> getRecentTransactions() { return recentTransactions; }
    public LiveData<Long> getUnreadNotifications() { return unreadNotifications; }
    public LiveData<String> getErrorLiveData() { return errorLiveData; }
    public LiveData<Boolean> getLoadingLiveData() { return loadingLiveData; }

    public void loadDashboard() {
        loadingLiveData.setValue(true);
        loadWallet();
        loadRecentTransactions();
        loadUnreadCount();
    }

    private void loadWallet() {
        api.getWallet().enqueue(new Callback<ApiResponse<Wallet>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Wallet>> call,
                                   @NonNull Response<ApiResponse<Wallet>> response) {
                if (response.isSuccessful() && response.body() != null &&
                        response.body().isSuccess()) {
                    walletLiveData.setValue(response.body().getData());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Wallet>> call, @NonNull Throwable t) {
                errorLiveData.setValue("Failed to load wallet");
            }
        });
    }

    private void loadRecentTransactions() {
        api.getTransactions(0, 5).enqueue(new Callback<ApiResponse<java.util.Map<String, Object>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<java.util.Map<String, Object>>> call,
                                   @NonNull Response<ApiResponse<java.util.Map<String, Object>>> response) {
                loadingLiveData.setValue(false);
                // Parse transactions (simplified)
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<java.util.Map<String, Object>>> call,
                                  @NonNull Throwable t) {
                loadingLiveData.setValue(false);
            }
        });
    }

    private void loadUnreadCount() {
        api.getUnreadCount().enqueue(new Callback<ApiResponse<java.util.Map<String, Long>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<java.util.Map<String, Long>>> call,
                                   @NonNull Response<ApiResponse<java.util.Map<String, Long>>> response) {
                if (response.isSuccessful() && response.body() != null &&
                        response.body().getData() != null) {
                    Long count = response.body().getData().get("unreadCount");
                    unreadNotifications.setValue(count != null ? count : 0L);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<java.util.Map<String, Long>>> call,
                                  @NonNull Throwable t) {
                // Silent fail
            }
        });
    }
}