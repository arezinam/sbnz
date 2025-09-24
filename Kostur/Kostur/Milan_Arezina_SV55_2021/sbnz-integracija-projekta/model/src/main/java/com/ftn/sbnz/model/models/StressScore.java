package com.ftn.sbnz.model.models;

import lombok.*;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StressScore {
    private UUID userId;
    private double score;
    private StressLevel level;

    public StressScore(UUID userId, double score) {
        this.userId = userId;
        this.score = score;
        this.level = StressLevel.fromScore(score);
    }
}