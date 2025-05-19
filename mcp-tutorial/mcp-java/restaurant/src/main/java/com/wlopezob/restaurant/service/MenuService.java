package com.wlopezob.restaurant.service;

import com.wlopezob.restaurant.dto.MenuDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MenuService {
    Flux<MenuDTO> getAllMenusWithDishes();
    Flux<MenuDTO> getAllMenusByCulinaryStyleId(String culinaryStyleId);
    Mono<MenuDTO> getMenuById(String id);
    Mono<MenuDTO> createMenu(MenuDTO menuDTO);
    Mono<MenuDTO> updateMenu(String id, MenuDTO menuDTO);
    Mono<Void> deleteMenu(String id);
} 