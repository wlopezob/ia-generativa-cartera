package com.wlopezob.restaurant.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "menus")
public record Menu(
    @Id String id,
    String name,
    String description,
    String culinaryStyleId,
    List<String> dishesId,
    Integer totalCalories,
    Boolean enabled,
    Double totalPrice
) {
} 