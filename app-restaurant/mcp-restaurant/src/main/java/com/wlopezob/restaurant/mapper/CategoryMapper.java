package com.wlopezob.restaurant.mapper;

import com.wlopezob.restaurant.dto.CategoryDTO;
import com.wlopezob.restaurant.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);
    
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    CategoryDTO toDto(Category category);
    
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    Category toEntity(CategoryDTO categoryDTO);
} 