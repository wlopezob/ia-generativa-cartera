package com.wlopezob.restaurant.controller;

import com.wlopezob.restaurant.dto.MenuDTO;
import com.wlopezob.restaurant.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    public Flux<MenuDTO> getAllMenus() {
        return menuService.getAllMenusWithDishes();
    }

    @GetMapping("/culinary-style/{culinaryStyleId}")
    public Flux<MenuDTO> getMenusByCulinaryStyleId(@PathVariable String culinaryStyleId) {
        return menuService.getAllMenusByCulinaryStyleId(culinaryStyleId);
    }

    @GetMapping("/{id}")
    public Mono<MenuDTO> getMenuById(@PathVariable String id) {
        return menuService.getMenuById(id);
    }

    @PostMapping
    public Mono<MenuDTO> createMenu(@RequestBody MenuDTO menuDTO) {
        return menuService.createMenu(menuDTO);
    }

    @PutMapping("/{id}")
    public Mono<MenuDTO> updateMenu(@PathVariable String id, @RequestBody MenuDTO menuDTO) {
        return menuService.updateMenu(id, menuDTO);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteMenu(@PathVariable String id) {
        return menuService.deleteMenu(id);
    }
} 