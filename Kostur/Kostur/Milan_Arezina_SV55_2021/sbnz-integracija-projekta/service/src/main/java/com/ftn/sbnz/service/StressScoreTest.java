package com.ftn.sbnz.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import com.ftn.sbnz.model.models.UserActivity;
import com.ftn.sbnz.model.models.TaskEvent;
import com.ftn.sbnz.model.models.SelfReportEvent;
import com.ftn.sbnz.model.models.StressScore;
import com.ftn.sbnz.model.models.StressLevel;

public class StressScoreTest {
    public static void main(String[] args) {
        try {
            KieContainer kc = KnowledgeSessionHelper.createRuleBase();
            KieSession kSession = KnowledgeSessionHelper.getStatefulKnowledgeSession(kc, "fwdKsession");

            kSession.addEventListener(new DefaultAgendaEventListener() {
                @Override
                public void afterMatchFired(AfterMatchFiredEvent event) {
                    System.out.println("==== Rule fired: " + event.getMatch().getRule().getName() + " ====");
                    event.getMatch().getObjects().forEach(obj -> {
                        if (obj instanceof StressScore) {
                            StressScore s = (StressScore) obj;
                            System.out.println("   StressScore -> Score: " + s.getScore() + ", Level: " + s.getLevel());
                        } else if (obj instanceof TaskEvent) {
                            TaskEvent t = (TaskEvent) obj;
                            System.out.println("   TaskEvent -> ID: " + t.getTaskId() + ", Intensity: " + t.getIntensity() + ", Active: " + t.isActive());
                        } else if (obj instanceof SelfReportEvent) {
                            SelfReportEvent sr = (SelfReportEvent) obj;
                            System.out.println("   SelfReport -> StressLevel: " + sr.getStressLevel() + ", Timestamp: " + sr.getTimestamp());
                        } else if (obj instanceof UserActivity) {
                            UserActivity ua = (UserActivity) obj;
                            System.out.println("   UserActivity -> Night: " + ua.isNight() + ", Weekend: " + ua.isWeekend());
                        }
                    });
                    System.out.println("=====================================");
                }
            });

            UUID userId = UUID.randomUUID();

            UserActivity ua = new UserActivity();
            ua.setUserId(userId);
            ua.setNight(true);
            ua.setWeekend(false);

            StressScore score = new StressScore();
            score.setUserId(userId);
            score.setScore(0);
            score.setLevel(StressLevel.LOW);

            TaskEvent task1 = new TaskEvent(userId, UUID.randomUUID(), Instant.now(), null, 2);
            TaskEvent task2 = new TaskEvent(userId, UUID.randomUUID(), Instant.now(), null, 1);

            SelfReportEvent sr = new SelfReportEvent(userId, 3, Instant.now());

            kSession.insert(ua);
            kSession.insert(score);
            kSession.insert(task1);
            kSession.insert(task2);
            kSession.insert(sr);

            int firedRules = kSession.fireAllRules();

            System.out.println("\n===== Final Stress Summary =====");
            System.out.println("User ID: " + score.getUserId());
            System.out.println("Score: " + score.getScore());
            System.out.println("Stress Level: " + score.getLevel());
            System.out.println("================================\n");

            System.out.println("Active Tasks:");
            kSession.getObjects(o -> o instanceof TaskEvent)
                    .forEach(o -> {
                        TaskEvent t = (TaskEvent) o;
                        System.out.println(String.format(" - Task %s | Intensity: %.1f | Active: %b", t.getTaskId(), t.getIntensity(), t.isActive()));
                    });

            System.out.println("\nSelf-Reports:");
            kSession.getObjects(o -> o instanceof SelfReportEvent)
                    .forEach(o -> {
                        SelfReportEvent sEvent = (SelfReportEvent) o;
                        System.out.println(String.format(" - Stress Level: %d | Timestamp: %s", sEvent.getStressLevel(), sEvent.getTimestamp()));
                    });

            System.out.println("\nNumber of rules fired: " + firedRules);

            kSession.dispose();

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
