package com.wlopezob.restaurant.service.impl;

import com.wlopezob.restaurant.dto.CategoryDTO;
import com.wlopezob.restaurant.dto.DishDTO;
import com.wlopezob.restaurant.mapper.CategoryMapper;
import com.wlopezob.restaurant.mapper.DishMapper;
import com.wlopezob.restaurant.model.Dish;
import com.wlopezob.restaurant.repository.CategoryRepository;
import com.wlopezob.restaurant.repository.DishRepository;
import com.wlopezob.restaurant.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DishServiceImpl implements DishService {

    private final DishRepository dishRepository;
    private final CategoryRepository categoryRepository;
    private final DishMapper dishMapper;
    private final CategoryMapper categoryMapper;

    @Override
    public Flux<DishDTO> getAllDishes() {
        return dishRepository.findAll()
                .flatMap(this::enrichDishWithCategory);
    }

    @Override
    public Flux<DishDTO> getAllDishesByCategoryId(String categoryId) {
        return dishRepository.findByCategoryId(categoryId)
                .flatMap(this::enrichDishWithCategory);
    }

    @Override
    public Mono<DishDTO> getDishById(String id) {
        return dishRepository.findById(id)
                .flatMap(this::enrichDishWithCategory);
    }

    @Override
    public Mono<DishDTO> createDish(DishDTO dishDTO) {
        Dish dish = dishMapper.toEntity(dishDTO);
        return dishRepository.save(dish)
                .flatMap(this::enrichDishWithCategory);
    }

    @Override
    public Mono<DishDTO> updateDish(String id, DishDTO dishDTO) {
        return dishRepository.findById(id)
                .flatMap(existingDish -> {
                    Dish updatedDish = dishMapper.toEntity(dishDTO);
                    updatedDish = new Dish(
                            existingDish.id(),
                            updatedDish.categoryId(),
                            updatedDish.name(),
                            updatedDish.descripcion(),
                            updatedDish.calories(),
                            updatedDish.enabled(),
                            updatedDish.price()
                    );
                    return dishRepository.save(updatedDish);
                })
                .flatMap(this::enrichDishWithCategory);
    }

    @Override
    public Mono<Void> deleteDish(String id) {
        return dishRepository.deleteById(id);
    }

    private Mono<DishDTO> enrichDishWithCategory(Dish dish) {
        DishDTO dishDTO = dishMapper.toDto(dish);
        return categoryRepository.findById(dish.categoryId())
                .map(categoryMapper::toDto)
                .map(categoryDTO -> {
                    dishDTO.setCategory(categoryDTO);
                    return dishDTO;
                })
                .defaultIfEmpty(dishDTO);
    }
} 