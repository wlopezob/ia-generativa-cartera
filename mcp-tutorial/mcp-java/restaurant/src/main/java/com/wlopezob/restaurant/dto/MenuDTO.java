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
    private String name;
    private String description;
    private String culinaryStyleId;
    private List<String> dishesId;
    private Integer totalCalories;
    private Boolean enabled;
    private Double totalPrice;
    
    // Datos enriquecidos
    private List<DishDTO> dishes;
    private CulinaryStyleDTO culinaryStyle;
} 