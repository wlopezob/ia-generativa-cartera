package com.wlopezob.restaurant.service.impl;

import com.wlopezob.restaurant.dto.CulinaryStyleDTO;
import com.wlopezob.restaurant.dto.DishDTO;
import com.wlopezob.restaurant.dto.MenuDTO;
import com.wlopezob.restaurant.mapper.CulinaryStyleMapper;
import com.wlopezob.restaurant.mapper.DishMapper;
import com.wlopezob.restaurant.mapper.MenuMapper;
import com.wlopezob.restaurant.model.Menu;
import com.wlopezob.restaurant.repository.CulinaryStyleRepository;
import com.wlopezob.restaurant.repository.DishRepository;
import com.wlopezob.restaurant.repository.MenuRepository;
import com.wlopezob.restaurant.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final DishRepository dishRepository;
    private final CulinaryStyleRepository culinaryStyleRepository;
    private final MenuMapper menuMapper;
    private final DishMapper dishMapper;
    private final CulinaryStyleMapper culinaryStyleMapper;

    @Override
    public Flux<MenuDTO> getAllMenusWithDishes() {
        return menuRepository.findAll()
                .flatMap(this::enrichMenuWithDishesAndCulinaryStyle);
    }

    @Override
    public Flux<MenuDTO> getAllMenusByCulinaryStyleId(String culinaryStyleId) {
        return menuRepository.findByCulinaryStyleId(culinaryStyleId)
                .flatMap(this::enrichMenuWithDishesAndCulinaryStyle);
    }

    @Override
    public Mono<MenuDTO> getMenuById(String id) {
        return menuRepository.findById(id)
                .flatMap(this::enrichMenuWithDishesAndCulinaryStyle);
    }

    @Override
    public Mono<MenuDTO> createMenu(MenuDTO menuDTO) {
        Menu menu = menuMapper.toEntity(menuDTO);
        return menuRepository.save(menu)
                .flatMap(this::enrichMenuWithDishesAndCulinaryStyle);
    }

    @Override
    public Mono<MenuDTO> updateMenu(String id, MenuDTO menuDTO) {
        return menuRepository.findById(id)
                .flatMap(existingMenu -> {
                    Menu updatedMenu = menuMapper.toEntity(menuDTO);
                    updatedMenu = new Menu(
                            existingMenu.id(),
                            updatedMenu.name(),
                            updatedMenu.description(),
                            updatedMenu.culinaryStyleId(),
                            updatedMenu.dishesId(),
                            updatedMenu.totalCalories(),
                            updatedMenu.enabled(),
                            updatedMenu.totalPrice()
                    );
                    return menuRepository.save(updatedMenu);
                })
                .flatMap(this::enrichMenuWithDishesAndCulinaryStyle);
    }

    @Override
    public Mono<Void> deleteMenu(String id) {
        return menuRepository.deleteById(id);
    }

    private Mono<MenuDTO> enrichMenuWithDishesAndCulinaryStyle(Menu menu) {
        MenuDTO menuDTO = menuMapper.toDto(menu);
        
        // Enriquecer con el estilo culinario
        Mono<CulinaryStyleDTO> culinaryStyleMono = culinaryStyleRepository.findById(menu.culinaryStyleId())
                .map(culinaryStyleMapper::toDto)
                .defaultIfEmpty(new CulinaryStyleDTO());
        
        // Enriquecer con los platos
        Flux<DishDTO> dishesMono = Flux.fromIterable(menu.dishesId())
                .flatMap(dishId -> dishRepository.findById(dishId)
                        .map(dishMapper::toDto));
        
        return Mono.zip(culinaryStyleMono, dishesMono.collectList(), (culinaryStyle, dishes) -> {
            menuDTO.setCulinaryStyle(culinaryStyle);
            menuDTO.setDishes(dishes);
            return menuDTO;
        });
    }
} 