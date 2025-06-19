package com.wlopezob.mcp_kibana.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogEntryDto {
    private String index;
    private String id;
    private Double score;
    private List<String> ignored;
    private LogSourceDto source;
    private List<Long> sort;
} 