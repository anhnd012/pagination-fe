package com.example.apiaiacall.dto;

import lombok.Data;

import java.util.List;

@Data
public class ExcelColumnsDto {

    private String issueType;

    private String issueKey;

    private String issueId;

    private String summary;

    private String created;

    private String customField_IssueName;

    private String components;

    private String labels;

    private String assignee;

    private String assigneeId;

    private String customField_RootCauseType;

    private String reporter;

    private String reporterId;

    private String customField_SystemProblems;

    private String customField_RootCause;

    private String customField_Workaround;

    private String status;

    private String resolved;

    private String customField_TimeToResolution;

    private String customField_TimeToFirstResponse;

    private String priority;

    private String resolutionTime;

    private String sla;
}
