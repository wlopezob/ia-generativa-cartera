package com.wlopezob.ux_api_restaurant.service;

import com.wlopezob.ux_api_restaurant.dto.HealthResponse;
import com.wlopezob.ux_api_restaurant.dto.InfoResponse;
import reactor.core.publisher.Mono;

/**
 * Interface para el servicio de información de la aplicación
 */
public interface ApplicationInfoService {

    /**
     * Obtiene el estado de salud de la aplicación
     */
    Mono<HealthResponse> getHealth();

    /**
     * Obtiene la información de la aplicación
     */
    Mono<InfoResponse> getInfo();
} 