package com.wlopezob.mcp_kibana.service.impl;

import com.wlopezob.mcp_kibana.client.KibanaProxyClient;
import com.wlopezob.mcp_kibana.dto.IndexListResponseDto;
import com.wlopezob.mcp_kibana.service.IndexService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IndexServiceImpl implements IndexService {
    private final KibanaProxyClient kibanaProxyClient;


    @Override
    public Mono<IndexListResponseDto> getGroupedIndexes() {
        return kibanaProxyClient.fetchIndices()
                .map(this::parseIndexes);
    }

    /**
     * Parses the raw text response from Kibana and extracts a sorted list of base index names (without date suffixes).
     * @param rawText the raw response from Kibana
     * @return IndexListResponseDto containing a sorted list of base index names
     */
    private IndexListResponseDto parseIndexes(String rawText) {
        String[] lines = rawText.split("\n");
        if (lines.length < 2) return IndexListResponseDto.builder().indices(Collections.emptyList()).build();
        List<String> headers = Arrays.asList(lines[0].trim().split("\\s+"));
        int indexCol = headers.indexOf("index");
        if (indexCol == -1) return IndexListResponseDto.builder().indices(Collections.emptyList()).build();
        Pattern pattern = Pattern.compile("^(.*?)(-\\d{4}[-.]\\d{2}[-.]\\d{2})?$", Pattern.CASE_INSENSITIVE);
        Set<String> baseNames = new HashSet<>();
        for (int i = 1; i < lines.length; i++) {
            String[] cols = lines[i].trim().split("\\s+");
            if (cols.length <= indexCol) continue;
            String indexName = cols[indexCol];
            Matcher m = pattern.matcher(indexName);
            String baseName = m.find() ? m.group(1) : indexName;
            baseNames.add(baseName);
        }
        List<String> result = baseNames.stream().sorted().collect(Collectors.toList());
        return IndexListResponseDto.builder().indices(result).build();
    }
} 