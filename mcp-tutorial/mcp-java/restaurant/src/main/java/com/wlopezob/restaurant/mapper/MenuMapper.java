package com.wlopezob.restaurant.mapper;

import com.wlopezob.restaurant.dto.MenuDTO;
import com.wlopezob.restaurant.model.Menu;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {DishMapper.class, CategoryMapper.class})
public interface MenuMapper {
    
    MenuMapper INSTANCE = Mappers.getMapper(MenuMapper.class);
    
    @Mapping(target = "id", source = "id")
    @Mapping(target = "dishesId", source = "dishesId")
    @Mapping(target = "categoryId", source = "categoryId")
    @Mapping(target = "totalCalories", source = "totalCalories")
    @Mapping(target = "enabled", source = "enabled")
    @Mapping(target = "totalPrice", source = "totalPrice")
    @Mapping(target = "dishes", ignore = true) // Se llenará manualmente
    @Mapping(target = "category", ignore = true) // Se llenará manualmente
    MenuDTO toDto(Menu menu);
    
    @Mapping(target = "id", source = "id")
    @Mapping(target = "dishesId", source = "dishesId")
    @Mapping(target = "categoryId", source = "categoryId")
    @Mapping(target = "totalCalories", source = "totalCalories")
    @Mapping(target = "enabled", source = "enabled")
    @Mapping(target = "totalPrice", source = "totalPrice")
    Menu toEntity(MenuDTO menuDTO);
} 