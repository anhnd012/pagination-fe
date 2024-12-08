package com.example.apiaiacall.controller;

import com.example.apiaiacall.dto.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class ApiAIAController {
    private final WebClient webClient;
    private static final String JIRA_SEARCH_URL = "/rest/api/2/search";

    public ApiAIAController() {
         webClient = WebClient.builder()
                .baseUrl("https://aia-one.atlassian.net")
                 .codecs(clientCodecConfigurer ->
                         clientCodecConfigurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024) // 10 MB
                 )
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " +
                        "dGhhbmgta2hhbmgubGVAYWlhLmNvbTpBVEFUVDN4RmZHRjBiX0Vyb2lYUTQzSms0UmlEZmhDN3p4bGowam9OQnhxaXFhNWE4UGQtRWgzLXBGZmxLOTBPT0I4Zk1TMi1aWXhSV212aXFXTmhIQnFMZzRhVklVZWo1WS16b3BkS3pVYUVpMEJWSWpQSzBGUGZoTGtuMGxTQUJvMG5CRVhILS1xemhvNHJVWjN2bnJCQWIyWmRaZUF2SktUdnlwcE90Yi1KQUw0ZHpZVGJuMzg9OUM0NEU5NDE=")
                .build();
    }

    @GetMapping("/jira-data")
    public Mono<AiaDto> getJiraData() {
        Mono<AiaDto> responseData = webClient.get()
                .uri("/rest/api/2/search?jql=assignee IN (5fbb3156aa1d30006ff2799d, 62fc55535fc3a72bda944d9a, 62fdbcaa43e43992b9a2fb4e, 712020:d38bbaf3-2b1a-454b-8da3-f8f320a5ce45, 712020:bbc7a667-0d86-4fde-9faa-2ae826f0f1c4, 712020:b9088dd5-c814-4bfa-8770-659099ccd1e3, 627dd3266ba8640069cfb086, 6269120434b9b700687b3b7c, 6417d9fd67102fc717c09586) AND project IN (AD, CS, PD, POS, OS) AND status IN (Closed, Escalated, \"In Progress\", \"In Progress\", Open, Pending, Reopened, Resolved, \"Waiting for customer\", \"Waiting for support\", Done) AND created >= \"2024-09-01\" AND type IN (Problem, Support, Bug) AND created <= \"2024-09-24\" ORDER BY resolution ASC, created DESC&startAt=0&maxResults=100")
                .retrieve()
                .bodyToMono(AiaDto.class);
        return responseData;
    }

    @GetMapping("/jira-excel-data")
    public Mono<List<ExcelColumnsDto>> getJiraExcelData() {
        return webClient.get()
                .uri("/rest/api/2/search?jql=assignee IN (5fbb3156aa1d30006ff2799d, 62fc55535fc3a72bda944d9a, 62fdbcaa43e43992b9a2fb4e, 712020:d38bbaf3-2b1a-454b-8da3-f8f320a5ce45, 712020:bbc7a667-0d86-4fde-9faa-2ae826f0f1c4, 712020:b9088dd5-c814-4bfa-8770-659099ccd1e3, 627dd3266ba8640069cfb086, 6269120434b9b700687b3b7c, 6417d9fd67102fc717c09586) AND project IN (AD, CS, PD, POS, OS) AND status IN (Closed, Escalated, \"In Progress\", \"In Progress\", Open, Pending, Reopened, Resolved, \"Waiting for customer\", \"Waiting for support\", Done) AND created >= \"2024-09-01\" AND type IN (Problem, Support, Bug) AND created <= \"2024-09-24\" ORDER BY resolution ASC, created DESC&startAt=0&maxResults=100")
                .retrieve()
                .bodyToMono(AiaDto.class)
                .flatMap(this::processJiraDataReactive);
    }

    private String buildJiraSearchUri() {
        return UriComponentsBuilder.fromPath(JIRA_SEARCH_URL)
                .queryParam("jql", buildJqlQuery())
                .queryParam("startAt", 0)
                .queryParam("maxResults", 100)
                .build()
                .toUriString();
    }


    private List<ExcelColumnsDto> processJiraData(AiaDto object) {
        List<ExcelColumnsDto> excelData = new ArrayList<>();
        for (IssueDto issue : object.getIssues()) {
            ExcelColumnsDto excelColumnsDto = createExcelColumnDto(issue);
            excelData.add(excelColumnsDto);
        }
        return excelData;
    }

    private Mono<List<ExcelColumnsDto>> processJiraDataReactive(AiaDto aiaDto) {
        // Transform issues in AiaDto into ExcelColumnsDto
        List<ExcelColumnsDto> excelData = aiaDto.getIssues().stream()
                .map(this::createExcelColumnDto)
                .toList();
        return Mono.just(excelData);
    }



    private String getLabels(ExcelColumnsDto excelColumnsDto) {
        return Optional.ofNullable(excelColumnsDto)
                .map(ExcelColumnsDto::getLabels)
                .filter(labels -> !labels.isEmpty()) // Check if the list is not empty
                .orElse("");
    }



    private byte[] generateExcelFile(List<ExcelColumnsDto> data) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Jira Data");
            createHeaderRow(sheet);
            populateDataRows(sheet, data);

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private void createHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {
                "Issue Type", "Issue Key", "Issue ID", "Summary", "Created",
                "Custom field (Issue Name)", "Components", "Labels", "Assignee", "Assignee ID",
                "Custom field (Root cause type)", "Reporter", "Reporter ID", "Custom field (System Problems)",
                "Custom field (Root cause)", "Custom field (Workaround)", "Status", "Resolved",
                "Custom field (Time to resolution)", "Custom field (Time to first response)", "Priority"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
    }

    private void populateDataRows(Sheet sheet, List<ExcelColumnsDto> data) {
        int rowCount = 1;
        for (ExcelColumnsDto rowData : data) {
            Row row = sheet.createRow(rowCount++);
            populateRow(row, rowData);
        }
    }
    @GetMapping(value = "/jira-excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> getAndExportJiraExcelData() {
        try {
            // Add debug logging
            System.out.println("Starting Jira data fetch...");

            AiaDto aiaDto = webClient.get()
                    .uri(buildJiraSearchUri())
                    .retrieve()
                    .bodyToMono(AiaDto.class)
                    .block();

            if (aiaDto == null || aiaDto.getIssues() == null) {
                System.out.println("No data received from Jira");
                return ResponseEntity.noContent().build();
            }

            System.out.println("Received " + aiaDto.getIssues().size() + " issues from Jira");

            List<ExcelColumnsDto> excelData = processJiraData(aiaDto);
            byte[] excelBytes = generateExcelFile(excelData);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "jira-data.xlsx");

            return ResponseEntity.ok().headers(headers).body(excelBytes);
        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }



    private void populateRow(Row row, ExcelColumnsDto data) {
        try {
            row.createCell(0).setCellValue(nullSafeString(data.getIssueType()));
            row.createCell(1).setCellValue(nullSafeString(data.getIssueKey()));
            row.createCell(2).setCellValue(nullSafeString(data.getIssueId()));
            row.createCell(3).setCellValue(nullSafeString(data.getSummary()));
            row.createCell(4).setCellValue(nullSafeString(converCreatedDateTime(data.getCreated())));
            row.createCell(5).setCellValue(nullSafeString(data.getCustomField_IssueName()));
            row.createCell(6).setCellValue(nullSafeString(data.getComponents()));
            row.createCell(7).setCellValue(nullSafeString(getLabels(data)));
            row.createCell(8).setCellValue(nullSafeString(data.getAssignee()));
            row.createCell(9).setCellValue(nullSafeString(data.getAssigneeId()));
            row.createCell(10).setCellValue(nullSafeString(data.getCustomField_RootCauseType()));
            row.createCell(11).setCellValue(nullSafeString(data.getReporter()));
            row.createCell(12).setCellValue(nullSafeString(data.getReporterId()));
            row.createCell(13).setCellValue(nullSafeString(data.getCustomField_SystemProblems()));
            row.createCell(14).setCellValue(nullSafeString(data.getCustomField_RootCause()));
            row.createCell(15).setCellValue(nullSafeString(data.getCustomField_Workaround()));
            row.createCell(16).setCellValue(nullSafeString(data.getStatus()));
            row.createCell(17).setCellValue(nullSafeString(converCreatedDateTime(data.getResolved())));
            row.createCell(18).setCellValue(nullSafeString(data.getCustomField_TimeToResolution()));
            row.createCell(19).setCellValue(nullSafeString(data.getCustomField_TimeToFirstResponse()));
            row.createCell(20).setCellValue(nullSafeString(data.getPriority()));
        } catch (Exception e) {
            System.err.println("Error populating row: " + e.getMessage());
            throw e;
        }
    }

    private String nullSafeString(Object value) {
        return value != null ? value.toString() : "";
    }

    private String buildJqlQuery() {
        return "assignee IN (5fbb3156aa1d30006ff2799d, 62fc55535fc3a72bda944d9a, 62fdbcaa43e43992b9a2fb4e, 712020:d38bbaf3-2b1a-454b-8da3-f8f320a5ce45, 712020:bbc7a667-0d86-4fde-9faa-2ae826f0f1c4, 712020:b9088dd5-c814-4bfa-8770-659099ccd1e3, 627dd3266ba8640069cfb086, 6269120434b9b700687b3b7c, 6417d9fd67102fc717c09586) AND project IN (AD, CS, PD, POS, OS) AND status IN (Closed, Escalated, \"In Progress\", \"In Progress\", Open, Pending, Reopened, Resolved, \"Waiting for customer\", \"Waiting for support\", Done) AND created >= \"2024-09-01\" AND type IN (Problem, Support, Bug) AND created <= \"2024-09-24\" ORDER BY resolution ASC, created DESC&startAt=0&maxResults=100";
    }

    private ExcelColumnsDto createExcelColumnDto(IssueDto issue) {
        ExcelColumnsDto dto = new ExcelColumnsDto();
        FieldDto fields = issue.getFields();

        String status = Optional.ofNullable(fields.getCustomfield_10010())
                              .map(field -> field.getCurrentStatus().getStatus())
                .orElse("");

        String resolved = Optional.ofNullable(fields.getCustomfield_10010())
                            .map(field -> field.getCurrentStatus())
                            .map(st -> st.getStatusDate())
                            .map(statusDate -> statusDate.getJira())
                            .filter(jiraDate -> jiraDate != null && !jiraDate.trim().isEmpty())
                            .orElse("Invalid Date");

        String timeResoltution = formatTime(fields.getCustomfield_10031().getCompletedCycles().get(0).getStartTime().getFriendly());
        String timeFirstResponse = formatTime(fields.getCustomfield_10032().getCompletedCycles().get(0).getStartTime().getFriendly());

        try {
            dto.setIssueType(fields.getIssuetype().getName());
            dto.setIssueKey(issue.getKey());
            dto.setIssueId(issue.getId());
            dto.setSummary(fields.getSummary());
            dto.setCreated(converCreatedDateTime(fields.getCreated()));
            dto.setReporter(fields.getReporter().getDisplayName());
            dto.setReporterId(fields.getReporter().getAccountId());
            dto.setCustomField_SystemProblems(fields.getCustomfield_10078().get(0).getValue());
            dto.setCustomField_RootCauseType(fields.getCustomfield_10079().getValue());
            dto.setCustomField_Workaround(fields.getCustomfield_10046());
            dto.setStatus(status);
            dto.setResolved(resolved);
            dto.setCustomField_TimeToResolution(timeResoltution);
            dto.setCustomField_TimeToFirstResponse(timeFirstResponse);
            dto.setCustomField_RootCause(fields.getCustomfield_10045());

            // Handle potential null values
            if (fields.getCustomfield_10072() != null) {
                String childValue = Optional.ofNullable(fields.getCustomfield_10072().getChild())
                        .map(CustomFieldOptionWithoutChildDto::getValue)
                        .orElse("No Child");
                dto.setCustomField_IssueName(fields.getCustomfield_10072().getValue() + "->" + childValue);
            }

            // Set components
            if (fields.getComponents() != null) {
                dto.setComponents(fields.getComponents().get(0).getName());
            }
            // Set labels
            if (fields.getLabels() != null) {
                dto.setLabels(getLabels(dto));
            }
            // Set other fields with null checking
            if (fields.getAssignee() != null) {
                dto.setAssignee(fields.getAssignee().getDisplayName());
                dto.setAssigneeId(fields.getAssignee().getAccountId());
            }
            if (fields.getPriority() != null) {
                dto.setPriority(fields.getPriority().getName());
            }
            // Add additional field mappings as needed
            return dto;
        } catch (Exception e) {
            System.err.println("Error creating Excel DTO for issue " + issue.getKey() + ": " + e.getMessage());
            throw e;
        }
    }

    public String formatTime(String time) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MMM/yy h:mm a", Locale.ENGLISH);
        LocalTime timeResolution = LocalTime.parse(time, inputFormatter);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("h:mm");
        String formattedTime = timeResolution.format(outputFormatter);
        return formattedTime;
    }

    public String converCreatedDateTime(String inputDate) {
        if(inputDate.equals("Invalid Date")){
            return "1/1/2024  0:00:00 AM";
        }
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(
                inputDate,
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        );;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a");
        String formattedDate = zonedDateTime.format(formatter);
        return formattedDate;
    }

}
