package com.wlopezob.crud.dto;

import lombok.Data;

@Data
public class TransactionResponse {
    private Long id;
    private Long senderPersonaId;
    private Long receiverPersonaId;
    private String fecha; // Formato: dd/MM/yyyy HH:mm:ss
    private Double monto;
    private String senderNombre;
    private String receiverNombre;
} 