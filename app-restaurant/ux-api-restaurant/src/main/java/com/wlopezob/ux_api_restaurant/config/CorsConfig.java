package com.wlopezob.ux_api_restaurant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Configuración centralizada de CORS para permitir peticiones desde cualquier origen
 * 
 * Esta es la ÚNICA configuración de CORS en la aplicación para evitar conflictos.
 * Utiliza CorsWebFilter que es la forma recomendada para Spring WebFlux.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        
        // Permitir peticiones desde cualquier origen
        corsConfig.setAllowedOrigins(Arrays.asList("*"));
        
        // Permitir todos los métodos HTTP necesarios
        corsConfig.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
        ));
        
        // Permitir todos los headers (más flexible para desarrollo)
        corsConfig.setAllowedHeaders(Arrays.asList("*"));
        
        // NOTA: No se puede usar allowCredentials(true) con allowedOrigins("*")
        // Si necesitas credenciales, debes especificar orígenes específicos
        corsConfig.setAllowCredentials(false);
        
        // Cache de preflight requests por 1 hora
        corsConfig.setMaxAge(3600L);
        
        // Exponer headers específicos al cliente si es necesario
        corsConfig.setExposedHeaders(Arrays.asList(
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Methods",
            "Content-Type"
        ));
        
        // Aplicar configuración a todas las rutas
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        
        return new CorsWebFilter(source);
    }
} 