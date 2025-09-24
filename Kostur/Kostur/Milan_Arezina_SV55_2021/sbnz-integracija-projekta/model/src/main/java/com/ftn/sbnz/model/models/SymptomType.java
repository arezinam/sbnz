package com.ftn.sbnz.model.models;

import lombok.*;

import java.time.Duration;
import java.time.Instant;

@Getter
@AllArgsConstructor
@ToString
public enum SymptomType {
    BURNOUT("Burnout", "Hronični stres i iscrpljenost"),
    CHRONIC_FATIGUE("ChronicFatigue", "Stalni umor i manjak energije"),
    LOW_FOCUS("LowFocus", "Smanjena koncentracija i pažnja");

    private final String code;
    private final String description;
}