package com.wlopezob.ux_api_restaurant.service;

import com.wlopezob.ux_api_restaurant.config.ApplicationConstants;
import com.wlopezob.ux_api_restaurant.dto.HealthResponse;
import com.wlopezob.ux_api_restaurant.dto.InfoResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Servicio para manejar la información de la aplicación
 */
@Service
public class ApplicationInfoService {

    /**
     * Obtiene el estado de salud de la aplicación
     */
    public Mono<HealthResponse> getHealth() {
        return Mono.just(new HealthResponse(
            ApplicationConstants.HEALTH_STATUS_OK, 
            ApplicationConstants.HEALTH_MESSAGE
        ));
    }

    /**
     * Obtiene la información de la aplicación
     */
    public Mono<InfoResponse> getInfo() {
        return Mono.just(new InfoResponse(
            ApplicationConstants.APP_NAME, 
            ApplicationConstants.APP_DESCRIPTION, 
            ApplicationConstants.APP_VERSION
        ));
    }
} 