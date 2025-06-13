package com.wlopezob.mcp_kibana.service;

import com.wlopezob.mcp_kibana.dto.IndexListResponseDto;
import reactor.core.publisher.Mono;
 
public interface IndexService {
    Mono<IndexListResponseDto> getGroupedIndexes();
} 