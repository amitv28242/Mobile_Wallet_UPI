package com.mobilewallet.consumer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.card.MaterialCardView;
import com.mobilewallet.consumer.R;
import com.mobilewallet.consumer.adapters.NotificationAdapter;
import com.mobilewallet.consumer.adapters.QuickServiceAdapter;
import com.mobilewallet.consumer.adapters.TransactionAdapter;
import com.mobilewallet.consumer.models.Notification;
import com.mobilewallet.consumer.models.Transaction;
import com.mobilewallet.consumer.models.Wallet;
import com.mobilewallet.consumer.utils.Constants;
import com.mobilewallet.consumer.utils.PreferenceManager;
import com.mobilewallet.consumer.utils.TokenManager;
import com.mobilewallet.consumer.viewmodels.DashboardViewModel;
import com.mobilewallet.consumer.viewmodels.WalletViewModel;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * DashboardActivity — Modern fintech dashboard.
 * Features:
 *  - Personalized greeting (time-based)
 *  - Wallet card with balance + quick actions
 *  - Quick services horizontal list
 *  - Today's spent/received stats
 *  - Recent transactions
 *  - Notification badge
 *  - Shimmer loading states
 *  - Pull-to-refresh
 */
public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "DashboardActivity";

    // ============ ViewModels ============
    private DashboardViewModel dashboardViewModel;
    private WalletViewModel walletViewModel;

    // ============ Views ============
    private MaterialCardView cvAvatar, walletCard;
    private ImageView ivAvatar, btnToggleBalance, btnNotification;
    private TextView tvGreeting, tvUserName, tvBalance, tvWalletNumber, tvSeeAll;
    private TextView tvTodaySpent, tvTodayReceived, tvNoTransactions;
    private View notificationBadge, notificationDot;
    private FrameLayout btnNotificationContainer;
    private RecyclerView rvQuickServices, rvRecentTransactions;
    private LinearLayout layoutEmptyTransactions;
    private SwipeRefreshLayout swipeRefresh;
    private ShimmerFrameLayout shimmerBalance, shimmerTransactions;

    // ============ Adapters ============
    private QuickServiceAdapter quickServiceAdapter;
    private TransactionAdapter transactionAdapter;
    private NotificationAdapter notificationAdapter;

    // ============ State ============
    private boolean isBalanceVisible = true;
    private Wallet currentWallet;

    // ============================================================
    // LIFECYCLE
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initViews();
        setupRecyclerViews();
        setupListeners();
        initViewModels();
        observeViewModel();

        // Load initial data
        loadDashboardData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh balance when returning to dashboard
        if (walletViewModel != null) {
            walletViewModel.loadBalance();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (shimmerBalance != null) {
            shimmerBalance.stopShimmer();
        }
        if (shimmerTransactions != null) {
            shimmerTransactions.stopShimmer();
        }
    }

    // ============================================================
    // INITIALIZATION
    // ============================================================

    private void initViews() {
        // Top bar
        cvAvatar = findViewById(R.id.cvAvatar);
        ivAvatar = findViewById(R.id.ivAvatar);
        btnToggleBalance = findViewById(R.id.btnToggleBalance);
        btnNotification = findViewById(R.id.btnNotification);
        btnNotificationContainer = findViewById(R.id.btnNotificationContainer);
        notificationBadge = findViewById(R.id.notificationBadge);
        notificationDot = findViewById(R.id.notificationDot);

        // Greeting
        tvGreeting = findViewById(R.id.tvGreeting);
        tvUserName = findViewById(R.id.tvUserName);

        // Wallet card
        walletCard = findViewById(R.id.walletCard);
        tvBalance = findViewById(R.id.tvBalance);
        tvWalletNumber = findViewById(R.id.tvWalletNumber);

        // Today's stats
        tvTodaySpent = findViewById(R.id.tvTodaySpent);
        tvTodayReceived = findViewById(R.id.tvTodayReceived);

        // Sections
        tvSeeAll = findViewById(R.id.tvSeeAll);
        tvNoTransactions = findViewById(R.id.tvNoTransactions);

        // Lists
        rvQuickServices = findViewById(R.id.rvQuickServices);
        rvRecentTransactions = findViewById(R.id.rvRecentTransactions);
        layoutEmptyTransactions = findViewById(R.id.layoutEmptyTransactions);

        // Shimmer / refresh
        swipeRefresh = findViewById(R.id.swipeRefresh);
        shimmerBalance = findViewById(R.id.shimmerBalance);
        shimmerTransactions = findViewById(R.id.shimmerTransactions);

        // Load balance visibility preference
        PreferenceManager prefs = PreferenceManager.getInstance();
        if (prefs != null) {
            isBalanceVisible = prefs.isBalanceVisible();
        }

        updateGreeting();
        updateUserName();
        updateBalanceVisibilityIcon();
    }

    private void setupRecyclerViews() {
        // Quick Services — horizontal
        quickServiceAdapter = new QuickServiceAdapter(this::onServiceClick);
        rvQuickServices.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvQuickServices.setAdapter(quickServiceAdapter);
        quickServiceAdapter.setServices(getQuickServices());

        // Recent Transactions — vertical
        transactionAdapter = new TransactionAdapter(this::onTransactionClick);
        rvRecentTransactions.setLayoutManager(new LinearLayoutManager(this));
        rvRecentTransactions.setAdapter(transactionAdapter);
        rvRecentTransactions.setNestedScrollingEnabled(false);

        // Notifications — currently unused, kept for API compatibility
        notificationAdapter = new NotificationAdapter(this::onNotificationClick);
    }

    private void setupListeners() {
        // Toggle balance visibility
        btnToggleBalance.setOnClickListener(v -> toggleBalanceVisibility());

        // Notification button
        btnNotificationContainer.setOnClickListener(v -> openNotifications());
        btnNotification.setOnClickListener(v -> openNotifications());

        // Wallet card click
        walletCard.setOnClickListener(v -> openWallet());

        // Quick actions inside wallet card
        setupQuickActions();

        // See all transactions
        tvSeeAll.setOnClickListener(v -> openTransactions());

        // Pull to refresh
        swipeRefresh.setOnRefreshListener(this::loadDashboardData);
        swipeRefresh.setColorSchemeResources(R.color.primary, R.color.secondary);
    }

    private void setupQuickActions() {
        View addMoney = findViewById(R.id.actionAddMoney);
        if (addMoney != null) addMoney.setOnClickListener(v -> openAddMoney());

        View sendMoney = findViewById(R.id.actionSendMoney);
        if (sendMoney != null) sendMoney.setOnClickListener(v -> openSendMoney());

        View scanQR = findViewById(R.id.actionScanQR);
        if (scanQR != null) scanQR.setOnClickListener(v -> openQRScanner());

        View generateQR = findViewById(R.id.actionGenerateQR);
        if (generateQR != null) generateQR.setOnClickListener(v -> openQRGenerator());
    }

    private void initViewModels() {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        walletViewModel = new ViewModelProvider(this).get(WalletViewModel.class);
    }

    private void observeViewModel() {
        // --- Wallet ---
        dashboardViewModel.getWalletLiveData().observe(this, wallet -> {
            if (wallet != null) {
                currentWallet = wallet;
                updateWalletUI(wallet);
            }
        });

        // --- Recent transactions ---
        dashboardViewModel.getRecentTransactions().observe(this, transactions -> {
            stopShimmer();
            updateTransactionsUI(transactions);
        });

        // --- Unread notification count ---
        dashboardViewModel.getUnreadNotifications().observe(this, this::updateNotificationBadge);

        // --- Loading state ---
        dashboardViewModel.getLoadingLiveData().observe(this, isLoading -> {
            if (isLoading != null && !isLoading) {
                swipeRefresh.setRefreshing(false);
            }
        });

        // --- Errors ---
        dashboardViewModel.getErrorLiveData().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ============================================================
    // DATA LOADING
    // ============================================================

    private void loadDashboardData() {
        startShimmer();

        dashboardViewModel.loadDashboard();

        if (walletViewModel != null) {
            walletViewModel.loadBalance();
        }
    }

    private void startShimmer() {
        if (shimmerBalance != null) {
            shimmerBalance.setVisibility(View.VISIBLE);
            shimmerBalance.startShimmer();
        }
        if (shimmerTransactions != null) {
            shimmerTransactions.setVisibility(View.VISIBLE);
            shimmerTransactions.startShimmer();
        }
    }

    private void stopShimmer() {
        if (shimmerBalance != null) {
            shimmerBalance.stopShimmer();
            shimmerBalance.setVisibility(View.GONE);
        }
        if (shimmerTransactions != null) {
            shimmerTransactions.stopShimmer();
            shimmerTransactions.setVisibility(View.GONE);
        }
    }

    // ============================================================
    // UI UPDATES
    // ============================================================

    private void updateGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting;
        if (hour < 12) {
            greeting = getString(R.string.greeting_morning);
        } else if (hour < 17) {
            greeting = getString(R.string.greeting_afternoon);
        } else if (hour < 21) {
            greeting = getString(R.string.greeting_evening);
        } else {
            greeting = getString(R.string.greeting_night);
        }
        tvGreeting.setText(greeting);
    }

    private void updateUserName() {
        String name = TokenManager.getInstance().getUserName();
        if (name == null || name.isEmpty()) {
            name = "User";
        }
        tvUserName.setText(name);
    }

    private void updateWalletUI(Wallet wallet) {
        if (wallet == null) return;

        // Wallet number
        if (wallet.getWalletNumber() != null) {
            tvWalletNumber.setText(wallet.getWalletNumber());
        }

        // Balance
        updateBalanceDisplay(wallet.getBalance());

        // Today's stats (placeholder — can be extended with dedicated API)
        if (tvTodaySpent != null) {
            tvTodaySpent.setText("₹0.00");
        }
        if (tvTodayReceived != null) {
            tvTodayReceived.setText("₹0.00");
        }
    }

    private void updateBalanceDisplay(BigDecimal balance) {
        if (balance == null) balance = BigDecimal.ZERO;

        if (isBalanceVisible) {
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            String formatted = formatter.format(balance);
            tvBalance.setText(formatted);
            tvBalance.setAlpha(1f);
        } else {
            tvBalance.setText("₹ ••••••");
            tvBalance.setAlpha(0.8f);
        }
    }

    private void updateTransactionsUI(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            layoutEmptyTransactions.setVisibility(View.VISIBLE);
            rvRecentTransactions.setVisibility(View.GONE);
            tvSeeAll.setVisibility(View.GONE);
            if (tvNoTransactions != null) {
                tvNoTransactions.setText(getString(R.string.no_transactions));
            }
        } else {
            layoutEmptyTransactions.setVisibility(View.GONE);
            rvRecentTransactions.setVisibility(View.VISIBLE);
            tvSeeAll.setVisibility(View.VISIBLE);
            transactionAdapter.setTransactions(transactions);
        }
    }

    private void updateNotificationBadge(Long unreadCount) {
        if (unreadCount == null || unreadCount == 0) {
            if (notificationBadge != null) notificationBadge.setVisibility(View.GONE);
            if (notificationDot != null) notificationDot.setVisibility(View.GONE);
        } else {
            if (notificationBadge != null) {
                if (notificationBadge instanceof TextView) {
                    ((TextView) notificationBadge).setText(
                            unreadCount > 99 ? "99+" : String.valueOf(unreadCount));
                }
                notificationBadge.setVisibility(View.VISIBLE);
            }
            if (notificationDot != null) {
                notificationDot.setVisibility(View.VISIBLE);
            }
        }
    }

    private void updateBalanceVisibilityIcon() {
        btnToggleBalance.setImageResource(isBalanceVisible
                ? R.drawable.ic_visibility
                : R.drawable.ic_visibility_off);
    }

    // ============================================================
    // USER ACTIONS
    // ============================================================

    private void toggleBalanceVisibility() {
        isBalanceVisible = !isBalanceVisible;

        // Persist
        PreferenceManager prefs = PreferenceManager.getInstance();
        if (prefs != null) {
            prefs.setBalanceVisible(isBalanceVisible);
        }

        updateBalanceVisibilityIcon();

        if (currentWallet != null) {
            updateBalanceDisplay(currentWallet.getBalance());
        } else {
            tvBalance.setText(isBalanceVisible ? "₹0.00" : "₹ ••••••");
        }

        tvBalance.animate()
                .alpha(isBalanceVisible ? 1f : 0.8f)
                .setDuration(200)
                .start();
    }

    private void onServiceClick(QuickServiceAdapter.Service service) {
        if (service == null) return;

        switch (service.name()) {
            case "Mobile":
            case "DTH":
            case "Electricity":
            case "Water":
            case "Gas":
            case "Bills": {
                Intent intent = new Intent(this, RechargeActivity.class);
                intent.putExtra("service_type", service.name());
                startActivity(intent);
                break;
            }
            case "Cards":
                openCards();
                break;
            default:
                Toast.makeText(this, service.name() + " coming soon",
                        Toast.LENGTH_SHORT).show();
        }
    }

    private void onTransactionClick(Transaction transaction) {
        if (transaction == null) return;
        Intent intent = new Intent(this, TransactionsActivity.class);
        intent.putExtra(Constants.EXTRA_TRANSACTION, transaction);
        startActivity(intent);
    }

    private void onNotificationClick(Notification notification) {
        if (notification == null) return;
        openNotifications();
    }

    // ============================================================
    // NAVIGATION
    // ============================================================

    private void openWallet() {
        startActivity(new Intent(this, WalletActivity.class));
    }

    private void openAddMoney() {
        Toast.makeText(this, "Add Money", Toast.LENGTH_SHORT).show();
    }

    private void openSendMoney() {
        startActivity(new Intent(this, QRScannerActivity.class));
    }

    private void openQRScanner() {
        startActivity(new Intent(this, QRScannerActivity.class));
    }

    private void openQRGenerator() {
        startActivity(new Intent(this, QRGeneratorActivity.class));
    }

    private void openTransactions() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("open_tab", "transactions");
        startActivity(intent);
    }

    private void openNotifications() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("open_tab", "notifications");
        startActivity(intent);
    }

    private void openCards() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("open_tab", "cards");
        startActivity(intent);
    }

    // ============================================================
    // QUICK SERVICES
    // ============================================================

    private List<QuickServiceAdapter.Service> getQuickServices() {
        List<QuickServiceAdapter.Service> services = new ArrayList<>();

        services.add(new QuickServiceAdapter.Service(
                "Mobile",
                R.drawable.ic_mobile,
                R.drawable.bg_circle_primary_light,
                R.color.primary));

        services.add(new QuickServiceAdapter.Service(
                "DTH",
                R.drawable.ic_tv,
                R.drawable.bg_circle_error_light,
                R.color.error));

        services.add(new QuickServiceAdapter.Service(
                "Electricity",
                R.drawable.ic_bolt,
                R.drawable.bg_circle_warning_light,
                R.color.warning));

        services.add(new QuickServiceAdapter.Service(
                "Water",
                R.drawable.ic_water,
                R.drawable.bg_circle_info_light,
                R.color.info));

        services.add(new QuickServiceAdapter.Service(
                "Cards",
                R.drawable.ic_card,
                R.drawable.bg_circle_success_light,
                R.color.success));

        services.add(new QuickServiceAdapter.Service(
                "Bills",
                R.drawable.ic_bill,
                R.drawable.bg_circle_primary_light,
                R.color.primary));

        return services;
    }
}