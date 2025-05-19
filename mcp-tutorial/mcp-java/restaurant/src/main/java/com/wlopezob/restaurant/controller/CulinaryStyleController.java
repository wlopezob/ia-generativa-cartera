package com.wlopezob.restaurant.controller;

import com.wlopezob.restaurant.dto.CulinaryStyleDTO;
import com.wlopezob.restaurant.service.CulinaryStyleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/culinary-styles")
@RequiredArgsConstructor
public class CulinaryStyleController {

    private final CulinaryStyleService culinaryStyleService;

    @GetMapping
    public Flux<CulinaryStyleDTO> getAllCulinaryStyles() {
        return culinaryStyleService.getAllCulinaryStyles();
    }

    @GetMapping("/{id}")
    public Mono<CulinaryStyleDTO> getCulinaryStyleById(@PathVariable String id) {
        return culinaryStyleService.getCulinaryStyleById(id);
    }

    @PostMapping
    public Mono<CulinaryStyleDTO> createCulinaryStyle(@RequestBody CulinaryStyleDTO culinaryStyleDTO) {
        return culinaryStyleService.createCulinaryStyle(culinaryStyleDTO);
    }

    @PutMapping("/{id}")
    public Mono<CulinaryStyleDTO> updateCulinaryStyle(
            @PathVariable String id, 
            @RequestBody CulinaryStyleDTO culinaryStyleDTO) {
        return culinaryStyleService.updateCulinaryStyle(id, culinaryStyleDTO);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteCulinaryStyle(@PathVariable String id) {
        return culinaryStyleService.deleteCulinaryStyle(id);
    }
} 