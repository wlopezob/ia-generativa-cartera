package com.wlopezob.restaurant.service;

import com.wlopezob.restaurant.dto.DishDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DishService {
    Flux<DishDTO> getAllDishes();
    Flux<DishDTO> getAllDishesByCategoryId(String categoryId);
    Mono<DishDTO> getDishById(String id);
    Mono<DishDTO> createDish(DishDTO dishDTO);
    Mono<DishDTO> updateDish(String id, DishDTO dishDTO);
    Mono<Void> deleteDish(String id);
} 