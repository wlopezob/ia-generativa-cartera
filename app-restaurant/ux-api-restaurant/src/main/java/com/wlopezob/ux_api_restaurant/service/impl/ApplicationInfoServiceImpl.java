package com.wlopezob.ux_api_restaurant.service.impl;

import com.wlopezob.ux_api_restaurant.config.ApplicationConstants;
import com.wlopezob.ux_api_restaurant.dto.HealthResponse;
import com.wlopezob.ux_api_restaurant.dto.InfoResponse;
import com.wlopezob.ux_api_restaurant.service.ApplicationInfoService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Implementación del servicio de información de la aplicación
 */
@Service
public class ApplicationInfoServiceImpl implements ApplicationInfoService {

    /**
     * Obtiene el estado de salud de la aplicación
     */
    @Override
    public Mono<HealthResponse> getHealth() {
        return Mono.just(new HealthResponse(
            ApplicationConstants.HEALTH_STATUS_OK, 
            ApplicationConstants.HEALTH_MESSAGE
        ));
    }

    /**
     * Obtiene la información de la aplicación
     */
    @Override
    public Mono<InfoResponse> getInfo() {
        return Mono.just(new InfoResponse(
            ApplicationConstants.APP_NAME, 
            ApplicationConstants.APP_DESCRIPTION, 
            ApplicationConstants.APP_VERSION
        ));
    }
} 