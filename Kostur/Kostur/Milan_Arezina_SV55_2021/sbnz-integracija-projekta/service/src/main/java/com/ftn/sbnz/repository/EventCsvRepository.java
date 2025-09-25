package com.ftn.sbnz.repository;

import com.ftn.sbnz.model.models.SelfReportEvent;
import com.ftn.sbnz.model.models.TaskEvent;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class EventCsvRepository {

    private final String selfReportFile = "self_reports.csv";
    private final String taskFile = "task_events.csv";

    public void saveSelfReport(SelfReportEvent event) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(selfReportFile, true))) {
            writer.write(event.getUserId() + "," + event.getStressLevel() + "," + event.getTimestamp().toString());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveTaskEvent(TaskEvent event) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(taskFile, true))) {
            writer.write(event.getUserId() + "," + event.getTaskId() + "," +
                    event.getStartTime().toString() + "," +
                    (event.getEndTime() != null ? event.getEndTime().toString() : "") + "," +
                    event.getIntensity());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<SelfReportEvent> readSelfReports() {
        List<SelfReportEvent> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(selfReportFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                SelfReportEvent event = new SelfReportEvent(
                        UUID.fromString(parts[0]),
                        Integer.parseInt(parts[1]),
                        Instant.parse(parts[2])
                );
                list.add(event);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<TaskEvent> readTaskEvents() {
        List<TaskEvent> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(taskFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                TaskEvent event = new TaskEvent(
                        UUID.fromString(parts[0]),
                        UUID.fromString(parts[1]),
                        Instant.parse(parts[2]),
                        parts[3].isEmpty() ? null : Instant.parse(parts[3]),
                        Double.parseDouble(parts[4])
                );
                list.add(event);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
