package com.ftn.sbnz.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class JiraService {

    private final WebClient.Builder builder;

    public JiraService(WebClient.Builder builder) {
        this.builder = builder;
    }

    public Mono<Map<String, Object>> getMyIssues(String accessToken, String cloudId) {
        WebClient client = builder.baseUrl("https://api.atlassian.com").build();

        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/ex/jira/" + cloudId + "/rest/api/3/search/jql")
                        .queryParam("jql", "assignee=currentUser()")
                        .queryParam("maxResults", 20)
                        .build())
                .headers(h -> h.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {});
    }
}
