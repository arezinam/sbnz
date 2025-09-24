package com.ftn.sbnz.model.models;

import lombok.*;
import java.time.Instant;
import java.util.UUID;
import org.kie.api.definition.type.Role;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Role(Role.Type.EVENT)
public class TaskEvent {

    private UUID userId;
    private UUID taskId;

    private Instant startTime;

    private Instant endTime;
    private double intensity;

    public boolean isActive() {
        return endTime == null;
    }

    // Drools sliding window needs a long timestamp
    public long getStartTimeMillis() {
        return startTime.toEpochMilli();
    }
}
