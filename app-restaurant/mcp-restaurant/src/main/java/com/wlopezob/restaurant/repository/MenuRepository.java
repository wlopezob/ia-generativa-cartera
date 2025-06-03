package com.wlopezob.restaurant.repository;

import com.wlopezob.restaurant.model.Menu;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface MenuRepository extends ReactiveMongoRepository<Menu, String> {
    Flux<Menu> findByCulinaryStyleId(String culinaryStyleId);
} 