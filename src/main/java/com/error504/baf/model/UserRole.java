package com.error504.baf.model;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("ROLE_ADMIN"),
    USER_AUTH("USER_AUTH"),
    USER("ROLE_USER");

    private String value;

    UserRole(String value){
        this.value=value;
    }
}
