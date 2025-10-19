package com.ftn.sbnz.controller;

import com.ftn.sbnz.DTO.TaskDTO;
import com.ftn.sbnz.service.JiraService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final JiraService jiraService;

    public TaskController(JiraService jiraService) {
        this.jiraService = jiraService;
    }

    @GetMapping("/my")
    public ResponseEntity<?> myTasks(HttpSession session) {
        String accessToken = (String) session.getAttribute("jiraAccessToken");
        String cloudId = (String) session.getAttribute("jiraCloudId");

        if (accessToken == null || cloudId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Not logged in"));
        }

        List<TaskDTO> tasks = jiraService.fetchMyTasks(accessToken, cloudId);
        return ResponseEntity.ok(tasks);
    }

}
