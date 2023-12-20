package com.example.bookloader.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    String firstName;
    String lastName;
    Integer age;
    Boolean active;
}
