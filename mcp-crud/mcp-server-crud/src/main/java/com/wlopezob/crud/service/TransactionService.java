package com.wlopezob.crud.service;

import com.wlopezob.crud.dto.TransactionListResponse;
import com.wlopezob.crud.dto.TransactionRequest;
import com.wlopezob.crud.dto.TransactionResponse;
import com.wlopezob.crud.entity.Transaction;
import com.wlopezob.crud.exception.SaldoInsuficienteException;
import com.wlopezob.crud.mapper.TransactionMapper;
import com.wlopezob.crud.repository.PersonaRepository;
import com.wlopezob.crud.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final PersonaRepository personaRepository;
    private final TransactionMapper transactionMapper;
    
    public Mono<TransactionListResponse> getAllTransactions() {
        log.info("Obteniendo todas las transacciones");
        return transactionRepository.findAll()
                .map(transactionMapper::toResponse)
                .collectList()
                .flatMap(this::enrichTransactionResponses)
                .map(transactions -> TransactionListResponse.builder()
                        .transactions(transactions)
                        .total(transactions.size())
                        .timestamp(LocalDateTime.now())
                        .mensaje("Lista de transacciones obtenida exitosamente")
                        .build());
    }
    
    public Mono<TransactionResponse> getTransactionById(Long id) {
        log.info("Obteniendo transacción por ID: {}", id);
        return transactionRepository.findById(id)
                .map(transactionMapper::toResponse)
                .flatMap(this::enrichTransactionResponse);
    }
    
    public Mono<TransactionListResponse> getTransactionsByPersonaId(Long personaId) {
        log.info("Obteniendo transacciones para la persona con ID: {}", personaId);
        return transactionRepository.findAllByPersonaId(personaId)
                .map(transactionMapper::toResponse)
                .collectList()
                .flatMap(this::enrichTransactionResponses)
                .map(transactions -> TransactionListResponse.builder()
                        .transactions(transactions)
                        .total(transactions.size())
                        .timestamp(LocalDateTime.now())
                        .mensaje("Transacciones para la persona con ID " + personaId)
                        .build());
    }
    
    @Transactional
    public Mono<TransactionResponse> createTransaction(TransactionRequest request) {
        log.info("Creando nueva transacción: {} -> {}, monto: {}", 
                request.getSenderPersonaId(), request.getReceiverPersonaId(), request.getMonto());
        
        if (request.getMonto() <= 0) {
            return Mono.error(new IllegalArgumentException("El monto debe ser mayor que cero"));
        }
        
        return personaRepository.findById(request.getSenderPersonaId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Persona remitente no encontrada")))
                .flatMap(sender -> {
                    if (sender.getSaldo() == null || sender.getSaldo() < request.getMonto()) {
                        return Mono.error(new SaldoInsuficienteException(
                                sender.getId(), 
                                sender.getSaldo() != null ? sender.getSaldo() : 0.0, 
                                request.getMonto()));
                    }
                    
                    return personaRepository.findById(request.getReceiverPersonaId())
                            .switchIfEmpty(Mono.error(new IllegalArgumentException("Persona receptora no encontrada")))
                            .flatMap(receiver -> {
                                // Actualizar saldos
                                sender.setSaldo(sender.getSaldo() - request.getMonto());
                                receiver.setSaldo(receiver.getSaldo() != null ? receiver.getSaldo() + request.getMonto() : request.getMonto());
                                
                                // Crear transacción
                                Transaction transaction = transactionMapper.toEntity(request);
                                transaction.setFecha(LocalDateTime.now());
                                
                                // Guardar cambios
                                return personaRepository.save(sender)
                                        .then(personaRepository.save(receiver))
                                        .then(transactionRepository.save(transaction))
                                        .map(transactionMapper::toResponse)
                                        .flatMap(this::enrichTransactionResponse);
                            });
                });
    }
    
    private Mono<List<TransactionResponse>> enrichTransactionResponses(List<TransactionResponse> responses) {
        return Mono.just(responses)
                .flatMapMany(list -> Mono.just(list).flatMapIterable(l -> l))
                .flatMap(this::enrichTransactionResponse)
                .collectList();
    }
    
    private Mono<TransactionResponse> enrichTransactionResponse(TransactionResponse response) {
        return personaRepository.findById(response.getSenderPersonaId())
                .map(sender -> {
                    response.setSenderNombre(sender.getNombre() + " " + sender.getApellido());
                    return response;
                })
                .flatMap(resp -> personaRepository.findById(response.getReceiverPersonaId())
                        .map(receiver -> {
                            resp.setReceiverNombre(receiver.getNombre() + " " + receiver.getApellido());
                            return resp;
                        }));
    }
} 