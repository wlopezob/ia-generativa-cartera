package com.wlopezob.restaurant.mapper;

import com.wlopezob.restaurant.dto.RequestDTO;
import com.wlopezob.restaurant.model.Request;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {MenuMapper.class, DishMapper.class})
public interface RequestMapper {
    
    RequestMapper INSTANCE = Mappers.getMapper(RequestMapper.class);
    
    @Mapping(target = "id", source = "id")
    @Mapping(target = "menusId", source = "menusId")
    @Mapping(target = "extraDishesId", source = "extraDishesId")
    @Mapping(target = "totalPrice", source = "totalPrice")
    @Mapping(target = "enabled", source = "enabled")
    @Mapping(target = "menus", ignore = true) // Se llenará manualmente
    @Mapping(target = "extraDishes", ignore = true) // Se llenará manualmente
    RequestDTO toDto(Request request);
    
    @Mapping(target = "id", source = "id")
    @Mapping(target = "menusId", source = "menusId")
    @Mapping(target = "extraDishesId", source = "extraDishesId")
    @Mapping(target = "totalPrice", source = "totalPrice")
    @Mapping(target = "enabled", source = "enabled")
    Request toEntity(RequestDTO requestDTO);
} 