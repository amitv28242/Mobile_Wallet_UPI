package com.mobilewallet.dto.bank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountResponse {
    private Long id;
    private String accountNumberMasked;
    private String ifscCode;
    private String bankName;
    private String accountHolderName;
    private Boolean isDefault;
    private String createdAt;
}