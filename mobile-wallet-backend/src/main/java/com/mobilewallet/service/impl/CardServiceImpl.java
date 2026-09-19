// FILE: src/main/java/com/mobilewallet/service/impl/CardServiceImpl.java
package com.mobilewallet.service.impl;

import com.mobilewallet.dto.card.CardRequest;
import com.mobilewallet.dto.card.CardResponse;
import com.mobilewallet.entity.Card;
import com.mobilewallet.entity.User;
import com.mobilewallet.exception.ResourceNotFoundException;
import com.mobilewallet.repository.CardRepository;
import com.mobilewallet.repository.UserRepository;
import com.mobilewallet.service.CardService;
import com.mobilewallet.util.AESEncryptionUtil;
import com.mobilewallet.util.AuditUtil;
import com.mobilewallet.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final AESEncryptionUtil encryptionUtil;
    private final AuditUtil auditUtil;

    @Override
    @Transactional
    public CardResponse addCard(Long userId, CardRequest request) {
        log.info("Adding card for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Validate card number
        if (!ValidationUtil.isValidCardNumber(request.getCardNumber())) {
            throw new IllegalArgumentException("Invalid card number");
        }

        // Validate CVV
        if (!ValidationUtil.isValidCVV(request.getCvv())) {
            throw new IllegalArgumentException("Invalid CVV");
        }

        // Determine card issuer
        String issuer = determineCardIssuer(request.getCardNumber());

        // Encrypt sensitive data
        String encryptedData = encryptionUtil.encrypt(
                request.getCardNumber() + "|" + request.getCvv()
        );

        String lastFour = request.getCardNumber().substring(
                request.getCardNumber().length() - 4
        );
        String maskedNumber = ValidationUtil.maskCardNumber(request.getCardNumber());

        Card card = Card.builder()
                .user(user)
                .cardNumberMasked(maskedNumber)
                .cardholderName(request.getCardholderName())
                .expiryMonth(request.getExpiryMonth())
                .expiryYear(request.getExpiryYear())
                .issuer(issuer)
                .lastFour(lastFour)
                .encryptedData(encryptedData)
                .isDefault(request.getIsDefault() != null && request.getIsDefault())
                .build();

        // Validate expiry date
        validateExpiryDate(request.getExpiryMonth(), request.getExpiryYear());

        // If this is the first card, make it default
        if (cardRepository.countByUserId(userId) == 0) {
            card.setIsDefault(true);
        } else if (card.getIsDefault()) {
            // Clear existing default if setting this as default
            cardRepository.clearDefaultCards(userId);
        }

        Card savedCard = cardRepository.save(card);

        auditUtil.logAction("CARD_ADDED", user, 
                "Card added: " + maskedNumber);

        return convertToResponse(savedCard);
    }

    @Override
    public List<CardResponse> getCards(Long userId) {
        log.info("Fetching cards for user: {}", userId);
        
        List<Card> cards = cardRepository.findByUser_Id(userId);
        return cards.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CardResponse getCard(Long userId, Long cardId) {
        log.info("Fetching card: {} for user: {}", cardId, userId);
        
        Card card = cardRepository.findByIdAndUser_Id(cardId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));
        
        return convertToResponse(card);
    }

    @Override
    @Transactional
    public CardResponse updateCard(Long userId, Long cardId, CardRequest request) {
        log.info("Updating card: {} for user: {}", cardId, userId);

        Card card = cardRepository.findByIdAndUser_Id(cardId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

        if (request.getCardholderName() != null) {
            card.setCardholderName(request.getCardholderName());
        }

        if (request.getExpiryMonth() != null && request.getExpiryYear() != null) {
            validateExpiryDate(request.getExpiryMonth(), request.getExpiryYear());
            card.setExpiryMonth(request.getExpiryMonth());
            card.setExpiryYear(request.getExpiryYear());
        }

        if (request.getIsDefault() != null && request.getIsDefault() && !card.getIsDefault()) {
            cardRepository.clearDefaultCards(userId);
            card.setIsDefault(true);
        }

        Card updatedCard = cardRepository.save(card);

        auditUtil.logAction("CARD_UPDATED", card.getUser(),
                "Card updated: " + card.getCardNumberMasked());

        return convertToResponse(updatedCard);
    }

    @Override
    @Transactional
    public void deleteCard(Long userId, Long cardId) {
        log.info("Deleting card: {} for user: {}", cardId, userId);

        Card card = cardRepository.findByIdAndUser_Id(cardId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

        // If this is the default card, clear default
        if (card.getIsDefault()) {
            cardRepository.clearDefaultCards(userId);
        }

        cardRepository.delete(card);

        auditUtil.logAction("CARD_DELETED", card.getUser(),
                "Card deleted: " + card.getCardNumberMasked());
    }

    @Override
    @Transactional
    public void setDefaultCard(Long userId, Long cardId) {
        log.info("Setting default card: {} for user: {}", cardId, userId);

        Card card = cardRepository.findByIdAndUser_Id(cardId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

        cardRepository.clearDefaultCards(userId);
        card.setIsDefault(true);
        cardRepository.save(card);

        auditUtil.logAction("CARD_DEFAULT_SET", card.getUser(),
                "Default card set: " + card.getCardNumberMasked());
    }

    private CardResponse convertToResponse(Card card) {
        return CardResponse.builder()
                .id(card.getId())
                .cardNumberMasked(card.getCardNumberMasked())
                .cardholderName(card.getCardholderName())
                .expiryMonth(card.getExpiryMonth())
                .expiryYear(card.getExpiryYear())
                .issuer(card.getIssuer())
                .lastFour(card.getLastFour())
                .isDefault(card.getIsDefault())
                .createdAt(card.getCreatedAt().toString())
                .build();
    }

    private String determineCardIssuer(String cardNumber) {
        if (cardNumber.startsWith("4")) {
            return "VISA";
        } else if (cardNumber.matches("^5[1-5].*")) {
            return "MASTERCARD";
        } else if (cardNumber.matches("^3[47].*")) {
            return "AMERICAN_EXPRESS";
        } else if (cardNumber.matches("^6(?:011|5).*")) {
            return "DISCOVER";
        } else if (cardNumber.matches("^3(?:0[0-5]|[68]).*")) {
            return "DINERS_CLUB";
        } else {
            return "UNKNOWN";
        }
    }

    private void validateExpiryDate(String month, String year) {
        try {
            int monthInt = Integer.parseInt(month);
            int yearInt = Integer.parseInt(year);
            
            if (monthInt < 1 || monthInt > 12) {
                throw new IllegalArgumentException("Invalid expiry month");
            }
            
            LocalDateTime expiry = LocalDateTime.of(yearInt, monthInt, 1, 0, 0);
            if (expiry.isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Card has expired");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid expiry date format");
        }
    }
}