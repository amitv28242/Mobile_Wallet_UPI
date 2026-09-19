// FILE: src/main/java/com/mobilewallet/service/impl/RechargeServiceImpl.java
package com.mobilewallet.service.impl;

import com.mobilewallet.dto.recharge.BillPaymentRequest;
import com.mobilewallet.dto.recharge.BillPaymentResponse;
import com.mobilewallet.dto.recharge.RechargeRequest;
import com.mobilewallet.dto.recharge.RechargeResponse;
import com.mobilewallet.entity.BillPayment;
import com.mobilewallet.entity.RechargeTransaction;
import com.mobilewallet.entity.User;
import com.mobilewallet.exception.ResourceNotFoundException;
import com.mobilewallet.repository.BillPaymentRepository;
import com.mobilewallet.repository.RechargeTransactionRepository;
import com.mobilewallet.repository.UserRepository;
import com.mobilewallet.service.RechargeService;
import com.mobilewallet.service.WalletService;
import com.mobilewallet.util.AuditUtil;
import com.mobilewallet.util.ReferenceNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RechargeServiceImpl implements RechargeService {

    private final RechargeTransactionRepository rechargeTransactionRepository;
    private final BillPaymentRepository billPaymentRepository;
    private final UserRepository userRepository;
    private final WalletService walletService;
    private final ReferenceNumberGenerator referenceGenerator;
    private final AuditUtil auditUtil;

    @Override
    @Transactional
    public RechargeResponse mobileRecharge(Long userId, RechargeRequest request) {
        log.info("Mobile recharge for user: {}, operator: {}, phone: {}", 
                userId, request.getOperator(), request.getPhoneNumber());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Validate wallet balance
        if (!walletService.hasSufficientBalance(userId, request.getAmount())) {
            throw new IllegalArgumentException("Insufficient wallet balance");
        }

        // Generate reference
        String referenceId = referenceGenerator.generateReference("RECHARGE");

        // Create recharge transaction
        RechargeTransaction recharge = RechargeTransaction.builder()
                .user(user)
                .operator(request.getOperator())
                .phoneNumber(request.getPhoneNumber())
                .amount(request.getAmount())
                .status(RechargeTransaction.RechargeStatus.PENDING)
                .referenceId(referenceId)
                .build();

        rechargeTransactionRepository.save(recharge);

        try {
            // Process recharge (mock implementation)
            // In production, this would call a third-party API
            processRecharge(recharge);

            // Debit wallet
            walletService.debitWallet(userId, request.getAmount(), referenceId);

            recharge.setStatus(RechargeTransaction.RechargeStatus.SUCCESS);
            RechargeTransaction savedRecharge = rechargeTransactionRepository.save(recharge);

            auditUtil.logAction("MOBILE_RECHARGE", user,
                    "Mobile recharge: " + request.getAmount() + 
                    " to " + request.getPhoneNumber());

            return convertToResponse(savedRecharge);
        } catch (Exception e) {
            log.error("Mobile recharge failed", e);
            recharge.setStatus(RechargeTransaction.RechargeStatus.FAILED);
            rechargeTransactionRepository.save(recharge);
            throw new RuntimeException("Mobile recharge failed: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public RechargeResponse dthRecharge(Long userId, RechargeRequest request) {
        log.info("DTH recharge for user: {}, operator: {}, phone: {}", 
                userId, request.getOperator(), request.getPhoneNumber());

        // Similar to mobile recharge but with DTH specific logic
        return mobileRecharge(userId, request);
    }

    @Override
    @Transactional
    public BillPaymentResponse payBill(Long userId, BillPaymentRequest request) {
        log.info("Bill payment for user: {}, type: {}, consumer: {}", 
                userId, request.getBillType(), request.getConsumerNumber());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Validate wallet balance
        if (!walletService.hasSufficientBalance(userId, request.getAmount())) {
            throw new IllegalArgumentException("Insufficient wallet balance");
        }

        // Generate reference
        String referenceId = referenceGenerator.generateReference("BILL");

        // Create bill payment
        BillPayment billPayment = BillPayment.builder()
                .user(user)
                .billType(request.getBillType())
                .billerCode(request.getBillerCode())
                .consumerNumber(request.getConsumerNumber())
                .amount(request.getAmount())
                .status(BillPayment.BillStatus.PENDING)
                .referenceId(referenceId)
                .dueDate(request.getDueDate())
                .build();

        billPaymentRepository.save(billPayment);

        try {
            // Process bill payment (mock implementation)
            processBillPayment(billPayment);

            // Debit wallet
            walletService.debitWallet(userId, request.getAmount(), referenceId);

            billPayment.setStatus(BillPayment.BillStatus.SUCCESS);
            BillPayment savedBillPayment = billPaymentRepository.save(billPayment);

            auditUtil.logAction("BILL_PAYMENT", user,
                    "Bill payment: " + request.getAmount() + 
                    " for " + request.getBillType());

            return convertToResponse(savedBillPayment);
        } catch (Exception e) {
            log.error("Bill payment failed", e);
            billPayment.setStatus(BillPayment.BillStatus.FAILED);
            billPaymentRepository.save(billPayment);
            throw new RuntimeException("Bill payment failed: " + e.getMessage());
        }
    }

    @Override
    public Page<RechargeResponse> getRechargeHistory(Long userId, Pageable pageable) {
        log.info("Fetching recharge history for user: {}", userId);
        Page<RechargeTransaction> recharges = rechargeTransactionRepository.findByUserId(userId, pageable);
        return recharges.map(this::convertToResponse);
    }

    @Override
    public Page<BillPaymentResponse> getBillPaymentHistory(Long userId, Pageable pageable) {
        log.info("Fetching bill payment history for user: {}", userId);
        Page<BillPayment> bills = billPaymentRepository.findByUserId(userId, pageable);
        return bills.map(this::convertToResponse);
    }

    @Override
    public RechargeResponse getRechargeByReference(Long userId, String referenceId) {
        log.info("Fetching recharge by reference: {} for user: {}", referenceId, userId);
        RechargeTransaction recharge = rechargeTransactionRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Recharge not found"));
        
        if (!recharge.getUser().getId().equals(userId)) {
            throw new SecurityException("Cannot access recharge of another user");
        }
        
        return convertToResponse(recharge);
    }

    @Override
    public BillPaymentResponse getBillByReference(Long userId, String referenceId) {
        log.info("Fetching bill by reference: {} for user: {}", referenceId, userId);
        BillPayment bill = billPaymentRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill payment not found"));
        
        if (!bill.getUser().getId().equals(userId)) {
            throw new SecurityException("Cannot access bill payment of another user");
        }
        
        return convertToResponse(bill);
    }

    @Override
    public Map<String, List<String>> getAvailableOperators() {
        // In production, this would come from a configuration or database
        return Map.of(
                "MOBILE", List.of("Airtel", "Jio", "Vi", "BSNL", "MTNL"),
                "DTH", List.of("Tata Sky", "Airtel Digital TV", "Dish TV", "Videocon d2h", "Sun Direct")
        );
    }

    @Override
    public List<String> getBillTypes() {
        return List.of("Electricity", "Water", "Gas", "Broadband", "Insurance", "Municipal Tax");
    }

    private void processRecharge(RechargeTransaction recharge) {
        // Mock implementation
        log.info("Processing recharge: {}", recharge.getReferenceId());
        try {
            Thread.sleep(2000); // Simulate processing
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void processBillPayment(BillPayment billPayment) {
        // Mock implementation
        log.info("Processing bill payment: {}", billPayment.getReferenceId());
        try {
            Thread.sleep(2000); // Simulate processing
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private RechargeResponse convertToResponse(RechargeTransaction recharge) {
        return RechargeResponse.builder()
                .id(recharge.getId())
                .referenceId(recharge.getReferenceId())
                .operator(recharge.getOperator())
                .phoneNumber(recharge.getPhoneNumber())
                .amount(recharge.getAmount())
                .status(recharge.getStatus().name())
                .createdAt(recharge.getCreatedAt())
                .build();
    }

    private BillPaymentResponse convertToResponse(BillPayment billPayment) {
        return BillPaymentResponse.builder()
                .id(billPayment.getId())
                .referenceId(billPayment.getReferenceId())
                .billType(billPayment.getBillType())
                .billerCode(billPayment.getBillerCode())
                .consumerNumber(billPayment.getConsumerNumber())
                .amount(billPayment.getAmount())
                .status(billPayment.getStatus().name())
                .dueDate(billPayment.getDueDate())
                .createdAt(billPayment.getCreatedAt())
                .build();
    }
}