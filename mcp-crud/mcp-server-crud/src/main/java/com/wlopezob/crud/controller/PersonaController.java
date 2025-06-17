package com.wlopezob.crud.controller;

import com.wlopezob.crud.dto.PersonaListResponse;
import com.wlopezob.crud.dto.PersonaRequest;
import com.wlopezob.crud.dto.PersonaResponse;
import com.wlopezob.crud.enums.TipoPersona;
import com.wlopezob.crud.service.PersonaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/personas")
@RequiredArgsConstructor
public class PersonaController {
    
    private final PersonaService personaService;    
    @GetMapping
    public Mono<List<PersonaResponse>> getAllPersonas() {
        return personaService.getAllPersonasSimple();
    }
    
    @GetMapping("/detailed")
    public Mono<PersonaListResponse> getAllPersonasDetailed() {
        return personaService.getAllPersonas();
    }
    
    @GetMapping("/{id}")
    public Mono<PersonaResponse> getPersonaById(@PathVariable Long id) {
        return personaService.getPersonaById(id);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<PersonaResponse> createPersona(@RequestBody PersonaRequest request) {
        return personaService.createPersona(request);
    }
    
    @PutMapping("/{id}")
    public Mono<PersonaResponse> updatePersona(@PathVariable Long id, @RequestBody PersonaRequest request) {
        return personaService.updatePersona(id, request);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deletePersona(@PathVariable Long id) {
        return personaService.deletePersona(id);
    }
    
    @GetMapping("/tipo/{tipoPersona}")
    public Mono<PersonaListResponse> getPersonasByTipo(@PathVariable TipoPersona tipoPersona) {
        return personaService.getPersonasByTipo(tipoPersona);
    }
    
    @GetMapping("/search/nombre")
    public Mono<PersonaListResponse> searchByNombre(@RequestParam String nombre) {
        return personaService.searchPersonasByNombre(nombre);
    }
    
    @GetMapping("/search/apellido")
    public Mono<PersonaListResponse> searchByApellido(@RequestParam String apellido) {
        return personaService.searchPersonasByApellido(apellido);
    }
    
    @GetMapping("/edad")
    public Mono<PersonaListResponse> getPersonasByEdadRange(
            @RequestParam Integer edadMin, 
            @RequestParam Integer edadMax) {
        return personaService.getPersonasByEdadRange(edadMin, edadMax);
    }
}
