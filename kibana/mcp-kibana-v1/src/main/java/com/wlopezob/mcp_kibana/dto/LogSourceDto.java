package com.wlopezob.mcp_kibana.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogSourceDto {
    private String clazz;
    private String level;
    private String version;
    private String timestamp;
    private String httpMethod;
    private String env;
    private String message;
    private String pid;
    private String thread;
    private String serviceName;
    private String trace;
    private String requestId;
    private String span;
    private String exportable;
} 