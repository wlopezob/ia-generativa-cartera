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

    @Tool(name = "getAllMenus", description = "Find all menus")
    public List<MenuDTO> getAllMenus() {
        return menuService.getAllMenusWithDishes().collectList().block();
    }

    @Tool(name = "getMenuById", description = "Find menu by id")
    public MenuDTO getMenuById(@ToolParam(description = "id of the menu") String id) {
        return menuService.getMenuById(id).block();
    }

    @Tool(name = "searchMenusByText", description = "Search menus and dishes by text in name or description")
    public List<MenuDTO> searchMenusByText(@ToolParam(description = "text to search in menu or dish name/description") String searchText) {
        return menuService.searchMenusByText(searchText).collectList().block();
    }

}
