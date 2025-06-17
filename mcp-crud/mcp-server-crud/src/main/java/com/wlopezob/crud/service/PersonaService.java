package com.wlopezob.crud.service;

import com.wlopezob.crud.dto.PersonaListResponse;
import com.wlopezob.crud.dto.PersonaRequest;
import com.wlopezob.crud.dto.PersonaResponse;
import com.wlopezob.crud.enums.TipoPersona;
import com.wlopezob.crud.mapper.PersonaMapper;
import com.wlopezob.crud.repository.PersonaRepository;
import com.wlopezob.crud.exception.DniDuplicadoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonaService {
    
    private final PersonaRepository personaRepository;
    private final PersonaMapper personaMapper;
    
    public Mono<PersonaListResponse> getAllPersonas() {
        log.info("Obteniendo todas las personas");
        return personaRepository.findAll()
                .map(personaMapper::toResponse)
                .collectList()
                .map(personas -> PersonaListResponse.builder()
                        .personas(personas)
                        .total(personas.size())
                        .timestamp(LocalDateTime.now())
                        .mensaje("Lista de personas obtenida exitosamente")
                        .build());
    }
    
    public Mono<PersonaResponse> getPersonaById(Long id) {
        log.info("Obteniendo persona por ID: {}", id);
        return personaRepository.findById(id)
                .map(personaMapper::toResponse);
    }
    
    public Mono<PersonaResponse> createPersona(PersonaRequest request) {
        log.info("Creando nueva persona: {}", request.getNombre());
        return personaRepository.findByDni(request.getDni())
                .flatMap(existing -> Mono.<PersonaResponse>error(new DniDuplicadoException(request.getDni())))
                .switchIfEmpty(
                    Mono.just(request)
                        .map(personaMapper::toEntity)
                        .flatMap(personaRepository::save)
                        .map(personaMapper::toResponse)
                );
    }
    
    public Mono<PersonaResponse> updatePersona(Long id, PersonaRequest request) {
        log.info("Actualizando persona con ID: {}", id);
        return personaRepository.findByDni(request.getDni())
                .flatMap(existing -> {
                    if (!existing.getId().equals(id)) {
                        return Mono.<PersonaResponse>error(new DniDuplicadoException(request.getDni()));
                    }
                    return Mono.empty();
                })
                .switchIfEmpty(
                    personaRepository.findById(id)
                        .flatMap(persona -> {
                            personaMapper.updateEntityFromRequest(request, persona);
                            return personaRepository.save(persona);
                        })
                        .map(personaMapper::toResponse)
                );
    }    
    public Mono<Void> deletePersona(Long id) {
        log.info("Eliminando persona con ID: {}", id);
        return personaRepository.deleteById(id);
    }
    
    public Mono<PersonaListResponse> getPersonasByTipo(TipoPersona tipoPersona) {
        log.info("Obteniendo personas por tipo: {}", tipoPersona);
        return personaRepository.findByTipoPersona(tipoPersona)
                .map(personaMapper::toResponse)
                .collectList()
                .map(personas -> PersonaListResponse.builder()
                        .personas(personas)
                        .total(personas.size())
                        .timestamp(LocalDateTime.now())
                        .mensaje("Personas filtradas por tipo: " + tipoPersona.getDescripcion())
                        .build());
    }
    
    public Mono<PersonaListResponse> searchPersonasByNombre(String nombre) {
        log.info("Buscando personas por nombre: {}", nombre);
        return personaRepository.findByNombreContainingIgnoreCase(nombre)
                .map(personaMapper::toResponse)
                .collectList()
                .map(personas -> PersonaListResponse.builder()
                        .personas(personas)
                        .total(personas.size())
                        .timestamp(LocalDateTime.now())
                        .mensaje("Búsqueda por nombre: " + nombre)
                        .build());
    }
    
    public Mono<PersonaListResponse> searchPersonasByApellido(String apellido) {
        log.info("Buscando personas por apellido: {}", apellido);
        return personaRepository.findByApellidoContainingIgnoreCase(apellido)
                .map(personaMapper::toResponse)
                .collectList()
                .map(personas -> PersonaListResponse.builder()
                        .personas(personas)
                        .total(personas.size())
                        .timestamp(LocalDateTime.now())
                        .mensaje("Búsqueda por apellido: " + apellido)
                        .build());
    }
    
    public Mono<PersonaListResponse> getPersonasByEdadRange(Integer edadMin, Integer edadMax) {
        log.info("Obteniendo personas por rango de edad: {} - {}", edadMin, edadMax);
        return personaRepository.findByEdadBetween(edadMin, edadMax)
                .map(personaMapper::toResponse)
                .collectList()
                .map(personas -> PersonaListResponse.builder()
                        .personas(personas)
                        .total(personas.size())
                        .timestamp(LocalDateTime.now())
                        .mensaje("Personas en rango de edad: " + edadMin + "-" + edadMax)
                        .build());
    }
    
    public Mono<List<PersonaResponse>> getAllPersonasSimple() {
        log.info("Obteniendo lista simple de todas las personas");
        return personaRepository.findAll()
                .map(personaMapper::toResponse)
                .collectList();
    }
}
