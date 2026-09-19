package com.mobilewallet.controller;

import com.mobilewallet.dto.card.CardRequest;
import com.mobilewallet.dto.card.CardResponse;
import com.mobilewallet.entity.User;
import com.mobilewallet.service.AuthService;
import com.mobilewallet.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Cards", description = "Card management APIs")
@SecurityRequirement(name = "bearerAuth")
public class CardController {

 private final CardService cardService;
 private final AuthService authService;

 @Operation(summary = "Add a new card")
 @PostMapping
 @PreAuthorize("hasRole('CONSUMER')")
 public ResponseEntity<CardResponse> addCard(@Valid @RequestBody CardRequest request) {
     User currentUser = authService.getCurrentUser();
     CardResponse response = cardService.addCard(currentUser.getId(), request);
     return new ResponseEntity<>(response, HttpStatus.CREATED);
 }

 @Operation(summary = "Get all cards")
 @GetMapping
 @PreAuthorize("hasRole('CONSUMER')")
 public ResponseEntity<List<CardResponse>> getCards() {
     User currentUser = authService.getCurrentUser();
     List<CardResponse> cards = cardService.getCards(currentUser.getId());
     return ResponseEntity.ok(cards);
 }

 @Operation(summary = "Get card by ID")
 @GetMapping("/{cardId}")
 @PreAuthorize("hasRole('CONSUMER')")
 public ResponseEntity<CardResponse> getCard(@PathVariable Long cardId) {
     User currentUser = authService.getCurrentUser();
     CardResponse response = cardService.getCard(currentUser.getId(), cardId);
     return ResponseEntity.ok(response);
 }

 @Operation(summary = "Update card")
 @PutMapping("/{cardId}")
 @PreAuthorize("hasRole('CONSUMER')")
 public ResponseEntity<CardResponse> updateCard(
         @PathVariable Long cardId,
         @Valid @RequestBody CardRequest request) {
     
     User currentUser = authService.getCurrentUser();
     CardResponse response = cardService.updateCard(currentUser.getId(), cardId, request);
     return ResponseEntity.ok(response);
 }

 @Operation(summary = "Delete card")
 @DeleteMapping("/{cardId}")
 @PreAuthorize("hasRole('CONSUMER')")
 public ResponseEntity<Map<String, String>> deleteCard(@PathVariable Long cardId) {
     User currentUser = authService.getCurrentUser();
     cardService.deleteCard(currentUser.getId(), cardId);
     return ResponseEntity.ok(Map.of("message", "Card deleted successfully"));
 }

 @Operation(summary = "Set default card")
 @PutMapping("/{cardId}/default")
 @PreAuthorize("hasRole('CONSUMER')")
 public ResponseEntity<Map<String, String>> setDefaultCard(@PathVariable Long cardId) {
     User currentUser = authService.getCurrentUser();
     cardService.setDefaultCard(currentUser.getId(), cardId);
     return ResponseEntity.ok(Map.of("message", "Default card set successfully"));
 }
}