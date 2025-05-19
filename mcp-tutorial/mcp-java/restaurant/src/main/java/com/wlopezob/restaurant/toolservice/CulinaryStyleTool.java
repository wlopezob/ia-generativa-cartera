package com.wlopezob.restaurant.toolservice;

import java.util.List;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import com.wlopezob.restaurant.dto.CulinaryStyleDTO;

import com.wlopezob.restaurant.service.CulinaryStyleService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class CulinaryStyleTool {

    private final CulinaryStyleService culinaryStyleService;

    @Tool(name = "findAllCulinaryStyles", description = "Find all culinary styles")
    public List<CulinaryStyleDTO> findAllCulinaryStyles() {
        return culinaryStyleService.getAllCulinaryStyles().collectList().block();
    }
}
