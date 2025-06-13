package com.wlopezob.mcp_kibana.mcp;

import com.wlopezob.mcp_kibana.dto.LogResponseDto;
import com.wlopezob.mcp_kibana.dto.LogSearchRequestDto;
import com.wlopezob.mcp_kibana.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class LogMcpToolService {
    private final LogService logService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    @Tool(description = "Search logs by index name, request ID, and date range")
    public LogResponseDto searchLogs(
            @ToolParam(description = "The name of the index to search (e.g., 'ms-data-sensitive-prod')",
            required = true)
            String indexName,
            
            @ToolParam(description = "The request ID to search for",
            required = true)
            String requestId,
            
            @ToolParam(description = "Start date in ISO format (e.g., '2025-05-07T00:00:00')",
            required = true)
            String startDate,
            
            @ToolParam(description = "End date in ISO format (e.g., '2025-05-08T00:00:00')",
            required = true)
            String endDate
    ) {
        LogSearchRequestDto request = LogSearchRequestDto.builder()
                .indexName(indexName)
                .requestId(requestId)
                .startDate(LocalDateTime.parse(startDate, DATE_FORMATTER))
                .endDate(LocalDateTime.parse(endDate, DATE_FORMATTER))
                .build();
                
        return logService.searchLogs(request).block();
    }
} 