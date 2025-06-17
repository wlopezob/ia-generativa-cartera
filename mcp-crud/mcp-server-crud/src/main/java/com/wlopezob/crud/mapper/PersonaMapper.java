package com.wlopezob.crud.mapper;

import com.wlopezob.crud.dto.PersonaRequest;
import com.wlopezob.crud.dto.PersonaResponse;
import com.wlopezob.crud.entity.Persona;
import com.wlopezob.crud.util.ConversionUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    imports = {ConversionUtil.class}
)
public interface PersonaMapper {
    
    @Mapping(target = "fecha", expression = "java(ConversionUtil.localDateToString(persona.getFecha()))")
    @Mapping(target = "tipoPersona", expression = "java(ConversionUtil.tipoPersonaToString(persona.getTipoPersona()))")
    @Mapping(target = "dni", source = "dni")
    PersonaResponse toResponse(Persona persona);
    
    List<PersonaResponse> toResponseList(List<Persona> personas);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fecha", expression = "java(ConversionUtil.stringToLocalDate(request.getFecha()))")
    @Mapping(target = "tipoPersona", expression = "java(ConversionUtil.stringToTipoPersona(request.getTipoPersona()))")
    @Mapping(target = "dni", source = "dni")
    Persona toEntity(PersonaRequest request);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fecha", expression = "java(ConversionUtil.stringToLocalDate(request.getFecha()))")
    @Mapping(target = "tipoPersona", expression = "java(ConversionUtil.stringToTipoPersona(request.getTipoPersona()))")
    @Mapping(target = "dni", source = "dni")
    void updateEntityFromRequest(PersonaRequest request, @MappingTarget Persona persona);
}
