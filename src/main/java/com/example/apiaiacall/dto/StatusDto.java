package com.example.apiaiacall.dto;

import lombok.Data;

@Data
public class StatusDto {
    private String self;
    private String description;
    private String iconUrl;
    private String name;
    private Object statusCategory;

}
