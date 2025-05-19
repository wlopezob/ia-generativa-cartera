package com.wlopezob.restaurant.service.impl;

import com.wlopezob.restaurant.dto.CategoryDTO;
import com.wlopezob.restaurant.dto.DishDTO;
import com.wlopezob.restaurant.dto.MenuDTO;
import com.wlopezob.restaurant.mapper.CategoryMapper;
import com.wlopezob.restaurant.mapper.DishMapper;
import com.wlopezob.restaurant.mapper.MenuMapper;
import com.wlopezob.restaurant.model.Menu;
import com.wlopezob.restaurant.repository.CategoryRepository;
import com.wlopezob.restaurant.repository.DishRepository;
import com.wlopezob.restaurant.repository.MenuRepository;
import com.wlopezob.restaurant.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final DishRepository dishRepository;
    private final CategoryRepository categoryRepository;
    private final MenuMapper menuMapper;
    private final DishMapper dishMapper;
    private final CategoryMapper categoryMapper;

    @Override
    public Flux<MenuDTO> getAllMenusWithDishes() {
        return menuRepository.findAll()
                .flatMap(this::enrichMenuWithDishesAndCategory);
    }

    @Override
    public Flux<MenuDTO> getAllMenusByCategoryId(String categoryId) {
        return menuRepository.findByCategoryId(categoryId)
                .flatMap(this::enrichMenuWithDishesAndCategory);
    }

    @Override
    public Mono<MenuDTO> getMenuById(String id) {
        return menuRepository.findById(id)
                .flatMap(this::enrichMenuWithDishesAndCategory);
    }

    @Override
    public Mono<MenuDTO> createMenu(MenuDTO menuDTO) {
        Menu menu = menuMapper.toEntity(menuDTO);
        return menuRepository.save(menu)
                .flatMap(this::enrichMenuWithDishesAndCategory);
    }

    @Override
    public Mono<MenuDTO> updateMenu(String id, MenuDTO menuDTO) {
        return menuRepository.findById(id)
                .flatMap(existingMenu -> {
                    Menu updatedMenu = menuMapper.toEntity(menuDTO);
                    updatedMenu = new Menu(
                            existingMenu.id(),
                            updatedMenu.dishesId(),
                            updatedMenu.categoryId(),
                            updatedMenu.totalCalories(),
                            updatedMenu.enabled(),
                            updatedMenu.totalPrice()
                    );
                    return menuRepository.save(updatedMenu);
                })
                .flatMap(this::enrichMenuWithDishesAndCategory);
    }

    @Override
    public Mono<Void> deleteMenu(String id) {
        return menuRepository.deleteById(id);
    }

    private Mono<MenuDTO> enrichMenuWithDishesAndCategory(Menu menu) {
        MenuDTO menuDTO = menuMapper.toDto(menu);
        
        // Enriquecer con la categoría
        Mono<CategoryDTO> categoryMono = categoryRepository.findById(menu.categoryId())
                .map(categoryMapper::toDto)
                .defaultIfEmpty(null);
        
        // Enriquecer con los platos
        Flux<DishDTO> dishesMono = Flux.fromIterable(menu.dishesId())
                .flatMap(dishId -> dishRepository.findById(dishId)
                        .flatMap(dish -> {
                            DishDTO dishDTO = dishMapper.toDto(dish);
                            return categoryRepository.findById(dish.categoryId())
                                    .map(categoryMapper::toDto)
                                    .map(categoryDTO -> {
                                        dishDTO.setCategory(categoryDTO);
                                        return dishDTO;
                                    })
                                    .defaultIfEmpty(dishDTO);
                        }))
                .collectList()
                .flatMapMany(Flux::fromIterable);
        
        return Mono.zip(categoryMono, dishesMono.collectList(), (category, dishes) -> {
            menuDTO.setCategory(category);
            menuDTO.setDishes(dishes);
            return menuDTO;
        });
    }
} 