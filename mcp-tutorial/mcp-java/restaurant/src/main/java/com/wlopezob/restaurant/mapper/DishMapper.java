package com.wlopezob.restaurant.mapper;

import com.wlopezob.restaurant.dto.DishDTO;
import com.wlopezob.restaurant.model.Dish;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface DishMapper {
    
    DishMapper INSTANCE = Mappers.getMapper(DishMapper.class);
    
    @Mapping(target = "id", source = "id")
    @Mapping(target = "categoryId", source = "categoryId")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "descripcion", source = "descripcion")
    @Mapping(target = "calories", source = "calories")
    @Mapping(target = "enabled", source = "enabled")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "category", ignore = true) // Se llenará manualmente
    DishDTO toDto(Dish dish);
    
    @Mapping(target = "id", source = "id")
    @Mapping(target = "categoryId", source = "categoryId")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "descripcion", source = "descripcion")
    @Mapping(target = "calories", source = "calories")
    @Mapping(target = "enabled", source = "enabled")
    @Mapping(target = "price", source = "price")
    Dish toEntity(DishDTO dishDTO);
} 