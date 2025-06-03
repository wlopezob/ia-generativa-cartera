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
public class RequestDTO {
    private String id;
    private List<String> menusId;
    private List<String> extraDishesId;
    private Double totalPrice;
    private Boolean enabled;
    
    // Datos enriquecidos
    private List<MenuDTO> menus;
    private List<DishDTO> extraDishes;
} 