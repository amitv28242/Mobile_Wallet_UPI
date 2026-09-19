package com.mobilewallet.consumer.api;

import com.mobilewallet.consumer.models.AddMoneyRequest;
import com.mobilewallet.consumer.models.AuthResponse;
import com.mobilewallet.consumer.models.BankAccount;
import com.mobilewallet.consumer.models.BillPaymentRequest;
import com.mobilewallet.consumer.models.Card;
import com.mobilewallet.consumer.models.LoginRequest;
import com.mobilewallet.consumer.models.OTPRequest;
import com.mobilewallet.consumer.models.Payment;
import com.mobilewallet.consumer.models.QR;
import com.mobilewallet.consumer.models.QRScanRequest;
import com.mobilewallet.consumer.models.RechargeRequest;
import com.mobilewallet.consumer.models.RegisterRequest;
import com.mobilewallet.consumer.models.RegisterResponse;
import com.mobilewallet.consumer.models.Transaction;
import com.mobilewallet.consumer.models.User;
import com.mobilewallet.consumer.models.Wallet;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    // ==================== AUTHENTICATION ====================

    @POST("auth/register")
    Call<ApiResponse<RegisterResponse>> register(@Body RegisterRequest request);
    @POST("auth/login")
    Call<ApiResponse<AuthResponse>> login(@Body LoginRequest request);

    @POST("auth/refresh")
    Call<ApiResponse<AuthResponse>> refreshToken(@Body Map<String, String> body);

    @POST("auth/logout")
    Call<ApiResponse<Map<String, String>>> logout(
            @Header("X-Refresh-Token") String refreshToken
    );

    @POST("auth/otp/generate")
    Call<ApiResponse<Map<String, Object>>> generateOTP(@Body Map<String, String> body);

    @POST("auth/otp/verify")
    Call<ApiResponse<Map<String, Boolean>>> verifyOTP(@Body OTPRequest request);

    @POST("auth/otp/resend")
    Call<ApiResponse<Map<String, Object>>> resendOTP(@Body Map<String, String> body);

    @POST("auth/guest")
    Call<ApiResponse<AuthResponse>> guestLogin(@Body Map<String, String> body);

    // ==================== WALLET ====================

    @GET("wallet")
    Call<ApiResponse<Wallet>> getWallet();

    @GET("wallet/balance")
    Call<ApiResponse<Wallet>> getBalance();

    @POST("wallet/add-money")
    Call<ApiResponse<Map<String, Object>>> addMoney(@Body AddMoneyRequest request);

    // ==================== PAYMENTS ====================

    @POST("payments")
    Call<ApiResponse<Payment>> initiatePayment(@Body Map<String, Object> body);

    @GET("payments/{paymentId}")
    Call<ApiResponse<Payment>> getPayment(@Path("paymentId") Long paymentId);

    @GET("payments/status/{referenceId}")
    Call<ApiResponse<Payment>> getPaymentStatus(@Path("referenceId") String referenceId);

    @GET("payments/history")
    Call<ApiResponse<Map<String, Object>>> getPaymentHistory(
            @Query("page") int page,
            @Query("size") int size
    );

    // ==================== QR ====================

    @POST("qr/generate")
    Call<ApiResponse<QR>> generateQR(@Body Map<String, Object> body);

    @POST("qr/scan")
    Call<ApiResponse<Payment>> scanQR(@Body QRScanRequest request);

    @POST("qr/validate")
    Call<ApiResponse<Map<String, Boolean>>> validateQR(@Body Map<String, String> body);

    // ==================== TRANSACTIONS ====================

    @GET("wallet/transactions")
    Call<ApiResponse<Map<String, Object>>> getTransactions(
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("wallet/transactions/{transactionId}")
    Call<ApiResponse<Transaction>> getTransaction(@Path("transactionId") Long transactionId);

    // ==================== CARDS ====================

    @GET("cards")
    Call<ApiResponse<List<Card>>> getCards();

    @POST("cards")
    Call<ApiResponse<Card>> addCard(@Body Map<String, String> body);

    @PUT("cards/{cardId}")
    Call<ApiResponse<Card>> updateCard(
            @Path("cardId") Long cardId,
            @Body Map<String, String> body
    );

    @DELETE("cards/{cardId}")
    Call<ApiResponse<Map<String, String>>> deleteCard(@Path("cardId") Long cardId);

    @PUT("cards/{cardId}/default")
    Call<ApiResponse<Map<String, String>>> setDefaultCard(@Path("cardId") Long cardId);

    // ==================== BANK ACCOUNTS ====================

    @GET("bank-accounts")
    Call<ApiResponse<List<BankAccount>>> getBankAccounts();

    @POST("bank-accounts")
    Call<ApiResponse<BankAccount>> addBankAccount(@Body Map<String, String> body);

    @PUT("bank-accounts/{accountId}")
    Call<ApiResponse<BankAccount>> updateBankAccount(
            @Path("accountId") Long accountId,
            @Body Map<String, String> body
    );

    @DELETE("bank-accounts/{accountId}")
    Call<ApiResponse<Map<String, String>>> deleteBankAccount(@Path("accountId") Long accountId);

    @PUT("bank-accounts/{accountId}/default")
    Call<ApiResponse<Map<String, String>>> setDefaultBankAccount(@Path("accountId") Long accountId);

    @POST("bank-accounts/{accountId}/transfer")
    Call<ApiResponse<Map<String, String>>> transferToBank(
            @Path("accountId") Long accountId,
            @Body Map<String, Object> body
    );

    // ==================== NOTIFICATIONS ====================

    @GET("notifications")
    Call<ApiResponse<Map<String, Object>>> getNotifications(
            @Query("page") int page,
            @Query("size") int size,
            @Query("unreadOnly") boolean unreadOnly
    );

    @GET("notifications/count/unread")
    Call<ApiResponse<Map<String, Long>>> getUnreadCount();

    @PUT("notifications/{notificationId}/read")
    Call<ApiResponse<Map<String, String>>> markNotificationRead(
            @Path("notificationId") Long notificationId
    );

    @PUT("notifications/read-all")
    Call<ApiResponse<Map<String, String>>> markAllNotificationsRead();

    @DELETE("notifications/{notificationId}")
    Call<ApiResponse<Map<String, String>>> deleteNotification(
            @Path("notificationId") Long notificationId
    );

    // ==================== RECHARGE & BILLS ====================

    @POST("recharge/mobile")
    Call<ApiResponse<Map<String, Object>>> mobileRecharge(@Body RechargeRequest request);

    @POST("recharge/dth")
    Call<ApiResponse<Map<String, Object>>> dthRecharge(@Body RechargeRequest request);

    @POST("recharge/bill")
    Call<ApiResponse<Map<String, Object>>> payBill(@Body BillPaymentRequest request);

    @GET("recharge/history")
    Call<ApiResponse<Map<String, Object>>> getRechargeHistory(
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("recharge/bills/history")
    Call<ApiResponse<Map<String, Object>>> getBillHistory(
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("recharge/operators")
    Call<ApiResponse<Map<String, List<String>>>> getOperators();

    @GET("recharge/bill-types")
    Call<ApiResponse<List<String>>> getBillTypes();

    // ==================== USER PROFILE ====================

    @GET("auth/me")
    Call<ApiResponse<User>> getCurrentUser();

    @PUT("auth/profile")
    Call<ApiResponse<User>> updateProfile(@Body Map<String, String> body);
}