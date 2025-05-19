package com.wlopezob.restaurant.mapper;

import com.wlopezob.restaurant.dto.CulinaryStyleDTO;
import com.wlopezob.restaurant.model.CulinaryStyle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CulinaryStyleMapper {
    
    CulinaryStyleMapper INSTANCE = Mappers.getMapper(CulinaryStyleMapper.class);
    
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    CulinaryStyleDTO toDto(CulinaryStyle culinaryStyle);
    
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    CulinaryStyle toEntity(CulinaryStyleDTO culinaryStyleDTO);
} 