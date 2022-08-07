package com.experiment.SpringBeanThreadSafety.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message {
    private int entityId;
    private String text;
}
