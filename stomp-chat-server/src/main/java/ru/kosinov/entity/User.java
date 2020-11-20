package ru.kosinov.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@AllArgsConstructor
@Data
public class User {

    private String name;

    private String email;

    private String preferredUsername;

    private Set<String> roles;

}
