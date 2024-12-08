package com.example.apiaiacall.dto;

import lombok.Data;

@Data
public class CustomFieldOnwardDto {
    private String summary;
    private StatusDto status;
    private PriorityDto priority;
    private Object issuetype;

}
