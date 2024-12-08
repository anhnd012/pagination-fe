package com.example.apiaiacall.dto;

import lombok.Data;

@Data
public class CurrentStatusDto {
    private String status;
    private String statusCategory;
    private StatusDateDto statusDate;
}
