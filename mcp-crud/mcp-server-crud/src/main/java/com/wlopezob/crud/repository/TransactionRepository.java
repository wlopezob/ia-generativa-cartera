package com.wlopezob.crud.repository;

import com.wlopezob.crud.entity.Transaction;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface TransactionRepository extends ReactiveCrudRepository<Transaction, Long> {
    
    Flux<Transaction> findBySenderPersonaId(Long senderPersonaId);
    
    Flux<Transaction> findByReceiverPersonaId(Long receiverPersonaId);
    
    @Query("SELECT * FROM transactions WHERE sender_persona_id = :personaId OR receiver_persona_id = :personaId ORDER BY fecha DESC")
    Flux<Transaction> findAllByPersonaId(Long personaId);
} 