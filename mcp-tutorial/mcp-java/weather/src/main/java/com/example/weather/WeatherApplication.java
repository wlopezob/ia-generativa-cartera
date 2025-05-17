package com.example.weather;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WeatherApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeatherApplication.class, args);
	}

	/*
	 *  configura un proveedor de herramientas en una aplicación Spring AI, 
	 * registrando los métodos anotados con @Tool de la clase WeatherService 
	 * para que puedan ser utilizados por modelos de lenguaje en tareas de procesamiento de lenguaje natural
	*/
	@Bean
	public ToolCallbackProvider weatherTools(WeatherService weatherService) {
		return MethodToolCallbackProvider.builder().toolObjects(weatherService).build();
	}
}
