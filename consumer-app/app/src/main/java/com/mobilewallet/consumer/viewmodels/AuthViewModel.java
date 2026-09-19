package com.mobilewallet.consumer.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.mobilewallet.consumer.api.ApiClient;
import com.mobilewallet.consumer.api.ApiInterface;
import com.mobilewallet.consumer.api.ApiResponse;
import com.mobilewallet.consumer.models.AuthResponse;
import com.mobilewallet.consumer.models.LoginRequest;
import com.mobilewallet.consumer.models.OTPRequest;
import com.mobilewallet.consumer.models.RegisterRequest;
import com.mobilewallet.consumer.models.RegisterResponse;
import com.mobilewallet.consumer.utils.TokenManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthViewModel extends AndroidViewModel {

    private static final String TAG = "AuthViewModel";
    private final ApiInterface api;

    private final MutableLiveData<AuthResponse> loginResult = new MutableLiveData<>();
    private final MutableLiveData<RegisterResponse> registerResult = new MutableLiveData<>();
    private final MutableLiveData<AuthResponse> guestLoginResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> otpVerified = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>(false);


    public AuthViewModel(@NonNull Application application) {
        super(application);
        api = ApiClient.getApiInterface(application);
    }

    public LiveData<AuthResponse> getLoginResult() { return loginResult; }
    public LiveData<RegisterResponse> getRegisterResult() { return registerResult; }
    public LiveData<AuthResponse> getGuestLoginResult() { return guestLoginResult; }
    public LiveData<Boolean> getOtpVerified() { return otpVerified; }
    public LiveData<String> getErrorLiveData() { return errorLiveData; }
    public LiveData<Boolean> getLoadingLiveData() { return loadingLiveData; }

    public void login(String username, String password) {
        loadingLiveData.setValue(true);
        errorLiveData.setValue(null);

        LoginRequest request = new LoginRequest(username, password);

        api.login(request).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<AuthResponse>> call,
                                   @NonNull Response<ApiResponse<AuthResponse>> response) {
                loadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<AuthResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        AuthResponse auth = apiResponse.getData();
                        saveAuthData(auth, false);
                        loginResult.setValue(auth);
                    } else {
                        errorLiveData.setValue(apiResponse.getMessage());
                    }
                } else {
                    errorLiveData.setValue("Login failed. Please try again.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<AuthResponse>> call, @NonNull Throwable t) {
                loadingLiveData.setValue(false);
                Log.e(TAG, "Login failed", t);
                errorLiveData.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void register(RegisterRequest request) {
        loadingLiveData.setValue(true);
        errorLiveData.setValue(null);

        api.register(request).enqueue(new Callback<ApiResponse<RegisterResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<RegisterResponse>> call,
                                   @NonNull Response<ApiResponse<RegisterResponse>> response) {
                loadingLiveData.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<RegisterResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        registerResult.setValue(apiResponse.getData());
                    } else {
                        errorLiveData.setValue(apiResponse.getMessage());
                    }
                } else {
                    errorLiveData.setValue("Registration failed. Please try again.");
                }
            }
            @Override
            public void onFailure(@NonNull Call<ApiResponse<RegisterResponse>> call, @NonNull Throwable t) {
                loadingLiveData.setValue(false);
                Log.e(TAG, "Registration failed", t);
                errorLiveData.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void guestLogin(String deviceId, String deviceName) {
        loadingLiveData.setValue(true);
        errorLiveData.setValue(null);

        // Sanitize device ID
        String sanitizedId = deviceId != null
                ? deviceId.replaceAll("[^a-zA-Z0-9]", "")
                : "";
        if (sanitizedId.length() < 4) {
            sanitizedId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        }

        Map<String, String> body = new HashMap<>();
        body.put("deviceId", sanitizedId);
        body.put("deviceName", deviceName != null ? deviceName : "Android Device");

        api.guestLogin(body).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<AuthResponse>> call,
                                   @NonNull Response<ApiResponse<AuthResponse>> response) {
                loadingLiveData.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<AuthResponse> apiResponse = response.body();

                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        AuthResponse auth = apiResponse.getData();
                        saveAuthData(auth, true);
                        guestLoginResult.setValue(auth);
                    } else {
                        errorLiveData.setValue(
                                apiResponse.getMessage() != null
                                        ? apiResponse.getMessage()
                                        : "Guest login failed");
                    }
                } else {
                    // Parse error body
                    String msg = "Guest login failed";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Guest login error body: " + errorBody);
                            ApiResponse<?> errResp = new Gson().fromJson(errorBody, ApiResponse.class);
                            if (errResp != null && errResp.getMessage() != null) {
                                msg = errResp.getMessage();
                            }
                        }
                    } catch (Exception ignored) {}
                    errorLiveData.setValue(msg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<AuthResponse>> call,
                                  @NonNull Throwable t) {
                loadingLiveData.setValue(false);
                Log.e(TAG, "Guest login network failure", t);
                errorLiveData.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void verifyOTP(String identifier, String purpose, String otpCode) {
        loadingLiveData.setValue(true);
        errorLiveData.setValue(null);

        OTPRequest request = new OTPRequest(identifier, purpose, otpCode);

        api.verifyOTP(request).enqueue(new Callback<ApiResponse<Map<String, Boolean>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Map<String, Boolean>>> call,
                                   @NonNull Response<ApiResponse<Map<String, Boolean>>> response) {
                loadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Map<String, Boolean>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        Boolean verified = apiResponse.getData().get("verified");
                        otpVerified.setValue(verified != null && verified);
                    } else {
                        errorLiveData.setValue(apiResponse.getMessage());
                    }
                } else {
                    errorLiveData.setValue("OTP verification failed");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Map<String, Boolean>>> call,
                                  @NonNull Throwable t) {
                loadingLiveData.setValue(false);
                errorLiveData.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void resendOTP(String identifier, String purpose) {
        loadingLiveData.setValue(true);
        errorLiveData.setValue(null);

        Map<String, String> body = new HashMap<>();
        body.put("identifier", identifier);
        body.put("purpose", purpose);

        api.resendOTP(body).enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Map<String, Object>>> call,
                                   @NonNull Response<ApiResponse<Map<String, Object>>> response) {
                loadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().isSuccess()) {
                        errorLiveData.setValue(response.body().getMessage());
                    }
                } else {
                    errorLiveData.setValue("Failed to resend OTP");
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

    public void logout() {
        TokenManager.getInstance().logout();
    }

    private void saveAuthData(AuthResponse auth, boolean isGuest) {
        TokenManager.getInstance().saveTokens(auth.getAccessToken(), auth.getRefreshToken());

        if (auth.getUser() != null) {
            TokenManager.getInstance().saveUserInfo(
                    auth.getUser().getId(),
                    auth.getUser().getRole(),
                    auth.getUser().getFullName(),
                    auth.getUser().getEmail(),
                    isGuest
            );
        }
    }

    public void clearError() {
        errorLiveData.setValue(null);
    }
}