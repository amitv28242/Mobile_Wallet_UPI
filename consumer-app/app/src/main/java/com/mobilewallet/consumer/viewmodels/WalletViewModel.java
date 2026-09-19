package com.mobilewallet.consumer.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mobilewallet.consumer.api.ApiClient;
import com.mobilewallet.consumer.api.ApiInterface;
import com.mobilewallet.consumer.api.ApiResponse;
import com.mobilewallet.consumer.models.AddMoneyRequest;
import com.mobilewallet.consumer.models.Wallet;

import java.math.BigDecimal;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletViewModel extends AndroidViewModel {

    private static final String TAG = "WalletViewModel";
    private final ApiInterface api;

    private final MutableLiveData<Wallet> walletLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> addMoneySuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>(false);

    public WalletViewModel(@NonNull Application application) {
        super(application);
        api = ApiClient.getApiInterface(application);
    }

    public LiveData<Wallet> getWalletLiveData() { return walletLiveData; }
    public LiveData<Boolean> getAddMoneySuccess() { return addMoneySuccess; }
    public LiveData<String> getErrorLiveData() { return errorLiveData; }
    public LiveData<Boolean> getLoadingLiveData() { return loadingLiveData; }

    public void loadWallet() {
        loadingLiveData.setValue(true);

        api.getWallet().enqueue(new Callback<ApiResponse<Wallet>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Wallet>> call,
                                   @NonNull Response<ApiResponse<Wallet>> response) {
                loadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Wallet> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        walletLiveData.setValue(apiResponse.getData());
                    } else {
                        errorLiveData.setValue(apiResponse.getMessage());
                    }
                } else {
                    errorLiveData.setValue("Failed to load wallet");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Wallet>> call, @NonNull Throwable t) {
                loadingLiveData.setValue(false);
                Log.e(TAG, "Load wallet failed", t);
                errorLiveData.setValue("Network error");
            }
        });
    }

    public void loadBalance() {
        api.getBalance().enqueue(new Callback<ApiResponse<Wallet>>() {
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
                Log.e(TAG, "Load balance failed", t);
            }
        });
    }

    public void addMoney(BigDecimal amount) {
        loadingLiveData.setValue(true);
        errorLiveData.setValue(null);

        AddMoneyRequest request = new AddMoneyRequest(amount);

        api.addMoney(request).enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Map<String, Object>>> call,
                                   @NonNull Response<ApiResponse<Map<String, Object>>> response) {
                loadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        addMoneySuccess.setValue(true);
                        loadWallet(); // Refresh balance
                    } else {
                        errorLiveData.setValue(response.body().getMessage());
                    }
                } else {
                    errorLiveData.setValue("Failed to add money");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Map<String, Object>>> call,
                                  @NonNull Throwable t) {
                loadingLiveData.setValue(false);
                errorLiveData.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void clearError() {
        errorLiveData.setValue(null);
    }
}