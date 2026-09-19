// FILE: src/main/java/com/mobilewallet/mapper/WalletMapper.java
package com.mobilewallet.mapper;

import com.mobilewallet.dto.wallet.WalletResponse;
import com.mobilewallet.entity.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WalletMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.fullName", target = "userFullName")
    @Mapping(source = "status", target = "status")
    WalletResponse toResponse(Wallet wallet);
}