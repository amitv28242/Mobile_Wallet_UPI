package com.mobilewallet.consumer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mobilewallet.consumer.R;
import com.mobilewallet.consumer.models.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private final List<Transaction> transactions = new ArrayList<>();
    private final OnTransactionClickListener listener;

    public interface OnTransactionClickListener {
        void onTransactionClick(Transaction transaction);
    }

    public TransactionAdapter(OnTransactionClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.bind(transaction, listener);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void setTransactions(List<Transaction> newTransactions) {
        transactions.clear();
        if (newTransactions != null) {
            transactions.addAll(newTransactions);
        }
        notifyDataSetChanged();
    }

    public void addTransactions(List<Transaction> newTransactions) {
        if (newTransactions != null) {
            int startPos = transactions.size();
            transactions.addAll(newTransactions);
            notifyItemRangeInserted(startPos, newTransactions.size());
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final FrameLayout flIcon;
        private final ImageView ivIcon;
        private final TextView tvTitle, tvSubtitle, tvAmount, tvDate;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            flIcon = itemView.findViewById(R.id.flIcon);
            ivIcon = itemView.findViewById(R.id.ivTransactionIcon);
            tvTitle = itemView.findViewById(R.id.tvTransactionTitle);
            tvSubtitle = itemView.findViewById(R.id.tvTransactionSubtitle);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDate = itemView.findViewById(R.id.tvDate);
        }

        void bind(Transaction transaction, OnTransactionClickListener listener) {
            boolean isCredit = transaction.isCredit();

            // Set icon background
            flIcon.setBackgroundResource(isCredit ?
                    R.drawable.bg_circle_success_light :
                    R.drawable.bg_circle_error_light);

            // Set icon
            ivIcon.setImageResource(isCredit ?
                    R.drawable.ic_arrow_down : R.drawable.ic_arrow_up);
            ivIcon.setColorFilter(ContextCompat.getColor(itemView.getContext(),
                    isCredit ? R.color.success : R.color.error));

            // Set title
            String title = transaction.getDescription();
            if (title == null || title.isEmpty()) {
                if (transaction.getPayment() != null) {
                    if (isCredit && transaction.getPayment().getPayer() != null) {
                        title = "From " + transaction.getPayment().getPayer().getFullName();
                    } else if (!isCredit && transaction.getPayment().getPayee() != null) {
                        title = "To " + transaction.getPayment().getPayee().getFullName();
                    }
                }
                if (title == null || title.isEmpty()) {
                    title = isCredit ? "Money Received" : "Money Sent";
                }
            }
            tvTitle.setText(title);

            // Set subtitle
            tvSubtitle.setText(transaction.getReference());

            // Set amount
            tvAmount.setText(transaction.getFormattedAmount());
            tvAmount.setTextColor(ContextCompat.getColor(itemView.getContext(),
                    isCredit ? R.color.success : R.color.error));

            // Set date (simplified)
            tvDate.setText(formatDate(transaction.getCreatedAt()));

            // Click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTransactionClick(transaction);
                }
            });
        }

        private String formatDate(String isoDate) {
            if (isoDate == null) return "";
            try {
                // Simplified: return part of date string
                return isoDate.substring(0, Math.min(10, isoDate.length()));
            } catch (Exception e) {
                return isoDate;
            }
        }
    }
}