package com.ftn.sbnz.service;

import com.ftn.sbnz.model.TokenResponse;
import com.ftn.sbnz.model.AccessibleResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class AtlassianAuthService {

    private final WebClient webClient;

    @Value("${atlassian.client-id}")
    private String clientId;

    @Value("${atlassian.client-secret}")
    private String clientSecret;

    @Value("${atlassian.redirect-uri}")
    private String redirectUri;

    public AtlassianAuthService(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("https://auth.atlassian.com").build();
    }

    public Mono<TokenResponse> exchangeCodeForToken(String code) {
        return webClient.post()
                .uri("/oauth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "grant_type", "authorization_code",
                        "client_id", clientId,
                        "client_secret", clientSecret,
                        "code", code,
                        "redirect_uri", redirectUri
                ))
                .retrieve()
                .bodyToMono(TokenResponse.class);
    }

    public Mono<List<AccessibleResource>> getAccessibleResources(String accessToken) {
        return WebClient.create("https://api.atlassian.com")
                .get()
                .uri("/oauth/token/accessible-resources")
                .headers(h -> h.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {});
    }
}
