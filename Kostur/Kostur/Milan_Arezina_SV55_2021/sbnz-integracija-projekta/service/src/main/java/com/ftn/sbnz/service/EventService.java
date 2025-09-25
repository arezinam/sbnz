package com.ftn.sbnz.service;

import com.ftn.sbnz.model.models.TaskEvent;
import com.ftn.sbnz.model.models.SelfReportEvent;
import com.ftn.sbnz.model.models.StressScore;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EventService {

    private final KieSession kieSession;

    private final Map<UUID, List<TaskEvent>> taskMap = new ConcurrentHashMap<>();
    private final Map<UUID, List<SelfReportEvent>> selfReportMap = new ConcurrentHashMap<>();
    private final Map<UUID, StressScore> stressScores = new ConcurrentHashMap<>();

    public EventService(KieSession kieSession) {
        this.kieSession = kieSession;

        this.kieSession.addEventListener(new DefaultAgendaEventListener() {
            @Override
            public void afterMatchFired(AfterMatchFiredEvent event) {
                System.out.println("Rule fired: " + event.getMatch().getRule().getName());
            }
        });
    }

    private void prepareScoreForRules(UUID userId) {
        StressScore score = stressScores.computeIfAbsent(userId, k -> new StressScore(userId, 0));
        if (kieSession.getFactHandle(score) != null) {
            // Remove from session
            kieSession.delete(kieSession.getFactHandle(score));
        }
        // Reset score and re-insert
        score.setScore(0);
        kieSession.insert(score);
    }

    public TaskEvent startTask(UUID userId, double intensity) {
        TaskEvent task = new TaskEvent();
        task.setUserId(userId);
        task.setTaskId(UUID.randomUUID());
        task.setIntensity(intensity);
        task.setStartTime(Instant.now());
        task.setEndTime(null);

        System.out.println("Started task with id: " + task.getTaskId());

        taskMap.computeIfAbsent(userId, k -> new ArrayList<>()).add(task);

        kieSession.insert(task);
        prepareScoreForRules(userId);  // Reset score before firing rules
        kieSession.fireAllRules();

        return task;
    }

    public TaskEvent endTask(UUID userId, UUID taskId) {
        List<TaskEvent> tasks = taskMap.getOrDefault(userId, Collections.emptyList());
        for (TaskEvent task : tasks) {
            if (task.getTaskId().equals(taskId) && task.getEndTime() == null) {
                task.setEndTime(Instant.now());
                kieSession.update(kieSession.getFactHandle(task), task);

                prepareScoreForRules(userId);  // Reset score before firing rules
                kieSession.fireAllRules();
                return task;
            }
        }
        return null;
    }

    public SelfReportEvent submitSelfReport(UUID userId, int stressLevel) {
        SelfReportEvent sr = new SelfReportEvent(userId, stressLevel);
        selfReportMap.computeIfAbsent(userId, k -> new ArrayList<>()).add(sr);

        kieSession.insert(sr);
        prepareScoreForRules(userId);  // Reset score before firing rules
        kieSession.fireAllRules();

        return sr;
    }

    public StressScore getStressScore(UUID userId) {
        return stressScores.getOrDefault(userId, new StressScore(userId, 0));
    }
}
