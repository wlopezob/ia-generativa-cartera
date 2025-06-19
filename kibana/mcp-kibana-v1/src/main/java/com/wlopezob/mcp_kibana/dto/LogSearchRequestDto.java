package com.wlopezob.mcp_kibana.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogSearchRequestDto {
    
    private String indexName;
    
    private String requestId;
    
    private LocalDateTime startDate;
    
    private LocalDateTime endDate;
} 