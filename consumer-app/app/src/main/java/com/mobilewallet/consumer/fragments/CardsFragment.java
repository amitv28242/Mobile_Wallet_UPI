package com.mobilewallet.consumer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.mobilewallet.consumer.R;
import com.mobilewallet.consumer.adapters.CardAdapter;
import com.mobilewallet.consumer.models.Card;

import java.util.ArrayList;
import java.util.List;

public class CardsFragment extends Fragment {

    private RecyclerView rvCards;
    private LinearLayout layoutEmpty;
    private MaterialButton btnAddCard;
    private CardAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cards, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvCards = view.findViewById(R.id.rvCards);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        btnAddCard = view.findViewById(R.id.btnAddCard);

        setupRecyclerView();
        setupListeners();

        // Load sample cards (replace with API call)
        loadSampleCards();
    }

    private void setupRecyclerView() {
        adapter = new CardAdapter(new CardAdapter.OnCardClickListener() {
            @Override
            public void onCardClick(Card card) {
                // Show card details
            }

            @Override
            public void onCardLongClick(Card card) {
                // Show options: set default, delete
            }
        });
        rvCards.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCards.setAdapter(adapter);
    }

    private void setupListeners() {
        btnAddCard.setOnClickListener(v -> {
            // Show add card dialog
        });
    }

    private void loadSampleCards() {
        // Replace with actual API call
        List<Card> cards = new ArrayList<>();
        if (cards.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvCards.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvCards.setVisibility(View.VISIBLE);
            adapter.setCards(cards);
        }
    }
}