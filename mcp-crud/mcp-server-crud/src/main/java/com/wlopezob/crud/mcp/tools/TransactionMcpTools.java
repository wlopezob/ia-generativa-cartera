package com.wlopezob.crud.mcp.tools;

import com.wlopezob.crud.dto.TransactionRequest;
import com.wlopezob.crud.dto.TransactionResponse;
import com.wlopezob.crud.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionMcpTools {

    private final TransactionService transactionService;

    @Tool(description = "Crear una nueva transacción (transferencia de dinero) entre personas")
    public TransactionResponse crearTransaccion(
            @ToolParam(description = "ID de la persona que envía el dinero", required = true) Long senderPersonaId,
            @ToolParam(description = "ID de la persona que recibe el dinero", required = true) Long receiverPersonaId,
            @ToolParam(description = "Monto de la transferencia", required = true) Double monto) {
        
        log.info("Ejecutando herramienta para crear transacción: {} -> {}, monto: {}", 
                senderPersonaId, receiverPersonaId, monto);
        
        TransactionRequest request = new TransactionRequest();
        request.setSenderPersonaId(senderPersonaId);
        request.setReceiverPersonaId(receiverPersonaId);
        request.setMonto(monto);
        
        Mono<TransactionResponse> responseMono = transactionService.createTransaction(request);
        return responseMono.block();
    }
    
    @Tool(description = "Buscar una transacción por su ID")
    public TransactionResponse buscarTransaccionPorId(
            @ToolParam(description = "ID de la transacción a buscar", required = true) Long id) {
        
        log.info("Ejecutando herramienta para buscar transacción con ID: {}", id);
        
        Mono<TransactionResponse> responseMono = transactionService.getTransactionById(id);
        return responseMono.block();
    }
    
    @Tool(description = "Listar todas las transacciones de una persona (enviadas y recibidas)")
    public List<TransactionResponse> listarTransaccionesPorPersona(
            @ToolParam(description = "ID de la persona", required = true) Long personaId) {
        
        log.info("Ejecutando herramienta para listar transacciones de la persona con ID: {}", personaId);
        
        return transactionService.getTransactionsByPersonaId(personaId)
                .map(response -> response.getTransactions())
                .block();
    }
} 