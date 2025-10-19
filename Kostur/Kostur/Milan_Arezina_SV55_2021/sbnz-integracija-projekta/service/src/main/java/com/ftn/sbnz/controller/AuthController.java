package com.ftn.sbnz.controller;

import com.atlassian.sal.api.net.ResponseStatusException;
import com.ftn.sbnz.model.TokenResponse;
import com.ftn.sbnz.model.AccessibleResource;
import com.ftn.sbnz.service.AtlassianAuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final WebClient webClient = WebClient.create("https://api.atlassian.com");

    private final AtlassianAuthService authService;

    @Value("${atlassian.client-id}")
    private String clientId;

    @Value("${atlassian.redirect-uri}")
    private String redirectUri;

    @Value("${frontend.url}")
    private String frontendUrl;

    public AuthController(AtlassianAuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public void login(HttpServletResponse response) throws IOException {
        String url = "https://auth.atlassian.com/authorize" +
                "?audience=api.atlassian.com" +
                "&client_id=" + clientId +
                "&scope=read:jira-work read:jira-user offline_access" +
                "&redirect_uri=" + redirectUri +
                "&state=" + UUID.randomUUID() +
                "&response_type=code&prompt=consent";

        response.sendRedirect(url);
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        String accessToken = (String) session.getAttribute("jiraAccessToken");
        String cloudId = (String) session.getAttribute("jiraCloudId");

        if (accessToken == null || cloudId == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "loggedIn", false,
                            "error", "Not logged in"
                    ));
        }

        Map<String, Object> user = webClient.get()
                .uri("/ex/jira/" + cloudId + "/rest/api/3/myself")
                .headers(h -> h.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        return ResponseEntity.ok(Map.of(
                "loggedIn", true,
                "accountId", user.get("accountId"),
                "displayName", user.get("displayName"),
                "emailAddress", user.get("emailAddress"),
                "avatarUrl", ((Map<?, ?>) user.get("avatarUrls")).get("48x48")
        ));
    }


    @GetMapping("/callback")
    public void callback(@RequestParam String code,
                         HttpSession session,
                         HttpServletResponse response) throws IOException {
        authService.exchangeCodeForToken(code)
                .flatMap(token -> authService.getAccessibleResources(token.getAccessToken())
                        .map(resources -> {
                            session.setAttribute("jiraAccessToken", token.getAccessToken());
                            session.setAttribute("jiraRefreshToken", token.getRefreshToken());
                            session.setAttribute("jiraCloudId", resources.get(0).getId());
                            return true;
                        })
                )
                .block(); // block here since we need to finish before redirect

        // Redirect back to React app
        response.sendRedirect(frontendUrl);
    }



}
