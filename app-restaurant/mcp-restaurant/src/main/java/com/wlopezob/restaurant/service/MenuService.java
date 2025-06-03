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
    
    /**
     * Busca menús habilitados que contengan el texto en su nombre o descripción,
     * o en el nombre o descripción de sus platos habilitados.
     * 
     * @param searchText Texto a buscar
     * @return Flux de MenuDTO que coinciden con los criterios de búsqueda
     */
    Flux<MenuDTO> searchMenusByText(String searchText);
} 