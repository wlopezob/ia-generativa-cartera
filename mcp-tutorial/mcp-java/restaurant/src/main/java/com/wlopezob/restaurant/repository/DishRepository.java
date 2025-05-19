package com.wlopezob.restaurant.repository;

import com.wlopezob.restaurant.model.Dish;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface DishRepository extends ReactiveMongoRepository<Dish, String> {
    Flux<Dish> findByCategoryId(String categoryId);
} 