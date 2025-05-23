package com.wlopezob.ux_api_restaurant.config;

import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class McpClientConfig {

    @Bean
    public RestClient.Builder restClientCustomizer() {
        System.out.println("Configurando OpenAI client con timeouts extendidos");
        
        ClientHttpRequestFactorySettings settings = new ClientHttpRequestFactorySettings(
                Duration.ofSeconds(60),
                Duration.ofSeconds(120),
                null
        );
        
        return RestClient.builder()
                .requestFactory(ClientHttpRequestFactories.get(settings));
    }
} 