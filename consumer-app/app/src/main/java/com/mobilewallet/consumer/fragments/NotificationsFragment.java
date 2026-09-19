package com.mobilewallet.consumer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mobilewallet.consumer.R;
import com.mobilewallet.consumer.adapters.NotificationAdapter;

public class NotificationsFragment extends Fragment {

    private RecyclerView rvNotifications;
    private LinearLayout layoutEmpty;
    private SwipeRefreshLayout swipeRefresh;
    private TextView tvMarkAllRead;
    private NotificationAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvNotifications = view.findViewById(R.id.rvNotifications);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        tvMarkAllRead = view.findViewById(R.id.tvMarkAllRead);

        setupRecyclerView();
        setupSwipeRefresh();
        setupListeners();
    }

    private void setupRecyclerView() {
        adapter = new NotificationAdapter(notification -> {
            // Handle notification click
        });
        rvNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        rvNotifications.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener(() -> {
            swipeRefresh.setRefreshing(false);
        });
        swipeRefresh.setColorSchemeResources(R.color.primary);
    }

    private void setupListeners() {
        tvMarkAllRead.setOnClickListener(v -> {
            // Mark all as read
        });
    }

    private void showEmpty(boolean show) {
        layoutEmpty.setVisibility(show ? View.VISIBLE : View.GONE);
        rvNotifications.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}