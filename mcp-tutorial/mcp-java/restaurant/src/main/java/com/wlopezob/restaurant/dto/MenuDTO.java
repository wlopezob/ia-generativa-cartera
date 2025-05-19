package com.wlopezob.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuDTO {
    private String id;
    private List<String> dishesId;
    private String categoryId;
    private Integer totalCalories;
    private Boolean enabled;
    private Double totalPrice;
    
    // Datos enriquecidos
    private List<DishDTO> dishes;
    private CategoryDTO category;
} 