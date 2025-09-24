package com.ftn.sbnz.model.models;

public enum StressLevel {
    LOW(0, 5),
    MEDIUM(6, 10),
    HIGH(11, 15),
    CRITICAL(16, Integer.MAX_VALUE);

    private final int min;
    private final int max;

    StressLevel(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public static StressLevel fromScore(double score) {
        for (StressLevel l : values()) {
            if (score >= l.min && score <= l.max) {
                return l;
            }
        }
        return LOW; // fallback
    }
}
