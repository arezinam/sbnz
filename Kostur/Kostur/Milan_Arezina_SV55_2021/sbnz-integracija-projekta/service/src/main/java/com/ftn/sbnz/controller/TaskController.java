package com.ftn.sbnz.controller;

import com.ftn.sbnz.service.JiraService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final JiraService jiraService;

    public TaskController(JiraService jiraService) {
        this.jiraService = jiraService;
    }

    @GetMapping("/my")
    public Mono<Map<String, Object>> getMyTasks(HttpSession session) {
        String accessToken = (String) session.getAttribute("jiraAccessToken");
        String cloudId = (String) session.getAttribute("jiraCloudId");

        if (accessToken == null || cloudId == null) {
            throw new RuntimeException("Not logged in. Please go to /auth/login first.");
        }

        return jiraService.getMyIssues(accessToken, cloudId);
    }

}
