package com.wlopezob.restaurant.toolservice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import com.wlopezob.restaurant.dto.DishDTO;
import com.wlopezob.restaurant.dto.MenuDTO;
import com.wlopezob.restaurant.dto.RequestDTO;
import com.wlopezob.restaurant.service.DishService;
import com.wlopezob.restaurant.service.MenuService;
import com.wlopezob.restaurant.service.RequestService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RequestTool {

    private final RequestService requestService;
    private final MenuService menuService;
    private final DishService dishService;

    @Tool(name = "getAllRequests", description = "Find all requests")
    public List<RequestDTO> getAllRequests() {
        return requestService.getAllRequests().collectList().block();
    }

    @Tool(name = "createRequest", description = "Create a new request with menus and optional extra dishes")
    public RequestDTO createRequest(
            @ToolParam(description = "List of menu IDs (required)") List<String> menusId,
            @ToolParam(description = "List of extra dish IDs (optional)") List<String> extraDishesId) {
        
        // Si extraDishesId es null, inicializar con lista vacía
        if (extraDishesId == null) {
            extraDishesId = new ArrayList<>();
        }
        
        // Lista final de extraDishesId para usar en el lambda
        List<String> finalExtraDishesId = extraDishesId;
        
        // Calcular precio, construir el DTO y guardarlo - todo en un flujo continuo
        return calculateTotalPrice(menusId, finalExtraDishesId)
                .map(totalPrice -> RequestDTO.builder()
                        .menusId(menusId)
                        .extraDishesId(finalExtraDishesId)
                        .totalPrice(totalPrice)
                        .enabled(true)
                        .build())
                .flatMap(requestService::createRequest)
                .block();
    }

    @Tool(name = "deleteRequest", description = "Delete a request by ID")
    public boolean deleteRequest(@ToolParam(description = "Request ID to delete") String id) {
        try {
            requestService.deleteRequest(id).block();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Calculates the total price based on menu prices and extra dish prices
     * 
     * @param menusId List of menu IDs
     * @param extraDishesId List of extra dish IDs
     * @return The total price as Mono<Double>
     */
    private Mono<Double> calculateTotalPrice(List<String> menusId, List<String> extraDishesId) {
        // Get and sum all menu prices
        Mono<Double> menusPriceMono = Mono.just(0.0);
        if (menusId != null && !menusId.isEmpty()) {
            menusPriceMono = Mono.zip(
                menusId.stream()
                    .map(menuId -> menuService.getMenuById(menuId)
                        .map(MenuDTO::getTotalPrice)
                        .defaultIfEmpty(0.0))
                    .toList(),
                prices -> {
                    double sum = 0.0;
                    for (Object price : prices) {
                        sum += (Double) price;
                    }
                    return sum;
                }
            );
        }
        
        // Get and sum all extra dish prices
        Mono<Double> dishesPriceMono = Mono.just(0.0);
        if (extraDishesId != null && !extraDishesId.isEmpty()) {
            dishesPriceMono = Mono.zip(
                extraDishesId.stream()
                    .map(dishId -> dishService.getDishById(dishId)
                        .map(DishDTO::getPrice)
                        .defaultIfEmpty(0.0))
                    .toList(),
                prices -> {
                    double sum = 0.0;
                    for (Object price : prices) {
                        sum += (Double) price;
                    }
                    return sum;
                }
            );
        }
        
        // Combine menu prices and dish prices
        return Mono.zip(menusPriceMono, dishesPriceMono, Double::sum);
    }
} 