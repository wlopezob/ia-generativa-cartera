package com.wlopezob.mcp_kibana.mcp;

import com.wlopezob.mcp_kibana.dto.IndexListResponseDto;
import com.wlopezob.mcp_kibana.service.IndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class IndexMcpToolService {
    private final IndexService indexService;

    @Tool(description = "Get the list of base index names from Kibana")
    public IndexListResponseDto listIndexes() {
        return indexService.getGroupedIndexes().block();
    }
} 