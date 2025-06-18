package com.wlopezob.crud.dto;

import lombok.Data;

@Data
public class TransactionRequest {
    private Long senderPersonaId;
    private Long receiverPersonaId;
    private Double monto;
} 