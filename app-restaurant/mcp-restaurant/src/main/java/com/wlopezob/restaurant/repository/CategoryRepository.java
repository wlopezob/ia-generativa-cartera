package com.wlopezob.restaurant.repository;

import com.wlopezob.restaurant.model.Category;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends ReactiveMongoRepository<Category, String> {
} 