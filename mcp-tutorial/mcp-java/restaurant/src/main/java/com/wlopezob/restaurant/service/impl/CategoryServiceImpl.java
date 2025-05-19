package com.wlopezob.restaurant.service.impl;

import com.wlopezob.restaurant.dto.CategoryDTO;
import com.wlopezob.restaurant.mapper.CategoryMapper;
import com.wlopezob.restaurant.model.Category;
import com.wlopezob.restaurant.repository.CategoryRepository;
import com.wlopezob.restaurant.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public Flux<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll()
                .map(categoryMapper::toDto);
    }

    @Override
    public Mono<CategoryDTO> getCategoryById(String id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toDto);
    }

    @Override
    public Mono<CategoryDTO> createCategory(CategoryDTO categoryDTO) {
        Category category = categoryMapper.toEntity(categoryDTO);
        return categoryRepository.save(category)
                .map(categoryMapper::toDto);
    }

    @Override
    public Mono<CategoryDTO> updateCategory(String id, CategoryDTO categoryDTO) {
        return categoryRepository.findById(id)
                .flatMap(existingCategory -> {
                    Category updatedCategory = categoryMapper.toEntity(categoryDTO);
                    updatedCategory = new Category(existingCategory.id(), updatedCategory.name());
                    return categoryRepository.save(updatedCategory);
                })
                .map(categoryMapper::toDto);
    }

    @Override
    public Mono<Void> deleteCategory(String id) {
        return categoryRepository.deleteById(id);
    }
} 