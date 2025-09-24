package com.ftn.sbnz.model.models;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class User {
    private UUID userId;
    private String firstName;

    public User(String firstName){
        this.userId = UUID.randomUUID();
        this.firstName = firstName;
    }
}
