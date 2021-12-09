package com.entarch.workflow.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowExecutionStatus {

    private String executionArn;
    private String status;
    private String currentStepName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime currentStepEnteredTime;
    private Map<String,Object> metadata;

}
