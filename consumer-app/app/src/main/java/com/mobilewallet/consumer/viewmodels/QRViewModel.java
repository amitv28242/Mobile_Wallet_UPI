package com.mobilewallet.consumer.viewmodels;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mobilewallet.consumer.api.ApiClient;
import com.mobilewallet.consumer.api.ApiInterface;
import com.mobilewallet.consumer.api.ApiResponse;
import com.mobilewallet.consumer.models.Payment;
import com.mobilewallet.consumer.models.QR;
import com.mobilewallet.consumer.models.QRScanRequest;
import com.mobilewallet.consumer.utils.QRCodeGenerator;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QRViewModel extends AndroidViewModel {

    private static final String TAG = "QRViewModel";
    private final ApiInterface api;

    private final MutableLiveData<QR> generatedQRLiveData = new MutableLiveData<>();
    private final MutableLiveData<Bitmap> qrBitmapLiveData = new MutableLiveData<>();
    private final MutableLiveData<Payment> scanResultLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>(false);

    public QRViewModel(@NonNull Application application) {
        super(application);
        api = ApiClient.getApiInterface(application);
    }

    public LiveData<QR> getGeneratedQRLiveData() { return generatedQRLiveData; }
    public LiveData<Bitmap> getQrBitmapLiveData() { return qrBitmapLiveData; }
    public LiveData<Payment> getScanResultLiveData() { return scanResultLiveData; }
    public LiveData<String> getErrorLiveData() { return errorLiveData; }
    public LiveData<Boolean> getLoadingLiveData() { return loadingLiveData; }

    public void generateQR(BigDecimal amount, String description) {
        loadingLiveData.setValue(true);
        errorLiveData.setValue(null);

        Map<String, Object> body = new HashMap<>();
        body.put("amount", amount);
        body.put("description", description);
        body.put("qrType", "PAYMENT");

        api.generateQR(body).enqueue(new Callback<ApiResponse<QR>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<QR>> call,
                                   @NonNull Response<ApiResponse<QR>> response) {
                loadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<QR> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        QR qr = apiResponse.getData();
                        generatedQRLiveData.setValue(qr);

                        // Generate QR bitmap
                        Bitmap bitmap = QRCodeGenerator.generateQRCode(qr.getQrPayload(), 600);
                        qrBitmapLiveData.setValue(bitmap);
                    } else {
                        errorLiveData.setValue(apiResponse.getMessage());
                    }
                } else {
                    errorLiveData.setValue("Failed to generate QR");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<QR>> call, @NonNull Throwable t) {
                loadingLiveData.setValue(false);
                Log.e(TAG, "QR generation failed", t);
                errorLiveData.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void processScannedQR(String qrPayload) {
        loadingLiveData.setValue(true);
        errorLiveData.setValue(null);

        String idempotencyKey = UUID.randomUUID().toString();
        QRScanRequest request = new QRScanRequest(qrPayload, idempotencyKey);

        api.scanQR(request).enqueue(new Callback<ApiResponse<Payment>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Payment>> call,
                                   @NonNull Response<ApiResponse<Payment>> response) {
                loadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Payment> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        scanResultLiveData.setValue(apiResponse.getData());
                    } else {
                        errorLiveData.setValue(apiResponse.getMessage());
                    }
                } else {
                    errorLiveData.setValue("QR processing failed");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Payment>> call, @NonNull Throwable t) {
                loadingLiveData.setValue(false);
                Log.e(TAG, "QR scan failed", t);
                errorLiveData.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void clearError() {
        errorLiveData.setValue(null);
    }
}