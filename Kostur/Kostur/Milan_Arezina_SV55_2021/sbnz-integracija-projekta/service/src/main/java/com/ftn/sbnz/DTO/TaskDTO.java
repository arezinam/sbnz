package com.ftn.sbnz.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {
    String id;
    String summary;
    String description;
    String created;
    String dueDate;
    String status;
}