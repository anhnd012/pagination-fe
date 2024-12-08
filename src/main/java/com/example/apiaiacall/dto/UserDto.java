package com.example.apiaiacall.dto;

import lombok.Data;

@Data
public class UserDto {
    private String self;
    private String accountId;
    private String emailAddress;
    private Object avatarUrls;
    private String displayName;
    private boolean active;
    private String timeZone;
    private String accountType;
}
