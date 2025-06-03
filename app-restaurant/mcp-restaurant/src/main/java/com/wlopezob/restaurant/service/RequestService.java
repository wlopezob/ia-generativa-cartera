package com.wlopezob.restaurant.service;

import com.wlopezob.restaurant.dto.RequestDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RequestService {
    Flux<RequestDTO> getAllRequests();
    Mono<RequestDTO> getRequestById(String id);
    Mono<RequestDTO> createRequest(RequestDTO requestDTO);
    Mono<RequestDTO> updateRequest(String id, RequestDTO requestDTO);
    Mono<Void> deleteRequest(String id);
} 