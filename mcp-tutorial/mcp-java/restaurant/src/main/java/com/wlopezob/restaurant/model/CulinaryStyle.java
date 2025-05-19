package com.wlopezob.restaurant.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "culinary_styles")
public record CulinaryStyle(
    @Id String id,
    String name,
    String description
) {
} 