// FILE: src/main/java/com/mobilewallet/service/CardService.java
package com.mobilewallet.service;

import com.mobilewallet.dto.card.CardRequest;
import com.mobilewallet.dto.card.CardResponse;

import java.util.List;

public interface CardService {

    CardResponse addCard(Long userId, CardRequest request);

    List<CardResponse> getCards(Long userId);

    CardResponse getCard(Long userId, Long cardId);

    CardResponse updateCard(Long userId, Long cardId, CardRequest request);

    void deleteCard(Long userId, Long cardId);

    void setDefaultCard(Long userId, Long cardId);
}