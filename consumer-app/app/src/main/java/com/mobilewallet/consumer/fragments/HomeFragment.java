package com.mobilewallet.consumer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.mobilewallet.consumer.R;
import com.mobilewallet.consumer.activities.QRGeneratorActivity;
import com.mobilewallet.consumer.activities.QRScannerActivity;
import com.mobilewallet.consumer.activities.RechargeActivity;
import com.mobilewallet.consumer.activities.TransactionsActivity;
import com.mobilewallet.consumer.adapters.QuickServiceAdapter;
import com.mobilewallet.consumer.adapters.TransactionAdapter;
import com.mobilewallet.consumer.models.Wallet;
import com.mobilewallet.consumer.utils.PreferenceManager;
import com.mobilewallet.consumer.utils.TokenManager;
import com.mobilewallet.consumer.viewmodels.DashboardViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private DashboardViewModel viewModel;
    private QuickServiceAdapter quickServiceAdapter;
    private TransactionAdapter transactionAdapter;

    private TextView tvGreeting, tvUserName, tvBalance, tvWalletNumber;
    private ImageView ivAvatar, btnToggleBalance;
    private MaterialCardView cvAvatar;
    private RecyclerView rvQuickServices, rvTransactions;
    private LinearLayout layoutEmptyTransactions;
    private View notificationBadge;
    private TextView tvSeeAll;

    private boolean isBalanceVisible = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerViews();
        setupListeners();
        initViewModel();
        loadData();
    }

    private void initViews(View view) {
        tvGreeting = view.findViewById(R.id.tvGreeting);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvBalance = view.findViewById(R.id.tvBalance);
        tvWalletNumber = view.findViewById(R.id.tvWalletNumber);
        ivAvatar = view.findViewById(R.id.ivAvatar);
        btnToggleBalance = view.findViewById(R.id.btnToggleBalance);
        cvAvatar = view.findViewById(R.id.cvAvatar);
        rvQuickServices = view.findViewById(R.id.rvQuickServices);
        rvTransactions = view.findViewById(R.id.rvTransactions);
        layoutEmptyTransactions = view.findViewById(R.id.layoutEmptyTransactions);
        notificationBadge = view.findViewById(R.id.notificationBadge);
        tvSeeAll = view.findViewById(R.id.tvSeeAll);

        // Set greeting based on time
        updateGreeting();

        // Set username
        String userName = TokenManager.getInstance().getUserName();
        if (userName != null) {
            tvUserName.setText(userName);
        }
    }

    private void setupRecyclerViews() {
        // Quick services
        quickServiceAdapter = new QuickServiceAdapter(service -> {
            handleServiceClick(service.name());
        });
        rvQuickServices.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvQuickServices.setAdapter(quickServiceAdapter);
        quickServiceAdapter.setServices(getQuickServices());

        // Transactions
        transactionAdapter = new TransactionAdapter(transaction -> {
            // Navigate to transaction details
        });
        rvTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTransactions.setAdapter(transactionAdapter);
        rvTransactions.setNestedScrollingEnabled(false);
    }

    private void setupListeners() {
        View.OnClickListener addMoneyClick = v -> {
            // Navigate to add money
        };

        View addMoneyAction = getView().findViewById(R.id.actionAddMoney);
        if (addMoneyAction != null) addMoneyAction.setOnClickListener(addMoneyClick);

        View sendMoneyAction = getView().findViewById(R.id.actionSendMoney);
        if (sendMoneyAction != null) sendMoneyAction.setOnClickListener(v -> {
            // Navigate to send money
        });

        View scanQRAction = getView().findViewById(R.id.actionScanQR);
        if (scanQRAction != null) scanQRAction.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), QRScannerActivity.class));
        });

        View generateQRAction = getView().findViewById(R.id.actionGenerateQR);
        if (generateQRAction != null) generateQRAction.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), QRGeneratorActivity.class));
        });

        btnToggleBalance.setOnClickListener(v -> toggleBalanceVisibility());

        tvSeeAll.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), TransactionsActivity.class));
        });
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        viewModel.getWalletLiveData().observe(getViewLifecycleOwner(), wallet -> {
            if (wallet != null) {
                updateWalletUI(wallet);
            }
        });
    }

    private void loadData() {
        viewModel.loadDashboard();
    }

    private void updateWalletUI(Wallet wallet) {
        if (wallet == null) return;

        tvWalletNumber.setText(wallet.getWalletNumber());

        if (isBalanceVisible) {
            tvBalance.setText(wallet.getFormattedBalance());
        } else {
            tvBalance.setText("₹••••••");
        }
    }

    private void toggleBalanceVisibility() {
        isBalanceVisible = !isBalanceVisible;
        PreferenceManager.getInstance().setBalanceVisible(isBalanceVisible);

        if (isBalanceVisible) {
            tvBalance.setText("₹0.00");
            btnToggleBalance.setImageResource(R.drawable.ic_visibility);
        } else {
            tvBalance.setText("₹••••••");
            btnToggleBalance.setImageResource(R.drawable.ic_visibility_off);
        }
    }

    private void updateGreeting() {
        int hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
        String greeting;
        if (hour < 12) {
            greeting = "Good Morning,";
        } else if (hour < 17) {
            greeting = "Good Afternoon,";
        } else {
            greeting = "Good Evening,";
        }
        tvGreeting.setText(greeting);
    }

    private List<QuickServiceAdapter.Service> getQuickServices() {
        List<QuickServiceAdapter.Service> services = new ArrayList<>();
        services.add(new QuickServiceAdapter.Service("Mobile",
                R.drawable.ic_mobile, R.drawable.bg_circle_primary_light, R.color.primary));
        services.add(new QuickServiceAdapter.Service("DTH",
                R.drawable.ic_tv, R.drawable.bg_circle_error_light, R.color.error));
        services.add(new QuickServiceAdapter.Service("Electricity",
                R.drawable.ic_bolt, R.drawable.bg_circle_warning_light, R.color.warning));
        services.add(new QuickServiceAdapter.Service("Water",
                R.drawable.ic_water, R.drawable.bg_circle_info_light, R.color.info));
        services.add(new QuickServiceAdapter.Service("Cards",
                R.drawable.ic_card, R.drawable.bg_circle_success_light, R.color.success));
        services.add(new QuickServiceAdapter.Service("Bills",
                R.drawable.ic_bill, R.drawable.bg_circle_primary_light, R.color.primary));
        return services;
    }

    private void handleServiceClick(String serviceName) {
        switch (serviceName) {
            case "Mobile":
            case "DTH":
            case "Electricity":
            case "Water":
            case "Bills":
                Intent intent = new Intent(getContext(), RechargeActivity.class);
                intent.putExtra("service_type", serviceName);
                startActivity(intent);
                break;
            case "Cards":
                // Navigate to cards
                break;
        }
    }
}