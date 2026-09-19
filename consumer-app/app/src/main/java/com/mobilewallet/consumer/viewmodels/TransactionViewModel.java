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
import com.mobilewallet.consumer.models.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionViewModel extends AndroidViewModel {

    private static final String TAG = "TransactionViewModel";
    private final ApiInterface api;
    private final Gson gson = new Gson();

    private final MutableLiveData<List<Transaction>> transactionsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Transaction> selectedTransaction = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>(false);

    private int currentPage = 0;
    private boolean hasMore = true;

    public TransactionViewModel(@NonNull Application application) {
        super(application);
        api = ApiClient.getApiInterface(application);
    }

    public LiveData<List<Transaction>> getTransactionsLiveData() { return transactionsLiveData; }
    public LiveData<Transaction> getSelectedTransaction() { return selectedTransaction; }
    public LiveData<String> getErrorLiveData() { return errorLiveData; }
    public LiveData<Boolean> getLoadingLiveData() { return loadingLiveData; }

    /**
     * Load transactions. If refresh == true, reset to page 0.
     */
    public void loadTransactions(boolean refresh) {
        if (refresh) {
            currentPage = 0;
            hasMore = true;
        }
        if (!hasMore) return;

        loadingLiveData.setValue(true);
        errorLiveData.setValue(null);

        api.getTransactions(currentPage, 20)
                .enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<Map<String, Object>>> call,
                                           @NonNull Response<ApiResponse<Map<String, Object>>> response) {
                        loadingLiveData.setValue(false);

                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<Map<String, Object>> apiResponse = response.body();
                            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                                List<Transaction> transactions =
                                        parseTransactions(apiResponse.getData());

                                List<Transaction> currentList = transactionsLiveData.getValue();
                                if (currentList == null || refresh) {
                                    currentList = new ArrayList<>();
                                }
                                currentList.addAll(transactions);
                                transactionsLiveData.setValue(currentList);

                                currentPage++;
                                hasMore = transactions.size() >= 20;
                            } else {
                                errorLiveData.setValue(apiResponse.getMessage());
                            }
                        } else {
                            errorLiveData.setValue("Failed to load transactions");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<Map<String, Object>>> call,
                                          @NonNull Throwable t) {
                        loadingLiveData.setValue(false);
                        Log.e(TAG, "Load transactions failed", t);
                        errorLiveData.setValue("Network error: " + t.getMessage());
                    }
                });
    }

    /**
     * Load a specific transaction by ID.
     */
    public void loadTransactionDetails(Long transactionId) {
        loadingLiveData.setValue(true);
        api.getTransaction(transactionId).enqueue(new Callback<ApiResponse<Transaction>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Transaction>> call,
                                   @NonNull Response<ApiResponse<Transaction>> response) {
                loadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null &&
                        response.body().isSuccess()) {
                    selectedTransaction.setValue(response.body().getData());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Transaction>> call,
                                  @NonNull Throwable t) {
                loadingLiveData.setValue(false);
                errorLiveData.setValue("Network error");
            }
        });
    }

    @SuppressWarnings("unchecked")
    private List<Transaction> parseTransactions(Map<String, Object> data) {
        List<Transaction> transactions = new ArrayList<>();
        try {
            Object content = data.get("content");
            if (content instanceof List) {
                for (Object item : (List<?>) content) {
                    Transaction t = gson.fromJson(gson.toJson(item), Transaction.class);
                    transactions.add(t);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing transactions", e);
        }
        return transactions;
    }
}