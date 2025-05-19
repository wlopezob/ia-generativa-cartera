package com.wlopezob.restaurant.toolservice;

import java.util.List;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import com.wlopezob.restaurant.dto.MenuDTO;
import com.wlopezob.restaurant.service.MenuService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuTool {

    private final MenuService menuService;

    @Tool(name = "getAllMenusByCulinaryStyleId", description = "Find all menus by culinary style id")
    public List<MenuDTO> getAllMenusByCulinaryStyleId(@ToolParam(description = "culinaryStyleId") String culinaryStyleId) {
        return menuService.getAllMenusByCulinaryStyleId(culinaryStyleId).collectList().block();
    }

    /*@Tool(name = "findAllMenuByCategory", description = "Find all menus by category")
    public List<MenuDTO> findAllMenuByCategory(@ToolParam(description = "categoryId") String categoryId) {
        return menuService.(categoryId).collectList().block();
    }*/

}
