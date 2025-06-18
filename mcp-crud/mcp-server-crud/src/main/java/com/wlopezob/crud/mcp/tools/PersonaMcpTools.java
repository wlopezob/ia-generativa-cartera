package com.wlopezob.crud.mcp.tools;

import com.wlopezob.crud.dto.PersonaRequest;
import com.wlopezob.crud.dto.PersonaResponse;
import com.wlopezob.crud.service.PersonaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonaMcpTools {

    private final PersonaService personaService;

    @Tool(description = "Listar todas las personas registradas en el sistema")
    public List<PersonaResponse> listarPersonas() {
        log.info("Ejecutando herramienta para listar personas");
        return personaService.getAllPersonasSimple().block();
    }

    @Tool(description = "Crear una nueva persona en el sistema")
    public PersonaResponse crearPersona(
            @ToolParam(description = "Nombre de la persona", required = true) String nombre,
            @ToolParam(description = "Apellido de la persona", required = true) String apellido,
            @ToolParam(description = "Edad de la persona", required = true) Integer edad,
            @ToolParam(description = "Fecha de nacimiento en formato dd/MM/yyyy", required = true) String fecha,
            @ToolParam(description = "Tipo de persona (Padre, Madre e Hijo)", required = true) String tipoPersona,
            @ToolParam(description = "DNI de la persona, debe ser único", required = true) String dni,
            @ToolParam(description = "Saldo de la persona", required = false) Double saldo) {
        
        log.info("Ejecutando herramienta para crear persona: {}", nombre);
        
        PersonaRequest request = new PersonaRequest();
        request.setNombre(nombre);
        request.setApellido(apellido);
        request.setEdad(edad);
        request.setFecha(fecha);
        request.setTipoPersona(tipoPersona);
        request.setDni(dni);
        request.setSaldo(saldo);
        
        Mono<PersonaResponse> responseMono = personaService.createPersona(request);
        return responseMono.block();
    }
    
    @Tool(description = "Actualizar una persona existente en el sistema")
    public PersonaResponse actualizarPersona(
            @ToolParam(description = "ID de la persona a actualizar", required = true) Long id,
            @ToolParam(description = "Nombre de la persona", required = true) String nombre,
            @ToolParam(description = "Apellido de la persona", required = true) String apellido,
            @ToolParam(description = "Edad de la persona", required = true) Integer edad,
            @ToolParam(description = "Fecha de nacimiento en formato dd/MM/yyyy", required = true) String fecha,
            @ToolParam(description = "Tipo de persona (Padre, Madre e Hijo)", required = true) String tipoPersona,
            @ToolParam(description = "DNI de la persona, debe ser único", required = true) String dni) {
        
        log.info("Ejecutando herramienta para actualizar persona con ID: {}", id);
        
        PersonaRequest request = new PersonaRequest();
        request.setNombre(nombre);
        request.setApellido(apellido);
        request.setEdad(edad);
        request.setFecha(fecha);
        request.setTipoPersona(tipoPersona);
        request.setDni(dni);
        
        Mono<PersonaResponse> responseMono = personaService.updatePersona(id, request);
        return responseMono.block();
    }
    
    @Tool(description = "Eliminar una persona del sistema por su ID")
    public String eliminarPersona(
            @ToolParam(description = "ID de la persona a eliminar", required = true) Long id) {
        
        log.info("Ejecutando herramienta para eliminar persona con ID: {}", id);
        
        personaService.deletePersona(id).block();
        return "Persona con ID " + id + " eliminada correctamente";
    }

    @Tool(description = "Buscar una persona por su ID")
    public PersonaResponse buscarPersonaPorId(
            @ToolParam(description = "ID de la persona a buscar", required = true) Long id) {
        
        log.info("Ejecutando herramienta para buscar persona con ID: {}", id);
        
        Mono<PersonaResponse> responseMono = personaService.getPersonaById(id);
        return responseMono.block();
    }
} 