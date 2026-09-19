// FILE: src/main/java/com/mobilewallet/mapper/BankAccountMapper.java
package com.mobilewallet.mapper;

import com.mobilewallet.dto.bank.BankAccountRequest;
import com.mobilewallet.dto.bank.BankAccountResponse;
import com.mobilewallet.entity.BankAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BankAccountMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "accountNumberMasked", ignore = true)
    @Mapping(target = "encryptedData", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    BankAccount toEntity(BankAccountRequest request);

    @Mapping(source = "accountNumberMasked", target = "accountNumberMasked")
    @Mapping(source = "isDefault", target = "isDefault")
    BankAccountResponse toResponse(BankAccount account);

    void updateEntity(@MappingTarget BankAccount account, BankAccountRequest request);
}