package com.wlopezob.restaurant.service;

import com.wlopezob.restaurant.dto.CategoryDTO;
import com.wlopezob.restaurant.model.Category;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CategoryService {
    Flux<CategoryDTO> getAllCategories();
    Mono<CategoryDTO> getCategoryById(String id);
    Mono<CategoryDTO> createCategory(CategoryDTO categoryDTO);
    Mono<CategoryDTO> updateCategory(String id, CategoryDTO categoryDTO);
    Mono<Void> deleteCategory(String id);
} 