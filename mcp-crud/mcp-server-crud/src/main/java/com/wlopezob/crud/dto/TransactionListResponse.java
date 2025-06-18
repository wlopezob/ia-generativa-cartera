package com.wlopezob.crud.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class TransactionListResponse {
    private List<TransactionResponse> transactions;
    private int total;
    private LocalDateTime timestamp;
    private String mensaje;
} 