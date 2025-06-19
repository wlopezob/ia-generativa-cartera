package com.wlopezob.mcp_kibana.client;

import com.wlopezob.mcp_kibana.config.KibanaProxyProperties;
import com.wlopezob.mcp_kibana.dto.LogSearchRequestDto;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.format.DateTimeFormatter;

@Component
public class KibanaProxyClient {
    private final WebClient webClient;
    private final KibanaProxyProperties properties;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public KibanaProxyClient(WebClient webClient, KibanaProxyProperties properties) {
        this.webClient = webClient;
        this.properties = properties;
    }

    public Mono<String> fetchIndices() {
        return webClient.post()
                .uri("https://monitoring.apiusoft.com/api/console/proxy?path=_cat/indices?v&method=GET")
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .header("kbn-xsrf", "true")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + properties.getAuth())
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> searchLogs(LogSearchRequestDto request) {
        String queryJson = buildLogSearchQuery(request);
        String path = request.getIndexName() + "-*/_search";
        
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                    .scheme("https")
                    .host("monitoring.apiusoft.com")
                    .path("/api/console/proxy")
                    .queryParam("path", path)
                    .queryParam("method", "POST")
                    .build())
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .header("kbn-xsrf", "true")
                .header(HttpHeaders.AUTHORIZATION, "Basic " +properties.getAuth())
                .bodyValue(queryJson)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> {
                    System.out.println("Error searching logs: " + error.getMessage());
                });
    }

    private String buildLogSearchQuery(LogSearchRequestDto request) {
        return """
            {
              "query": {
                "bool": {
                  "must": [
                    {
                      "range": {
                        "@timestamp": {
                          "gte": "%s",
                          "lt": "%s"
                        }
                      }
                    },
                    {
                      "match": {
                        "Request-ID": "%s"
                      }
                    }
                  ]
                }
              },
              "sort": [
                {
                  "@timestamp": {
                    "order": "asc"
                  }
                }
              ],
              "size": 100
            }
            """.formatted(
                request.getStartDate().format(DATE_FORMATTER),
                request.getEndDate().format(DATE_FORMATTER),
                request.getRequestId()
            );
    }
} 