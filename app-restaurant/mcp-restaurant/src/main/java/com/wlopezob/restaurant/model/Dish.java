package com.wlopezob.restaurant.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "dishes")
public record Dish(
    @Id String id,
    String categoryId,
    String name,
    String descripcion,
    Double calories,
    Boolean enabled,
    Double price
) {
} 