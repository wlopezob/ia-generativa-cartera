package com.wlopezob.ux_api_restaurant.config;

import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.time.Duration;

@Configuration
public class McpClientConfig {

    @Bean
    public RestClientCustomizer restClientCustomizer() {
        return restClientBuilder -> {
            System.out.println("Configurando OpenAI client con timeouts extendidos");
            
            // Configurar factory con timeouts personalizados
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(Duration.ofSeconds(120));
            factory.setReadTimeout(Duration.ofSeconds(120));
            
            restClientBuilder.requestFactory(factory);
        };
    }
} 