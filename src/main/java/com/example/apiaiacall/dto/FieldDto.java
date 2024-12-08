package com.example.apiaiacall.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class FieldDto {
    private CustomFieldOptionDto customfield_10072;  // Nested object
    private UserDto customfield_10073;  // Nested object
    private String customfield_10074;
    private List<CustomFieldOptionWithoutChildDto> customfield_10077;  // List of nested objects
    private List<CustomFieldOptionWithoutChildDto> customfield_10078;  // List of nested objects
    private CustomFieldOptionWithoutChildDto customfield_10079;  // Nested object
    private ComponentDto resolution;  // Nested object
    private PriorityDto priority;  // Nested object
    private List<Object> labels;  // List of Strings
    private Object aggregatetimeoriginalestimate;  // Can be null
    private List<IssueLinkDto> issuelinks;  // List of nested objects
    private UserDto assignee;  // Nested object
    private StatusDto status;  // Nested object
    private List<ComponentDto> components;  // List of nested objects
    private Object customfield_10050;  // Can be null
    private String customfield_10045;
    private String customfield_10046;
    private CustomField10031Dto customfield_10031;
    private CustomField10031Dto customfield_10032;
    private List<Object> customfield_10051;  // Empty list
    private Object aggregatetimeestimate;  // Can be null
    private UserDto creator;  // Nested object
    private UserDto reporter;
    private IssueTypeDto issuetype;
    private String summary;
    private String created;
    private CustomField10010Dto customfield_10010;
}
