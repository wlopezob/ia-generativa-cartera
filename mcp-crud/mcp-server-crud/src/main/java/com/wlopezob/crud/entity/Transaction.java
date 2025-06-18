package com.wlopezob.crud.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("transactions")
public class Transaction {
    
    @Id
    private Long id;
    
    private Long senderPersonaId;
    
    private Long receiverPersonaId;
    
    private LocalDateTime fecha;
    
    private Double monto;
} 