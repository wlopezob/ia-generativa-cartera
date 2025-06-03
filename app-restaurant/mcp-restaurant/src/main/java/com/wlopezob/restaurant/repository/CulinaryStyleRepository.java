package com.wlopezob.restaurant.repository;

import com.wlopezob.restaurant.model.CulinaryStyle;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CulinaryStyleRepository extends ReactiveMongoRepository<CulinaryStyle, String> {
} 