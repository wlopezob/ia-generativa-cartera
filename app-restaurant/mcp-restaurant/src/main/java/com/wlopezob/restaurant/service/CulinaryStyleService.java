package com.wlopezob.restaurant.service;

import com.wlopezob.restaurant.dto.CulinaryStyleDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CulinaryStyleService {
    Flux<CulinaryStyleDTO> getAllCulinaryStyles();
    Mono<CulinaryStyleDTO> getCulinaryStyleById(String id);
    Mono<CulinaryStyleDTO> createCulinaryStyle(CulinaryStyleDTO culinaryStyleDTO);
    Mono<CulinaryStyleDTO> updateCulinaryStyle(String id, CulinaryStyleDTO culinaryStyleDTO);
    Mono<Void> deleteCulinaryStyle(String id);
} 