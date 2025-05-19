package com.wlopezob.restaurant.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "categories")
public record Category(
    @Id String id,
    String name
) {
} 