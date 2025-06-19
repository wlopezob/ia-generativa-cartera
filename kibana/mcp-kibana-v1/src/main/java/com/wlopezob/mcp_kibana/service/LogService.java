package com.wlopezob.mcp_kibana.service;

import com.wlopezob.mcp_kibana.dto.LogResponseDto;
import com.wlopezob.mcp_kibana.dto.LogSearchRequestDto;
import reactor.core.publisher.Mono;

public interface LogService {
    Mono<LogResponseDto> searchLogs(LogSearchRequestDto request);
} 