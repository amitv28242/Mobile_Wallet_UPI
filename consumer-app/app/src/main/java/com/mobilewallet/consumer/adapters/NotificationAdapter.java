package com.mobilewallet.consumer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mobilewallet.consumer.R;
import com.mobilewallet.consumer.models.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final List<Notification> notifications = new ArrayList<>();
    private final OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification);
    }

    public NotificationAdapter(OnNotificationClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(notifications.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public void setNotifications(List<Notification> newNotifications) {
        notifications.clear();
        if (newNotifications != null) {
            notifications.addAll(newNotifications);
        }
        notifyDataSetChanged();
    }

    public void addNotifications(List<Notification> newNotifications) {
        if (newNotifications != null) {
            int startPos = notifications.size();
            notifications.addAll(newNotifications);
            notifyItemRangeInserted(startPos, newNotifications.size());
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivIcon;
        private final TextView tvTitle, tvMessage, tvTime;
        private final View unreadIndicator;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivNotificationIcon);
            tvTitle = itemView.findViewById(R.id.tvNotificationTitle);
            tvMessage = itemView.findViewById(R.id.tvNotificationMessage);
            tvTime = itemView.findViewById(R.id.tvNotificationTime);
            unreadIndicator = itemView.findViewById(R.id.unreadIndicator);
        }

        void bind(Notification notification, OnNotificationClickListener listener) {
            tvTitle.setText(notification.getTitle());
            tvMessage.setText(notification.getMessage());
            tvTime.setText(formatTime(notification.getCreatedAt()));

            // Set icon based on type
            int iconRes = R.drawable.ic_notification;
            int colorRes = R.color.primary;

            if (notification.getType() != null) {
                switch (notification.getType()) {
                    case "PAYMENT_SENT":
                    case "PAYMENT_RECEIVED":
                        iconRes = R.drawable.ic_payment;
                        colorRes = R.color.success;
                        break;
                    case "SECURITY":
                        iconRes = R.drawable.ic_security;
                        colorRes = R.color.error;
                        break;
                    case "OTP":
                        iconRes = R.drawable.ic_lock;
                        colorRes = R.color.warning;
                        break;
                    case "WALLET":
                        iconRes = R.drawable.ic_wallet;
                        colorRes = R.color.primary;
                        break;
                }
            }

            ivIcon.setImageResource(iconRes);
            ivIcon.setColorFilter(ContextCompat.getColor(itemView.getContext(), colorRes));

            // Unread indicator
            unreadIndicator.setVisibility(
                    Boolean.TRUE.equals(notification.getIsRead()) ? View.GONE : View.VISIBLE
            );

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onNotificationClick(notification);
                }
            });
        }

        private String formatTime(String isoTime) {
            if (isoTime == null) return "";
            try {
                return isoTime.substring(0, Math.min(16, isoTime.length())).replace("T", " ");
            } catch (Exception e) {
                return isoTime;
            }
        }
    }
}