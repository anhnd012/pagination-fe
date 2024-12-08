package com.example.apiaiacall.dto;

import lombok.Data;

import java.util.List;

@Data
public class IssueDto {
    private String expand;

    private String id;

    private String self;

    private String key;

    private FieldDto fields;
}
