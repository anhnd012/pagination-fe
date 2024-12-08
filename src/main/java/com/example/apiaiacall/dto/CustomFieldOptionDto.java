package com.example.apiaiacall.dto;

import lombok.Data;

@Data
public class CustomFieldOptionDto {
    private String self;
    private String value;
    private String id;
    private CustomFieldOptionWithoutChildDto child;

}
