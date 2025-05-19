package com.wlopezob.restaurant.repository;

import com.wlopezob.restaurant.model.Request;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepository extends ReactiveMongoRepository<Request, String> {
} 