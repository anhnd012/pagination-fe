package com.example.apiaiacall.dto;

import lombok.Data;

@Data
public class IssueLinkDto {
    private String id;
    private String self;
    private Object type;
    private CustomFieldOnwardDto fields;
}
