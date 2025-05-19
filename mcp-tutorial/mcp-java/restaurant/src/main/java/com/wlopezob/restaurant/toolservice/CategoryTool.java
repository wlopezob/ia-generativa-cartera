package com.wlopezob.restaurant.toolservice;

import java.util.List;

import org.springframework.ai.moderation.Categories;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import com.wlopezob.restaurant.dto.CategoryDTO;
import com.wlopezob.restaurant.service.CategoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryTool {

    private final CategoryService categoryService;


    @Tool(name = "greet", description = "Greet the user")
    public String greet(@ToolParam(description = "The name of the user") String name) {
        return "Hello " + name;
    }

    @Tool(name = "findAllCategories", description = "Find all categories")
    public List<CategoryDTO> findAllCategories() {
        return categoryService.getAllCategories().collectList().block();
    }
   
}
