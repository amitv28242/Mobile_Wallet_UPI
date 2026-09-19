package com.mobilewallet.consumer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobilewallet.consumer.R;
import com.mobilewallet.consumer.adapters.QuickServiceAdapter;

import java.util.ArrayList;
import java.util.List;

public class BillsFragment extends Fragment {

    private RecyclerView rvBillTypes;
    private QuickServiceAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bills, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvBillTypes = view.findViewById(R.id.rvBillTypes);
        setupRecyclerView();
        loadBillTypes();
    }

    private void setupRecyclerView() {
        adapter = new QuickServiceAdapter(service -> {
            // Navigate to bill payment
        });
        rvBillTypes.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rvBillTypes.setAdapter(adapter);
    }

    private void loadBillTypes() {
        List<QuickServiceAdapter.Service> services = new ArrayList<>();
        services.add(new QuickServiceAdapter.Service("Electricity",
                R.drawable.ic_bolt, R.drawable.bg_circle_warning_light, R.color.warning));
        services.add(new QuickServiceAdapter.Service("Water",
                R.drawable.ic_water, R.drawable.bg_circle_info_light, R.color.info));
        services.add(new QuickServiceAdapter.Service("Gas",
                R.drawable.ic_fire, R.drawable.bg_circle_error_light, R.color.error));
        services.add(new QuickServiceAdapter.Service("Broadband",
                R.drawable.ic_wifi, R.drawable.bg_circle_primary_light, R.color.primary));
        services.add(new QuickServiceAdapter.Service("Insurance",
                R.drawable.ic_shield, R.drawable.bg_circle_success_light, R.color.success));
        services.add(new QuickServiceAdapter.Service("Municipal",
                R.drawable.ic_city, R.drawable.bg_circle_primary_light, R.color.primary));
        adapter.setServices(services);
    }
}