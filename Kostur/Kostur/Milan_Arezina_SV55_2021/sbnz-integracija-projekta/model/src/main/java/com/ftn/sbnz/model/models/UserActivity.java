package com.ftn.sbnz.model.models;


import lombok.*;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserActivity {
    private UUID userId;
    private boolean isNight;
    private boolean isWeekend;
}
