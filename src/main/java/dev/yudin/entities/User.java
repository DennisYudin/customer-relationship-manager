package dev.yudin.entities;

import lombok.Data;

@Data
public class User {
    private long id;
    private String name;
    private String surname;
    private String email;
    private String login;
    private String password;
    private String type;
}

