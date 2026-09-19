// FILE: src/main/java/com/mobilewallet/mapper/PaymentMapper.java
package com.mobilewallet.mapper;

import com.mobilewallet.dto.payment.PaymentResponse;
import com.mobilewallet.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "payer.id", source = "payer.id")
    @Mapping(target = "payer.fullName", source = "payer.fullName")
    @Mapping(target = "payer.walletNumber", expression = "java(payment.getWallet().getWalletNumber())")
    @Mapping(target = "payee.id", source = "payee.id")
    @Mapping(target = "payee.fullName", source = "payee.fullName")
    @Mapping(target = "payee.walletNumber", ignore = true)
    PaymentResponse toResponse(Payment payment);

    default PaymentResponse.PayeeInfo mapPayeeInfo(Payment payment) {
        return PaymentResponse.PayeeInfo.builder()
                .id(payment.getPayee().getId())
                .fullName(payment.getPayee().getFullName())
                .walletNumber(null) // Will be set by service
                .build();
    }
}

