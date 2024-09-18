package com.university.StudentDropper.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentDTO {

    String name;
    String surname;
    Integer age;
    String email;
    String password;
    String role;
}
