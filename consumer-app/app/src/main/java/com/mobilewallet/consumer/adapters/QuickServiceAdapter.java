package com.mobilewallet.consumer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobilewallet.consumer.R;

import java.util.ArrayList;
import java.util.List;

public class QuickServiceAdapter extends RecyclerView.Adapter<QuickServiceAdapter.ViewHolder> {

    public record Service(String name, int iconRes, int bgColorRes, int iconTintRes) {
    }

    private final List<Service> services = new ArrayList<>();
    private final OnServiceClickListener listener;

    public interface OnServiceClickListener {
        void onServiceClick(Service service);
    }

    public QuickServiceAdapter(OnServiceClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quick_service, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(services.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public void setServices(List<Service> newServices) {
        services.clear();
        if (newServices != null) {
            services.addAll(newServices);
        }
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final FrameLayout iconContainer;
        private final ImageView ivIcon;
        private final TextView tvName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconContainer = itemView.findViewById(R.id.iconContainer);
            ivIcon = itemView.findViewById(R.id.ivServiceIcon);
            tvName = itemView.findViewById(R.id.tvServiceName);
        }

        void bind(Service service, OnServiceClickListener listener) {
            ivIcon.setImageResource(service.iconRes);
            tvName.setText(service.name);
            iconContainer.setBackgroundResource(service.bgColorRes);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onServiceClick(service);
                }
            });
        }
    }
}