// FILE: src/main/java/com/mobilewallet/service/RechargeService.java
package com.mobilewallet.service;

import com.mobilewallet.dto.recharge.BillPaymentRequest;
import com.mobilewallet.dto.recharge.BillPaymentResponse;
import com.mobilewallet.dto.recharge.RechargeRequest;
import com.mobilewallet.dto.recharge.RechargeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface RechargeService {

    RechargeResponse mobileRecharge(Long userId, RechargeRequest request);

    RechargeResponse dthRecharge(Long userId, RechargeRequest request);

    BillPaymentResponse payBill(Long userId, BillPaymentRequest request);

    Page<RechargeResponse> getRechargeHistory(Long userId, Pageable pageable);

    Page<BillPaymentResponse> getBillPaymentHistory(Long userId, Pageable pageable);

    RechargeResponse getRechargeByReference(Long userId, String referenceId);

    BillPaymentResponse getBillByReference(Long userId, String referenceId);

    Map<String, List<String>> getAvailableOperators();

    List<String> getBillTypes();
}