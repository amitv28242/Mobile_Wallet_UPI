// FILE: src/main/java/com/mobilewallet/dto/transaction/TransactionListResponse.java
package com.mobilewallet.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionListResponse {
    private List<TransactionDTO> transactions;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
}