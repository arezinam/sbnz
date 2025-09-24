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
public class SelfReportEvent {

    private UUID userId;
    private int stressLevel;

    private Instant timestamp;

    public SelfReportEvent(UUID userId, int stressLevel) {
        this.userId = userId;
        this.stressLevel = stressLevel;
        this.timestamp = Instant.now();
    }

    // Drools sliding window needs a long timestamp
    public long getTimestampMillis() {
        return timestamp.toEpochMilli();
    }
}
