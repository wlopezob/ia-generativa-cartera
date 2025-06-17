package com.wlopezob.crud.repository;

import com.wlopezob.crud.entity.Persona;
import com.wlopezob.crud.enums.TipoPersona;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PersonaRepository extends ReactiveCrudRepository<Persona, Long> {
    
    Flux<Persona> findByTipoPersona(TipoPersona tipoPersona);
    
    Flux<Persona> findByNombreContainingIgnoreCase(String nombre);
    
    Flux<Persona> findByApellidoContainingIgnoreCase(String apellido);
    
    @Query("SELECT * FROM personas WHERE edad BETWEEN :edadMin AND :edadMax")
    Flux<Persona> findByEdadBetween(Integer edadMin, Integer edadMax);

    Mono<Persona> findByDni(String dni);
}
