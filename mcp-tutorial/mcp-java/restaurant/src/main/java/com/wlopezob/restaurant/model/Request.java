package com.wlopezob.restaurant.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "requests")
public record Request(
    @Id String id,
    List<String> menusId,
    List<String> extraDishesId,
    Double totalPrice,
    Boolean enabled
) {
} 