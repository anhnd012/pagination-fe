package com.example.apiaiacall.dto;

import lombok.Data;

import java.util.List;

@Data
public class AiaDto {
    private String expand;

    private int startAt;

    private int maxResults;

    private int total;

    private List<IssueDto> issues;
}
