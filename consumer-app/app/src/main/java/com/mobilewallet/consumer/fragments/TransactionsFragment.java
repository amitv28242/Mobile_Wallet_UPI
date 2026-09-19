package com.mobilewallet.consumer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mobilewallet.consumer.R;
import com.mobilewallet.consumer.adapters.TransactionAdapter;
import com.mobilewallet.consumer.viewmodels.TransactionViewModel;

public class TransactionsFragment extends Fragment {

    private TransactionViewModel viewModel;
    private TransactionAdapter adapter;
    private RecyclerView rvTransactions;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayout layoutEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transactions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvTransactions = view.findViewById(R.id.rvTransactions);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);

        setupRecyclerView();
        setupSwipeRefresh();

        viewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        observeViewModel();

        viewModel.loadTransactions(true);
    }

    private void setupRecyclerView() {
        adapter = new TransactionAdapter(transaction -> {
            // Handle click
        });
        rvTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTransactions.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener(() -> viewModel.loadTransactions(true));
        swipeRefresh.setColorSchemeResources(R.color.primary);
    }

    private void observeViewModel() {
        viewModel.getTransactionsLiveData().observe(getViewLifecycleOwner(), transactions -> {
            swipeRefresh.setRefreshing(false);
            if (transactions == null || transactions.isEmpty()) {
                layoutEmpty.setVisibility(View.VISIBLE);
                rvTransactions.setVisibility(View.GONE);
            } else {
                layoutEmpty.setVisibility(View.GONE);
                rvTransactions.setVisibility(View.VISIBLE);
                adapter.setTransactions(transactions);
            }
        });

        viewModel.getLoadingLiveData().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null && !isLoading) {
                swipeRefresh.setRefreshing(false);
            }
        });
    }
}