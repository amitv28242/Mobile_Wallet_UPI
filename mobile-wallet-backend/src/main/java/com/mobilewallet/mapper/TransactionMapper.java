// FILE: src/main/java/com/mobilewallet/mapper/TransactionMapper.java
package com.mobilewallet.mapper;

import com.mobilewallet.dto.transaction.TransactionDTO;
import com.mobilewallet.entity.TransactionHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "payment.id", source = "payment.id")
    @Mapping(target = "payment.referenceId", source = "payment.referenceId")
    @Mapping(target = "payment.paymentType", source = "payment.paymentType")
    @Mapping(target = "payment.payer.id", source = "payment.payer.id")
    @Mapping(target = "payment.payer.fullName", source = "payment.payer.fullName")
    @Mapping(target = "payment.payee.id", source = "payment.payee.id")
    @Mapping(target = "payment.payee.fullName", source = "payment.payee.fullName")
    TransactionDTO toDTO(TransactionHistory transaction);
}