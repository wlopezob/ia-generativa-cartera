package com.wlopezob.crud.controller;

import com.wlopezob.crud.dto.TransactionListResponse;
import com.wlopezob.crud.dto.TransactionRequest;
import com.wlopezob.crud.dto.TransactionResponse;
import com.wlopezob.crud.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    
    private final TransactionService transactionService;
    
    @GetMapping("/{id}")
    public Mono<TransactionResponse> getTransactionById(@PathVariable Long id) {
        return transactionService.getTransactionById(id);
    }
    
    @GetMapping("/persona/{personaId}")
    public Mono<TransactionListResponse> getTransactionsByPersonaId(@PathVariable Long personaId) {
        return transactionService.getTransactionsByPersonaId(personaId);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TransactionResponse> createTransaction(@RequestBody TransactionRequest request) {
        return transactionService.createTransaction(request);
    }
} 