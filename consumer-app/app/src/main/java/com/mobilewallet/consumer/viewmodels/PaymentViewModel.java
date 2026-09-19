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
import com.mobilewallet.consumer.models.Payment;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentViewModel extends AndroidViewModel {

    private static final String TAG = "PaymentViewModel";
    private final ApiInterface api;

    private final MutableLiveData<Payment> paymentResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>(false);

    public PaymentViewModel(@NonNull Application application) {
        super(application);
        api = ApiClient.getApiInterface(application);
    }

    public LiveData<Payment> getPaymentResult() { return paymentResult; }
    public LiveData<String> getErrorLiveData() { return errorLiveData; }
    public LiveData<Boolean> getLoadingLiveData() { return loadingLiveData; }

    public void initiatePayment(String payeeIdentifier, BigDecimal amount, String description) {
        loadingLiveData.setValue(true);
        errorLiveData.setValue(null);

        Map<String, Object> body = new HashMap<>();
        body.put("payeeIdentifier", payeeIdentifier);
        body.put("amount", amount);
        body.put("description", description);
        body.put("paymentType", "WALLET_TRANSFER");
        body.put("idempotencyKey", UUID.randomUUID().toString());

        api.initiatePayment(body).enqueue(new Callback<ApiResponse<Payment>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Payment>> call,
                                   @NonNull Response<ApiResponse<Payment>> response) {
                loadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Payment> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        paymentResult.setValue(apiResponse.getData());
                    } else {
                        errorLiveData.setValue(apiResponse.getMessage());
                    }
                } else {
                    errorLiveData.setValue("Payment failed");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Payment>> call, @NonNull Throwable t) {
                loadingLiveData.setValue(false);
                Log.e(TAG, "Payment failed", t);
                errorLiveData.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void clearError() {
        errorLiveData.setValue(null);
    }
}