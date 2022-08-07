package com.experiment.SpringBeanThreadSafety.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    private String name;
    private String emailId;
}
