package com.ftn.sbnz.service;

import com.ftn.sbnz.DTO.TaskDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Service
public class JiraService {

    private final WebClient.Builder builder;

    public JiraService(WebClient.Builder builder) {
        this.builder = builder;
    }

    public List<TaskDTO> fetchMyTasks(String accessToken, String cloudId) {
        WebClient client = builder.baseUrl("https://api.atlassian.com").build();

        Map<String, Object> body = Map.of(
                "jql", "assignee=currentUser()",
                "fields", new String[] { "id", "summary", "description", "created", "duedate", "status" }
        );

        Map<String, Object> resp = client.post()
                .uri("/ex/jira/" + cloudId + "/rest/api/3/search/jql")
                .headers(h -> h.setBearerAuth(accessToken))
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        var issues = (List<Map<String, Object>>) resp.get("issues");

        return issues.stream().map(issue -> {
            Map<String, Object> fields = (Map<String, Object>) issue.get("fields");

            String description = extractDescription(fields.get("description"));

            Map<String, Object> status = (Map<String, Object>) fields.get("status");
            Map<String, Object> statusCategory = status != null ? (Map<String, Object>) status.get("statusCategory") : null;
            String column = statusCategory != null ? (String) statusCategory.get("name") : null;

            return new TaskDTO(
                    (String) issue.get("id"),
                    (String) fields.get("summary"),   // your "title"
                    description,
                    (String) fields.get("created"),
                    (String) fields.get("duedate"),
                    column
            );
        }).toList();

    }

    private String extractDescription(Object descObj) {
        if (descObj == null) return null;
        if (descObj instanceof String s) return s;

        if (descObj instanceof Map<?, ?> descMap) {
            // Navigate into content[0].content[0].text
            var content = (List<Map<String, Object>>) descMap.get("content");
            if (content != null && !content.isEmpty()) {
                var firstParagraph = content.get(0);
                var innerContent = (List<Map<String, Object>>) firstParagraph.get("content");
                if (innerContent != null && !innerContent.isEmpty()) {
                    return (String) innerContent.get(0).get("text");
                }
            }
        }
        return null;
    }
}
