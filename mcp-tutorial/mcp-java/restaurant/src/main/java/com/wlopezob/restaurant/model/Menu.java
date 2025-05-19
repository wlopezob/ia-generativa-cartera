package com.wlopezob.restaurant.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "menus")
public record Menu(
    @Id String id,
    List<String> dishesId,
    String categoryId,
    Integer totalCalories,
    Boolean enabled,
    Double totalPrice
) {
} 