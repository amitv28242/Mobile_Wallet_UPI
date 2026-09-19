package com.mobilewallet.consumer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobilewallet.consumer.R;
import com.mobilewallet.consumer.models.Card;

import java.util.ArrayList;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private final List<Card> cards = new ArrayList<>();
    private final OnCardClickListener listener;

    public interface OnCardClickListener {
        void onCardClick(Card card);
        void onCardLongClick(Card card);
    }

    public CardAdapter(OnCardClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(cards.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public void setCards(List<Card> newCards) {
        cards.clear();
        if (newCards != null) cards.addAll(newCards);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCardNumber, tvCardholderName, tvExpiry, tvIssuer, ivDefaultBadge;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCardNumber = itemView.findViewById(R.id.tvCardNumber);
            tvCardholderName = itemView.findViewById(R.id.tvCardholderName);
            tvExpiry = itemView.findViewById(R.id.tvExpiry);
            tvIssuer = itemView.findViewById(R.id.tvIssuer);
            ivDefaultBadge = itemView.findViewById(R.id.ivDefaultBadge);
        }

        void bind(Card card, OnCardClickListener listener) {
            tvCardNumber.setText(card.getCardNumberMasked());
            tvCardholderName.setText(card.getCardholderName());
            tvExpiry.setText(card.getFormattedExpiry());
            tvIssuer.setText(card.getIssuer());

            if (Boolean.TRUE.equals(card.getIsDefault())) {
                ivDefaultBadge.setVisibility(View.VISIBLE);
            } else {
                ivDefaultBadge.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onCardClick(card);
            });

            itemView.setOnLongClickListener(v -> {
                if (listener != null) listener.onCardLongClick(card);
                return true;
            });
        }
    }
}