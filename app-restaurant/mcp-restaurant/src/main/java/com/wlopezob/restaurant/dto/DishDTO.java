package com.wlopezob.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DishDTO {
    private String id;
    private String categoryId;
    private String name;
    private String descripcion;
    private Double calories;
    private Boolean enabled;
    private Double price;
    private CategoryDTO category; // Agregamos referencia a la categoría para enriquecer la respuesta
} 