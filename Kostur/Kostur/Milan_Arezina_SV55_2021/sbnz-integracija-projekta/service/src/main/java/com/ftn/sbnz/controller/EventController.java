package com.ftn.sbnz.controller;

import com.ftn.sbnz.model.models.StressScore;
import com.ftn.sbnz.model.models.TaskEvent;
import com.ftn.sbnz.model.models.SelfReportEvent;
import com.ftn.sbnz.service.EventService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/task/start")
    public TaskEvent startTask(@RequestParam UUID userId, @RequestParam double intensity) {
        return eventService.startTask(userId, intensity);
    }

    @PostMapping("/task/end")
    public TaskEvent endTask(@RequestParam UUID userId, @RequestParam UUID taskId) {
        return eventService.endTask(userId, taskId);
    }

    @PostMapping("/self-report")
    public SelfReportEvent selfReport(@RequestParam UUID userId, @RequestParam int stressLevel) {
        return eventService.submitSelfReport(userId, stressLevel);
    }

    @GetMapping("/stress/{userId}")
    public StressScore getStressScore(@PathVariable UUID userId) {
        return eventService.getStressScore(userId);
    }
}
